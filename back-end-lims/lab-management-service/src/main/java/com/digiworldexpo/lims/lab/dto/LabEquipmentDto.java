package com.digiworldexpo.lims.lab.dto;

import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

import com.digiworldexpo.lims.entities.lab_management.TestConfigurationMaster;
import com.digiworldexpo.lims.entities.lab_management.ReportParameter;

import lombok.Data;

@Data
public class LabEquipmentDto {

	private UUID id;

	private UUID labId;
	
	private String equipmentName;

	private String equipmentType;

	private String manufacture;

	private String modelNumber;

	private String serialNumber;

	private Timestamp purchaseDate;

	private Timestamp installationDate;

	private Set<TestConfigurationMaster> tests;

	private boolean active;

	private Set<ReportParameter> testParameters;


	private String resultFormat;


	
}
