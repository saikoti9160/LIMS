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
import com.digiworldexpo.lims.lab.request.WarehouseMasterRequestDto;
import com.digiworldexpo.lims.lab.response.WarehouseMasterResponseDTO;
import com.digiworldexpo.lims.lab.response.WarehouseSearchResponseDTO;
import com.digiworldexpo.lims.lab.service.WarehouseMasterService;
import com.digiworldexpo.lims.lab.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/warehouse")
public class WarehouseMasterController {
	
	private final WarehouseMasterService warehouseMasterService;
	private final HttpStatusCode httpStatusCode;
	public WarehouseMasterController(WarehouseMasterService warehouseMasterService, HttpStatusCode httpStatusCode) {
		super();
		this.warehouseMasterService = warehouseMasterService;
		this.httpStatusCode = httpStatusCode;
	}
	
	
	 @PostMapping("/save")
	    public ResponseEntity<ResponseModel<WarehouseMasterResponseDTO>> saveWarehouseMaster(
	    		@RequestHeader("createdBy") UUID createdBy,
	    		  @RequestBody WarehouseMasterRequestDto warehouseMasterRequestDto) {
	        log.info("Begin Department Controller -> saveDepartment() method userId = " + createdBy);
	        ResponseModel<WarehouseMasterResponseDTO> savedDepartment = warehouseMasterService.saveWarehouseMaster(warehouseMasterRequestDto, createdBy);
	        log.info("End Department Controller -> saveDepartment() method");
	        HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(savedDepartment.getStatusCode());
	        return ResponseEntity.status(httpStatusFromCode).body(savedDepartment);
	    }
	 
	 @GetMapping("/get-all")
	 public ResponseEntity<ResponseModel<List<WarehouseSearchResponseDTO>>> getAllWarehouses(
			 @RequestParam(required = true) UUID createdBy,
		        @RequestParam(required = false) String keyword,
		        @RequestParam(required = false) Boolean flag,
		        @RequestParam(defaultValue = "0") Integer pageNumber,
		        @RequestParam(defaultValue = "10") Integer pageSize) {
	     log.info("Begin WarehouseMasterController -> getAllWarehouses() method");

	     ResponseModel<List<WarehouseSearchResponseDTO>> response = warehouseMasterService.getAllWarehouses(createdBy, keyword, flag, pageNumber, pageSize);

	     log.info("End WarehouseMasterController -> getAllWarehouses() method");
	     HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
	     return ResponseEntity.status(httpStatusFromCode).body(response);
	 }


	 
	 @GetMapping("/get/{id}")
	 public ResponseEntity<ResponseModel<WarehouseMasterResponseDTO>> getWarehouseById(
			 @PathVariable("id") UUID id) {
	     log.info("Begin WarehouseMasterController -> getWarehouseById() method");
	     ResponseModel<WarehouseMasterResponseDTO> response = warehouseMasterService.getById(id);
	     log.info("End WarehouseMasterController -> getWarehouseById() method");
	     HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
	     return ResponseEntity.status(httpStatusFromCode).body(response);
	 }
	 
	 @PutMapping("/update/{id}")
	 public ResponseEntity<ResponseModel<WarehouseMasterResponseDTO>> updateWarehouseMaster(
	         @PathVariable("id") UUID id,
	         @RequestBody WarehouseMasterRequestDto warehouseMasterRequestDto) {
	     log.info("Begin WarehouseMasterController -> updateWarehouseMaster() method");
	     ResponseModel<WarehouseMasterResponseDTO> response = warehouseMasterService.updateWarehouseMaster(id, warehouseMasterRequestDto);
	     log.info("End WarehouseMasterController -> updateWarehouseMaster() method");
	     HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
	     return ResponseEntity.status(httpStatusFromCode).body(response);
	 }
	 
	 @DeleteMapping("/delete/{id}")
	 public ResponseEntity<ResponseModel<WarehouseMasterResponseDTO>> deleteWarehouseMaster(@PathVariable("id") UUID id) {
	     log.info("Begin WarehouseMasterController -> deleteWarehouseMaster() method");
	     ResponseModel<WarehouseMasterResponseDTO> response = warehouseMasterService.deleteWarehouseMaster(id);
	     log.info("End WarehouseMasterController -> deleteWarehouseMaster() method");
	     HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
	     return ResponseEntity.status(httpStatusFromCode).body(response);
	 }


}

