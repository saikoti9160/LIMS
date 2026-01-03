package com.digiworldexpo.lims.lab.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentDto {
	private UUID id;
	private String equipmentName;
	private String model;
	private UUID labId;
}
