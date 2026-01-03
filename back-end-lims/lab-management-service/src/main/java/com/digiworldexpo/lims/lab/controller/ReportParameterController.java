package com.digiworldexpo.lims.lab.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digiworldexpo.lims.entities.lab_management.ReportParameter;
import com.digiworldexpo.lims.lab.dto.ReportParameterRequestDto;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.service.ReportParameterService;
import com.digiworldexpo.lims.lab.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/test-parameter")
@Slf4j
public class ReportParameterController {
	

	private final ReportParameterService testParameterservice;

	private final HttpStatusCode httpStatusCode;
	
	
	
    public ReportParameterController(ReportParameterService testParameterservice, HttpStatusCode httpStatusCode) {
		super();
		this.testParameterservice = testParameterservice;
		this.httpStatusCode = httpStatusCode;
	}


    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseModel<ReportParameterRequestDto>> updateTestParameter(@PathVariable UUID id,@RequestBody ReportParameterRequestDto testParameterDto)
    {
    	 log.info("Begin TestParameterController Controller -> updateTestParameter() method");
         ResponseModel<ReportParameterRequestDto> updatedTestParameter = testParameterservice.updateTestParameter(id, testParameterDto);
         log.info("Begin TestParameterController Controller -> updateTestParameter() method");
         HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(updatedTestParameter.getStatusCode());
         return ResponseEntity.status(httpStatusFromCode).body(updatedTestParameter);
    }
    
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseModel<ReportParameter>> deleteTestParameterById(@PathVariable UUID id)
    {
    	 log.info("Begin  TestParameterController Controller -> deleteTestParameterById() method");
         ResponseModel<ReportParameter> response = testParameterservice.deleteTestParameterById(id);
         log.info("End  TestParameterController Controller -> deleteTestParameterById() method");
         HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
         return ResponseEntity.status(httpStatusFromCode).body(response);
    }
    
}
