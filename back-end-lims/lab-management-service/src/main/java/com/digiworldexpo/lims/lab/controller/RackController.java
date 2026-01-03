package com.digiworldexpo.lims.lab.controller;


import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import com.digiworldexpo.lims.entities.lab_management.Rack;
import com.digiworldexpo.lims.lab.dto.RackRequestDto;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.service.RackService;
import com.digiworldexpo.lims.lab.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/rack")
@Slf4j
public class RackController {

	private final RackService rackservice;

	private final HttpStatusCode httpStatusCode;

	public RackController(RackService rackservice, HttpStatusCode httpStatusCode) {
		super();
		this.rackservice = rackservice;
		this.httpStatusCode = httpStatusCode;
	}

	@PostMapping("/save")
	public ResponseEntity<ResponseModel<Rack>> createRack(@RequestHeader UUID userId, @RequestBody Rack requestRack) {
		log.info("Begin RackController -> createRack() method");
		ResponseModel<Rack> responseModel = rackservice.createRack(userId, requestRack);
		log.info("End RackController -> createRack() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(responseModel);
	}

	@PostMapping("/get-all")
	public ResponseEntity<ResponseModel<List<RackRequestDto>>> getAllRacks(@RequestParam UUID labId,
			@RequestParam(required = false) String searchText, @RequestParam(defaultValue = "0") Integer pageNumber,
			@RequestParam(defaultValue = "10") Integer pageSize) {
		log.info("Begin RackController -> getAllRacks() method");
		ResponseModel<List<RackRequestDto>> responseModel = rackservice.getAllRacks(labId, searchText, pageNumber,
				pageSize);
		log.info("End RackController -> createRack() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(responseModel);
	}

	@GetMapping("/get/{id}")
	public ResponseEntity<ResponseModel<RackRequestDto>> getRackById(@PathVariable UUID id) {
		log.info("Begin RackController -> getRackById() method");
		ResponseModel<RackRequestDto> responseModel = rackservice.getRackById(id);
		log.info("End RackController -> getRackById() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(responseModel);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<ResponseModel<RackRequestDto>> updateRackById(@RequestBody RackRequestDto requestRack,
			@PathVariable UUID id) {
		log.info("Begin RackController -> updateRackById() method");
		ResponseModel<RackRequestDto> responseModel = rackservice.updateRackById(requestRack, id);
		log.info("End RackController -> updateRackById() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(responseModel);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResponseModel<Rack>> deleteRackById(@PathVariable UUID id) {
		log.info("Begin RackController -> deleteRackById() method");
		ResponseModel<Rack> responseModel = rackservice.deleteRackById(id);
		log.info("End RackController -> deleteRackById() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(responseModel);
	}
}

