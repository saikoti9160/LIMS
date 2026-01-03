package com.digiworldexpo.lims.master.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digiworldexpo.lims.entities.master.Designation;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.service.DesignationService;
import com.digiworldexpo.lims.master.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/designation")
@Slf4j
@CrossOrigin
public class DesignationController {
	
	private final DesignationService designationService;
	private HttpStatusCode httpStatusCode;
	
	public DesignationController(DesignationService designationService, HttpStatusCode httpStatusCode) {
		this.designationService = designationService;
		this.httpStatusCode = httpStatusCode;
	}

	@PostMapping("/save")
	public ResponseEntity<ResponseModel<Designation>> addDesignation(@RequestBody Designation designation, @RequestParam("createdBy") UUID createdBy) {
	    log.info("Begin of Designation Controller -> addDesignation() method");
	    ResponseModel<Designation> responseModel = designationService.addDesignation(designation, createdBy);
	    log.info("End of Designation Controller -> addDesignation() method");
	    return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode()))
	                         .body(responseModel);
	}

	@PostMapping("/get-all")
	public ResponseEntity<ResponseModel<List<Designation>>> getAllDesignations(@RequestParam(required = false) String startsWith,@RequestParam(required = false) UUID createdBy,
			@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize, 
			@RequestParam(required = false, defaultValue = "designationName") String sortedBy) {
		
	    log.info("Begin of Designation Controller -> getAllDesignations() method");
	    ResponseModel<List<Designation>> responseModel = designationService.getAllDesignations(startsWith, createdBy, pageNumber, pageSize, sortedBy);
	    log.info("End of Designation Controller -> getAllDesignations() method");
	    return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode()))
	                         .body(responseModel);
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<ResponseModel<Designation>> updateDesignationById(@PathVariable UUID id, @RequestBody Designation designation, @RequestParam("modifiedBy") UUID modifiedBy){
		log.info("Begin of Designation Controller -> updateDesignationById() method");
		ResponseModel<Designation> responseModel = designationService.updateDesignationById(id, designation, modifiedBy);
		log.info("Begin of Designation Controller -> updateDesignationById() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
	
	@GetMapping("/get/{id}")
	public ResponseEntity<ResponseModel<Designation>> getDesignationById(@PathVariable UUID id){
		log.info("Begin of Designation Controller -> getDesignationById() method");
		ResponseModel<Designation> responseModel = designationService.getDesignationById(id);
		log.info("Begin of Designation Controller -> getDesignationById() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResponseModel<Designation>> deleteDesignationById(@PathVariable UUID id){
		log.info("Begin of Designation Controller -> deleteDesignationById() method");
		ResponseModel<Designation> responseModel = designationService.deleteDesignationById(id);
		log.info("Begin of Designation Controller -> deleteDesignationById() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
}
