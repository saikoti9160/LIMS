package com.digiworldexpo.lims.lab.serviceimpl;


import java.sql.Timestamp;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.digiworldexpo.lims.entities.lab_management.Lab;
import com.digiworldexpo.lims.entities.lab_management.LabDepartment;
import com.digiworldexpo.lims.entities.lab_management.TestConfigurationMaster;
import com.digiworldexpo.lims.lab.dto.LabDepartmentDto;
import com.digiworldexpo.lims.lab.dto.LabDto;
import com.digiworldexpo.lims.lab.exception.RecordNotFoundException;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.repository.LabDepertmentRepo;
import com.digiworldexpo.lims.lab.repository.LabRepository;
import com.digiworldexpo.lims.lab.repository.TestConfigurationRepository;
import com.digiworldexpo.lims.lab.service.LabDepartmentService;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class LabDepartmentServiceImpl implements LabDepartmentService {

	private final LabDepertmentRepo departmentRepository;
	
	private final LabRepository labRepository;

	private final TestConfigurationRepository configurationRepository;

	

	public LabDepartmentServiceImpl(LabDepertmentRepo departmentRepository, LabRepository labRepository,
			TestConfigurationRepository configurationRepository) {
		super();
		this.departmentRepository = departmentRepository;
		this.labRepository = labRepository;
		this.configurationRepository = configurationRepository;
	}

	@Override
    public ResponseModel<LabDepartment> saveLabDepartment(UUID userId,LabDepartment department) {
        log.info("Begin DepartmentServiceImpl -> saveLabDepartment() method");
   
		ResponseModel<LabDepartment> response = new ResponseModel<>();

		try {
		    department.setCreatedBy(userId);
		    department.setCreatedOn(new Timestamp(System.currentTimeMillis()));

		    log.debug("Saving department details to the repository.");
		    LabDepartment savedDepartment = departmentRepository.saveAndFlush(department);

		    log.debug("Department saved with ID={}", savedDepartment.getId());

		    if (department.getTestConfigurations() != null && !department.getTestConfigurations().isEmpty()) {
		        log.debug("Test configurations found. Associating test configurations with saved department.");
		        for (TestConfigurationMaster testConfig : department.getTestConfigurations()) {
		            testConfig.setLabDepartment(savedDepartment);
		        }

		        log.debug("Saving associated test configurations.");
		        configurationRepository.saveAll(department.getTestConfigurations());
		    }
		    log.info("Department saved with ID {}", savedDepartment.getId());
		    response.setStatusCode(HttpStatus.CREATED.toString());
            response.setMessage("Lab department successfully saved..!");
            response.setData(savedDepartment);
   
		    } catch (Exception e) {
            log.error("Error while saving department: {}", e.getMessage());
            response.setStatusCode(HttpStatus.BAD_REQUEST.toString());
            response.setMessage("Failed to save department: " + e.getMessage());
            response.setData(null);
        }
        return response;
    }

    // Method to convert DepartmentDTO to Department entity
    private LabDepartment convertToDepartmentEntity(LabDepartmentDto departmentDto) {
        LabDepartment department = new LabDepartment();
        BeanUtils.copyProperties(departmentDto, department);
        if (departmentDto.getLabId() != null && departmentDto.getLabId() != null) {
            Lab lab = new Lab();
            BeanUtils.copyProperties(departmentDto.getLabId(), lab);
            department.setLab(lab);
        }
        return department;
    }

    // Method to convert Department entity to DepartmentDTO
    private LabDepartmentDto convertToDepartmentDto(LabDepartment department) {
    	LabDepartmentDto departmentDto = new LabDepartmentDto();
        BeanUtils.copyProperties(department, departmentDto);

        if (department.getLab() != null) {
            Lab lab = department.getLab();
            LabDto labDto = new LabDto();
            BeanUtils.copyProperties(lab, labDto);
            departmentDto.setLabId(labDto.getId()); 
        }

        return departmentDto;
    }


	    @Override
	    public ResponseModel<Page<LabDepartmentDto>> getAllLabDepartments(String searchTerm, Integer page, Integer size) {
	    	log.info("Begin DepartmentServiceImpl -> getAllLabDepartments() method with search and pagination");

	    	ResponseModel<Page<LabDepartmentDto>> response = new ResponseModel<>();

	    	try {
	    	    log.debug("Validating pagination parameters: page={}, size={}", page, size);
	    	    if (page == null || size == null || page < 0 || size < 1) {
	    	        log.warn("Invalid pagination parameters: page must be >= 0 and size must be > 0.");
	    	        throw new IllegalArgumentException("Invalid pagination parameters: page must be >= 0 and size must be > 0.");
	    	    }

	    	    log.debug("Creating pageable with page={} and size={}", page, size);
	    	    Pageable pageable = PageRequest.of(page, size);
	    	    Page<LabDepartment> departments;

	    	    if (searchTerm != null && !searchTerm.trim().isEmpty()) {
	    	        log.info("Searching departments with searchTerm: {}", searchTerm);
	    	        departments = departmentRepository.findByName(searchTerm, pageable);
	    	    } else {
	    	        log.info("Fetching all departments without search filters.");
	    	        departments = departmentRepository.findAll(pageable);
	    	    }

	    	    log.debug("Converting departments to Department DTOs.");
	    	    Page<LabDepartmentDto> departmentDTOs = departments.map(this::convertToDepartmentDto);

	    	    log.info("Successfully retrieved {} department(s) with searchTerm: {}");
	            response.setData(departmentDTOs);
	            response.setMessage("Fetched departments successfully.");
	            response.setStatusCode(HttpStatus.OK.toString());
	            response.setTotalCount((int) departments.getTotalElements());
	            response.setPageNumber(page);
	            response.setPageSize(size);
	
	            log.info("Successfully retrieved {} Lab departments on page {} with page size {}.", departmentDTOs.getNumberOfElements(), page, size);
	        } catch (IllegalArgumentException e) {
	            response.setData(null);
	            response.setMessage(e.getMessage());
	            response.setStatusCode(HttpStatus.BAD_REQUEST.toString());
	            log.warn("Invalid pagination parameters: {}", e.getMessage());
	        } catch (Exception e) {
	            response.setData(null);
	            response.setMessage("Failed to fetch departments.");
	            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	            log.error("Error occurred while fetching departments: {}", e.getMessage());
	        }
	
	        log.info("End DepartmentServiceImpl -> getAllLabDepartments() method with search and pagination");
	        return response;
	    }
	

    @Override
    public ResponseModel<LabDepartmentDto> getLabDepartmentById(UUID id) {
        log.info("Begin DepartmentServiceImpl -> getDepartmentById() method");

    	ResponseModel<LabDepartmentDto> response = new ResponseModel<>();

    	try {
    	    log.debug("Attempting to fetch department with ID={}", id);
    	    // Fetch the department entity
    	    LabDepartment department = departmentRepository.findById(id)
    	            .orElseThrow(() -> new RecordNotFoundException("Department not found with ID: " + id));

    	    log.info("Department with ID={} found successfully.", id);

    	    log.debug("Converting department entity with ID={} to Department DTO.", id);
    	    // Convert the department entity to DepartmentDTO
    	    LabDepartmentDto departmentDTO = convertToDepartmentDto(department);

    	    log.info("Department DTO created successfully for department ID={}", id);

    	    response.setStatusCode(HttpStatus.OK.toString());
            response.setMessage("Department fetched successfully.");
            response.setData(departmentDTO);

        } catch (RecordNotFoundException e) {
        	log.error("Department not found: {}", e.getMessage());
            response.setStatusCode(HttpStatus.NOT_FOUND.toString());
            response.setMessage(e.getMessage());
            response.setData(null);

        } catch (Exception e) {
        	log.error("Error while fetching department: {}", e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.setMessage("Failed to fetch department: " + e.getMessage());
            response.setData(null);
        }

        log.info("End DepartmentServiceImpl -> getDepartmentById() method");
        return response;
    }

    @Override
    public ResponseModel<LabDepartmentDto> updateLabDepartment(UUID id, LabDepartmentDto departmentDto) {
        log.info("Begin DepartmentServiceImpl -> updateLabDepartment() method");

        ResponseModel<LabDepartmentDto> response = new ResponseModel<>();
        try {
        	log.debug("Checking if the departmentDto object is null.");
            if (departmentDto == null) {
                response.setStatusCode(HttpStatus.BAD_REQUEST.toString());
                response.setMessage("Department data cannot be null.");
                response.setData(null);
                log.error("DepartmentDTO object is null.");
                return response;
            }

            // Check if department exists
            log.debug("Checking if department with ID={} exists.", id);
            if (!departmentRepository.existsById(id)) {
                response.setStatusCode(HttpStatus.NOT_FOUND.toString());
                response.setMessage("Department not found with ID: " + id);
                response.setData(null);
                log.error("Department with ID {} not found", id);
                return response;
            }

            // Convert DepartmentDTO to Department entity
            log.debug("Converting DepartmentDTO to Department entity.");
            LabDepartment department = convertToDepartmentEntity(departmentDto);

            // Set the department ID to ensure we're updating the correct department
            
            department.setId(id);
            log.debug("Assigned department ID={} to the department entity for update.", id);

            // Save the updated department
            log.debug("Saving the updated department.");
            LabDepartment updatedDepartment = departmentRepository.save(department);

            // Convert the updated department to DepartmentDTO
            LabDepartmentDto updatedDepartmentDTO = convertToDepartmentDto(updatedDepartment);

            log.info("Successfully updated department with ID {}", id);
            response.setStatusCode(HttpStatus.OK.toString());
            response.setMessage("Department updated successfully.");
            response.setData(updatedDepartmentDTO);

        } catch (Exception e) {
            log.error("Error while updating department: {}", e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.setMessage("Failed to update department: " + e.getMessage());
            response.setData(null);
        }

        log.info("End DepartmentServiceImpl -> updateLabDepartment() method");
        return response;
    }

    @Override
    public ResponseModel<LabDepartment> deleteLabDepartmentById(UUID id) {
        log.info("Begin DepartmentServiceImpl -> deleteLabDepartmentById() method");


    	ResponseModel<LabDepartment> response = new ResponseModel<>();

    	try {
    	    log.debug("Checking if the department ID is null.");
    	    if (id == null) {
    	        response.setStatusCode(HttpStatus.BAD_REQUEST.toString());
    	        response.setMessage("Department ID cannot be null.");
    	        response.setData(null);
    	        log.error("Department ID is null.");
    	       
    	    }

    	    log.debug("Checking if department with ID={} exists.", id);
    	    if (departmentRepository.existsById(id)) {
    	       
    	    	LabDepartment department = departmentRepository.findById(id).orElseThrow(()->new RecordNotFoundException("No record found with this ID: " + id));
    	    	department.setActive(false);
    	    	departmentRepository.save(department);
    	    	
    	        response.setStatusCode(HttpStatus.OK.toString());
    	        response.setMessage("Department deleted successfully.");
    	        response.setData(department);
    	        log.info("Successfully deleted department with ID={}", id);

    	    } else {
    	        log.error("Department with ID={} not found.", id);
                response.setStatusCode(HttpStatus.NOT_FOUND.toString());
                response.setMessage("Department not found with ID: " + id);
                log.error("Department with ID {} not found", id);
            }
        } catch (Exception e) {
            log.error("Error while deleting department with ID {}: {}", id, e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.setMessage("Failed to delete department: " + e.getMessage());
            response.setData(null);
        }

        log.info("End DepartmentServiceImpl -> deleteLabDepartmentById() method");
        return response;
    }


}
