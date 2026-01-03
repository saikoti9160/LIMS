package com.digiworldexpo.lims.lab.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabDepartmentDto {
	
	private UUID id;
	private boolean active;
	private String departmentName;
	
	private UUID labId;
	
}