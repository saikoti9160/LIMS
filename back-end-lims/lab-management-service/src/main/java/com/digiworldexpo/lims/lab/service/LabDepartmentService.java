package com.digiworldexpo.lims.lab.service;

import java.util.UUID;

import org.springframework.data.domain.Page;

import com.digiworldexpo.lims.entities.lab_management.LabDepartment;
import com.digiworldexpo.lims.lab.dto.LabDepartmentDto;
import com.digiworldexpo.lims.lab.model.ResponseModel;

public interface LabDepartmentService {

    ResponseModel<LabDepartment> saveLabDepartment(UUID userId, LabDepartment department);

    ResponseModel<Page<LabDepartmentDto>> getAllLabDepartments(String searchTerm,Integer page, Integer size);

    ResponseModel<LabDepartmentDto> getLabDepartmentById(UUID id);

    ResponseModel<LabDepartmentDto> updateLabDepartment(UUID id, LabDepartmentDto departmentDto);

    ResponseModel<LabDepartment> deleteLabDepartmentById(UUID id);
}
