package com.digiworldexpo.lims.master.model.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CountryRequest {
	
	private String continentName; 
	
	private String continentCode;
	
	private String countryName;
	
	private String countryCode;
	
	private String phoneCode;
	
	private String currency;
	
	private String currencySymbol;
	
	private UUID createdBy;
	
	private UUID modifiedBy;
	
}
