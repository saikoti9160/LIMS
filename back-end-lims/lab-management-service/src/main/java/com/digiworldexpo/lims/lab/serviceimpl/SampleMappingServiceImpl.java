package com.digiworldexpo.lims.lab.serviceimpl;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.digiworldexpo.lims.entities.lab_management.SampleMapping;
import com.digiworldexpo.lims.entities.lab_management.SampleMaster;
import com.digiworldexpo.lims.lab.exception.RecordNotFoundException;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.repository.SampleMappingRepository;
import com.digiworldexpo.lims.lab.repository.SampleRepository;
import com.digiworldexpo.lims.lab.service.SampleMappingService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SampleMappingServiceImpl implements SampleMappingService {

	private final SampleMappingRepository sampleMappingRepository;

	private final SampleRepository sampleRepository;

	public SampleMappingServiceImpl(SampleMappingRepository sampleMappingRepository,
			SampleRepository sampleRepository) {
		super();
		this.sampleMappingRepository = sampleMappingRepository;
		this.sampleRepository = sampleRepository;
	}

	@Transactional
	@Override
	public ResponseModel<SampleMapping> saveSampleMapping(UUID userId, SampleMapping sampleMapping) {
		log.info("Begin SampleMappingServiceImpl -> createSampleMap() method...");
		ResponseModel<SampleMapping> responseModel = new ResponseModel<>();
		try {
			
			if (sampleMapping.getSampleMasters() == null || sampleMapping.getSampleMasters().isEmpty()) {
	            log.error("At least one SampleMaster must be associated with the SampleMapping.");
	            throw new IllegalArgumentException("At least one SampleMaster must be provided.");
	        }

	        // Fetch and validate SampleMasters
	        Set<SampleMaster> validSampleMasters = sampleMapping.getSampleMasters().stream()
	                .map(sampleMaster -> sampleRepository.findById(sampleMaster.getId())
	                        .orElseThrow(() -> new RecordNotFoundException(
	                                "No record found for SampleMaster ID: " + sampleMaster.getId())))
	                .collect(Collectors.toSet());

	        log.info("All SampleMaster IDs validated successfully.");

	        sampleMapping.setSampleMasters(validSampleMasters);

	        // Set audit fields
	        sampleMapping.setCreatedOn(new Timestamp(System.currentTimeMillis()));
	        sampleMapping.setCreatedBy(userId);

	        // Save the SampleMapping entity
	        SampleMapping savedSampleMapping = sampleMappingRepository.save(sampleMapping);
	        log.info("SampleMapping saved successfully with ID: {}", savedSampleMapping.getId());

		        // Prepare the response
		        responseModel.setData(savedSampleMapping);
		        responseModel.setMessage("Sample Mapping saved successfully");
		        responseModel.setStatusCode(HttpStatus.CREATED.toString());
		        
		} catch (Exception e) {
			log.error("Error occurred while creating SampleMapping: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Failed to create SampleMapping due to an internal error.");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}
		log.info("End SampleMappingServiceImpl -> createSampleMap() method...");
		return responseModel;
	}

	@Transactional
	@Override
	public ResponseModel<List<SampleMapping>> getAllSampleMapping(UUID labId, Integer pageNumber, Integer pageSize,
			String searchText) {
		log.info("Begin SampleMappingServiceImpl -> getAllSampleMapping() method...");
		ResponseModel<List<SampleMapping>> responseModel = new ResponseModel<>();
		try {
			 if (pageNumber == null || pageNumber < 0 || pageSize == null || pageSize <= 0) {
		            log.error("Invalid pagination parameters: pageNumber = {}, pageSize = {}", pageNumber, pageSize);
		            throw new IllegalArgumentException("Page number and page size must be positive.");
		        }

		        log.info("Fetching SampleMappings for labId: {} with pageNumber: {}, pageSize: {}", labId, pageNumber, pageSize);

		        // Prepare pageable request
		        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Direction.ASC, "testName"));

		        // Fetch SampleMappings based on searchText
		        Page<SampleMapping> sampleMappingPage;
		        if (searchText != null && !searchText.trim().isEmpty()) {
		            log.info("Searching SampleMappings by testName containing: {}", searchText);
		            sampleMappingPage = sampleMappingRepository.findByTestName(searchText, pageable);

		            if (!sampleMappingPage.hasContent()) {
		                log.info("No matches found in testName, searching by sampleMaster.sampleName: {}", searchText);
		                sampleMappingPage = sampleMappingRepository.findBySampleName(searchText, pageable);
		            }
		        } else {
		            log.info("Fetching SampleMappings for labId: {} with pagination", labId);
		            sampleMappingPage = sampleMappingRepository.findByLabId(labId, pageable);
		        }

		        // Set response data
		        responseModel.setData(sampleMappingPage.getContent());
		        responseModel.setMessage(sampleMappingPage.hasContent() ? "SampleMappings fetched successfully." : "No SampleMappings found.");
		        responseModel.setStatusCode(sampleMappingPage.hasContent() ? HttpStatus.OK.toString() : HttpStatus.NO_CONTENT.toString());
		        responseModel.setTotalCount((int) sampleMappingPage.getTotalElements());
		        responseModel.setPageNumber(sampleMappingPage.getNumber());
		        responseModel.setPageSize(sampleMappingPage.getSize());

		        log.info("SampleMappings fetch completed. Total records: {}", sampleMappingPage.getTotalElements());
		    } catch (IllegalArgumentException e) {
		        log.error("Invalid input: {}", e.getMessage());
		        responseModel.setData(null);
		        responseModel.setMessage(e.getMessage());
		        responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
		    } catch (Exception e) {
		        log.error("Error occurred while fetching SampleMappings: {}", e.getMessage());
		        responseModel.setData(null);
		        responseModel.setMessage("Failed to retrieve SampleMappings due to an internal error.");
		        responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		    }

		    log.info("End SampleMappingServiceImpl -> getAllSampleMapping() method...");
		    return responseModel;
	}

	@Transactional
	@Override
	public ResponseModel<SampleMapping> getByIdSampleMapping(UUID id) {
		log.info("Begin SampleMappingServiceImpl -> getByIdSampleMapping().....");
		ResponseModel<SampleMapping> responseModel = new ResponseModel<>();
		try {
			if (id == null) {
				log.error("SampleMapping ID cannot be null.");
				responseModel.setData(null);
				responseModel.setMessage("SampleMapping ID cannot be null.");
				responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			}

			log.info("Fetching SampleMapping with ID: {}", id);

			// Retrieve SampleMapping by ID
			SampleMapping sampleMapping = sampleMappingRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("SampleMapping not found with ID: " + id));

			// Set the response model
			responseModel.setData(sampleMapping);
			responseModel.setMessage("SampleMapping retrieved successfully.");
			responseModel.setStatusCode(HttpStatus.OK.toString());
			log.info("SampleMapping retrieved successfully for ID: {}", id);
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
			log.error("Error occurred while fetching test configuration: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Failed to retrieve test configuration due to an internal error.");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}
		log.info("end SampleMappingServiceImpl -> getByIdSampleMapping().....");
		return responseModel;
	}

	@Transactional
	@Override
	public ResponseModel<SampleMapping> updateSampleMapping(UUID id,SampleMapping sampleMapping) {
		log.info("Begin SampleMappingServiceImpl -> updateSampleMapping() method with sampleMappingId: {}",
				sampleMapping.getId());
		ResponseModel<SampleMapping> responseModel = new ResponseModel<>();

		try {
			if (id == null) {
	            log.error("SampleMapping ID cannot be null.");
	            throw new IllegalArgumentException("SampleMapping ID cannot be null.");
	        }

	        // Fetch existing SampleMapping by ID
	        SampleMapping existingSampleMapping = sampleMappingRepository.findById(id)
	                .orElseThrow(() -> new RecordNotFoundException("SampleMapping not found with ID: " + id));

	        log.info("SampleMapping found with ID: {}", id);

	        // Update testName and labId
	        existingSampleMapping.setTestName(sampleMapping.getTestName());
	        existingSampleMapping.setLabId(sampleMapping.getLabId());
	        existingSampleMapping.setSampleTypes(sampleMapping.getSampleTypes());

	        // Update SampleMaster relationships
	        if (sampleMapping.getSampleMasters() != null && !sampleMapping.getSampleMasters().isEmpty()) {
	            log.info("Updating SampleMasters for SampleMapping ID: {}", id);

	            Set<SampleMaster> updatedSampleMasters = new HashSet<>();

	            for (SampleMaster sampleMaster : sampleMapping.getSampleMasters()) {
	                UUID sampleMasterId = sampleMaster.getId();
	                SampleMaster existingSampleMaster = sampleRepository.findById(sampleMasterId)
	                        .orElseThrow(() -> new RecordNotFoundException("SampleMaster not found with ID: " + sampleMasterId));
	                updatedSampleMasters.add(existingSampleMaster);
	            }

	            existingSampleMapping.setSampleMasters(updatedSampleMasters);
	        }

	        log.info("Saving updated SampleMapping with ID: {}", id);

	        SampleMapping updatedSampleMapping = sampleMappingRepository.save(existingSampleMapping);


	        responseModel.setData(updatedSampleMapping);
		        responseModel.setMessage("SampleMapping updated successfully.");
		        responseModel.setStatusCode(HttpStatus.OK.toString());

		        log.info("SampleMapping updated successfully with ID: {}", id);
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
			log.error("Error occurred while updating test configuration: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Failed to update test configuration due to an internal error.");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}
		log.info("End SampleMappingServiceImpl -> updateSampleMapping() method.!");
		return responseModel;

	}

	@Transactional
	@Override
	public ResponseModel<SampleMapping> deleteSampleMapping(UUID id) {
		log.info("Begin SampleMappingServiceImpl -> deleteSampleMapping() method.!");
		ResponseModel<SampleMapping> responseModel = new ResponseModel<>();

		try {
			 if (id == null) {
		            log.error("SampleMapping ID cannot be null.");
		            throw new IllegalArgumentException("SampleMapping ID cannot be null.");
		        }

		        // Fetch SampleMapping entity
		        SampleMapping sampleMapping = sampleMappingRepository.findById(id)
		                .orElseThrow(() -> new RecordNotFoundException("SampleMapping not found with ID: " + id));

		        log.info("SampleMapping found. Proceeding with deletion for ID: {}", id);

		        // Remove associations from SampleMasters
		        if (sampleMapping.getSampleMasters() != null && !sampleMapping.getSampleMasters().isEmpty()) {
		            for (SampleMaster sampleMaster : sampleMapping.getSampleMasters()) {
		                sampleMaster.getSampleMappings().remove(sampleMapping); // Break bidirectional relationship
		            }
		            sampleMapping.getSampleMasters().clear(); // Remove all associations
		        }

		        // Now, safely delete the SampleMapping entity
		        sampleMappingRepository.delete(sampleMapping);

			
//		        sampleMapping.setActive(false);
//				sampleMappingRepository.save(sampleMapping);

			responseModel.setData(sampleMapping);
			responseModel.setMessage("Sample deleted successfully for ID: " + id);
			responseModel.setStatusCode(HttpStatus.OK.toString());

			log.info("SampleMapping deleted successfully with ID: {}", id);
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
			log.error("Error occurred while deleting samplemapping object: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Failed to delete sample due to an internal error.");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}
		log.info("End SampleMappingServiceImpl -> deleteSampleMapping() method.!");
		return responseModel;
	}

}