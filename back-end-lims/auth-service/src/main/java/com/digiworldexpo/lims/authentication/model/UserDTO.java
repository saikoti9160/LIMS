package com.digiworldexpo.lims.authentication.model;


import java.util.UUID;

import com.digiworldexpo.lims.entities.master.Department;
import com.digiworldexpo.lims.entities.master.Role;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserDTO {
	private UUID id;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private String phoneCode;
	private String phone;
	private String position;
	private String dateOfBirth;
	private String profilePic;
	private String status;
	private Department department;
	private Role role;
	private String userSequenceId;

}
