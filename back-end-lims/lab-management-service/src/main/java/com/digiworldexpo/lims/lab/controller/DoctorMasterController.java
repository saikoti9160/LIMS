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
import com.digiworldexpo.lims.lab.request.DoctorMasterRequestDTO;
import com.digiworldexpo.lims.lab.response.DoctorMasterResponseDTO;
import com.digiworldexpo.lims.lab.response.DoctorSearchResponseDTO;
import com.digiworldexpo.lims.lab.service.DoctorMasterService;
import com.digiworldexpo.lims.lab.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/doctor")
public class DoctorMasterController {
	
	private final DoctorMasterService doctorMasterService;
	private final HttpStatusCode httpStatusCode;
	public DoctorMasterController(DoctorMasterService doctorMasterService, HttpStatusCode httpStatusCode) {
		super();
		this.doctorMasterService = doctorMasterService;
		this.httpStatusCode = httpStatusCode;
	}
	
	@PostMapping("/save")
	public ResponseEntity<ResponseModel<DoctorMasterResponseDTO>> saveDoctorMaster(
	        @RequestHeader("createdBy") UUID createdBy,
	        @RequestBody DoctorMasterRequestDTO doctorMasterRequestDTO) {  
	    log.info("Begin DoctorMaster Controller -> saveDoctorMaster() method user = " + createdBy);
	    ResponseModel<DoctorMasterResponseDTO> response = doctorMasterService.saveDoctorMaster(createdBy, doctorMasterRequestDTO);
	    log.info("End DoctorMaster Controller -> saveDoctorMaster() method");
	    HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
	    return ResponseEntity.status(httpStatus).body(response);
	}

	@GetMapping("/get/{id}")
	public ResponseEntity<ResponseModel<DoctorMasterResponseDTO>> getDoctorById(@PathVariable("id") UUID id) {
	    log.info("Begin DoctorMasterController -> getDoctorById() method");
	    ResponseModel<DoctorMasterResponseDTO> response = doctorMasterService.getDoctorById(id);
	    log.info("End DoctorMasterController -> getDoctorById() method");
	    HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
	    return ResponseEntity.status(httpStatusFromCode).body(response);
	}

	 @PutMapping("/update/{id}")
	    public ResponseEntity<ResponseModel<DoctorMasterResponseDTO>> updateDoctorById(@PathVariable("id") UUID id, 
	                                                                          @RequestBody DoctorMasterRequestDTO doctorMasterDTO) {
	        log.info("Begin DoctorMaster Controller -> updateDoctorMaster() method");
	        ResponseModel<DoctorMasterResponseDTO> response = doctorMasterService.updateDoctorById(id,doctorMasterDTO);
	        log.info("End DoctorMaster Controller -> updateDoctorMaster() method");
	        HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
	        return ResponseEntity.status(httpStatusFromCode).body(response);
	    }
	 
	 @DeleteMapping("/delete/{id}")
	    public ResponseEntity<ResponseModel<DoctorMasterResponseDTO>> deleteDoctorById(@PathVariable("id") UUID id) {
	        log.info("Begin DoctorMaster Controller -> deleteDoctorMaster() method");
	        ResponseModel<DoctorMasterResponseDTO> response = doctorMasterService.deleteDoctorById(id);
	        log.info("End DoctorMaster Controller -> deleteDoctorMaster() method");
	        HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
	        return ResponseEntity.status(httpStatusFromCode).body(response);
	    }
	 
	 @GetMapping("/get-all")
	 public ResponseEntity<ResponseModel<List<DoctorSearchResponseDTO>>> getAll(
			    @RequestParam(required = false) UUID createdBy,
		        @RequestParam(required = false) String keyword,
		        @RequestParam(required = false) Boolean flag,
		        @RequestParam(defaultValue = "0") Integer pageNumber,
		        @RequestParam(defaultValue = "10") Integer pageSize) {
	     log.info("Begin DoctorMaster Controller -> getAll() method");

	     ResponseModel<List<DoctorSearchResponseDTO>> response = doctorMasterService.getAllDoctors(createdBy,keyword, flag, pageNumber, pageSize);

	     log.info("End DoctorMaster Controller -> getAll() method");
	     HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
	     return ResponseEntity.status(httpStatusFromCode).body(response);
	 }

}
