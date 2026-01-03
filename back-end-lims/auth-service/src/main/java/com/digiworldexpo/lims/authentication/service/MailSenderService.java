package com.digiworldexpo.lims.authentication.service;

public interface MailSenderService {
	
	String sendWelcomeMail(String email , String labName);

}
