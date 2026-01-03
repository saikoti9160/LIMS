package com.digiworldexpo.lims.master.service;

import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.entities.master.BranchType;
import com.digiworldexpo.lims.master.model.response.ResponseModel;

public interface BranchTypeService {

	ResponseModel<BranchType> addBranchType(BranchType branchType, UUID createdBy);

	ResponseModel<BranchType> getBranchType(UUID id);

	ResponseModel<List<BranchType>> getAllBranches(String startsWith, int pageNumber, int pageSize, String sortBy, UUID createdBy);

	ResponseModel<BranchType> updateBranch(UUID id, UUID modifiedBy, BranchType branchType);

	ResponseModel<BranchType> deleteBranchType(UUID id);

}
