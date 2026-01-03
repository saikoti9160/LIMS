package com.digiworldexpo.lims.lab.dto;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SampleMasterRequestDto{
	
	
	private UUID id;
	private UUID labId;
	private String sampleName;
	private List<String> sampleType;
	private boolean active;
	private UUID modifiedBy;
	private Timestamp modifiedOn;
}
