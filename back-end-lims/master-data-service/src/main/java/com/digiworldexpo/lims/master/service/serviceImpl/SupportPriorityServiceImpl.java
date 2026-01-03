package com.digiworldexpo.lims.master.service.serviceImpl;

import java.util.List;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.digiworldexpo.lims.entities.master.SupportPriority;
import com.digiworldexpo.lims.master.exception.DuplicateRecordFoundException;
import com.digiworldexpo.lims.master.exception.RecordNotFoundException;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.repository.SupportPriorityRepository;
import com.digiworldexpo.lims.master.service.SupportPriorityService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SupportPriorityServiceImpl implements SupportPriorityService {

	private final SupportPriorityRepository supportPriorityRepository;

	SupportPriorityServiceImpl(SupportPriorityRepository supportPriorityRepository) {
		this.supportPriorityRepository = supportPriorityRepository;
	}

	@Override
	public ResponseModel<SupportPriority> saveSupportPriority(SupportPriority supportPriority) {
		log.info("Begin SupportPriorityServiceImpl -> saveSupportPriority() method...");
		ResponseModel<SupportPriority> responseModel = new ResponseModel<>();

		try {
			if (supportPriority.getName() == null || supportPriority.getName().trim().isEmpty()) {
				responseModel.setMessage("Support Priority name cannot be null or empty");
				responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
				return responseModel;
			}

			Optional<SupportPriority> existingPriority = supportPriorityRepository
					.findByName(supportPriority.getName());
			if (existingPriority.isPresent()) {
				throw new DuplicateRecordFoundException(
						"Support Priority with name '" + supportPriority.getName() + "' already exists.");
			}

			SupportPriority savedPriority = supportPriorityRepository.save(supportPriority);
			responseModel.setData(savedPriority);
			responseModel.setMessage("Support Priority saved successfully");
			responseModel.setStatusCode(HttpStatus.OK.toString());
		} catch (DuplicateRecordFoundException e) {
			log.info("Duplicate record found: {}", e.getMessage());
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.CONFLICT.toString());
		} catch (Exception e) {
			log.info("Error occurred while saving Support Priority: {}", e.getMessage());
			responseModel.setMessage("Failed to save Support Priority");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End SupportPriorityServiceImpl -> saveSupportPriority() method...");
		return responseModel;
	}

	@Override
	public ResponseModel<List<SupportPriority>> getSupportPriorities(String startsWith, int pageNumber, int pageSize,
	        String sortBy) {
	    log.info("Begin SupportPriorityServiceImpl -> getSupportPriorities() method...");
	    ResponseModel<List<SupportPriority>> responseModel = new ResponseModel<>();

	    try {
	        if (pageNumber < 0 || pageSize < 1) {
	            throw new IllegalArgumentException("Invalid pagination parameters.");
	        }

	        Sort sort = Sort.by(Sort.Direction.ASC, sortBy);
	        List<SupportPriority> priorities;

	        if (startsWith != null && !startsWith.trim().isEmpty()) {
	            priorities = supportPriorityRepository.findSupportPrioritiesByName(startsWith, sort);
	        } else {
	            priorities = supportPriorityRepository.findAll(sort);
	        }

	        Pageable pageable = PageRequest.of(pageNumber, pageSize);
	        int start = (int) pageable.getOffset();
	        int end = Math.min(start + pageable.getPageSize(), priorities.size());
	        List<SupportPriority> paginatedPriorities = priorities.subList(start, end);

	        responseModel.setData(paginatedPriorities);
	        responseModel.setMessage("Support Priorities retrieved successfully.");
	        responseModel.setStatusCode(HttpStatus.OK.toString());
	        responseModel.setTotalCount(priorities.size());
	        responseModel.setPageNumber(pageNumber);
	        responseModel.setPageSize(pageSize);
	    } catch (IllegalArgumentException e) {
	        responseModel.setData(null);
	        responseModel.setMessage(e.getMessage());
	        responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
	        log.info("Invalid input parameters: {}", e.getMessage());
	    } catch (Exception e) {
	        log.info("Error occurred while retrieving Support Priorities: {}", e.getMessage());
	        responseModel.setMessage("Failed to retrieve Support Priorities");
	        responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	    }

	    log.info("End SupportPriorityServiceImpl -> getSupportPriorities() method...");
	    return responseModel;
	}


	@Override
	public ResponseModel<SupportPriority> getSupportPriorityById(UUID id) {
		log.info("Begin SupportPriorityServiceImpl -> getSupportPriorityById() method...");
		ResponseModel<SupportPriority> responseModel = new ResponseModel<>();

		try {
			SupportPriority priority = supportPriorityRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("Support Priority not found with ID: " + id));

			responseModel.setData(priority);
			responseModel.setMessage("Support Priority retrieved successfully");
			responseModel.setStatusCode(HttpStatus.OK.toString());
		} catch (RecordNotFoundException e) {
			log.info("Support Priority not found: {}", e.getMessage());
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
		} catch (Exception e) {
			log.info("Error occurred while retrieving Support Priority: {}", e.getMessage());
			responseModel.setMessage("Failed to retrieve Support Priority");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End SupportPriorityServiceImpl -> getSupportPriorityById() method...");
		return responseModel;
	}

	@Override
	public ResponseModel<SupportPriority> updateSupportPriority(UUID id, SupportPriority updatedSupportPriority) {
		log.info("Begin SupportPriorityServiceImpl -> updateSupportPriority() method...");
		ResponseModel<SupportPriority> responseModel = new ResponseModel<>();

		try {
			if (id == null || updatedSupportPriority == null || updatedSupportPriority.getName().trim().isEmpty()) {
				responseModel.setData(null);
				responseModel.setMessage("Invalid input parameters");
				responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
				return responseModel;
			}

			SupportPriority existingPriority = supportPriorityRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("Support Priority not found with ID: " + id));

			if (supportPriorityRepository.findByName(updatedSupportPriority.getName())
					.filter(priority -> !priority.getId().equals(id)).isPresent()) {
				throw new DuplicateRecordFoundException(
						"Support Priority with name '" + updatedSupportPriority.getName() + "' already exists.");
			}

			existingPriority.setName(updatedSupportPriority.getName());
			SupportPriority savedPriority = supportPriorityRepository.save(existingPriority);

			responseModel.setData(savedPriority);
			responseModel.setMessage("Support Priority updated successfully with ID: " + id);
			responseModel.setStatusCode(HttpStatus.OK.toString());
		} catch (RecordNotFoundException e) {
			log.info("Support Priority not found: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
		} catch (DuplicateRecordFoundException e) {
			log.info("Duplicate record found: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.CONFLICT.toString());
		} catch (Exception e) {
			log.info("Error occurred while updating Support Priority: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Failed to update Support Priority");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End SupportPriorityServiceImpl -> updateSupportPriority() method...");
		return responseModel;
	}

	@Override
	public ResponseModel<SupportPriority> deleteSupportPriority(UUID id) {
		log.info("Begin SupportPriorityServiceImpl -> deleteSupportPriority() method...");
		ResponseModel<SupportPriority> responseModel = new ResponseModel<>();

		try {
			if (!supportPriorityRepository.existsById(id)) {
				throw new RecordNotFoundException("Support Priority not found with ID: " + id);
			}

			supportPriorityRepository.deleteById(id);
			responseModel.setMessage("Support Priority deleted successfully");
			responseModel.setStatusCode(HttpStatus.OK.toString());
		} catch (RecordNotFoundException e) {
			log.info("Support Priority not found: {}", e.getMessage());
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
		} catch (Exception e) {
			log.info("Error occurred while deleting Support Priority: {}", e.getMessage());
			responseModel.setMessage("Failed to delete Support Priority");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End SupportPriorityServiceImpl -> deleteSupportPriority() method...");
		return responseModel;
	}
}