package com.digiworldexpo.lims.lab.controller;

import java.util.List;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.request.BranchMasterRequestDTO;
import com.digiworldexpo.lims.lab.response.BranchMasterResponseDTO;
import com.digiworldexpo.lims.lab.service.BranchMasterService;
import com.digiworldexpo.lims.lab.util.HttpStatusCode;


import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/branchMaster")
@Slf4j
public class BranchMasterController {
	@Autowired
	private BranchMasterService branchMasterService;

	@Autowired
	private HttpStatusCode statusCode;

	public BranchMasterController(BranchMasterService branchMasterService) {
		super();
		this.branchMasterService = branchMasterService;
	}
	
	@PostMapping("/")
    public ResponseEntity<ResponseModel<BranchMasterResponseDTO>> addBranchMaster(
            @RequestBody BranchMasterRequestDTO branchMaster,
            @RequestHeader("createdBy") UUID createdBy) {
        log.info("Begin addBranchMaster -> addBranchMaster() method", branchMaster);
        ResponseModel<BranchMasterResponseDTO> response = branchMasterService.saveBranchMaster(createdBy, branchMaster);
        log.info("End addBranchMaster -> addBranchMaster() method", branchMaster);
        HttpStatus httpStatus = statusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(httpStatus).body(response);
    }
	@GetMapping("/{id}")
	public ResponseEntity<ResponseModel<BranchMasterResponseDTO>> getBranchById(@PathVariable UUID id) {
	    log.info("Begin getBranchById -> getBranchById() method with ID: {}", id);
	    
	    ResponseModel<BranchMasterResponseDTO> response = branchMasterService.getBranchById(id);
	    
	    log.info("End getBranchById -> getBranchById() method with ID: {}", id);
	    
	    HttpStatus httpStatus = statusCode.getHttpStatusFromCode(response.getStatusCode());
	    return ResponseEntity.status(httpStatus).body(response);
	}
	@DeleteMapping("/{id}")
	public ResponseEntity<ResponseModel<BranchMasterResponseDTO>> deleteBranch(@PathVariable UUID id) {
	    log.info("Begin deleteBranch -> deleteBranch() method with ID: {}", id);

	    ResponseModel<BranchMasterResponseDTO> response = branchMasterService.deleteBranch(id);

	    log.info("End deleteBranch -> deleteBranch() method with ID: {}", id);

	    HttpStatus httpStatus = statusCode.getHttpStatusFromCode(response.getStatusCode());
	    return ResponseEntity.status(httpStatus).body(response);
	}
	@PutMapping("/{id}")
	public ResponseEntity<ResponseModel<BranchMasterResponseDTO>> updateBranch(
	        @RequestHeader("updatedBy") UUID updatedBy,
	        @PathVariable UUID id,
	        @RequestBody BranchMasterRequestDTO updatedBranchDTO) {

	    log.info("Begin updateBranch -> updateBranch() method with ID: {}", id);

	    ResponseModel<BranchMasterResponseDTO> response = branchMasterService.updateBranch(updatedBy, id, updatedBranchDTO);

	    log.info("End updateBranch -> updateBranch() method with ID: {}", id);

	    HttpStatus httpStatus = statusCode.getHttpStatusFromCode(response.getStatusCode());
	    return ResponseEntity.status(httpStatus).body(response);
	}

	    @PostMapping("/all")
	    public ResponseEntity<ResponseModel<List<BranchMasterResponseDTO>>> getAllBranches(
	            @RequestParam(required = false) String searchBy,
	            @RequestParam(defaultValue = "0") int pageNumber,
	            @RequestParam(defaultValue = "10") int pageSize,
	            @RequestParam(defaultValue = "branchSequenceId") String sortBy,
	            @RequestParam UUID createdBy) {

	        log.info("Received request to get all branches with keyword '{}'", searchBy,pageNumber,pageSize,sortBy,createdBy);

	        ResponseModel<List<BranchMasterResponseDTO>> response = branchMasterService.getAllBranches(
	        		searchBy, pageNumber, pageSize, sortBy, createdBy);

	        HttpStatus httpStatus = statusCode.getHttpStatusFromCode(response.getStatusCode());
	        log.info("Fetched all staff with response: {}", response);
	        return ResponseEntity.status(httpStatus).body(response);
	    }
	}


