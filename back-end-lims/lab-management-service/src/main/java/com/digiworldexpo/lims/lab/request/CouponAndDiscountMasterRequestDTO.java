package com.digiworldexpo.lims.lab.request;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponAndDiscountMasterRequestDTO {

    private String couponName;
    private String couponDescription;
    
    private UUID discountId;
    private UUID labId;
    
    private Double discountAmount;
    private String discountPercentage;
    private Double minAmountRequired;
    private Double maxAmountRequired;
    private String gender;
    private Integer ageRange;
    private Integer fromAge;
    private Integer toAge;
    private List<String> visitFrequency;
    private Integer specificNumber;
    private String others;
    
    private Timestamp startDate;
    private Timestamp endDate;

    private UUID id;
    
}