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
import com.digiworldexpo.lims.lab.request.OutsourceMasterRequestDTO;
import com.digiworldexpo.lims.lab.response.OutsourceMasterResponseDTO;
import com.digiworldexpo.lims.lab.response.OutsourceSearchResponse;
import com.digiworldexpo.lims.lab.service.OutsourceMasterService;
import com.digiworldexpo.lims.lab.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/outsource")
public class OutsourceMasterController {
	
	private final OutsourceMasterService outsourceMasterService;
	private final HttpStatusCode httpStatusCode;
	public OutsourceMasterController(OutsourceMasterService outsourceMasterService, HttpStatusCode httpStatusCode) {
		super();
		this.outsourceMasterService = outsourceMasterService;
		this.httpStatusCode = httpStatusCode;
	}
	
	@PostMapping("/save")
    public ResponseEntity<ResponseModel<OutsourceMasterResponseDTO>> saveOutsourceMaster(
            @RequestHeader("createdBy") UUID createdBy,
            @RequestBody OutsourceMasterRequestDTO outsourceMasterDTO) {
        
        log.info("Begin OutsourceMasterController -> saveOutsourceMaster() method");
        ResponseModel<OutsourceMasterResponseDTO> response = outsourceMasterService.saveOutsourceMaster(createdBy, outsourceMasterDTO);
        log.info("End OutsourceMasterController -> saveOutsourceMaster() method");
        HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(httpStatus).body(response);
    }
	
	 @GetMapping("/get-All-Outsources")
	    public ResponseEntity<ResponseModel<List<OutsourceSearchResponse>>> getAllOutsources(
	            @RequestHeader("createdBy") UUID createdBy,
	            @RequestParam(value = "keyword", required = false) String keyword,
	            @RequestParam(value = "flag", required = false) Boolean flag,
	            @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
	            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
	        
	        ResponseModel<List<OutsourceSearchResponse>> response = outsourceMasterService.getAllOutsources(
	                createdBy, keyword, flag, pageNumber, pageSize);

	        return ResponseEntity.status(Integer.parseInt(response.getStatusCode())).body(response);
	    }
	 
	 @GetMapping("/get/{id}")
	 public ResponseEntity<ResponseModel<OutsourceMasterResponseDTO>> getOutsourceById(@PathVariable("id") UUID id) {

	     log.info("Begin OutsourceMasterController -> getOutsourceById() method");
	     ResponseModel<OutsourceMasterResponseDTO> response = outsourceMasterService.getOutsourceById(id);
	     log.info("End OutsourceMasterController -> getOutsourceById() method");
	     HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
	     return ResponseEntity.status(httpStatus).body(response);
	 }
	 
	 @PutMapping("/update/{id}")
	 public ResponseEntity<ResponseModel<OutsourceMasterResponseDTO>> updateOutsourceMaster(
	         @PathVariable("id") UUID id,
	         @RequestBody OutsourceMasterRequestDTO outsourceMasterDTO) {

	     log.info("Begin OutsourceMasterController -> updateOutsourceMaster() method");
	     ResponseModel<OutsourceMasterResponseDTO> response = outsourceMasterService.updateOutsourceById(id, outsourceMasterDTO);
	     log.info("End OutsourceMasterController -> updateOutsourceMaster() method");
	     HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
	     return ResponseEntity.status(httpStatus).body(response);
	 }
	 
	    @DeleteMapping("/delete/{id}")
	    public ResponseEntity<ResponseModel<OutsourceMasterResponseDTO>> deleteOutsourceMasterById(@PathVariable("id") UUID id) {
	        
	        log.info("Begin OutsourceMasterController -> deleteOutsourceMasterById() method");
	        ResponseModel<OutsourceMasterResponseDTO> response = outsourceMasterService.deleteOutsourceById(id);
	        log.info("End OutsourceMasterController -> deleteOutsourceMasterById() method");
	        
	        HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
	        return ResponseEntity.status(httpStatus).body(response);
	    }




}
