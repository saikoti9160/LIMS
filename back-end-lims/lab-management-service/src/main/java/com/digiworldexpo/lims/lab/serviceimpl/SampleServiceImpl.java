package com.digiworldexpo.lims.lab.serviceimpl;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.digiworldexpo.lims.entities.lab_management.SampleMaster;
import com.digiworldexpo.lims.lab.dto.SampleMasterRequestDto;
import com.digiworldexpo.lims.lab.exception.RecordNotFoundException;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.repository.LabRepository;
import com.digiworldexpo.lims.lab.repository.SampleMappingRepository;
import com.digiworldexpo.lims.lab.repository.SampleRepository;
import com.digiworldexpo.lims.lab.service.SampleService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SampleServiceImpl implements SampleService {

	private final SampleRepository sampleRepository;

	private final LabRepository labRepository;
	
	private final SampleMappingRepository mappingRepository;
	

	public SampleServiceImpl(SampleRepository sampleRepository, LabRepository labRepository,
			SampleMappingRepository mappingRepository) {
		super();
		this.sampleRepository = sampleRepository;
		this.labRepository = labRepository;
		this.mappingRepository = mappingRepository;
	}

	@Transactional
	@Override
	public ResponseModel<SampleMaster> createSample(UUID userId, SampleMaster sampleMaster) {
		log.info("Begin sample service -> createSample()......");
		ResponseModel<SampleMaster> responseModel = new ResponseModel<>();

		try {
			  // Check if a sample with the same name already exists
//	        Optional<SampleMaster> existingSample = sampleRepository.findBySampleName(sampleMaster.getSampleName());
//
//	        if (existingSample.isPresent()) {
//	            log.warn("Sample with name '{}' already exists", sampleMaster.getSampleName());
//	            responseModel.setData(null);
//	            responseModel.setMessage("Sample name already exists");
//	            responseModel.setStatusCode(HttpStatus.CONFLICT.toString()); // 409 Conflict
//	            return responseModel;
//	        }


			 log.info("Setting createdBy and createdOn fields for SampleMaster");
			    sampleMaster.setCreatedBy(userId);
			    sampleMaster.setCreatedOn(new Timestamp(System.currentTimeMillis()));

			    log.info("Saving sample to the database");
			    SampleMaster savedSample = sampleRepository.save(sampleMaster);

			    responseModel.setData(savedSample);
			    responseModel.setMessage("Sample  saved successfully");
			    responseModel.setStatusCode(HttpStatus.CREATED.toString());
			    log.info("Sample created successfully with ID: {}", savedSample.getId());

		} catch (Exception e) {
			log.error("Error occurred while creating sample: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Failed to create sample");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End sample service -> createSample().....");
		return responseModel;
	}

	@Transactional
	@Override
	public ResponseModel<List<SampleMasterRequestDto>> getAllSamples(UUID labId, Integer pageNumber, Integer pageSize,
			String searchText) {
		log.info("Begin sample service -> getAllSamples()......");

		ResponseModel<List<SampleMasterRequestDto>> responseModel = new ResponseModel<>();

		try {
			if (pageNumber == null || pageNumber < 0 || pageSize == null || pageSize <= 0) {
				throw new IllegalArgumentException("Page number and size must be positive integers.");
			}

			 log.info("Creating Pageable object with pageNumber: {} and pageSize: {}", pageNumber, pageSize);
			    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Direction.ASC, "sampleName"));

			    Page<SampleMaster> samplesPage;

			    // If search text is provided, search for samples by name
			    if (searchText != null && !searchText.isEmpty()) {
			        log.info("Searching samples by sampleName containing: {}", searchText);
			        samplesPage = sampleRepository.findSamplesWithSearchText(searchText, pageable);
			    } else {
			        log.info("Fetching all samples for labId: {} with pagination...", labId);
			        samplesPage = sampleRepository.findByLabId(labId, pageable);
			    }

			    // Map entities to DTOs
			    List<SampleMasterRequestDto> sampleList = samplesPage.getContent().stream().map(this::convertEntityToDto)
			            .collect(Collectors.toList());

			    log.info("Samples retrieved successfully. Total count: {}", samplesPage.getTotalElements());

			responseModel.setData(sampleList);
			responseModel.setMessage("Samples retrieved successfully.");
			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setTotalCount((int) samplesPage.getTotalElements());
			responseModel.setPageNumber(samplesPage.getNumber());
			responseModel.setPageSize(samplesPage.getSize());
			responseModel.setSortedBy("sampleName");

		} catch (IllegalArgumentException e) {
			log.error("Invalid input for pagination: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Invalid pagination parameters: " + e.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
		} catch (Exception e) {
			log.error("Error occurred while fetching samples: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Failed to fetch samples due to an internal error.");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End sample service -> getAllSamples() method...");
		return responseModel;
	}

	@Transactional
	@Override
	public ResponseModel<SampleMasterRequestDto> updateSamples(UUID id, SampleMasterRequestDto sampleMasterDto) {
		log.info("Begin SampleServiceImpl -> updateSamples() method...");
		ResponseModel<SampleMasterRequestDto> responseModel = new ResponseModel<>();

		try {
			// Validate input
			 if (sampleMasterDto == null) {
			        responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			        responseModel.setMessage("SampleMaster data cannot be null.");
			        responseModel.setData(null);
			        log.error("SampleMaster DTO is null.");
			     
			    }

			    // Check if the sample exists
			    if (!sampleRepository.existsById(id)) {
			        responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
			        responseModel.setMessage("Sample not found with ID: " + id);
			        responseModel.setData(null);
			        log.error("Sample with ID {} not found.", id);
			   
			    }

			    sampleMasterDto.setId(id);
			   sampleMasterDto.setModifiedBy(id);
			   sampleMasterDto.setModifiedOn(new Timestamp(System.currentTimeMillis()));

			    SampleMaster existingSample = convertDtoToEntity(sampleMasterDto);
			    
			    SampleMaster updatedSample = sampleRepository.save(existingSample);

			    SampleMasterRequestDto updatedSampleDto = convertEntityToDto(updatedSample);

			    // Prepare the response
			    responseModel.setStatusCode(HttpStatus.OK.toString());
			    responseModel.setMessage("Sample updated successfully.");
			    responseModel.setData(updatedSampleDto);

			    log.info("Successfully updated sample with ID {}", id);

		} catch (IllegalArgumentException e) {
			log.error("Validation error: {}", e.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage(e.getMessage());
			responseModel.setData(null);

		} catch (Exception e) {
			log.error("Error while updating sample: {}", e.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Failed to update sample: " + e.getMessage());
			responseModel.setData(null);
		}

		log.info("End SampleServiceImpl -> updateSamples() method...");
		return responseModel;
	}

	@Transactional
	@Override
	public ResponseModel<SampleMaster> deleteSample(UUID id) {
		log.info("Begin SampleServiceImpl -> deleteSample() method...");
		ResponseModel<SampleMaster> responseModel = new ResponseModel<>();

		try {

			if (id == null) {
		        responseModel.setMessage("Sample ID cannot be null.");
		        responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
		        responseModel.setData(null);
		        log.error("Sample ID is null.");
		    }

		
		    // Delete the sample
//		    SampleMaster sampleMaster = sampleRepository.findById(id).orElseThrow(()->new RecordNotFoundException("No record found with this ID: " + id));
//		    sampleMaster.setActive(false);
//		    sampleRepository.save(sampleMaster);
		    
//	        sampleMaster.getTestConfigurations().clear(); // Clear sample's test configurations
//	        sampleRepository.save(sampleMaster);  // Save changes to update the join table

		    SampleMaster sampleMaster = sampleRepository.findById(id)
	                .orElseThrow(() -> new RecordNotFoundException("No record found with this ID: " + id));

	        // Remove all relationships in the join table
	        sampleRepository.deleteSampleMappings(id);

	        // Now delete the sample
	        sampleRepository.delete(sampleMaster);

		    // Prepare response after successful deletion
		    responseModel.setMessage("Sample deleted successfully for ID: " + id);
		    responseModel.setStatusCode(HttpStatus.OK.toString());
		    responseModel.setData(sampleMaster);

		    log.info("Sample with ID {} deleted successfully.", id);

		} catch (IllegalArgumentException e) {
			log.error("Validation error: {}", e.getMessage());
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setData(null);

		} catch (Exception e) {
			log.error("Unexpected error occurred while deleting sample: {}", e.getMessage());
			responseModel.setMessage("Failed to delete sample due to an internal error.");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setData(null);
		}

		log.info("End SampleServiceImpl -> deleteSample() method...");
		return responseModel;
	}

	@Transactional
	@Override
	public ResponseModel<SampleMasterRequestDto> getSamplesById(UUID id) {
		log.info("Begin SampleServiceImpl -> getSamplesById() method...");
		ResponseModel<SampleMasterRequestDto> responseModel = new ResponseModel<>();

		try {
			if (id == null) {
		        responseModel.setMessage("Sample ID cannot be null.");
		        responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
		        responseModel.setData(null);
		        log.error("Sample ID is null.");
		        return responseModel;
		    }

		    // Retrieve sample by ID
		    SampleMaster sampleMaster = sampleRepository.findById(id)
		            .orElseThrow(() -> new RecordNotFoundException("No sample found with ID: " + id));

		    // Convert the entity to DTO
		    SampleMasterRequestDto sampleMasterDto = convertEntityToDto(sampleMaster);

		    // Prepare the successful response
		    responseModel.setData(sampleMasterDto);
		    responseModel.setMessage("Sample retrieved successfully for ID: " + id);
		    responseModel.setStatusCode(HttpStatus.OK.toString());

		    log.info("Sample retrieved successfully for ID: {}", id);

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
			log.error("Unexpected error occurred while fetching sample: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Failed to retrieve sample due to an internal error.");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End SampleServiceImpl -> getSamplesById() method...");
		return responseModel;
	}

	private SampleMaster convertDtoToEntity(SampleMasterRequestDto sampleMasterDto) {
		SampleMaster sampleMaster = new SampleMaster();
		BeanUtils.copyProperties(sampleMasterDto, sampleMaster);
		return sampleMaster;
	}

	private SampleMasterRequestDto convertEntityToDto(SampleMaster sampleMaster) {
		SampleMasterRequestDto sampleMasterRequestDto = new SampleMasterRequestDto();
		BeanUtils.copyProperties(sampleMaster, sampleMasterRequestDto);
		return sampleMasterRequestDto;
	}

	public ResponseModel<List<SampleMaster>> getSamplesBySampleName(String sampleName) {
	    List<SampleMaster> sampleMasters = sampleRepository.findBySampleName(sampleName);
	    ResponseModel<List<SampleMaster>> responseModel = new ResponseModel<>();

	    if (sampleMasters.isEmpty()) {
	        responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
	        responseModel.setMessage("No samples found.");
	    } else {
	        responseModel.setStatusCode(HttpStatus.OK.toString());
	        responseModel.setMessage("Samples found.");
	        responseModel.setData(sampleMasters);
	    }

	    return responseModel;
	}


}
