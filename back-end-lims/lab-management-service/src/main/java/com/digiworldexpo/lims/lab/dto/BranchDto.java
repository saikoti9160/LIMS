package com.digiworldexpo.lims.lab.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchDto {
	private UUID id;
	private String branchName;
	private String branchType;
	private String contactPerson;
	private String email;
	private String phoneNumber;
	private String continent;
	private String country;
	private String state;
	private String city;
	private String address;
	private String zipCode;
	private UUID labId;
}
