package com.digiworldexpo.lims.lab.controller;

import java.util.UUID;

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

import com.digiworldexpo.lims.lab.dto.LabManagementDTO;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.request.LabFilterRequestDTO;
import com.digiworldexpo.lims.lab.response.LabMainResponse;
import com.digiworldexpo.lims.lab.service.LabService;
import com.digiworldexpo.lims.lab.util.HttpStatusCode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Lab Management", description = "API for managing lab operations")
@Slf4j
@RestController
@RequestMapping("/lab")
public class LabController {

	private final LabService labService;
	private final HttpStatusCode httpStatusCode;

	public LabController(LabService labService, HttpStatusCode httpStatusCode) {
		super();
		this.labService = labService;
		this.httpStatusCode = httpStatusCode;
	}

	@Operation(summary = "Save Lab", description = "Creates a new lab entry")
	@PostMapping("/save")
	public ResponseEntity<ResponseModel<LabManagementDTO>> saveLab(@RequestBody LabManagementDTO labManagementDTO,
			@RequestHeader("userId") UUID userId) {
		log.info("Begin LabController -> saveLab() method..." + userId);

		ResponseModel<LabManagementDTO> responseModel = labService.saveLab(userId, labManagementDTO);
		HttpStatus status = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());

		log.info("End LabController -> saveLab() method");
		return ResponseEntity.status(status).body(responseModel);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<ResponseModel<LabManagementDTO>> updateLab(@RequestHeader("userId") UUID userId,
			@PathVariable UUID id, @RequestBody LabManagementDTO labManagementDTO) {
		log.info("Begin LabController -> updateLab() method...");

		ResponseModel<LabManagementDTO> responseModel = labService.updateLab(userId, id, labManagementDTO);
		HttpStatus status = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());

		log.info("End LabController -> updateLab() method");
		return ResponseEntity.status(status).body(responseModel);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ResponseModel<LabManagementDTO>> getLabById(@PathVariable("id") UUID labId) {
		log.info("Begin LabController -> getLabById() method...");

		ResponseModel<LabManagementDTO> responseModel = labService.getLabById(labId);
		HttpStatus status = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());

		log.info("End LabController -> getLabById() method");
		return ResponseEntity.status(status).body(responseModel);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResponseModel<LabManagementDTO>> deleteLabById(@PathVariable("id") UUID labId) {
		log.info("Begin LabController -> softDeleteLabById() method...");

		ResponseModel<LabManagementDTO> responseModel = labService.deleteLabById(labId);
		HttpStatus status = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());

		log.info("End LabController -> softDeleteLabById() method");
		return ResponseEntity.status(status).body(responseModel);
	}

	@PostMapping("/filter")
	public ResponseEntity<ResponseModel<LabMainResponse>> getLabs(@RequestBody LabFilterRequestDTO filterRequest,
			@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize,
			@RequestParam(defaultValue = "labName") String sortBy) {

		log.info("Received request to fetch labs with filter: {}", filterRequest);

		ResponseModel<LabMainResponse> response = labService.getLabs(filterRequest, pageNumber, pageSize, sortBy);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

}
