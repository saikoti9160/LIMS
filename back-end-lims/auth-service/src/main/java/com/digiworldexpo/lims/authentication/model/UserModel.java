package com.digiworldexpo.lims.authentication.model;

import java.util.UUID;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserModel {
	
	private String firstName;
	private String lastName;
	private String email;
	private String phoneCode;
	private String phone;
	private String password;
	private String confirmPassword;
	private String country;
	private String state;
	private String city;
	private String address;
	private UUID role;
	private UUID  accountType;
	private boolean isSuperAdminCreated;
	
	private UUID department;
	private String position;
	private String dateOfBirth;
	private String profilePic;
	private String status;
	
	
	

	private UUID designation;
	private UUID labId;	
	private String labName;

}
