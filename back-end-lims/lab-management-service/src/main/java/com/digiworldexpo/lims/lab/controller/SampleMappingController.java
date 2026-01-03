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

import com.digiworldexpo.lims.entities.lab_management.SampleMapping;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.service.SampleMappingService;
import com.digiworldexpo.lims.lab.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;
@RestController
@RequestMapping("/api/sample-mapping")
@Slf4j
public class SampleMappingController {
	

	private final SampleMappingService sampleMappingService;

	private final HttpStatusCode httpStatusCode;
	
	
	
	public SampleMappingController(SampleMappingService sampleMappingService, HttpStatusCode httpStatusCode) {
		super();
		this.sampleMappingService = sampleMappingService;
		this.httpStatusCode = httpStatusCode;
	}

	@PostMapping("/save")
	public ResponseEntity<ResponseModel<SampleMapping>> saveSampleMapping(@RequestHeader UUID userId,@RequestBody SampleMapping sampleMapping)
	{
		log.info("Begin SampleController -> createsampleMap().....");
		ResponseModel<SampleMapping> responseModel=sampleMappingService.saveSampleMapping(userId,sampleMapping);
		log.info("end SampleController -> createsampleMap().....");
		HttpStatus httpStatusFromCode=httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(responseModel);
	}
	
	@PostMapping("/get-all")
	public ResponseEntity<ResponseModel<List<SampleMapping>>> getAllSampleMapping(@RequestParam(required = true) UUID labId, @RequestParam(required = false) String searchText,
			@RequestParam(defaultValue = "0") Integer pageNumber,
			@RequestParam(defaultValue = "10") Integer pageSize)
	{
		log.info("Begin SampleController -> getAllSampleMapping() method...");
		ResponseModel<List<SampleMapping>> responseModel=sampleMappingService.getAllSampleMapping( labId,pageNumber,pageSize,searchText);
		 log.info("End SampleController -> getAllSampleMapping() method...");
            HttpStatus httpStatusfromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
    		return ResponseEntity.status(httpStatusfromCode).body(responseModel);
	}
	
	@GetMapping("/get/{id}")
	public ResponseEntity<ResponseModel<SampleMapping>> getByIdSampleMapping(@PathVariable UUID id)
	{
		log.info("Begin SampleController -> getByIdSampleMapping().....");
		ResponseModel<SampleMapping> responseModel=sampleMappingService.getByIdSampleMapping(id);
		log.info("end SampleController -> getByIdSampleMapping().....");
		HttpStatus httpStatusFromCode=httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(responseModel);
	}	
	
	@PutMapping("/update/{id}")
	public ResponseEntity<ResponseModel<SampleMapping>> updateSampleMapping(@PathVariable UUID id,@RequestBody SampleMapping sampleMapping)
	{
		log.info("Begin SampleController -> updateSampleMapping().....");
		ResponseModel<SampleMapping> responseModel=sampleMappingService.updateSampleMapping(id,sampleMapping);
		log.info("end SampleController -> updateSampleMapping().....");
		HttpStatus httpStatusFromCode=httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(responseModel);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResponseModel<SampleMapping>> deleteSampleMapping(@PathVariable UUID id)
	{
		log.info("Begin SampleController -> removeSampleMapping().....");
		ResponseModel<SampleMapping> responseModel=sampleMappingService.deleteSampleMapping(id);
		log.info("end SampleController -> removeSampleMapping().....");
		HttpStatus httpStatusFromCode=httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(responseModel);
	}
	
}
