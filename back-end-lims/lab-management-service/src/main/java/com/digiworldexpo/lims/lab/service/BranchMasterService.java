package com.digiworldexpo.lims.lab.service;

import java.util.List;

import java.util.UUID;


import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.request.BranchMasterRequestDTO;
import com.digiworldexpo.lims.lab.response.BranchMasterResponseDTO;

public interface BranchMasterService {
	ResponseModel<BranchMasterResponseDTO> saveBranchMaster(UUID createdBy, BranchMasterRequestDTO branchMasterRequestDTO);
	
    ResponseModel<List<BranchMasterResponseDTO>> getAllBranches(
            String searchBy, int pageNumber, int pageSize, String sortBy, UUID createdBy);

    ResponseModel<BranchMasterResponseDTO> getBranchById(UUID id);

    ResponseModel<BranchMasterResponseDTO> deleteBranch(UUID id);

    ResponseModel<BranchMasterResponseDTO> updateBranch(UUID creatorId, UUID id, BranchMasterRequestDTO updatedBranchMasterDTO);
	

}
