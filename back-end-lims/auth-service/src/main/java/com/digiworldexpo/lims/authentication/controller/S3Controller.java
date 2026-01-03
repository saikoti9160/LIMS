package com.digiworldexpo.lims.authentication.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.digiworldexpo.lims.authentication.model.ResponseModel;
import com.digiworldexpo.lims.authentication.service.S3Service;
import com.digiworldexpo.lims.authentication.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class S3Controller {

	private final S3Service s3Service;

	private final HttpStatusCode httpStatusCode;

	public S3Controller(S3Service s3Service, HttpStatusCode httpStatusCode) {
		this.s3Service = s3Service;
		this.httpStatusCode = httpStatusCode;
	}

	/**
	 * Handles HTTP POST requests to upload a file to an S3 bucket.
	 * 
	 * @param destinationKey : The key (path) where the file will be stored in the
	 *                       S3 bucket.
	 * @param file           : The file to be uploaded, represented as a
	 *                       MultipartFile object.
	 * @return A ResponseEntity containing a ResponseHolder object, which includes:
	 *         - The public URL of the uploaded file (in the `data` field), - A
	 *         success message, - The HTTP status, and - A timestamp.
	 * @throws IOException If an error occurs during the file upload process.
	 *                     <p>
	 *                     This endpoint allows clients to upload a file to the
	 *                     specified S3 bucket by providing a destination key and
	 *                     the file itself. The method calls
	 *                     {@code s3Service.uploadFile(destinationKey, file)} to
	 *                     perform the actual upload. After a successful upload, the
	 *                     public URL of the file is returned in the response, along
	 *                     with a success message, the timestamp of the operation,
	 *                     and a "201 Created" status.
	 *                     </p>
	 */

	@PostMapping("/upload")
	ResponseEntity<ResponseModel<String>> uploadFile(@RequestParam("destinationKey") String destinationKey,
			@RequestParam("file") MultipartFile file) {
		log.info("Begin S3Controller Controller -> uploadFile() method");
		ResponseModel<String> response = s3Service.uploadFile(destinationKey, file);

		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		log.info("End S3Controller Controller -> uploadFile() method");
		return ResponseEntity.status(httpStatusFromCode).body(response);
	}
}
