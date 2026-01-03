package com.digiworldexpo.lims.lab.request;

import java.sql.Timestamp;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountMasterRequestDTO {
    private String discountName;
    private String discountType;
    private UUID labId;
    private boolean active;
    
    private Timestamp createdOn;
}