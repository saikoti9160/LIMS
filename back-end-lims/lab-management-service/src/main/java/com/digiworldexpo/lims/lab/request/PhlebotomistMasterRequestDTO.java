package com.digiworldexpo.lims.lab.request;

import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.lab.dto.PhlebotomistAvailabilityDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhlebotomistMasterRequestDTO {

    private String name;
    private String phoneNumber;
    private String phoneCode;
    private String dateOfBirth;
   
    
    private UUID roleId;
    private String email;
    private String setPassword;
    
    private UUID labId;
    
    private String employeeId;
    
    private List<PhlebotomistAvailabilityDTO> availabilities;
}