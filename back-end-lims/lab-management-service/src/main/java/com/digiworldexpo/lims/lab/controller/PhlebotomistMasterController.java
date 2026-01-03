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

import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.request.PhlebotomistMasterRequestDTO;
import com.digiworldexpo.lims.lab.response.PhlebotomistMasterResponseDTO;
import com.digiworldexpo.lims.lab.response.PhlebotomistMasterSearchResponse;
import com.digiworldexpo.lims.lab.service.PhlebotomistMasterService;
import com.digiworldexpo.lims.lab.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("/phlebotomist")
public class PhlebotomistMasterController {
	
	private final PhlebotomistMasterService phlebotomistMasterService;
	private final HttpStatusCode httpStatusCode;
	public PhlebotomistMasterController(PhlebotomistMasterService phlebotomistMasterService,
			HttpStatusCode httpStatusCode) {
		super();
		this.phlebotomistMasterService = phlebotomistMasterService;
		this.httpStatusCode = httpStatusCode;
	}
	
	@PostMapping("/save")
	public ResponseEntity<ResponseModel<PhlebotomistMasterResponseDTO>> savePhlebotomistMaster(
			 @RequestHeader("createdBy") UUID createdBy, 
	        @RequestBody PhlebotomistMasterRequestDTO phlebotomistMasterDTO) {
	    log.info("Begin PhlebotomistMaster Controller -> save() method");
	    ResponseModel<PhlebotomistMasterResponseDTO> save = phlebotomistMasterService.savePhlebotomistMaster(createdBy, phlebotomistMasterDTO);
	    log.info("End PhlebotomistMaster Controller -> save() method");
	    HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(save.getStatusCode());
	    return ResponseEntity.status(httpStatus).body(save);
	}

	
	 @GetMapping("/get-all")
	    public ResponseEntity<ResponseModel<List<PhlebotomistMasterSearchResponse>>> getAllPhlebotomist(
	    		 @RequestParam(required = true) UUID createdBy,
			        @RequestParam(required = false) String keyword,
			        @RequestParam(required = false) Boolean flag,
			        @RequestParam(defaultValue = "0") Integer pageNumber,
			        @RequestParam(defaultValue = "10") Integer pageSize) {
	        log.info("Begin PhlebotomistMaster Controller -> getAll() method");
	        ResponseModel<List<PhlebotomistMasterSearchResponse>> response = phlebotomistMasterService.getAllPhlebotomist(createdBy,keyword, flag, pageNumber, pageSize);
	        log.info("End PhlebotomistMaster Controller -> getAll() method");
	        HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
	        return ResponseEntity.status(httpStatus).body(response);
	    }
	 
	 @PutMapping("/update/{id}")
	 public ResponseEntity<ResponseModel<PhlebotomistMasterResponseDTO>> update(
	         @PathVariable("id") UUID id, 
	         @RequestBody PhlebotomistMasterRequestDTO phlebotomistMasterDTO) {
	     log.info("Begin PhlebotomistMaster Controller -> update() method");
	     ResponseModel<PhlebotomistMasterResponseDTO> updateResponse = phlebotomistMasterService.updatePhlebotomist(id,phlebotomistMasterDTO);
	     log.info("End PhlebotomistMaster Controller -> update() method");
	     HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(updateResponse.getStatusCode());
	     return ResponseEntity.status(httpStatus).body(updateResponse);
	 }
	 
	 @GetMapping("/get/{id}")
	 public ResponseEntity<ResponseModel<PhlebotomistMasterResponseDTO>> getPhlebotomistById(@PathVariable("id") UUID id) {
	     log.info("Begin PhlebotomistMaster Controller -> getById() method");
	     ResponseModel<PhlebotomistMasterResponseDTO> response = phlebotomistMasterService.getPhlebotomistById(id);
	     log.info("End PhlebotomistMaster Controller -> getById() method");
	     HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
	     return ResponseEntity.status(httpStatus).body(response);
	 }
	 
	 @DeleteMapping("/delete/{id}")
	 public ResponseEntity<ResponseModel<PhlebotomistMasterResponseDTO>> deletePhlebotomistById(@PathVariable("id") UUID id) {
	     log.info("Begin PhlebotomistMaster Controller -> delete() method");
	     ResponseModel<PhlebotomistMasterResponseDTO> response = phlebotomistMasterService.deletePhlebotomistById(id);
	     log.info("End PhlebotomistMaster Controller -> delete() method");
	     HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
	     return ResponseEntity.status(httpStatus).body(response);
	 }
}
