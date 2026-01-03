package com.digiworldexpo.lims.lab.service;

import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.request.OutsourceMasterRequestDTO;
import com.digiworldexpo.lims.lab.response.OutsourceMasterResponseDTO;
import com.digiworldexpo.lims.lab.response.OutsourceSearchResponse;

public interface OutsourceMasterService {
	
	ResponseModel<OutsourceMasterResponseDTO> saveOutsourceMaster(UUID createdBy, OutsourceMasterRequestDTO outsourceMasterDTO);
	
	ResponseModel<List<OutsourceSearchResponse>> getAllOutsources(UUID createdBy, String keyword, Boolean flag, Integer pageNumber, Integer pageSize);
	
	ResponseModel<OutsourceMasterResponseDTO> getOutsourceById(UUID id);
	
	ResponseModel<OutsourceMasterResponseDTO> updateOutsourceById(UUID id,  OutsourceMasterRequestDTO outsourceMasterDTO);
	
	ResponseModel<OutsourceMasterResponseDTO> deleteOutsourceById(UUID id);

}
