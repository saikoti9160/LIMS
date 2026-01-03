package com.digiworldexpo.lims.master.service.serviceImpl;

import java.sql.Timestamp;

import java.util.List;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import com.digiworldexpo.lims.entities.master.Department;

import com.digiworldexpo.lims.master.exception.BadRequestException;

import com.digiworldexpo.lims.master.exception.DuplicateRecordFoundException;
import com.digiworldexpo.lims.master.exception.RecordNotFoundException;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.repository.DepartmentRepository;
import com.digiworldexpo.lims.master.service.DepartmentService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DepartmentServiceImpl implements DepartmentService {

	private final DepartmentRepository departmentRepository;

	DepartmentServiceImpl(DepartmentRepository departmentRepository) {
		this.departmentRepository = departmentRepository;
	}

	@Override
	public ResponseModel<Department> saveDepartment(Department department, UUID createdBy) {
		log.info("Begin DepartmentServiceImpl -> saveDepartment() method...");
		ResponseModel<Department> responseModel = new ResponseModel<>();

		try {
			if (department.getName() == null || department.getName().trim().isEmpty()) {
				responseModel.setData(null);
				responseModel.setMessage("Department name cannot be null or empty");
				responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
				return responseModel;
			}

			department.setCreatedBy(createdBy);

			Optional<Department> existingDepartment = departmentRepository.findByCreatedByAndNameIgnoreCase(createdBy,
					department.getName());
			if (existingDepartment.isPresent()) {
				throw new DuplicateRecordFoundException(
						"Department with name '" + department.getName() + "' already exists for this user.");
			}

			Department savedDepartment = departmentRepository.save(department);
			responseModel.setData(savedDepartment);
			responseModel.setMessage("Department saved successfully");
			responseModel.setStatusCode(HttpStatus.OK.toString());

		} catch (DuplicateRecordFoundException exception) {
			log.info("Duplicate record found: {}", exception.getMessage());
			responseModel.setData(null);
			responseModel.setMessage(exception.getMessage());
			responseModel.setStatusCode(HttpStatus.CONFLICT.toString());
		} catch (Exception e) {
			log.info("Error occurred while saving Department: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Failed to save Department");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End DepartmentServiceImpl -> saveDepartment() method...");
		return responseModel;
	}

	@Override
	public ResponseModel<List<Department>> getDepartments(String startsWith, int pageNumber, int pageSize,
			String sortBy, UUID createdBy) {
		log.info("Begin DepartmentServiceImpl -> getDepartments() method...");
		ResponseModel<List<Department>> responseModel = new ResponseModel<>();

		try {
			if (pageNumber < 0 || pageSize < 1) {
				throw new IllegalArgumentException("Invalid pagination parameters.");
			}

			Sort sort = Sort.by(Sort.Direction.ASC, sortBy);
			Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

			long totalCount = departmentRepository.countByCreatedBy(createdBy);

			Page<Department> departmentPage;
			if (startsWith != null && !startsWith.trim().isEmpty()) {
				// Use the updated repository method for searching departments that contain the keyword
				departmentPage = departmentRepository.findByCreatedByAndNameContaining(createdBy, startsWith, pageable);
				log.info("Searching departments with name containing '{}' for createdBy '{}'", startsWith, createdBy);
			} else {
				// Retrieve all departments if startsWith is null or empty
				departmentPage = departmentRepository.findByCreatedBy(createdBy, pageable);
				log.info("Retrieving all departments for createdBy '{}'", createdBy);
			}

			responseModel.setData(departmentPage.getContent());
			responseModel.setMessage("Departments retrieved successfully.");
			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setTotalCount((int) totalCount);
			responseModel.setPageNumber(pageNumber);
			responseModel.setPageSize(pageSize);

		} catch (IllegalArgumentException e) {
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			log.info("Invalid input parameters: {}", e.getMessage());
		} catch (Exception e) {
			responseModel.setData(null);
			responseModel.setMessage("Failed to retrieve departments.");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			log.info("Error occurred while retrieving departments: {}", e.getMessage());
		}

		log.info("End DepartmentServiceImpl -> getDepartments() method...");
		return responseModel;
	}

	@Override
	public ResponseModel<Department> getDepartmentById(UUID id) {
		log.info("Begin DepartmentServiceImpl -> getDepartmentById() method...");
		ResponseModel<Department> responseModel = new ResponseModel<>();
		try {
			Department department = departmentRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("Department not found with ID: " + id));

			responseModel.setData(department);
			responseModel.setMessage("Department retrieved successfully for ID: " + id);
			responseModel.setStatusCode(HttpStatus.OK.toString());
		} catch (RecordNotFoundException e) {
			log.info("Department not found: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
		} catch (Exception e) {
			log.info("Error occurred while fetching department: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Failed to retrieve department");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End DepartmentServiceImpl -> getDepartmentById() method...");
		return responseModel;
	}



	@Override
	@Transactional
	public ResponseModel<Department> updateDepartment(UUID id, Department updatedDepartment, UUID userId) {

		log.info("Begin DepartmentServiceImpl -> updateDepartment() method...");
		ResponseModel<Department> responseModel = new ResponseModel<>();

		try {
			if (updatedDepartment.getName() == null || updatedDepartment.getName().trim().isEmpty()) {
				throw new IllegalArgumentException("Department name cannot be null or empty during update.");
			}

			Department existingDepartment = departmentRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("Department not found with ID: " + id));

			Optional<Department> duplicateDepartment = departmentRepository.findByName(updatedDepartment.getName());
			if (duplicateDepartment.isPresent() && !duplicateDepartment.get().getId().equals(id)) {
				throw new DuplicateRecordFoundException(
						"Department with name '" + updatedDepartment.getName() + "' already exists.");
			}

			// Copy properties but preserve id, createdBy, and createdOn(Prevents overwriting id,createdBy,createdOn)
			BeanUtils.copyProperties(updatedDepartment, existingDepartment, "id", "createdBy", "createdOn");

			existingDepartment.setModifiedBy(userId);
			existingDepartment.setModifiedOn(new Timestamp(System.currentTimeMillis()));

			// Save the updated department which was copied from updatedDepartment to existingDepartment
			Department savedDepartment = departmentRepository.save(existingDepartment);

			responseModel.setData(savedDepartment);
			responseModel.setMessage("Department updated successfully.");
			responseModel.setStatusCode(HttpStatus.OK.toString());

		} catch (RecordNotFoundException e) {
			log.warn("Department not found: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
		} catch (DuplicateRecordFoundException e) {
			log.warn("Duplicate department found: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.CONFLICT.toString());
		} catch (IllegalArgumentException e) {
			log.warn("Validation failed: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
		} catch (Exception e) {
			log.error("Error occurred while updating department: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("An unexpected error occurred while updating the department.");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End DepartmentServiceImpl -> updateDepartment() method...");
		return responseModel;
	}

	@Override
	public ResponseModel<Department> deleteDepartment(UUID id) {
		log.info("Begin DepartmentServiceImpl -> deleteDepartment() method...");
		ResponseModel<Department> responseModel = new ResponseModel<>();

		try {

			if (!departmentRepository.existsById(id)) {
				throw new RecordNotFoundException("Department not found with ID: " + id);
			}
			departmentRepository.deleteById(id);
			responseModel.setMessage("Department deleted successfully for ID: " + id);
			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setData(null);
		} catch (RecordNotFoundException e) {
			log.info("Department not found: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
		} catch (Exception e) {
			log.info("Error occurred while deleting department: {}", e.getMessage());
			responseModel.setMessage("Failed to delete department");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End DepartmentServiceImpl -> deleteDepartment() method...");
		return responseModel;
	}

}

