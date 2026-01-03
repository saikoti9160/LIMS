package com.digiworldexpo.lims.lab.dto;

import java.sql.Timestamp;
import java.util.UUID;

import com.digiworldexpo.lims.constants.WeekDay;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorAvailabilityDTO {
    
	private UUID id;
	private WeekDay weekDay;
	private Timestamp startTime;
	private Timestamp endTime;
	private boolean available;

}