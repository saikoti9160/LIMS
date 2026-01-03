package com.digiworldexpo.lims.master.service;

import java.util.List;


import java.util.UUID;

import com.digiworldexpo.lims.entities.master.Department;
import com.digiworldexpo.lims.master.model.response.ResponseModel;

public interface DepartmentService {


	ResponseModel<Department> saveDepartment(Department department, UUID createdBy);

	
	ResponseModel<List<Department>> getDepartments(String startsWith, int pageNumber, int pageSize, String sortBy,
			UUID createdBy);


	ResponseModel<Department> getDepartmentById(UUID id);

	ResponseModel<Department> updateDepartment(UUID id, Department updatedDepartment, UUID userId);

	ResponseModel<Department> deleteDepartment(UUID id);


}