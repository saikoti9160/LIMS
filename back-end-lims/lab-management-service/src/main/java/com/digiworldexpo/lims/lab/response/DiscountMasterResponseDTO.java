package com.digiworldexpo.lims.lab.response;

import java.sql.Timestamp;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountMasterResponseDTO {
	
	private UUID id;
    private String discountName;
    private String discountType;
    
    private UUID labId;
    
    private boolean active;
    private UUID createdBy;
    private Timestamp createdOn;
    private UUID modifiedBy;
    private Timestamp modifiedOn;

}