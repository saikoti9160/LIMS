package com.digiworldexpo.lims.master.service.serviceImpl;

import java.util.List;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.digiworldexpo.lims.entities.master.SupportIssueType;
import com.digiworldexpo.lims.master.exception.DuplicateRecordFoundException;
import com.digiworldexpo.lims.master.exception.RecordNotFoundException;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.repository.SupportIssueTypeRepository;
import com.digiworldexpo.lims.master.service.SupportIssueTypeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SupportIssueTypeServiceImpl implements SupportIssueTypeService {


	private final SupportIssueTypeRepository supportIssueTypeRepository;

	public SupportIssueTypeServiceImpl(SupportIssueTypeRepository supportIssueTypeRepository) {
		this.supportIssueTypeRepository = supportIssueTypeRepository;
	}

	@Override
	public ResponseModel<SupportIssueType> saveSupportIssueType(SupportIssueType supportIssueType) {
		log.info("Begin SupportIssueTypeServiceImpl -> saveSupportIssueType() method...");
		ResponseModel<SupportIssueType> responseModel = new ResponseModel<>();

		try {
			if (supportIssueType.getName() == null || supportIssueType.getName().trim().isEmpty()) {
				responseModel.setData(null);
				responseModel.setMessage("Support Issue Type name cannot be null or empty");
				responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
				return responseModel;
			}

			Optional<SupportIssueType> existingSupportIssueType = supportIssueTypeRepository
					.findByName(supportIssueType.getName());
			if (existingSupportIssueType.isPresent()) {
				throw new DuplicateRecordFoundException(
						"Support Issue Type with name '" + supportIssueType.getName() + "' already exists.");
			}

			SupportIssueType savedSupportIssueType = supportIssueTypeRepository.save(supportIssueType);
			responseModel.setData(savedSupportIssueType);
			responseModel.setMessage("Support Issue Type saved successfully");
			responseModel.setStatusCode(HttpStatus.OK.toString());

		} catch (DuplicateRecordFoundException exception) {
			log.info("Duplicate record found: {}", exception.getMessage());
			responseModel.setData(null);
			responseModel.setMessage(exception.getMessage());
			responseModel.setStatusCode(HttpStatus.CONFLICT.toString());
		} catch (Exception e) {
			log.info("Error occurred while saving Support Issue Type: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Failed to save Support Issue Type");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End SupportIssueTypeServiceImpl -> saveSupportIssueType() method...");
		return responseModel;
	}

	@Override
	public ResponseModel<List<SupportIssueType>> getSupportIssueTypes(String startsWith, int pageNumber, int pageSize, 
	                                                                  String sortBy) {
	    log.info("Begin SupportIssueTypeServiceImpl -> getSupportIssueTypes() method...");
	    ResponseModel<List<SupportIssueType>> responseModel = new ResponseModel<>();

	    try {
	        if (pageNumber < 0 || pageSize < 1) {
	            throw new IllegalArgumentException("Invalid pagination parameters");
	        }

	        Sort sort = Sort.by(Sort.Direction.ASC, sortBy);
	        List<SupportIssueType> supportIssueTypes;

	        if (startsWith != null && !startsWith.trim().isEmpty()) {
	            supportIssueTypes = supportIssueTypeRepository.findSupportIssueTypesByStartsWith(startsWith, sort);
	            log.info("Searching support issue types with name containing: {}", startsWith);
	        } else {
	            supportIssueTypes = supportIssueTypeRepository.findAll(sort);
	            log.info("Retrieving all support issue types.");
	        }

	        Pageable pageable = PageRequest.of(pageNumber, pageSize);
	        int start = (int) pageable.getOffset();
	        int end = Math.min(start + pageable.getPageSize(), supportIssueTypes.size());
	        List<SupportIssueType> paginatedSupportIssueTypes = supportIssueTypes.subList(start, end);

	        responseModel.setData(paginatedSupportIssueTypes);
	        responseModel.setMessage("Support Issue Types retrieved successfully.");
	        responseModel.setStatusCode(HttpStatus.OK.toString());
	        responseModel.setTotalCount(supportIssueTypes.size());
	        responseModel.setPageNumber(pageNumber);
	        responseModel.setPageSize(pageSize);

	    } catch (IllegalArgumentException e) {
	        responseModel.setData(null);
	        responseModel.setMessage(e.getMessage());
	        responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
	        log.info("Invalid input parameters: {}", e.getMessage());
	    } catch (Exception e) {
	        responseModel.setData(null);
	        responseModel.setMessage("Failed to retrieve Support Issue Types.");
	        responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	        log.info("Error occurred while retrieving support issue types: {}", e.getMessage());
	    }

	    log.info("End SupportIssueTypeServiceImpl -> getSupportIssueTypes() method...");
	    return responseModel;
	}


	@Override
	public ResponseModel<SupportIssueType> getSupportIssueTypeById(UUID id) {
		log.info("Begin SupportIssueTypeServiceImpl -> getSupportIssueTypeById() method...");
		ResponseModel<SupportIssueType> responseModel = new ResponseModel<>();

		try {
			if (id == null) {
				throw new IllegalArgumentException("Support Issue Type ID cannot be null");
			}

			SupportIssueType supportIssueType = supportIssueTypeRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("Support Issue Type not found with ID: " + id));

			responseModel.setData(supportIssueType);
			responseModel.setMessage("Support Issue Type retrieved successfully for ID: " + id);
			responseModel.setStatusCode(HttpStatus.OK.toString());
		} catch (IllegalArgumentException e) {
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			log.info("Invalid input parameters: {}", e.getMessage());
		} catch (RecordNotFoundException e) {
			log.info("Support Issue Type not found: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
		} catch (Exception e) {
			log.info("Error occurred while fetching support issue type: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Failed to retrieve support issue type");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End SupportIssueTypeServiceImpl -> getSupportIssueTypeById() method...");
		return responseModel;
	}

	@Override
	public ResponseModel<SupportIssueType> updateSupportIssueType(UUID id, SupportIssueType updatedSupportIssueType) {
		log.info("Begin SupportIssueTypeServiceImpl -> updateSupportIssueType() method...");
		ResponseModel<SupportIssueType> responseModel = new ResponseModel<>();

		try {
			if (id == null || updatedSupportIssueType == null || updatedSupportIssueType.getName().trim().isEmpty()) {
				throw new IllegalArgumentException("Invalid input parameters: ID or name cannot be null or empty");
			}

			SupportIssueType existingSupportIssueType = supportIssueTypeRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("Support Issue Type not found with ID: " + id));

			if (supportIssueTypeRepository.findByName(updatedSupportIssueType.getName())
					.filter(issueType -> !issueType.getId().equals(id)).isPresent()) {
				throw new DuplicateRecordFoundException(
						"Support Issue Type with name '" + updatedSupportIssueType.getName() + "' already exists.");
			}

			existingSupportIssueType.setName(updatedSupportIssueType.getName());
			SupportIssueType savedSupportIssueType = supportIssueTypeRepository.save(existingSupportIssueType);

			responseModel.setData(savedSupportIssueType);
			responseModel.setMessage("Support Issue Type updated successfully for ID: " + id);
			responseModel.setStatusCode(HttpStatus.OK.toString());
		} catch (IllegalArgumentException e) {
			log.info("Invalid input parameters: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
		} catch (RecordNotFoundException e) {
			log.info("Support Issue Type not found: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
		} catch (DuplicateRecordFoundException e) {
			log.info("Duplicate record found: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.CONFLICT.toString());
		} catch (Exception e) {
			log.info("Error occurred while updating support issue type: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Failed to update support issue type");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End SupportIssueTypeServiceImpl -> updateSupportIssueType() method...");
		return responseModel;
	}

	@Override
	public ResponseModel<SupportIssueType> deleteSupportIssueType(UUID id) {
		log.info("Begin SupportIssueTypeServiceImpl -> deleteSupportIssueType() method...");
		ResponseModel<SupportIssueType> responseModel = new ResponseModel<>();

		try {
			if (id == null) {
				throw new IllegalArgumentException("Support Issue Type ID cannot be null");
			}

			if (!supportIssueTypeRepository.existsById(id)) {
				throw new RecordNotFoundException("Support Issue Type not found with ID: " + id);
			}

			supportIssueTypeRepository.deleteById(id);
			responseModel.setMessage("Support Issue Type deleted successfully for ID: " + id);
			responseModel.setStatusCode(HttpStatus.OK.toString());
		} catch (IllegalArgumentException e) {
			log.info("Error occurred: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
		} catch (RecordNotFoundException e) {
			log.info("Department not found: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
		} catch (Exception e) {
			log.info("Error occurred while deleting support issue type: {}", e.getMessage());
			responseModel.setMessage("Failed to delete support issue type");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End SupportIssueTypeServiceImpl -> deleteSupportIssueType() method...");
		return responseModel;
	}

}