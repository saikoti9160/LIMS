package com.digiworldexpo.lims.lab.serviceimpl;


import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.digiworldexpo.lims.entities.lab_management.Rack;
import com.digiworldexpo.lims.lab.dto.RackRequestDto;
import com.digiworldexpo.lims.lab.exception.RecordNotFoundException;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.repository.LabRepository;
import com.digiworldexpo.lims.lab.repository.RackRepository;
import com.digiworldexpo.lims.lab.service.RackService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RackServiceImpl implements RackService {


	private final RackRepository rackRepository;

		private final LabRepository labRepository; 
		
	  
	public RackServiceImpl(RackRepository rackRepository, LabRepository labRepository) {
			super();
			this.rackRepository = rackRepository;
			this.labRepository = labRepository;
		}

	@Transactional
	@Override
	public ResponseModel<Rack> createRack(UUID userId,Rack rackRequest) {
		log.info("Begin RackServiceImpl -> createRack() method ");
		ResponseModel<Rack> responseModel = new ResponseModel<>();
		try {
			
		    log.debug("Received rack creation request: {}", rackRequest);

		    Rack savedRack = rackRepository.save(rackRequest);

		    log.info("Rack created successfully with ID: {}", savedRack.getId());

		    // Set the response details
		    responseModel.setData(savedRack);
			responseModel.setMessage("Rack has created succesfully..!");
			responseModel.setStatusCode(HttpStatus.OK.toString());
		} catch (Exception e) {
			log.error("Error occurred while creating sample: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Failed to create sample");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}
		log.info("End RackServiceImpl -> createRack() method");
		return responseModel;
	}

	@Transactional
	@Override
	public ResponseModel<List<RackRequestDto>> getAllRacks(UUID labId,String searchText, Integer pageNumber, Integer pageSize) {
	    log.info("Begin RackServiceImpl -> getAllRacks() method");
	    ResponseModel<List<RackRequestDto>> responseModel = new ResponseModel<>();

	    try {
	    	 if (pageNumber == null || pageNumber < 0 || pageSize == null || pageSize <= 0) {
	    	        throw new IllegalArgumentException("Page number and size must be positive integers.");
	    	    }
	    	    log.debug("Pagination parameters validated: pageNumber = {}, pageSize = {}");

	    	    PageRequest pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Direction.ASC, "rackNumber"));

	    	    Page<Rack> racksPage;

	    	    if (searchText != null && !searchText.isEmpty()) {
	    	        log.info("Searching racks with search text: {}", searchText);
	    	        racksPage = rackRepository.findRacksWithSearchText(searchText, pageable);
	    	    } else {
	    	        log.info("Fetching all racks for labId: {}", labId);
	    	        racksPage = rackRepository.findAllByLabId(labId, pageable);
	    	    }

	    	    if (racksPage.isEmpty()) {
	    	        log.info("No racks found for the given criteria.");
	    	        responseModel.setData(Collections.emptyList());
	    	        responseModel.setMessage("No racks found.");
	    	        responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
	    	       } else {
	    	        log.info("Found {} racks for the given criteria.", racksPage.getTotalElements());
	    	        List<RackRequestDto> responseDto = racksPage.getContent().stream()
	    	                .map(this::convertEntityToDto)
	    	                .collect(Collectors.toList());

	    	        responseModel.setData(responseDto);
	    	        responseModel.setMessage("Rack data fetched successfully.");
	    	        responseModel.setStatusCode(HttpStatus.OK.toString());
	    	        responseModel.setTotalCount((int) racksPage.getTotalElements());
	    	        responseModel.setPageNumber(racksPage.getNumber());
	    	        responseModel.setPageSize(racksPage.getSize());
	    	        responseModel.setSortedBy("rackName");
	    	  }

	    } catch (IllegalArgumentException e) {
	        log.error("Invalid input for pagination: {}", e.getMessage());
	        responseModel.setData(null);
	        responseModel.setMessage("Invalid pagination parameters: " + e.getMessage());
	        responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
	    } catch (Exception e) {
	        log.error("Error occurred while fetching racks: {}", e.getMessage());
	        responseModel.setData(null);
	        responseModel.setMessage("Failed to retrieve racks.");
	        responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	    }

	    log.info("End RackServiceImpl -> getAllRacks() method");
	    return responseModel;
	}

	@Transactional
	@Override
	public ResponseModel<RackRequestDto> getRackById(UUID id) {
		log.info("Begin RackServiceImpl -> getRackById() method....!");
		ResponseModel<RackRequestDto> responseModel = new ResponseModel<>();
		try {
			 if (id == null) {
			        log.error("Rack ID is null");
			        responseModel.setData(null);
			        responseModel.setMessage("Id cannot be null");
			        responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			        
			    }

			    log.info("Fetching rack with ID: {}", id);
			    Rack responseEntity = rackRepository.findById(id)
			            .orElseThrow(() -> new RecordNotFoundException("Rack not found with ID: " + id));

			    RackRequestDto responseDto = convertEntityToDto(responseEntity);
			    responseModel.setData(responseDto);
			    responseModel.setMessage("Rack data fetched successfully.");
			    responseModel.setStatusCode(HttpStatus.OK.toString());

		} catch (RecordNotFoundException e) {
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
			responseModel.setMessage(e.getMessage());
			responseModel.setData(null);
			log.error("Rack not found: {}", e.getMessage());

		} catch (Exception e) {
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Failed to fetch Rackr: " + e.getMessage());
			responseModel.setData(null);
			log.error("Error while fetching Rack: {}", e.getMessage());
		}
		log.info("End RackController -> getRackById() method....!");
		return responseModel;
	}

	@Transactional
	@Override
	public ResponseModel<RackRequestDto> updateRackById(RackRequestDto rackRequestDto, UUID id) {
		log.info("Begin RackController -> updateRackById() method....!");
		ResponseModel<RackRequestDto> responseModel = new ResponseModel<>();

		try {

			 if (rackRequestDto == null || id == null) {
			        log.error("Id or RackRequestDTO is null");
			        responseModel.setData(null);
			        responseModel.setMessage("Id and rack object cannot be null");
			        responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			      
			    }

			    log.info("Fetching rack with ID: {}", id);
			    Rack existingRack = rackRepository.findById(id)
			            .orElseThrow(() -> new RecordNotFoundException("Rack not found with ID: " + id));

			    log.info("Updating rack with ID: {}", id);
			    Rack updatedRack = convertDtotoEntity(rackRequestDto);
			    updatedRack.setId(existingRack.getId());

			    log.info("Saving updated rack with ID: {}", id);
			    Rack savedRack = rackRepository.save(updatedRack);

			    RackRequestDto responseDto = convertEntityToDto(savedRack);

			    responseModel.setData(responseDto);
			    responseModel.setMessage("Rack updated successfully!");
			    responseModel.setStatusCode(HttpStatus.OK.toString());

		} catch (RecordNotFoundException e) {
			log.error("Error: Rack not found with ID: " + id, e);
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
		} catch (Exception e) {
			log.error("Error occurred while updating Rack: ", e);
			responseModel.setData(null);
			responseModel.setMessage("An error occurred while updating the rack: " + e.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End RackController -> updateRackById() method");
		return responseModel;
	}

	@Transactional
	@Override
	public ResponseModel<Rack> deleteRackById(UUID id) {
		log.info("Begin RackController -> deleteRackById() method");
		ResponseModel<Rack> responseModel = new ResponseModel<>();
		try {
			 if (id == null) {
			        log.error("Id cannot be null");
			        responseModel.setData(null);
			        responseModel.setMessage("Id cannot be null");
			        responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			     
			    }

			    log.info("Checking if rack exists with ID: {}", id);
			    Rack rack = rackRepository.findById(id).orElseThrow(()->new RecordNotFoundException("No record found with this ID: " + id));
			   
			    rack.setActive(false);
			    rackRepository.save(rack);
			    responseModel.setMessage("rack deleted successfully for ID: " + id);
			    responseModel.setStatusCode(HttpStatus.OK.toString());
			    responseModel.setData(rack);
			    log.info("Rack with ID {} deleted successfully.", id);

		} catch (IllegalArgumentException e) {
			log.error("Validation error: {}", e.getMessage());
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setData(null);

		} catch (Exception e) {
			log.error("Unexpected error occurred while deleting Rack: {}", e.getMessage());
			responseModel.setMessage("Failed to delete rack due to an internal error.");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setData(null);
		}

		log.info("End RackServiceImpl -> deleteRackById() method");
		return responseModel;
	}

	private  RackRequestDto convertEntityToDto(Rack rack) {

		RackRequestDto rackDto = new RackRequestDto();
	
		BeanUtils.copyProperties(rack, rackDto);
	
		return rackDto;
	}

	private  Rack convertDtotoEntity(RackRequestDto rackRequestDto) {
	
		Rack rackEntity = new Rack();
		BeanUtils.copyProperties(rackRequestDto, rackEntity);
		

		return rackEntity;
	}

}
