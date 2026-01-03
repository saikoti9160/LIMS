package com.digiworldexpo.lims.lab.request;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

//import com.digiworldexpo.lims.lab.dto.TestParameterDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileConfigurationRequestDTO {
	
	 private String profileName;
	    private Boolean outSource;
	    private Timestamp timeToShareReport;
	    private Integer turnaroundTimeHours;
	    private Integer turnaroundTimeDays;
	    private Integer turnaroundTimeMinutes;
	    private String profileDescription;
	    private String profileInstructions;
	    private List<UUID> tests;
	    private Double totalAmount;
	    
	    private UUID labId;
	    
//	    private List<TestParameterDTO> testParameters; 

}
