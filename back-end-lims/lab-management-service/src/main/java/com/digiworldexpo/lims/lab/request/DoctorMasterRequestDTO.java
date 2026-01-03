package com.digiworldexpo.lims.lab.request;

import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.lab.dto.DoctorAvailabilityDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorMasterRequestDTO {
    
	private String doctorName;
	private String phoneNumber;
	private String phoneCode;

	private String dateOfBirth;

	private UUID departmentId;

	private UUID labId;

	private UUID roleId;

	private String email;
	private String setPassword;
	private Boolean showOnAppointment;
	private String doctorPasskey;
	private Boolean isReportApprover;

	private List<DoctorAvailabilityDTO> availabilities;
    
}

