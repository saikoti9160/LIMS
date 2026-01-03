package com.digiworldexpo.lims.lab.serviceimpl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digiworldexpo.lims.entities.lab_management.Branch;
import com.digiworldexpo.lims.lab.exception.RecordNotFoundException;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.repository.BranchRepository;
import com.digiworldexpo.lims.lab.service.BranchService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BranchServiceImpl implements BranchService {
	@Autowired
	private BranchRepository branchRepository;

	public BranchServiceImpl(BranchRepository branchRepository) {
		this.branchRepository = branchRepository;
	}

	
	@Transactional
	@Override
	public ResponseModel<Branch> deleteBranchById(UUID branchId) {
	    log.info("Begin BranchService -> deleteBranchById() method...");
	    ResponseModel<Branch> responseModel = new ResponseModel<>();

	    try {
	        Branch branch = branchRepository.findById(branchId)
	                .orElseThrow(() -> new RecordNotFoundException("Branch not found"));

	        branch.setActive(false);
	        branchRepository.save(branch);

	        responseModel.setData(null);
	        responseModel.setMessage("Branch soft-deleted successfully");
	        responseModel.setStatusCode(HttpStatus.OK.toString());

	    } catch (RecordNotFoundException e) {
	        log.warn("Branch not found: {}", branchId);
	        responseModel.setData(null);
	        responseModel.setMessage(e.getMessage());
	        responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
	    } catch (Exception e) {
	        log.error("Error occurred while soft-deleting Branch: {}", e.getMessage(), e);
	        responseModel.setData(null);
	        responseModel.setMessage("Failed to soft delete Branch");
	        responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	    }

	    log.info("End BranchService -> deleteBranchById() method...");
	    return responseModel;
	}}

