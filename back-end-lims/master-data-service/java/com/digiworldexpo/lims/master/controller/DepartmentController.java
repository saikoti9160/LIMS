package com.digiworldexpo.lims.master.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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

import com.digiworldexpo.lims.entities.master.Department;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.service.DepartmentService;
import com.digiworldexpo.lims.master.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/department")

@CrossOrigin(origins = "*")

public class DepartmentController {

	@Autowired
	private DepartmentService departmentService;

	private HttpStatusCode httpStatusCode;

	DepartmentController(DepartmentService departmentService, HttpStatusCode httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}

	@PostMapping("/save")
	public ResponseEntity<ResponseModel<Department>> saveDepartment(@RequestBody Department department,
			@RequestHeader("createdBy") UUID createdBy) {
		log.info("Begin Department Controller -> saveDepartment() method");
		ResponseModel<Department> responseModel = departmentService.saveDepartment(department, createdBy);
		log.info("End Department Controller -> saveDepartment() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(responseModel);
	}

	@PostMapping("/get-all")
	public ResponseEntity<ResponseModel<List<Department>>> getDepartments(
			@RequestParam(required = false) String startsWith, @RequestParam(defaultValue = "0") int pageNumber,
			@RequestParam(defaultValue = "10") int pageSize, @RequestParam(defaultValue = "name") String sortBy,
			@RequestHeader("createdBy") UUID createdBy) {
		log.info("Begin DepartmentController -> getDepartments() method");
		ResponseModel<List<Department>> responseModel = departmentService.getDepartments(startsWith, pageNumber,
				pageSize, sortBy, createdBy);
		log.info("End DepartmentController -> getDepartments() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(responseModel);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ResponseModel<Department>> getDepartmentById(@PathVariable UUID id) {
		log.info("End Department Controller -> getDepartmentById() method");
		ResponseModel<Department> response = departmentService.getDepartmentById(id);
		log.info("End Department Controller -> getDepartmentById() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ResponseModel<Department>> deleteDepartment(@PathVariable UUID id) {
		log.info("Begin Department Controller -> deleteDepartment() method");
		ResponseModel<Department> response = departmentService.deleteDepartment(id);
		log.info("End Department Controller -> deleteDepartment() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(response);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<ResponseModel<Department>> updateDepartment(@PathVariable UUID id,
			@RequestBody Department updatedDepartment, @RequestHeader("userId") UUID userId) {
		log.info("Begin DepartmentController -> updateDepartment() method");
		ResponseModel<Department> responseModel = departmentService.updateDepartment(id, updatedDepartment, userId);
		log.info("End DepartmentController -> updateDepartment() method");

		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(responseModel);
	}

}
