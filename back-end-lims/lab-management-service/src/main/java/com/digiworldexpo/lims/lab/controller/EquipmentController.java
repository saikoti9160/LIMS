package com.digiworldexpo.lims.lab.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digiworldexpo.lims.entities.lab_management.Equipment;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.service.EquipmentService;
import com.digiworldexpo.lims.lab.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/equipment")
@Slf4j
public class EquipmentController {
	@Autowired
	private EquipmentService equipmentService;

	@Autowired
	private HttpStatusCode httpStatusCode;



	@DeleteMapping("/delete/equipment/{id}")
	public ResponseEntity<ResponseModel<Equipment>> deleteEquipmentById(@PathVariable("id") UUID equipmentId) {
		log.info("Begin EquipmentController -> deleteEquipmentById() method...");

		ResponseModel<Equipment> responseModel = equipmentService.deleteEquipmentById(equipmentId);
		HttpStatus status = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());

		log.info("End EquipmentController -> deleteEquipmentById() method");
		return ResponseEntity.status(status).body(responseModel);
	}
}
