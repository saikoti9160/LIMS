package com.digiworldexpo.lims.lab.service;


import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.entities.lab_management.LabEquipment;
import com.digiworldexpo.lims.lab.dto.LabEquipmentDto;
import com.digiworldexpo.lims.lab.model.ResponseModel;

public interface LabEquipmentService {
	
	ResponseModel<LabEquipment> createEquipment(UUID userId,LabEquipment  equipment);

	ResponseModel<List<LabEquipmentDto>> getAllEquipment(UUID labId,String searchText,Integer pageSize,Integer pageNumber);

	ResponseModel<LabEquipmentDto> getEquipmentById(UUID id);

	ResponseModel<LabEquipmentDto> updateEquipment(UUID id, LabEquipmentDto equipmentRequestDto);

	ResponseModel<LabEquipment> deleteEquipment(UUID id);


 
}