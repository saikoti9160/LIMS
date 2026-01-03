package com.digiworldexpo.lims.lab.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
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

import com.digiworldexpo.lims.entities.lab_management.LabDepartment;
import com.digiworldexpo.lims.lab.dto.LabDepartmentDto;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.service.LabDepartmentService;
import com.digiworldexpo.lims.lab.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController

@RequestMapping("/lab-department")
public class LabDepartmentController {

	private final LabDepartmentService departmentService;

	private final HttpStatusCode httpStatusCode;

	public LabDepartmentController(LabDepartmentService departmentService, HttpStatusCode httpStatusCode) {
		super();
		this.departmentService = departmentService;
		this.httpStatusCode = httpStatusCode;
	}

	@PostMapping("/save")

    public ResponseEntity<ResponseModel<LabDepartment>> saveLabDepartment(@RequestHeader UUID userId,@RequestBody LabDepartment department) {
        log.info("Begin Department Controller -> saveLabDepartment() method");
        ResponseModel<LabDepartment> savedDepartment = departmentService.saveLabDepartment(userId,department);
        log.info("End Department Controller -> saveLabDepartment() method");
        HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(savedDepartment.getStatusCode());
        return ResponseEntity.status(httpStatusFromCode).body(savedDepartment);
    }


    @GetMapping("/all")
    public ResponseEntity<ResponseModel<Page<LabDepartmentDto>>> getAllLabDepartments(
    		 @RequestParam(required = false) String searchTerm,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Begin Department Controller -> getAllLabDepartments() method with pagination");
        ResponseModel<Page<LabDepartmentDto>> departments = departmentService.getAllLabDepartments(searchTerm,page, size);
        log.info("End Department Controller -> getAllLabDepartments() method with pagination");
        HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(departments.getStatusCode());
        return ResponseEntity.status(httpStatusFromCode).body(departments);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseModel<LabDepartmentDto>> getLabDepartmentById(@PathVariable UUID id) {
        log.info("Begin Department Controller -> getLabDepartmentById() method");
        ResponseModel<LabDepartmentDto> department = departmentService.getLabDepartmentById(id);
        log.info("End Department Controller -> getLabDepartmentById() method");
        HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(department.getStatusCode());
        return ResponseEntity.status(httpStatusFromCode).body(department);
    }

    @PutMapping("/update/{id}") 
    public ResponseEntity<ResponseModel<LabDepartmentDto>> updateLabDepartment(@PathVariable UUID id, @RequestBody LabDepartmentDto departmentDTO) {
        log.info("Begin Department Controller -> updateLabDepartment() method");
        ResponseModel<LabDepartmentDto> updatedDepartment = departmentService.updateLabDepartment(id, departmentDTO);
        log.info("End Department Controller -> updateLabDepartment() method");
        HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(updatedDepartment.getStatusCode());
        return ResponseEntity.status(httpStatusFromCode).body(updatedDepartment);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseModel<LabDepartment>> deleteLabDepartment(@PathVariable UUID id) {
        log.info("Begin Department Controller -> deleteLabDepartment() method");
        ResponseModel<LabDepartment> response = departmentService.deleteLabDepartmentById(id);
        log.info("End Department Controller -> deleteLabDepartment() method");
        HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(httpStatusFromCode).body(response);
    }


}
