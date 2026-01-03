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
import com.digiworldexpo.lims.lab.request.DepartmentRequestDTO;
import com.digiworldexpo.lims.lab.response.DepartmentMasterSearchResponse;
import com.digiworldexpo.lims.lab.response.DepartmentResponseDTO;
import com.digiworldexpo.lims.lab.service.DepartmentService;
import com.digiworldexpo.lims.lab.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final HttpStatusCode httpStatusCode;

    public DepartmentController(DepartmentService departmentService, HttpStatusCode httpStatusCode) {
		super();
		this.departmentService = departmentService;
		this.httpStatusCode = httpStatusCode;
	}

	@PostMapping("/save")
	public ResponseEntity<ResponseModel<DepartmentResponseDTO>> saveDepartment(
			@RequestHeader("createdBy") UUID createdBy, @RequestBody DepartmentRequestDTO departmentRequestDTO) {
		log.info("Begin Department Controller -> saveDepartment() method " + createdBy);
		ResponseModel<DepartmentResponseDTO> savedDepartment = departmentService.saveDepartment(createdBy,
				departmentRequestDTO);
		log.info("End Department Controller -> saveDepartment() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(savedDepartment.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(savedDepartment);
	}

	@GetMapping("/all")
	public ResponseEntity<ResponseModel<List<DepartmentMasterSearchResponse>>> getAllDepartments(
			@RequestParam(required = true) UUID createdBy, 
			@RequestParam(required = false) String searchTerm,
			@RequestParam(required = false) Boolean flag, 
			@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer size) {

		log.info("Begin Department Controller -> getAllDepartments() method with pagination");
		ResponseModel<List<DepartmentMasterSearchResponse>> departments = departmentService.getAllDepartments(createdBy,
				searchTerm, flag, page, size);
		log.info("End Department Controller -> getAllDepartments() method with pagination");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(departments.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(departments);
	}

    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseModel<DepartmentResponseDTO>> getDepartmentById(@PathVariable UUID id) {
        log.info("Begin Department Controller -> getDepartmentById() method");
        ResponseModel<DepartmentResponseDTO> department = departmentService.getDepartmentById(id);
        log.info("End Department Controller -> getDepartmentById() method");
        HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(department.getStatusCode());
        return ResponseEntity.status(httpStatusFromCode).body(department);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseModel<DepartmentResponseDTO>> updateDepartment(@PathVariable UUID id, @RequestBody DepartmentRequestDTO departmentDTO) {
        log.info("Begin Department Controller -> updateDepartment() method");
        ResponseModel<DepartmentResponseDTO> updatedDepartment = departmentService.updateDepartment(id, departmentDTO);
        log.info("End Department Controller -> updateDepartment() method");
        HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(updatedDepartment.getStatusCode());
        return ResponseEntity.status(httpStatusFromCode).body(updatedDepartment);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseModel<DepartmentResponseDTO>> deleteDepartment(@PathVariable UUID id) {
        log.info("Begin Department Controller -> deleteDepartment() method");
        ResponseModel<DepartmentResponseDTO> response = departmentService.deleteDepartmentById(id);
        log.info("End Department Controller -> deleteDepartment() method");
        HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(httpStatusFromCode).body(response);
    }

}
