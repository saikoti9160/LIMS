package com.digiworldexpo.lims.authentication.serviceimpl;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.model.DecryptRequest;
import com.amazonaws.services.kms.model.DecryptResult;
import com.amazonaws.services.kms.model.EncryptRequest;
import com.amazonaws.services.kms.model.EncryptResult;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KmsService {
	

	@Value("${aws.kms.keyId}")
	private String kmsKeyId;

	@Autowired
	private AWSKMS kmsClient;
	
	
	private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String OTHER_CHAR = "!@#$%&*()_+-=[]|,./?><";

    private static final String PASSWORD_ALLOW_BASE = CHAR_LOWER + CHAR_UPPER + NUMBER + OTHER_CHAR;
    private static SecureRandom random = new SecureRandom();

    
	public String encrypt(String plaintext) {
		log.info("Begin KmsService -> encrypt() method");
		EncryptRequest request = new EncryptRequest().withKeyId(kmsKeyId)
				.withPlaintext(ByteBuffer.wrap(plaintext.getBytes()));
		EncryptResult result = kmsClient.encrypt(request);
		log.info("End KmsService -> encrypt() method");
		return Base64.getEncoder().encodeToString(result.getCiphertextBlob().array());
	}

	
	public String decrypt(String encryptedText) {
		log.info("Begin KmsService -> decrypt() method");
		byte[] ciphertextBlob = Base64.getDecoder().decode(encryptedText);
		DecryptRequest request = new DecryptRequest().withCiphertextBlob(ByteBuffer.wrap(ciphertextBlob));
		DecryptResult result = kmsClient.decrypt(request);
		log.info("Begin KmsService -> decrypt() method");
		return new String(result.getPlaintext().array());
	}

	
    public static String generateRandomPassword(int length) {
    	log.info("Begin KmsService -> generateRandomPassword() method");
        if (length < 4) {
            throw new IllegalArgumentException("Password length must be at least 4 characters");
        }

        StringBuilder password = new StringBuilder(length);

        // at least 1 lowercase, 1 uppercase, 1 digit, and 1 special character
        password.append(randomChar(CHAR_LOWER));
        password.append(randomChar(CHAR_UPPER));
        password.append(randomChar(NUMBER));
        password.append(randomChar(OTHER_CHAR));

        // fill in the rest of the password with random characters
        for (int i = 4; i < length; i++) {
            password.append(randomChar(PASSWORD_ALLOW_BASE));
        }

        for (int i = 0; i < length - 1; i++) {
            int randomIndex = i + random.nextInt(length - i);
            char temp = password.charAt(i);
            password.setCharAt(i, password.charAt(randomIndex));
            password.setCharAt(randomIndex, temp);
        }
        log.info("Begin KmsService -> generateRandomPassword() method");
        return password.toString();
    }

   
    private static char randomChar(String input) {
        int randomIndex = random.nextInt(input.length());
        return input.charAt(randomIndex);
    }

}
