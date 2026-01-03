package com.digiworldexpo.lims.lab.request;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutsourceMasterRequestDTO {
    
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
    
    private List<UUID> tests;
    private List<UUID> profiles; 
    
    private String email;
    private String password;
    private boolean active;
    
    private String outsourceSequenceId;
}