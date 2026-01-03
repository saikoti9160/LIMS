package com.digiworldexpo.lims.lab.service;

import java.util.UUID;

import com.digiworldexpo.lims.entities.lab_management.Branch;
import com.digiworldexpo.lims.lab.model.ResponseModel;

public interface BranchService {


	ResponseModel<Branch> deleteBranchById(UUID branchId);

}
