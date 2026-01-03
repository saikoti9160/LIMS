package com.digiworldexpo.lims.lab.service;

import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.request.DepartmentRequestDTO;
import com.digiworldexpo.lims.lab.response.DepartmentMasterSearchResponse;
import com.digiworldexpo.lims.lab.response.DepartmentResponseDTO;

public interface DepartmentService {

	
	ResponseModel<DepartmentResponseDTO> saveDepartment(UUID createdBy, DepartmentRequestDTO departmentRequestDTO);

	ResponseModel<List<DepartmentMasterSearchResponse>> getAllDepartments(
			 UUID createdBy, String searchTerm, Boolean flag,  Integer page, Integer size);

    ResponseModel<DepartmentResponseDTO> getDepartmentById(UUID id);

    ResponseModel<DepartmentResponseDTO> updateDepartment(UUID id, DepartmentRequestDTO departmentDTO);

    ResponseModel<DepartmentResponseDTO> deleteDepartmentById(UUID id);
}
