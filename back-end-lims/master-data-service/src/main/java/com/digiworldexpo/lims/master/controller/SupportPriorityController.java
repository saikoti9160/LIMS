package com.digiworldexpo.lims.master.controller;

import java.util.List;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.digiworldexpo.lims.entities.master.SupportPriority;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.service.SupportPriorityService;
import com.digiworldexpo.lims.master.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/support-priority")

@CrossOrigin(origins = "*")
public class SupportPriorityController {

	private final SupportPriorityService supportPriorityService;
	private final HttpStatusCode httpStatusCode;

	SupportPriorityController(SupportPriorityService supportPriorityService, HttpStatusCode httpStatusCode) {
		this.supportPriorityService = supportPriorityService;
		this.httpStatusCode = httpStatusCode;
	}

	@PostMapping("/save")
	public ResponseEntity<ResponseModel<SupportPriority>> saveSupportPriority(
			@RequestBody SupportPriority supportPriority) {
		log.info("Begin SupportPriorityController -> saveSupportPriority() method");
		ResponseModel<SupportPriority> response = supportPriorityService.saveSupportPriority(supportPriority);
		log.info("End SupportPriorityController -> saveSupportPriority() method");
		HttpStatus status = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		return ResponseEntity.status(status).body(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ResponseModel<SupportPriority>> getSupportPriorityById(@PathVariable UUID id) {
		log.info("Begin SupportPriorityController -> getSupportPriorityById() method");
		ResponseModel<SupportPriority> response = supportPriorityService.getSupportPriorityById(id);
		log.info("End SupportPriorityController -> getSupportPriorityById() method");
		HttpStatus status = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		return ResponseEntity.status(status).body(response);
	}

	@PostMapping("/get-all")
	public ResponseEntity<ResponseModel<List<SupportPriority>>> getSupportPriorities(
			@RequestParam(required = false) String startsWith, @RequestParam(defaultValue = "0") int pageNumber,
			@RequestParam(defaultValue = "10") int pageSize, @RequestParam(defaultValue = "name") String sortBy) {
		log.info("Begin SupportPriorityController -> getSupportPriorities() method");
		ResponseModel<List<SupportPriority>> response = supportPriorityService.getSupportPriorities(startsWith,
				pageNumber, pageSize, sortBy);
		log.info("End SupportPriorityController -> getSupportPriorities() method");
		HttpStatus status = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		return ResponseEntity.status(status).body(response);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<ResponseModel<SupportPriority>> updateSupportPriority(@PathVariable UUID id,
			@RequestBody SupportPriority updatedPriority) {
		log.info("Begin SupportPriorityController -> updateSupportPriority() method");
		ResponseModel<SupportPriority> response = supportPriorityService.updateSupportPriority(id, updatedPriority);
		log.info("End SupportPriorityController -> updateSupportPriority() method");
		HttpStatus status = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		return ResponseEntity.status(status).body(response);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResponseModel<SupportPriority>> deleteSupportPriority(@PathVariable UUID id) {
		log.info("Begin SupportPriorityController -> deleteSupportPriority() method");
		ResponseModel<SupportPriority> response = supportPriorityService.deleteSupportPriority(id);
		log.info("End SupportPriorityController -> deleteSupportPriority() method");
		HttpStatus status = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		return ResponseEntity.status(status).body(response);
	}
}
