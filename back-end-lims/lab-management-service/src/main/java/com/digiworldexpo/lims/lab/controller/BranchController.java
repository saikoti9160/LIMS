package com.digiworldexpo.lims.lab.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digiworldexpo.lims.entities.lab_management.Branch;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.service.BranchService;
import com.digiworldexpo.lims.lab.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin
@RequestMapping("/branch")
@Slf4j
public class BranchController {

	@Autowired
	private BranchService branchService;

	@Autowired
	private HttpStatusCode statusCode;

	public BranchController(BranchService branchService) {
		super();
		this.branchService = branchService;
	}


	@DeleteMapping("/branch/{id}")
	public ResponseEntity<ResponseModel<Branch>> deleteBranchById(@PathVariable("id") UUID branchId) {
		log.info("Begin BranchController -> deleteBranchById() method...");
		ResponseModel<Branch> responseModel = branchService.deleteBranchById(branchId);
		log.info("End BranchController -> deleteBranchById() method");
		return ResponseEntity.status(statusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}

}
