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

import com.digiworldexpo.lims.entities.lab_management.LabEquipment;
import com.digiworldexpo.lims.lab.dto.LabEquipmentDto;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.service.LabEquipmentService;
import com.digiworldexpo.lims.lab.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/lab-equipments")
@Slf4j
public class LabEquipmentController {


    private final LabEquipmentService equipmentService;

    private final HttpStatusCode httpStatusCode;

	public LabEquipmentController(LabEquipmentService equipmentService, HttpStatusCode httpStatusCode) {
		super();
		this.equipmentService = equipmentService;
		this.httpStatusCode = httpStatusCode;
	}

	@PostMapping("/save")
    public ResponseEntity<ResponseModel<LabEquipment>> createEquipment(@RequestHeader UUID userId,@RequestBody LabEquipment equipment) {
        log.info("Begin EquipmentController -> createEquipment() endpoint...");
        ResponseModel<LabEquipment> responseModel = equipmentService.createEquipment(userId,equipment);
      log.info("End EquipmentController -> createEquipment() endpoint...");
      HttpStatus httpStatusFromCode=httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
      return ResponseEntity.status(httpStatusFromCode).body(responseModel);
    }
    
    @PostMapping("/get-all")
    public ResponseEntity<ResponseModel<List<LabEquipmentDto>>>  getAllEquipment( @RequestParam UUID labId,@RequestParam(required = false) String searchText,
			@RequestParam(defaultValue = "0") Integer pageNumber,
		@RequestParam(defaultValue = "10") Integer pageSize)
    {
    	log.info("Begin EquipmentController -> getAllEquipment() endpoint...");
    	ResponseModel<List<LabEquipmentDto>> responseModel=equipmentService.getAllEquipment(labId,searchText,pageSize,pageNumber);
    	log.info("end EquipmentController -> getAllEquipment() endpoint...");
    	HttpStatus httpStatusFromCode= httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
    	 return ResponseEntity.status(httpStatusFromCode).body(responseModel);
    }
    
    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseModel<LabEquipmentDto>> getEquipmentById(@PathVariable UUID id)
    {
    	log.info("Begin EquipmentController -> getEquipmentById() endpoint...");   
    	ResponseModel<LabEquipmentDto>  responseModel=equipmentService.getEquipmentById(id);
    	log.info(" end EquipmentController -> getEquipmentById() endpoint...");
    	HttpStatus httpStatusFromCode= httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
      return ResponseEntity.status(httpStatusFromCode).body(responseModel);
    }
    
    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseModel<LabEquipmentDto>> updateEquipment(@PathVariable UUID id,@RequestBody LabEquipmentDto equipmentRequestDto)
    {
    	log.info("Begin EquipmentService -> updateEquipment() method...");
    	ResponseModel<LabEquipmentDto> responseModel=equipmentService.updateEquipment(id,equipmentRequestDto);
    	log.info("End EquipmentService -> updateEquipment() method...");
    	HttpStatus httpStatusFromCode=httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
    	return ResponseEntity.status(httpStatusFromCode).body(responseModel);
    	
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseModel<LabEquipment>> deleteEquipment(@PathVariable UUID id)
    {
    	log.info("Begin EquipmentService -> deleteEquipment() method...");
    	ResponseModel<LabEquipment> responseModel=equipmentService.deleteEquipment(id);
    	log.info("end EquipmentService -> deleteEquipment() method...");
    	HttpStatus httpStatusFromCode=httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
    	return ResponseEntity.status(httpStatusFromCode).body(responseModel);
    }
    
}
