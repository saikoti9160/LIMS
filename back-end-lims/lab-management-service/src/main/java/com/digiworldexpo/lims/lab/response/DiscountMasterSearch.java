package com.digiworldexpo.lims.lab.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiscountMasterSearch {
	
	private UUID id;
	private String discountName;
	private String discountType;
}
