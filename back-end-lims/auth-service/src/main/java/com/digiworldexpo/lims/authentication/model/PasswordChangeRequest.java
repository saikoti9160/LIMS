package com.digiworldexpo.lims.authentication.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PasswordChangeRequest {
	private String email;
	private String newPassword;
	private String verificationCode;
}
