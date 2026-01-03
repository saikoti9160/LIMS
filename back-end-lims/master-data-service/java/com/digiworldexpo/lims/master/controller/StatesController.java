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
import org.springframework.web.multipart.MultipartFile;

import com.digiworldexpo.lims.entities.master.States;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.service.StatesService;
import com.digiworldexpo.lims.master.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/state")
@Slf4j
@CrossOrigin
public class StatesController {

	private final StatesService statesService;
	private final HttpStatusCode httpStatusCode;
	
	public StatesController(StatesService statesService, HttpStatusCode httpStatusCode) {
		this.statesService = statesService;
		this.httpStatusCode = httpStatusCode;
	}
	
	@PostMapping("/upload-file")
	public ResponseEntity<ResponseModel<String>> uploadStatesFile(@RequestParam("file") MultipartFile multipartFile){
		log.info("Begin of States Controller -> uploadStatesFile() method");
		ResponseModel<String> responseModel = statesService.uploadStatesFile(multipartFile);
		log.info("End of States Controller -> uploadStatesFile() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
	
	@PostMapping("/get-all")
	public ResponseEntity<ResponseModel<List<States>>> getAllStates(@RequestParam(required = false) String startsWith, @RequestParam(required = false) List<String> countryNames,
			@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize,@RequestParam(required = false, defaultValue = "stateName") String sortedBy){
		log.info("Begin of States Controller -> getAllStates() method");
		ResponseModel<List<States>> responseModel = statesService.getAllStates(startsWith, countryNames, pageNumber, pageSize, sortedBy);
		log.info("End of States Controller -> getAllStates() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
	
	@PostMapping("/save")
	public ResponseEntity<ResponseModel<States>> saveState(@RequestBody States stateRequest, @RequestParam(required = false, name = "createdBy") UUID createdBy){
		log.info("Begin of States Controller -> saveState() method");
		ResponseModel<States> responseModel = statesService.saveState(stateRequest, createdBy);
		log.info("End of States Controller -> saveState() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<ResponseModel<States>> updateStateById(@PathVariable UUID id, @RequestBody States stateRequest, @RequestParam(required = false, name = "modifiedBy") UUID modifiedBy){
		log.info("Begin of States Controller -> updateStateById() method");
		ResponseModel<States> responseModel = statesService.updateStateById(id, stateRequest, modifiedBy);
		log.info("End of States Controller -> updateStateById() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
	
	@GetMapping("/get/{id}")
	public ResponseEntity<ResponseModel<States>> getStateById(@PathVariable UUID id){
		log.info("Begin of States Controller -> getStateById() method");
		ResponseModel<States> responseModel = statesService.getStateById(id);
		log.info("End of States Controller -> getStateById() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResponseModel<States>> deleteStateById(@PathVariable UUID id){
		log.info("Begin of States Controller -> deleteStateById() method");
		ResponseModel<States> responseModel = statesService.deleteStateById(id);
		log.info("End of States Controller -> deleteStateById() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
}
