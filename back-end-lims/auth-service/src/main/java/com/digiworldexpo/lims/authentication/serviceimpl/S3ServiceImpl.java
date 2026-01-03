package com.digiworldexpo.lims.authentication.serviceimpl;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.digiworldexpo.lims.authentication.model.ResponseModel;
import com.digiworldexpo.lims.authentication.service.S3Service;
import com.digiworldexpo.lims.authentication.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class S3ServiceImpl implements S3Service {

    @Value("${aws.s3.bucketName}")
    private String bucketName;
    
    private final AmazonS3 amazonS3;
    
    private final HttpStatusCode httpStatusCode;
    
    public S3ServiceImpl(AmazonS3 amazonS3,HttpStatusCode httpStatusCode) {
        this.amazonS3 = amazonS3;
        this.httpStatusCode = httpStatusCode;
    }
    
    /**
     * Uploads a file to an Amazon S3 bucket and returns its public URL.
     * @param key - The key (path) under which to store the file in the S3 bucket.
     * @param multipartFile - The file to upload, represented as a MultipartFile object.
     * @return A String containing the public URL of the uploaded file.
     * @throws IOException If an error occurs while uploading the file to S3 or accessing the file input stream.
     * <p>This method takes in a file and uploads it to the specified S3 bucket under the provided key.
     * Metadata for the file, such as its size and content type, are set using the MultipartFile information.
     * The uploaded file is assigned a public-read ACL, making it accessible to anyone with the URL.
     * After the upload, the method calls {@code generatePublicUrl(bucketName, key)} to construct the public URL of the uploaded file.</p>
     */
    @Override
    public ResponseModel<String> uploadFile(String key, MultipartFile multipartFile) {
        log.info("Begin uploadFile() method");
        ResponseModel<String> responseModel;
        
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(multipartFile.getSize());
            metadata.setContentType(multipartFile.getContentType());

            System.out.println("ldjfnldjbfdjb "+key);
            String uniqueKey = generateUniqueKey();
            key = key + uniqueKey + "_" + multipartFile.getOriginalFilename();
            log.info("Generated key: {}", key);

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, multipartFile.getInputStream(), metadata);
            putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);

            amazonS3.putObject(putObjectRequest);
            String uploadedFileUrl = generatePublicUrl(bucketName, key);
            
            log.info("File uploaded successfully. Public URL: {}", uploadedFileUrl);

            responseModel = createResponseModel(HttpStatus.OK.toString(), "File uploaded successfully.", uploadedFileUrl);
            
        } catch (AmazonServiceException amazonServiceException) {
        	
            log.error("AmazonServiceException: {}", amazonServiceException.getMessage());
            responseModel = createResponseModel(HttpStatus.INTERNAL_SERVER_ERROR.toString(), 
                "AWS service error: " + amazonServiceException.getMessage(), null);
            
        } catch (IOException ioException) {
            log.error("IOException: {}", ioException.getMessage());
            responseModel = createResponseModel(HttpStatus.BAD_REQUEST.toString(), 
                "File processing error: " + ioException.getMessage(), null);
            
        } catch (Exception exception) {
            log.error("Unexpected exception: {}", exception.getMessage());
            responseModel = createResponseModel(HttpStatus.INTERNAL_SERVER_ERROR.toString(), 
                "File upload failed: " + exception.getMessage(), null);
        }
        
        log.info("End uploadFile() method");
        return responseModel;
    }


    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        multipartFile.transferTo(file);
        return file;
    }
    
    /**
     * Generates a public URL for an object stored in an Amazon S3 bucket.
     * @param bucketName - The name of the S3 bucket where the object is stored.
     * @param key - The key (path) of the object within the S3 bucket.
     * @return A String representing the public URL of the object.
     * <p>This method constructs a public URL using the specified bucket name and key.
     * The URL follows the standard format for accessing objects in S3, allowing users
     * to directly access the uploaded file, provided the file's access control list 
     * (ACL) permits public access.</p>
     */
    private String generatePublicUrl(String bucketName, String key) {
        return "https://" + bucketName + ".s3.amazonaws.com/" + key;
    }
    
     
    private String generateUniqueKey() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String alphanumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        
        String randomPart = random.ints(2, 0, alphanumeric.length())
                                  .mapToObj(alphanumeric::charAt)
                                  .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                                  .toString();
        
        return datePart + randomPart;
    }

     private <T> ResponseModel<T> createResponseModel(String statusCode, String message, T data) {
    	    ResponseModel<T> responseModel = new ResponseModel<>();
    	    responseModel.setStatusCode(statusCode);
    	    responseModel.setMessage(message);
    	    responseModel.setData(data);
    	    responseModel.setTimestamp(String.valueOf(System.currentTimeMillis()));
    	    return responseModel;
    	}
}
