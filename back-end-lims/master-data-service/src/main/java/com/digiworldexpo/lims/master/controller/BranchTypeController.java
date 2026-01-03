package com.digiworldexpo.lims.master.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digiworldexpo.lims.entities.master.BranchType;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.service.BranchTypeService;
import com.digiworldexpo.lims.master.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/branch")
@CrossOrigin(origins = "*")
@Slf4j
public class BranchTypeController {
	@Autowired
	public BranchTypeService branchTypeService;
	@Autowired
	private HttpStatusCode httpStatusCode;

	@PostMapping("/save")
	public ResponseEntity<ResponseModel<BranchType>> saveBranchType(@RequestBody BranchType branchType,
			@RequestParam UUID createdBy) {
		log.info("Begin of  BranchTypeController -> saveBranchType() method");
		ResponseModel<BranchType> branchResponse = branchTypeService.addBranchType(branchType, createdBy);
		log.info("End of  BranchTypeController -> saveBranchType() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(branchResponse.getStatusCode()))
				.body(branchResponse);

	}

	@GetMapping("/fetch/{id}")
	public ResponseEntity<ResponseModel<BranchType>> getBranchTypeById(@PathVariable UUID id) {
		log.info("Begin of  BranchTypeController -> getBranchTypeById() method");
		ResponseModel<BranchType> branchResponse = branchTypeService.getBranchType(id);
		log.info("End of  BranchTypeController -> getBranchTypeById() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(branchResponse.getStatusCode()))
				.body(branchResponse);
	}

	@GetMapping("/fetchAll")
	public ResponseEntity<ResponseModel<List<BranchType>>> fetchAllBranches(
			@RequestParam(required = false) String startsWith, @RequestParam(defaultValue = "0") int pageNumber,
			@RequestParam(defaultValue = "10") int pageSize,
			@RequestParam(defaultValue = "branchTypeName") String sortBy, @RequestParam UUID createdBy) {
		log.info("Begin of  BranchTypeController -> fetchAllBranches() method");
		ResponseModel<List<BranchType>> branchResponse = branchTypeService.getAllBranches(startsWith, pageNumber,
				pageSize, sortBy, createdBy);
		log.info("End of  BranchTypeController -> fetchAllBranches() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(branchResponse.getStatusCode().toString()))
				.body(branchResponse);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<ResponseModel<BranchType>> updateBranchType(@PathVariable UUID id,
			@RequestBody BranchType branchType, @RequestParam UUID modifiedBy) {
		log.info("Begin of  BranchTypeController -> fetchAllBranches() method");
		ResponseModel<BranchType> branchResponse = branchTypeService.updateBranch(id, modifiedBy, branchType);
		log.info("End of  BranchTypeController -> fetchAllBranches() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(branchResponse.getStatusCode()))
				.body(branchResponse);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResponseModel<BranchType>> removeBranchType(@PathVariable UUID id) {
		log.info("Begin of  BranchTypeController -> removeBranchType() method");
		ResponseModel<BranchType> branchResponseModel = branchTypeService.deleteBranchType(id);
		log.info("End of  BranchTypeController -> removeBranchType() method");
		return ResponseEntity
				.status(httpStatusCode.getHttpStatusFromCode(branchResponseModel.getStatusCode().toString()))
				.body(branchResponseModel);

	}

}
