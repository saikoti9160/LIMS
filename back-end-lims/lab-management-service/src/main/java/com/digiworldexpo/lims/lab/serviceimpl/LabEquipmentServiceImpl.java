package com.digiworldexpo.lims.lab.serviceimpl;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.digiworldexpo.lims.entities.lab_management.LabEquipment;
import com.digiworldexpo.lims.entities.lab_management.TestConfigurationMaster;
import com.digiworldexpo.lims.entities.lab_management.ReportParameter;
import com.digiworldexpo.lims.lab.dto.LabEquipmentDto;
import com.digiworldexpo.lims.lab.exception.RecordNotFoundException;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.repository.LabEquipmentRepository;
import com.digiworldexpo.lims.lab.repository.TestConfigurationRepository;
import com.digiworldexpo.lims.lab.repository.ReportParameterRepository;
import com.digiworldexpo.lims.lab.service.LabEquipmentService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LabEquipmentServiceImpl implements LabEquipmentService {

	private final LabEquipmentRepository equipmentRepository;

	private final TestConfigurationRepository configurationRepository;

	private final ReportParameterRepository parameterRepository;

	public LabEquipmentServiceImpl(LabEquipmentRepository equipmentRepository,
			TestConfigurationRepository configurationRepository, ReportParameterRepository parameterRepository) {
		super();
		this.equipmentRepository = equipmentRepository;
		this.configurationRepository = configurationRepository;
		this.parameterRepository = parameterRepository;
	}

	@Override
	public ResponseModel<LabEquipment> createEquipment(UUID userId, LabEquipment equipment) {
		log.info("Begin EquipmentService -> createEquipment() method...");

		ResponseModel<LabEquipment> responseModel = new ResponseModel<>();
		try {
		    log.debug("Processing associated tests for the equipment.");

		    // Process Tests associated with the equipment	
//		    if (equipment.getTests() != null && !equipment.getTests().isEmpty()) {
//		        log.debug("Found {} tests to process.", equipment.getTests());
//
//		        List<UUID> testIds = equipment.getTests().stream()
//		                .map(TestConfigurationMaster::getId)
//		                .filter(Objects::nonNull)
//		                .collect(Collectors.toList());

//		        if (!testIds.isEmpty()) {
//		            log.debug("Fetching test configurations for IDs: {}", testIds);
//		            Set<TestConfigurationMaster> testSet = new HashSet<>(configurationRepository.findAllById(testIds));
//		            testSet.forEach(test -> {
//		                if (test.getEquipment() == null) {
//		                    log.debug("Associating test with equipment.");
//		                    test.setEquipment(equipment);
//		                }
//		            });
//		            equipment.setTests(testSet);
//		        } else {
//		            log.warn("No valid test IDs found.");
//		        }
//		    }

		    log.debug("Processing associated test parameters for the equipment.");

		    // Process TestParameters associated with the equipment
//		    if (equipment.getTestParameters() != null && !equipment.getTestParameters().isEmpty()) {
//		        log.debug("Found {} test parameters to process.", equipment.getTestParameters().size());
//
//		        List<UUID> parameterIds = equipment.getTestParameters().stream()
//		                .map(TestParameter::getId)
//		                .filter(Objects::nonNull)
//		                .collect(Collectors.toList());
//
//		        if (!parameterIds.isEmpty()) {
//		            log.debug("Fetching test parameters for IDs: {}", parameterIds);
//		            Set<TestParameter> parameterSet = new HashSet<>(parameterRepository.findAllById(parameterIds));
//		            parameterSet.forEach(parameter -> {
//		                parameter.setCreatedBy(userId);
//		                parameter.setCreatedOn(new Timestamp(System.currentTimeMillis()));
////		                parameter.setEquipment(equipment);
//		                log.debug("Assigned parameters to the equipment.");
//		            });
//		            equipment.setTestParameters(parameterSet);
//		        } else {
//		            log.warn("No valid test parameter IDs found.");
//		        }
//		    }

		    // Set created by and created on for the equipment
		    equipment.setCreatedBy(userId);
		    equipment.setCreatedOn(new Timestamp(System.currentTimeMillis()));

		    log.debug("Saving the equipment to the repository.");
		    // Save the equipment
		    LabEquipment savedEquipment = equipmentRepository.save(equipment);

		    // Build success response
			responseModel.setData(savedEquipment);
			responseModel.setMessage("Equipment created successfully.");
			responseModel.setStatusCode(HttpStatus.CREATED.toString());
			log.info("Equipment created successfully with ID: {}", savedEquipment.getId());

		} catch (Exception e) {
			// Handle exceptions
			log.error("Error occurred while creating Equipment: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Failed to create Equipment: " + e.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End EquipmentService -> createEquipment() method...");
		return responseModel;
	}

	@Transactional
	@Override
	public ResponseModel<List<LabEquipmentDto>> getAllEquipment(UUID labId, String searchText, Integer pageSize,
			Integer pageNumber) {
		log.info("Begin EquipmentService -> getAllEquipment() method...");

		ResponseModel<List<LabEquipmentDto>> responseModel = new ResponseModel<>();
		try {
		    log.debug("Validating pagination parameters: pageNumber={} pageSize={}");
		    
		    if (pageNumber == null || pageNumber < 0 || pageSize == null || pageSize <= 0) {
		        log.error("Invalid pagination parameters: page number and size must be positive integers.");
		        throw new IllegalArgumentException("Page number and size must be positive integers.");
		    }

		    // Create pageable object with sorting by equipment name
		    PageRequest pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Direction.ASC, "equipmentName"));
		    log.debug("Created Pageable object: {}", pageable);

		    Page<LabEquipment> allEquipment;

		    // Search or fetch all equipment based on provided search text
		    if (searchText != null && !searchText.trim().isEmpty()) {
		        log.info("Searching for equipment with search text: {}", searchText);
		        allEquipment = equipmentRepository.findByLabIdAndNameContainingIgnoreCase(labId, searchText, pageable);
		    } else {
		        log.info("Fetching all equipment for labId: {}", labId);
		        allEquipment = equipmentRepository.findByLabId(labId, pageable);
		    }

		    // Check if no equipment found
		    if (allEquipment.isEmpty()) {
		        log.info("No equipment found in the database.");
		        responseModel.setData(null);
		        responseModel.setMessage("No equipment found");
		        responseModel.setStatusCode(HttpStatus.NO_CONTENT.toString());
		    } else {
		        // Convert entities to DTOs
		        log.debug("Converting LabEquipment entities to DTOs.");
		        List<LabEquipmentDto> equipmentDto = allEquipment.stream()
		                .map(entity -> convertEntityToDto(entity))
		                .collect(Collectors.toList());

		        responseModel.setData(equipmentDto);
		        responseModel.setMessage("Equipment retrieved successfully");
		        responseModel.setStatusCode(HttpStatus.OK.toString());
		        log.info("Successfully retrieved equipment.");		}
		} catch (Exception e) {
			log.error("Error occurred while fetching equipment: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Failed to fetch equipment");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End EquipmentService -> getAllEquipment() method...");
		return responseModel;
	}

	@Transactional
	@Override
	public ResponseModel<LabEquipmentDto> getEquipmentById(UUID id) {
		log.info("Begin EquipmentService -> getEquipmentById() method...");

		ResponseModel<LabEquipmentDto> responseModel = new ResponseModel<>();
		try {
		    // Validate the equipment ID
		    if (id == null) {
		        log.error("Equipment ID is null.");
		        throw new IllegalArgumentException("Equipment id cannot be null");
		    }

		    // Fetch equipment by ID
		    log.info("Fetching equipment with ID: {}", id);
		    LabEquipment equipmentResponse = equipmentRepository.findById(id)
		            .orElseThrow(() -> new RecordNotFoundException("Equipment not found with this id: " + id));

		    // Convert the fetched equipment entity to DTO
		    log.debug("Converting fetched equipment to DTO: {}");
		    LabEquipmentDto equipmentDto = convertEntityToDto(equipmentResponse);

		    // Set response details
		    responseModel.setData(equipmentDto);
		    responseModel.setMessage("Equipment retrieved successfully for ID: " + id);
		    responseModel.setStatusCode(HttpStatus.OK.toString());
		    log.info("Successfully retrieved equipment for ID: {}", id);
		    
		    responseModel.builder().data(equipmentDto).message("Equipment retrieved successfully for ID: " + id).statusCode(HttpStatus.OK.toString()).build();

		} catch (RecordNotFoundException e) {
			log.error("Record not found: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
		} catch (IllegalArgumentException e) {
			log.error("Invalid argument: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
		} catch (Exception e) {
			log.error("Error occurred while fetching sample: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Failed to retrieve sample due to an internal error.");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}
		log.info("End EquipmentService -> getEquipmentById() method...");
		return responseModel;
	}

	@Override
	@Transactional
	public ResponseModel<LabEquipmentDto> updateEquipment(UUID id, LabEquipmentDto equipmentRequestDto) {
		log.info("Begin EquipmentService -> updateEquipment() method...");

		ResponseModel<LabEquipmentDto> responseModel = new ResponseModel<>();
		try {
			if (!equipmentRepository.existsById(id)) {
				responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
				responseModel.setMessage("Equipment not found with ID: " + id);
				log.error("Equipment with ID {} not found", id);
			}
		    // Set the ID on the equipmentRequestDto before conversion
		    equipmentRequestDto.setId(id);
		    log.debug("Updating equipment with ID: {}", id);

			LabEquipment equipmentEntity = convertDtoToEntity(equipmentRequestDto);			
			LabEquipment updatedEquipment = equipmentRepository.save(equipmentEntity);

		    log.debug("Saved updated equipment: {}", updatedEquipment);

		    // Convert updated entity to DTO
		    LabEquipmentDto equipmentDto = convertEntityToDto(updatedEquipment);

		    // Set the response details
		    responseModel.setStatusCode(HttpStatus.OK.toString());
		    responseModel.setMessage("Equipment updated successfully.");
		    responseModel.setData(equipmentDto);

		    log.info("Successfully updated Equipment with ID {}", id);

		} catch (Exception e) {
			log.error("Error while updating Equipment: {}", e.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Failed to update Equipment: " + e.getMessage());
			responseModel.setData(null);
		}
		log.info("End EquipmentService -> updateEquipment() method...");
		return responseModel;
	}

	@Transactional
	@Override
	public ResponseModel<LabEquipment> deleteEquipment(UUID id) {
		log.info("Begin EquipmentService -> deleteEquipment() method...");

		ResponseModel<LabEquipment> responseModel = new ResponseModel<>();
		try {
			if (id == null) {
				responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
				responseModel.setMessage("equipment ID cannot be null.");
				responseModel.setData(null);
				log.error("equipment ID is null.");
			}


		        log.info("Deleting equipment with ID: {}", id);
		        LabEquipment equipment = equipmentRepository.findById(id).orElseThrow(()->new RecordNotFoundException("No record found with this ID: " + id));
		        equipment.setActive(false);
		        equipmentRepository.save(equipment);
		        log.info("Successfully deleted equipment with ID: {}", id);

		        responseModel.setData(equipment);
		        responseModel.setStatusCode(HttpStatus.OK.toString());
		        responseModel.setMessage("Equipment not found with ID: " + id);
		        log.error("Equipment with ID {} not found", id);
		    }
			catch (Exception e) {
			log.error("Error while deleting equipment with ID: ", id, e.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Failed to delete equipment: " + e.getMessage());
			responseModel.setData(null);
		}
		log.info("end EquipmentService -> deleteEquipment() method...");
		return responseModel;

	}

	private LabEquipment convertDtoToEntity(LabEquipmentDto equipmentRequestDto) {
	    LabEquipment equipment = new LabEquipment();
	    BeanUtils.copyProperties(equipmentRequestDto, equipment);

	    // Handle Test Configuration Master (Update only existing IDs)
//	    if (equipmentRequestDto.getTests() != null) {
//	    	System.out.println("lab equipment :"+equipmentRequestDto);
//	    	
//	        Set<TestConfigurationMaster> updatedTests = equipmentRequestDto.getTests().stream()
//	                .map(testDto -> configurationRepository.findById(testDto.getId()).orElse(null))
//	                .filter(Objects::nonNull)
//	                .peek(test -> test.setEquipment(equipment))  // Set parent reference
//	                .collect(Collectors.toSet());
//	        equipment.setTests(updatedTests);
//	    }
//
//	    if (equipmentRequestDto.getTestParameters() != null) {
//	        Set<TestParameter> updatedTestParameters = equipmentRequestDto.getTestParameters().stream()
//	                .map(testParamDto -> parameterRepository.findById(testParamDto.getId()).orElse(null))
//	                .filter(Objects::nonNull)
//	                .peek(testParam -> testParam.setEquipment(equipment))  // Set parent reference
//	                .collect(Collectors.toSet());
//	        equipment.setTestParameters(updatedTestParameters);
//	    }

	    return equipment;
	}

	private LabEquipmentDto convertEntityToDto(LabEquipment equipment) {
	    LabEquipmentDto equipmentDto = new LabEquipmentDto();
	    BeanUtils.copyProperties(equipment, equipmentDto, "tests", "testParameters");

	    // Convert Test Configuration Masters to DTOs
//	    if (equipment.getTests() != null && !equipment.getTests().isEmpty()) {
//	        Set<TestConfigurationMaster> testDtos = equipment.getTests().stream()
//	                .map(test -> {
//	                    TestConfigurationMaster testDto = new TestConfigurationMaster();
//	                    BeanUtils.copyProperties(test, testDto);
//	                    return testDto;
//	                })
//	                .collect(Collectors.toSet());
//	        equipmentDto.setTests(testDtos);
//	    } else {
//	        equipmentDto.setTests(Collections.emptySet());
//	    }
//
//	    // Convert Test Parameters to DTOs
//	    if (equipment.getTestParameters() != null && !equipment.getTestParameters().isEmpty()) {
//	        Set<TestParameter> testParameterDtos = equipment.getTestParameters().stream()
//	                .map(testParam -> {
//	                    TestParameter testParamDto = new TestParameter();
//	                    BeanUtils.copyProperties(testParam, testParamDto);
//	                    return testParamDto;
//	                })
//	                .collect(Collectors.toSet());
//	        equipmentDto.setTestParameters(testParameterDtos);
//	    } else {
//	        equipmentDto.setTestParameters(Collections.emptySet());
//	    }

	    return equipmentDto;
	}
}
