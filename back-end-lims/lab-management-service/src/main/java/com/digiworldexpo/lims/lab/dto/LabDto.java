package com.digiworldexpo.lims.lab.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabDto {
	private UUID id;
	private String labManagerName;
	private String labName;
	private UUID labTypeId;
	private String email;
	private String continent;
	private String country;
	private String state;
	private String city;
	private String address;
	private String zipCode;
	private String phone;
	private String phoneCode;
	private UUID packageId;
	private boolean hasBranches;
	private String logo;
	private String password;
	private boolean active;
}	
