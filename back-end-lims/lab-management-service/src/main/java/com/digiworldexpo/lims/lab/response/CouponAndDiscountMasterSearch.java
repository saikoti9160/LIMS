package com.digiworldexpo.lims.lab.response;

import java.sql.Timestamp;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CouponAndDiscountMasterSearch {
	
	private UUID id;
	private String couponName;
	private Timestamp startDate;
	private Timestamp endDate;

}
