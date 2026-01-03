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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digiworldexpo.lims.entities.lab_management.TestConfigurationMaster;
import com.digiworldexpo.lims.lab.dto.TestConfigurationRequestDto;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.service.TestConfigurationService;
import com.digiworldexpo.lims.lab.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/test-configuration")
@Slf4j
public class TestConfigurationController {


	private final TestConfigurationService testConfigurationService;

	private final HttpStatusCode httpStatusCode;
	

	public TestConfigurationController(TestConfigurationService testConfigurationService,
			HttpStatusCode httpStatusCode) {
		super();
		this.testConfigurationService = testConfigurationService;
		this.httpStatusCode = httpStatusCode;
	}

	@PostMapping("/save")
	public ResponseEntity<ResponseModel<TestConfigurationMaster>> createTestconfiguration(@RequestParam(required =true) UUID userId,
			@RequestBody TestConfigurationMaster testconfiguration) {
		log.info("Begin TestConfigurationController -> createTestconfiguration");
		ResponseModel<TestConfigurationMaster> responseModel = testConfigurationService
				.createTestconfiguration(userId,testconfiguration);
		log.info("end SampleController -> createsample().....");
		HttpStatus httpStatusfromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusfromCode).body(responseModel);
	}
	
	@PostMapping("/get-all")  
	public ResponseEntity<ResponseModel<List<TestConfigurationRequestDto>>> getAllTestConfiguration(@RequestParam(required = true) UUID labId, @RequestParam(required = false) String searchText,
			@RequestParam(defaultValue = "0") Integer pageNumber,
			@RequestParam(defaultValue = "10") Integer pageSize)
	{
		log.info("Begin TestConfigurationController -> getAllTestConfiguration() method...");
		ResponseModel<List<TestConfigurationRequestDto>> responseModel= testConfigurationService.getAllTestConfiguration( labId,pageNumber,pageSize,searchText);
		 log.info("End TestConfigurationController -> getAllTestConfiguration() method with an error.");
            HttpStatus httpStatusfromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
    		return ResponseEntity.status(httpStatusfromCode).body(responseModel);
        
	}
	
	@GetMapping("/get/{id}")
	public ResponseEntity<ResponseModel<TestConfigurationRequestDto>> getTestConfigurationById(@PathVariable UUID id) {
	    log.info("Begin TestConfigurationController -> getTestConfigurationById() ....!");
	    ResponseModel<TestConfigurationRequestDto> responseModel = testConfigurationService.getTestConfigurationById(id);
	    log.info("End TestConfigurationController -> getTestConfigurationById() ....!");
	    HttpStatus httpStatusfromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusfromCode).body(responseModel);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<ResponseModel<TestConfigurationRequestDto>> updateTestConfiguration(
	        @PathVariable UUID id,
	        @RequestBody TestConfigurationRequestDto updatedConfigurationDto) {
	    log.info("Begin TestConfigurationController -> updateTestConfiguration() ....!");
	    ResponseModel<TestConfigurationRequestDto> responseModel = testConfigurationService.updateTestConfiguration(id, updatedConfigurationDto);
	    log.info("End TestConfigurationController -> updateTestConfiguration() ....!");
	    HttpStatus httpStatusfromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusfromCode).body(responseModel);
	}
	
	@DeleteMapping("/remove/{id}")
	public ResponseEntity<ResponseModel<TestConfigurationMaster>> deleteTestConfiguration(@PathVariable UUID id) {
	    log.info("Begin TestConfigurationController -> deleteTestConfiguration() ....!");
	    ResponseModel<TestConfigurationMaster> responseModel = testConfigurationService.deleteTestConfiguration(id);
	   log.info("End TestConfigurationController -> deleteTestConfiguration() ....!");
	   HttpStatus httpStatusfromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusfromCode).body(responseModel);
	}


}
