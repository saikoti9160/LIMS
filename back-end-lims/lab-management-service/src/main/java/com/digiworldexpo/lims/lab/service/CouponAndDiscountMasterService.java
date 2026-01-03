package com.digiworldexpo.lims.lab.service;

import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.request.CouponAndDiscountMasterRequestDTO;
import com.digiworldexpo.lims.lab.response.CouponAndDiscountMasterResponseDTO;
import com.digiworldexpo.lims.lab.response.CouponAndDiscountMasterSearch;

public interface CouponAndDiscountMasterService {

	ResponseModel<CouponAndDiscountMasterResponseDTO> save( UUID createdBy, CouponAndDiscountMasterRequestDTO couponAndDiscountMasterRequestDTO);

	ResponseModel<List<CouponAndDiscountMasterSearch>> getAllCouponAndDiscount(String searchTerm,  Boolean flag, UUID createdBy, Integer pageNumber, Integer pageSize);

	ResponseModel<CouponAndDiscountMasterResponseDTO> updateById(UUID id, CouponAndDiscountMasterRequestDTO couponAndDiscountMasterDTO);

	ResponseModel<CouponAndDiscountMasterResponseDTO> getById(UUID id);

	ResponseModel<CouponAndDiscountMasterResponseDTO> deleteById(UUID id);


}