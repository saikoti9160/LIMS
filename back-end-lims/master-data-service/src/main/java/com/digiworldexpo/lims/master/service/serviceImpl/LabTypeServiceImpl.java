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

import com.digiworldexpo.lims.entities.master.LabType;
import com.digiworldexpo.lims.master.exception.DuplicateRecordFoundException;
import com.digiworldexpo.lims.master.exception.RecordNotFoundException;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.repository.LabTypeRepository;
import com.digiworldexpo.lims.master.service.LabTypeService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LabTypeServiceImpl implements LabTypeService {

    private final LabTypeRepository labTypeRepository;

    public LabTypeServiceImpl(LabTypeRepository labTypeRepository) {
        this.labTypeRepository = labTypeRepository;
    }
    

    @Override
    public ResponseModel<LabType> saveLabType(LabType labType, UUID createdBy) {
        log.info("Begin LabTypeServiceImpl -> saveLabType() method...");
        ResponseModel<LabType> responseModel = new ResponseModel<>();

        try {
            if (labType.getName() == null || labType.getName().trim().isEmpty()) {
                responseModel.setData(null);
                responseModel.setMessage("Lab Type name cannot be null or empty");
                responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
                return responseModel;
            }

            labType.setCreatedBy(createdBy);

            Optional<LabType> existingLabType = labTypeRepository.findByCreatedByAndNameIgnoreCase(createdBy, labType.getName());
            if (existingLabType.isPresent()) {
                throw new DuplicateRecordFoundException(
                        "Lab Type with name '" + labType.getName() + "' already exists for this user.");
            }

            LabType savedLabType = labTypeRepository.save(labType);
            responseModel.setData(savedLabType);
            responseModel.setMessage("Lab Type saved successfully");
            responseModel.setStatusCode(HttpStatus.OK.toString());

        } catch (DuplicateRecordFoundException exception) {
            log.info("Duplicate record found: {}", exception.getMessage());
            responseModel.setData(null);
            responseModel.setMessage(exception.getMessage());
            responseModel.setStatusCode(HttpStatus.CONFLICT.toString());
        } catch (Exception e) {
            log.info("Error occurred while saving Lab Type: {}", e.getMessage());
            responseModel.setData(null);
            responseModel.setMessage("Failed to save Lab Type");
            responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }

        log.info("End LabTypeServiceImpl -> saveLabType() method...");
        return responseModel;
    }
    
    @Override
    public ResponseModel<List<LabType>> getLabTypes(String startsWith, int pageNumber, int pageSize, String sortBy, UUID createdBy) {
        log.info("Begin LabTypeServiceImpl -> getLabTypes() method...");
        ResponseModel<List<LabType>> responseModel = new ResponseModel<>();

        try {
            if (pageNumber < 0 || pageSize < 1) {
                throw new IllegalArgumentException("Invalid pagination parameters.");
            }

            Sort sort = Sort.by(Sort.Direction.ASC, sortBy);
            Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

            Page<LabType> labTypePage;

            if (startsWith != null && !startsWith.trim().isEmpty()) {
                labTypePage = labTypeRepository.findByCreatedByAndNameStartingWith(createdBy, startsWith, pageable);
                log.info("Searching lab types with name starting with '{}' for createdBy '{}'", startsWith, createdBy);
            } else {
                labTypePage = labTypeRepository.findByCreatedBy(createdBy, pageable);
                log.info("Retrieving all lab types for createdBy '{}'", createdBy);
            }

            List<LabType> labTypes = labTypePage.getContent();
            
            responseModel.setData(labTypes);
            responseModel.setMessage("Lab Types retrieved successfully.");
            responseModel.setStatusCode(HttpStatus.OK.toString());
            responseModel.setTotalCount((int) labTypePage.getTotalElements()); // Set total count of items
            responseModel.setPageNumber(pageNumber);
            responseModel.setPageSize(pageSize);

        } catch (IllegalArgumentException e) {
            responseModel.setData(null);
            responseModel.setMessage(e.getMessage());
            responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
            log.info("Invalid input parameters: {}", e.getMessage());
        } catch (Exception e) {
            responseModel.setData(null);
            responseModel.setMessage("Failed to retrieve lab types.");
            responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            log.info("Error occurred while retrieving lab types: {}", e.getMessage());
        }

        log.info("End LabTypeServiceImpl -> getLabTypes() method...");
        return responseModel;
    }

    @Override
    public ResponseModel<LabType> getLabTypeById(UUID id) {
        log.info("Begin LabTypeServiceImpl -> getLabTypeById() method...");
        ResponseModel<LabType> responseModel = new ResponseModel<>();

        try {
            if (id == null) {
                responseModel.setData(null);
                responseModel.setMessage("LabType ID cannot be null");
                responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
                return responseModel;
            }

            LabType labType = labTypeRepository.findById(id)
                    .orElseThrow(() -> new RecordNotFoundException("LabType not found with ID: " + id));

            responseModel.setData(labType);
            responseModel.setMessage("LabType retrieved successfully of id " + id);
            responseModel.setStatusCode(HttpStatus.OK.toString());
        } catch (RecordNotFoundException e) {
            log.info("LabType not found: {}", e.getMessage());
            responseModel.setData(null);
            responseModel.setMessage(e.getMessage());
            responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
        } catch (Exception e) {
            log.info("Error occurred while fetching LabType: {}", e.getMessage());
            responseModel.setData(null);
            responseModel.setMessage("Failed to retrieve LabType");
            responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }

        log.info("End LabTypeServiceImpl -> getLabTypeById() method...");
        return responseModel;
    }

    @Override
    @Transactional
    public ResponseModel<LabType> updateLabType(UUID id, LabType updatedLabType, UUID userId) {
        log.info("Begin LabTypeServiceImpl -> updateLabType() method...");
        ResponseModel<LabType> responseModel = new ResponseModel<>();

        try {
            if (updatedLabType.getName() == null || updatedLabType.getName().trim().isEmpty()) {
                throw new IllegalArgumentException("LabType name cannot be null or empty during update.");
            }

            LabType existingLabType = labTypeRepository.findById(id)
                    .orElseThrow(() -> new RecordNotFoundException("LabType not found with ID: " + id));

            Optional<LabType> duplicateLabType = labTypeRepository.findByName(updatedLabType.getName());
            if (duplicateLabType.isPresent() && !duplicateLabType.get().getId().equals(id)) {
                throw new DuplicateRecordFoundException(
                        "LabType with name '" + updatedLabType.getName() + "' already exists.");
            }

            BeanUtils.copyProperties(updatedLabType, existingLabType, "id", "createdBy", "createdOn");

            existingLabType.setModifiedBy(userId);
            existingLabType.setModifiedOn(new Timestamp(System.currentTimeMillis()));

            LabType savedLabType = labTypeRepository.save(existingLabType);

            responseModel.setData(savedLabType);
            responseModel.setMessage("LabType updated successfully.");
            responseModel.setStatusCode(HttpStatus.OK.toString());

        } catch (RecordNotFoundException e) {
            responseModel.setData(null);
            responseModel.setMessage(e.getMessage());
            responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
        } catch (DuplicateRecordFoundException e) {
            responseModel.setData(null);
            responseModel.setMessage(e.getMessage());
            responseModel.setStatusCode(HttpStatus.CONFLICT.toString());
        } catch (IllegalArgumentException e) {
            responseModel.setData(null);
            responseModel.setMessage(e.getMessage());
            responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
        } catch (Exception e) {
            responseModel.setData(null);
            responseModel.setMessage("An unexpected error occurred while updating the LabType.");
            responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }

        log.info("End LabTypeServiceImpl -> updateLabType() method...");
        return responseModel;
    }


    @Override
    public ResponseModel<LabType> deleteLabType(UUID id) {
        log.info("Begin LabTypeServiceImpl -> deleteLabType() method...");
        ResponseModel<LabType> responseModel = new ResponseModel<>();

        try {
            if (id == null) {
                responseModel.setMessage("LabType ID cannot be null");
                responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
                return responseModel;
            }
            
            if (!labTypeRepository.existsById(id)) {
                throw new RecordNotFoundException("LabType not found with ID: " + id);
            }

            labTypeRepository.deleteById(id);
            responseModel.setMessage("LabType deleted successfully of id " + id);
            responseModel.setStatusCode(HttpStatus.OK.toString());
        } catch (RecordNotFoundException e) {
            log.info("LabType not found: {}", e.getMessage());
            responseModel.setMessage(e.getMessage());
            responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
        } catch (Exception e) {
            log.info("Error occurred while deleting LabType: {}", e.getMessage());
            responseModel.setMessage("Failed to delete LabType");
            responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }

        log.info("End LabTypeServiceImpl -> deleteLabType() method...");
        return responseModel;
    }


}