package com.digiworldexpo.lims.lab.dto;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.entities.lab_management.TestNumericConfig;
import com.digiworldexpo.lims.entities.lab_management.TestReference;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReportParameterRequestDto {

	private UUID id;

	private UUID labId;

	private boolean active;
	
	  private TestReference textReference;
	  private TestNumericConfig numericConfiguration;
	  private String unit;
	  private String remarks;
}
