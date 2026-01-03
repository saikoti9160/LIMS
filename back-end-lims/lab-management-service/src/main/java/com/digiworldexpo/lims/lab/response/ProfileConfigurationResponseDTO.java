package com.digiworldexpo.lims.lab.response;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileConfigurationResponseDTO {
	
	private UUID id;
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
    
    

}
