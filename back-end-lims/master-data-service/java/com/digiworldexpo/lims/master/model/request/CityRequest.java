package com.digiworldexpo.lims.master.model.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CityRequest {

	private String stateName;
	
	private String stateCode;
	
	private String cityName;
	
	private UUID createdBy;
	
	private UUID modifiedBy;

}
