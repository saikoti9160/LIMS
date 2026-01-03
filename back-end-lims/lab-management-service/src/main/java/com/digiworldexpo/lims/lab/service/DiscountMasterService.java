package com.digiworldexpo.lims.lab.service;

import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.request.DiscountMasterRequestDTO;
import com.digiworldexpo.lims.lab.response.DiscountMasterResponseDTO;
import com.digiworldexpo.lims.lab.response.DiscountMasterSearch;

public interface DiscountMasterService {

	ResponseModel<DiscountMasterResponseDTO> saveDiscountMaster(UUID createdBy, DiscountMasterRequestDTO discountMasterDTO);
	
	ResponseModel<List<DiscountMasterSearch>> getAllDiscounts(UUID createdBy, String keyword, Boolean flag, Integer pageNumber, Integer pageSize);
	
	ResponseModel<DiscountMasterResponseDTO> getDiscountById(UUID id);

    ResponseModel<DiscountMasterResponseDTO> updateDiscount(UUID id, DiscountMasterRequestDTO discountMaster);

    ResponseModel<DiscountMasterResponseDTO> deleteDiscount(UUID id);

}
