package com.digiworldexpo.lims.lab.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabManagementDTO {
	private LabDto labDto;
	private List<BranchDto> branches;
	private List<EquipmentDto> equipmentList;
}
