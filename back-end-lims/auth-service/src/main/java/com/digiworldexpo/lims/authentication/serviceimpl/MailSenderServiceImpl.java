package com.digiworldexpo.lims.authentication.serviceimpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.digiworldexpo.lims.authentication.service.MailSenderService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MailSenderServiceImpl implements MailSenderService {

	@Autowired
	private CommonService commonService;

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public String sendWelcomeMail(String email , String labName) {	
		log.info("Begin MailSenderServiceImpl -> sendWelcomeMail() method");
		String body = getHtmlContentFromS3(
				"https://medworld-mailtemplates.s3.ap-south-1.amazonaws.com/WelcomeMail/WelcomeMail.html");
		if (body == null) {
			log.error("Error retrieving email body from S3");
			return "Error retrieving email body from S3";
		}
		try {
			body.replace("{labName}", labName);
			commonService.sendEmail(email, body, "User registration Successful");
			log.info("End MailSenderServiceImpl -> sendWelcomeMail() method");
			return "Email sent successfully to " + email;
		} catch (MailException e) {
			log.error("An error while sending welcomMail to "+email);
			return "Error sending email: " + e.getMessage();
		}
	}

	private String getHtmlContentFromS3(String url) {
		try {
			ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
			if (response.getStatusCode() == HttpStatus.OK) {
				return response.getBody();
			} else {
				return null;
			}
		} catch (RestClientException e) {
			return null;
		}
	}

}
