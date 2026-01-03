package com.digiworldexpo.lims.lab.service;

import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.request.ReferralMasterRequestDTO;
import com.digiworldexpo.lims.lab.response.ReferralMasterResponseDTO;
import com.digiworldexpo.lims.lab.response.ReferralMasterSearchResponse;

public interface ReferralMasterService {
	
	
	ResponseModel<ReferralMasterResponseDTO> saveReferralMaster(UUID createdBy, ReferralMasterRequestDTO referralMasterRequestDTO);

	ResponseModel<ReferralMasterResponseDTO> getReferralById(UUID id);

	ResponseModel<ReferralMasterResponseDTO> updateReferralMaster(UUID id,
			ReferralMasterRequestDTO referralMasterRequestDTO);

	ResponseModel<ReferralMasterResponseDTO> deleteReferralMaster(UUID id);

	ResponseModel<List<ReferralMasterSearchResponse>> getAllReferrals(UUID createdBy, String keyword, Boolean flag, Integer pageNumber, Integer pageSize);

}
