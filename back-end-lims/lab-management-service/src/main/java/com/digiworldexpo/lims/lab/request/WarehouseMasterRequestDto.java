package com.digiworldexpo.lims.lab.request;

import java.sql.Timestamp;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseMasterRequestDto {
	
	private String warehouseName;
	private String location;
	private String description;

	private UUID labId;

	private boolean active;

	private UUID createdBy;
	private Timestamp createdOn;
	private UUID modifiedBy;
	private Timestamp modifiedOn;
}