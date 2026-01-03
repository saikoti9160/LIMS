package com.digiworldexpo.lims.lab.service;

import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.request.WarehouseMasterRequestDto;
import com.digiworldexpo.lims.lab.response.WarehouseMasterResponseDTO;
import com.digiworldexpo.lims.lab.response.WarehouseSearchResponseDTO;

public interface WarehouseMasterService {
	
	 ResponseModel<WarehouseMasterResponseDTO> saveWarehouseMaster(WarehouseMasterRequestDto warehouseMasterRequestDto, UUID createdBy);
	 
	 ResponseModel<List<WarehouseSearchResponseDTO>> getAllWarehouses(UUID createdBy, String keyword, Boolean flag, Integer pageNumber, Integer pageSize);
	 
	 ResponseModel<WarehouseMasterResponseDTO> getById(UUID id);
	 
	  ResponseModel<WarehouseMasterResponseDTO> updateWarehouseMaster(UUID id, WarehouseMasterRequestDto warehouseMasterRequestDto);
	  
	  ResponseModel<WarehouseMasterResponseDTO> deleteWarehouseMaster(UUID id);


}

