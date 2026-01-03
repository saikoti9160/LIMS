package com.digiworldexpo.lims.lab.serviceimpl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.digiworldexpo.lims.entities.lab_management.Lab;
import com.digiworldexpo.lims.entities.lab_management.LabDepartment;
import com.digiworldexpo.lims.lab.exception.RecordNotFoundException;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.repository.DepartmentRepository;
import com.digiworldexpo.lims.lab.repository.LabRepository;
import com.digiworldexpo.lims.lab.request.DepartmentRequestDTO;
import com.digiworldexpo.lims.lab.response.DepartmentMasterSearchResponse;
import com.digiworldexpo.lims.lab.response.DepartmentResponseDTO;
import com.digiworldexpo.lims.lab.service.DepartmentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DepartmentServiceImpl implements DepartmentService {
	
	private final DepartmentRepository departmentRepository;
	private final LabRepository labRepository;

   

    public DepartmentServiceImpl(DepartmentRepository departmentRepository, LabRepository labRepository) {
		super();
		this.departmentRepository = departmentRepository;
		this.labRepository = labRepository;
	}

    @Override
    public ResponseModel<DepartmentResponseDTO> saveDepartment(UUID createdBy, DepartmentRequestDTO departmentRequestDTO) {
        log.info("Begin DepartmentServiceImpl -> saveDepartment() method");

        ResponseModel<DepartmentResponseDTO> response = new ResponseModel<>();
        try {
            LabDepartment department = convertToDepartmentEntity(departmentRequestDTO);
            department.setCreatedBy(createdBy);

            Lab lab = labRepository.findById(departmentRequestDTO.getLabId())
                    .orElseThrow(() -> new RecordNotFoundException(" Lab ID not found"));

            department.setLab(lab);

            LabDepartment savedDepartment = departmentRepository.saveAndFlush(department);

            DepartmentResponseDTO savedDepartmentResponseDTO = convertToDepartmentResponseDTO(savedDepartment);

            savedDepartmentResponseDTO.setLabId(savedDepartment.getLab().getId());

            response.setStatusCode(HttpStatus.OK.toString());
            response.setMessage("Department saved successfully.");
            response.setData(savedDepartmentResponseDTO);
            log.info("Department saved with ID {}", savedDepartment.getId());
        } catch (Exception e) {
            log.error("Error while saving department: {}", e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.toString());
            response.setMessage("Failed to save department: " + e.getMessage());
            response.setData(null);
        }

        log.info("End DepartmentServiceImpl -> saveDepartment() method");
        return response;
    }

	private LabDepartment convertToDepartmentEntity(DepartmentRequestDTO departmentRequestDTO) {
		LabDepartment department = new LabDepartment();
		BeanUtils.copyProperties(departmentRequestDTO, department);
		return department;
	}

	private DepartmentResponseDTO convertToDepartmentResponseDTO(LabDepartment department) {
		DepartmentResponseDTO departmentResponseDTO = new DepartmentResponseDTO();
		BeanUtils.copyProperties(department, departmentResponseDTO);
		return departmentResponseDTO;
	}

	@Override
	public ResponseModel<List<DepartmentMasterSearchResponse>> getAllDepartments(
	        UUID createdBy, String searchTerm, Boolean flag, Integer page, Integer size) {

	    log.info("Begin DepartmentServiceImpl -> getAllDepartments()");
	    ResponseModel<List<DepartmentMasterSearchResponse>> response = new ResponseModel<>();

	    try {
	        List<LabDepartment> departmentList;

	        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
	            departmentList = departmentRepository.findByNameAndCreatedByAndActive(searchTerm, createdBy, flag);
	        } else {
	            departmentList = departmentRepository.findAllByCreatedByAndActive(createdBy, true);
	        }

	        List<DepartmentMasterSearchResponse> dtoList = departmentList.stream()
	                .map(department -> new DepartmentMasterSearchResponse(
	                        department.getId(),
	                        department.getDepartmentName() != null ? department.getDepartmentName() : "N/A"))
	                .collect(Collectors.toList());

	        int start = page * size;
	        int end = Math.min(start + size, dtoList.size());
	        List<DepartmentMasterSearchResponse> paginatedList = dtoList.subList(start, end);

	        response.setData(paginatedList);
	        response.setTotalCount(departmentList.size());
	        response.setPageNumber(page);
	        response.setPageSize(size);
	        response.setStatusCode(HttpStatus.OK.toString());
	        response.setMessage("Departments fetched successfully.");

	        log.info("Successfully fetched {} departments.", paginatedList.size());
	    } catch (IllegalArgumentException e) {
	        log.error("Invalid pagination parameters: {}", e.getMessage());
	        response.setData(null);
	        response.setMessage(e.getMessage());
	        response.setStatusCode(HttpStatus.BAD_REQUEST.toString());
	    } catch (Exception e) {
	        log.error("Error occurred while fetching departments: {}", e.getMessage());
	        response.setData(null);
	        response.setMessage("Failed to fetch departments.");
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	    }

	    log.info("End DepartmentServiceImpl -> getAllDepartments()");
	    return response;
	}


	@Override
	public ResponseModel<DepartmentResponseDTO> getDepartmentById(UUID id) {
		log.info("Begin DepartmentServiceImpl -> getDepartmentById() method");

		ResponseModel<DepartmentResponseDTO> response = new ResponseModel<>();
		try {
			LabDepartment department = departmentRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("Department not found with ID: " + id));

			DepartmentResponseDTO departmentResponseDTO = convertToDepartmentResponseDTO(department);

			response.setStatusCode(HttpStatus.OK.toString());
			response.setMessage("Department fetched successfully.");
			response.setData(departmentResponseDTO);

		} catch (RecordNotFoundException e) {
			response.setStatusCode(HttpStatus.NOT_FOUND.toString());
			response.setMessage(e.getMessage());
			response.setData(null);
			log.error("Department not found: {}", e.getMessage());

		} catch (Exception e) {
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			response.setMessage("Failed to fetch department: " + e.getMessage());
			response.setData(null);
			log.error("Error while fetching department: {}", e.getMessage());
		}

		log.info("End DepartmentServiceImpl -> getDepartmentById() method");
		return response;
	}
    
	@Override
	public ResponseModel<DepartmentResponseDTO> updateDepartment(UUID id, DepartmentRequestDTO departmentDTO) {
		log.info("Begin DepartmentServiceImpl -> updateDepartment() method");

		ResponseModel<DepartmentResponseDTO> response = new ResponseModel<>();
		try {
			LabDepartment existingDepartment = departmentRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("Department not found with ID: " + id));

			LabDepartment department = convertToDepartmentEntity(departmentDTO);

			department.setId(id);

			department.setLab(existingDepartment.getLab());
			department.setCreatedBy(existingDepartment.getCreatedBy());

			LabDepartment updatedDepartment = departmentRepository.save(department);

			DepartmentResponseDTO updatedDepartmentResponseDTO = convertToDepartmentResponseDTO(updatedDepartment);

			response.setStatusCode(HttpStatus.OK.toString());
			response.setMessage("Department updated successfully.");
			response.setData(updatedDepartmentResponseDTO);
			log.info("Successfully updated department with ID {}", id);

		} catch (RecordNotFoundException e) {
			log.error("Department not found: {}", e.getMessage());
			response.setStatusCode(HttpStatus.NOT_FOUND.toString());
			response.setMessage(e.getMessage());
			response.setData(null);
		} catch (Exception e) {
			log.error("Error while updating department: {}", e.getMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			response.setMessage("Failed to update department: " + e.getMessage());
			response.setData(null);
		}

		log.info("End DepartmentServiceImpl -> updateDepartment() method");
		return response;
	}

	@Override
	public ResponseModel<DepartmentResponseDTO> deleteDepartmentById(UUID id) {
		log.info("Begin DepartmentServiceImpl -> deleteDepartmentById() method");

		ResponseModel<DepartmentResponseDTO> response = new ResponseModel<>();
		try {
			LabDepartment department = departmentRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("Department not found with ID: " + id));

			departmentRepository.deleteById(id);

			DepartmentResponseDTO departmentResponseDTO = convertToDepartmentResponseDTO(department);

			response.setStatusCode(HttpStatus.NO_CONTENT.toString());
			response.setMessage("Department deleted successfully with ID: " + id);
			response.setData(departmentResponseDTO);
			log.info("Successfully deleted department with ID: {}", id);
		} catch (RecordNotFoundException e) {
			log.error("Department deletion failed: {}", e.getMessage());
			response.setStatusCode(HttpStatus.NOT_FOUND.toString());
			response.setMessage(e.getMessage());
			response.setData(null);
		} catch (Exception e) {
			log.error("Error while deleting department with ID {}: {}", id, e.getMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			response.setMessage("Failed to delete department: " + e.getMessage());
			response.setData(null);
		}

		log.info("End DepartmentServiceImpl -> deleteDepartmentById() method");
		return response;
	}

}
