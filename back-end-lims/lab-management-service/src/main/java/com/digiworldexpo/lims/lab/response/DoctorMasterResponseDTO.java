package com.digiworldexpo.lims.lab.response;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.lab.dto.DoctorAvailabilityDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorMasterResponseDTO {
    
    private UUID id;
    private String doctorName;
    private String phoneNumber;
    private String phoneCode;
    
    private String dateOfBirth;
    
    private UUID departmentId;
    
    private UUID roleId;  
    
    private String email;
    private String setPassword;
    private Boolean showOnAppointment;
    private String doctorPasskey;
    private Boolean isReportApprover;

    private List<DoctorAvailabilityDTO> availabilities;
    
    private UUID labId;
    
    private boolean active;
    
    private UUID createdBy;
    private Timestamp createdOn;
    private UUID modifiedBy;
    private Timestamp modifiedOn;
    
    private String doctorSequenceId;
}