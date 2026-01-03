package com.digiworldexpo.lims.master.model.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class StateRequest {

	private String stateName;
	
	private String stateCode;
	
	private String countryName;
	
	private String countryCode;
	
	private UUID createdBy;
	
	private UUID modifiedBy;
}
