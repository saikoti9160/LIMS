package com.digiworldexpo.lims.lab.response;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.lab.dto.PhlebotomistAvailabilityDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhlebotomistMasterResponseDTO {

    private UUID id;
    private String name;
    private String phoneNumber;
    private String phoneCode;
    private String dateOfBirth;
    private UUID roleId;
    private String email;
    private UUID labId;
    
    private String employeeId;
    
    private boolean active;
    private UUID createdBy;	
    private Timestamp createdOn;
    private UUID modifiedBy;
    private Timestamp modifiedOn;
    private String phlebotomistSequenceId;
    
    private List<PhlebotomistAvailabilityDTO> availabilities;
}