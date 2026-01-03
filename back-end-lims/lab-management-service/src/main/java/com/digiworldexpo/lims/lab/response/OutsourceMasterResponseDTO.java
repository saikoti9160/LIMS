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
public class OutsourceMasterResponseDTO {
    
    private UUID id;
    private String outsourceCenterName;
    private String contactPersonName;
    private String phoneNumber;
    private String country;
    private String state;
    private String city;
    private String pinCode;
    private String address;
    
    private UUID labId;
    private UUID roleId;
    
//    private String status;
    
    private List<UUID> tests;
    private List<UUID> profiles; 
    
    private String email;
    private boolean active;
    
    private UUID createdBy;
    private Timestamp createdOn;
    private UUID modifiedBy;
    private Timestamp modifiedOn;
    
    
    private String outsourceSequenceId;
    
}
