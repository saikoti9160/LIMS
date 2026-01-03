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

import com.digiworldexpo.lims.entities.lab_management.SampleMaster;
import com.digiworldexpo.lims.lab.dto.SampleMasterRequestDto;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.service.SampleService;
import com.digiworldexpo.lims.lab.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/samples")
@Slf4j
public class SampleController {
	
	private final  SampleService  sampleService;

	private  final HttpStatusCode httpStatusCode;
	

	public SampleController(SampleService sampleService, HttpStatusCode httpStatusCode) {
		super();
		this.sampleService = sampleService;
		this.httpStatusCode = httpStatusCode;
	}


	@PostMapping("save")
	public ResponseEntity<ResponseModel<SampleMaster>> createSample(@RequestHeader UUID userId,@RequestBody SampleMaster sampleMaster)
	{
		log.info("Begin SampleController -> createsample().....for that : "+userId);
		ResponseModel<SampleMaster> responseModel=sampleService.createSample(userId,sampleMaster);
		log.info("end SampleController -> createsample().....");
		HttpStatus httpStatusFromCode=httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(responseModel);
	}
	
	 
	@PostMapping("/get-all")  
	public ResponseEntity<ResponseModel<List<SampleMasterRequestDto>>> getAllSamples(@RequestParam UUID labId ,@RequestParam(required = false) String searchText,
																		     @RequestParam(defaultValue = "0") Integer pageNumber,
																		    	@RequestParam(defaultValue = "10") Integer pageSize)
	{			
		log.info("Begin SampleController -> getAllSamples() method...");
		ResponseModel<List<SampleMasterRequestDto>> responseModel= sampleService.getAllSamples(labId,pageNumber,pageSize,searchText);
		log.info("Begin SampleController -> getAllSamples() method...");
		HttpStatus httpStatusfrpmCode=httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusfrpmCode).body(responseModel);
        
	}
	
	
	@GetMapping("/get/{id}")
	public ResponseEntity<ResponseModel<SampleMasterRequestDto>> getSamplesById(@PathVariable UUID id){			
		log.info("Begin SampleController -> getSamplesById() method...");
	
		ResponseModel<SampleMasterRequestDto> sampleMasterDto = sampleService.getSamplesById(id);
		log.info("end SampleController -> getSamplesById() method...");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(sampleMasterDto.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(sampleMasterDto);
	} 
	
	@GetMapping("/get/sample-names")
	public ResponseEntity<ResponseModel<List<SampleMaster>>> getSamplesBySampleName(@RequestParam String SampleName) {
	    log.info("Begin SampleController -> getSamplesBySampleName() method...");
	    ResponseModel<List<SampleMaster>> sampleMasterDto = sampleService.getSamplesBySampleName(SampleName);

	    log.info("End SampleController -> getSamplesBySampleName() method...");
	    HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(sampleMasterDto.getStatusCode());
	    return ResponseEntity.status(httpStatusFromCode).body(sampleMasterDto);
	}

	@PutMapping("/update/{id}")
    public ResponseEntity<ResponseModel<SampleMasterRequestDto>> updateSample(
            @PathVariable UUID id, @RequestBody SampleMasterRequestDto sampleMasterDto) {
    	 log.info("Begin Sample Controller -> updateSample() method");
        ResponseModel<SampleMasterRequestDto> responseModel = sampleService.updateSamples(id, sampleMasterDto);
        log.info("End Sample Controller -> updateSample() method");
        HttpStatus status =httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
        return ResponseEntity.status(status).body(responseModel);
    }
	
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseModel<SampleMaster>> deleteSample(@PathVariable UUID id) {
    	 log.info("Begin Sample Controller -> deleteSample() method");
        ResponseModel<SampleMaster> responseModel = sampleService.deleteSample(id);
        log.info("End Sample Controller -> deleteSample() method");
        HttpStatus status =httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
        return ResponseEntity.status(status).body(responseModel);
    }

	 
}
