package com.digiworldexpo.lims.lab.model;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserModel {
	
	private String email;
	private String phoneCode;
	private String phone;
	private String password;
	private String confirmPassword;
	private UUID lab;
	private String country;
	private String state;
	private String city;
	private String address;
	private UUID role;
	private UUID  accountType;
	private  String firstName;
	private  String lastName;

}
