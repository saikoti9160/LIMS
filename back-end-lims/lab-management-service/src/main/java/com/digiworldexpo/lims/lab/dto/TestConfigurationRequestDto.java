package com.digiworldexpo.lims.lab.dto;

import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

import com.digiworldexpo.lims.entities.lab_management.LabDepartment;
import com.digiworldexpo.lims.entities.lab_management.ReportParameter;
import com.digiworldexpo.lims.entities.lab_management.SampleMapping;
import com.digiworldexpo.lims.entities.lab_management.SampleMaster;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestConfigurationRequestDto  {

	private UUID id;
	private UUID labId;
  
	private Timestamp timeToShareReport;

	private Double testPrice;

	private LabDepartment labDepartment;

	  private SampleMapping sampleMapping;
	  
	  private ReportParameter reportParameters;
	  
	private Boolean isOutsource;

	private boolean active;
	
	private String description;

	private String instructions;

    private Integer analyticalDays;

    private Integer analyticalHours;

    private Integer analyticalMinutes;
    
    private Timestamp modifiedOn;
	private UUID modifiedBy;


}