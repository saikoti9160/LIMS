package com.digiworldexpo.lims.lab.response;

import java.sql.Timestamp;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferralMasterResponseDTO {
    private UUID id;
    private String referralName;
    private String phoneNumber;
    private String dateOfBirth;
    private UUID roleId;
    private UUID labId;
    private String email;
    private boolean active;
    private UUID createdBy;
    private Timestamp createdOn;
    private UUID modifiedBy;
    private Timestamp modifiedOn;
    
    private String referralSequenceId;
    
    
}