package com.digiworldexpo.lims.lab.service;

import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.request.PhlebotomistMasterRequestDTO;
import com.digiworldexpo.lims.lab.response.PhlebotomistMasterResponseDTO;
import com.digiworldexpo.lims.lab.response.PhlebotomistMasterSearchResponse;

public interface PhlebotomistMasterService {

	ResponseModel<PhlebotomistMasterResponseDTO> savePhlebotomistMaster(UUID createdBy, PhlebotomistMasterRequestDTO phlebotomistMasterDTO);
	
	ResponseModel<List<PhlebotomistMasterSearchResponse>> getAllPhlebotomist(UUID createdBy, String keyword, Boolean flag, Integer pageNumber, Integer pageSize);
	
	ResponseModel<PhlebotomistMasterResponseDTO> updatePhlebotomist(UUID id, PhlebotomistMasterRequestDTO phlebotomistMasterDTO);
	
	ResponseModel<PhlebotomistMasterResponseDTO> getPhlebotomistById(UUID id);
	
	ResponseModel<PhlebotomistMasterResponseDTO> deletePhlebotomistById(UUID id);
	
	

}

