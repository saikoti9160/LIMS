package com.digiworldexpo.lims.lab.service;


import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.entities.lab_management.Rack;
import com.digiworldexpo.lims.lab.dto.RackRequestDto;
import com.digiworldexpo.lims.lab.model.ResponseModel;

public interface RackService {

	ResponseModel<Rack> createRack(UUID userId,Rack requestRack);

	ResponseModel<List<RackRequestDto>> getAllRacks(UUID labId,String searchText, Integer pageNumber, Integer pageSize);

	ResponseModel<RackRequestDto> getRackById(UUID id);

	ResponseModel<Rack> deleteRackById(UUID id);

	ResponseModel<RackRequestDto> updateRackById(RackRequestDto requestRack, UUID id);

}