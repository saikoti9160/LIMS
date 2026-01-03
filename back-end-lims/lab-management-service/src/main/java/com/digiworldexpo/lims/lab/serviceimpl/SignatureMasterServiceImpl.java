package com.digiworldexpo.lims.lab.serviceimpl;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.digiworldexpo.lims.entities.lab_management.Lab;
import com.digiworldexpo.lims.entities.lab_management.SignatureMaster;
import com.digiworldexpo.lims.lab.exception.RecordNotFoundException;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.repository.LabRepository;
import com.digiworldexpo.lims.lab.repository.SignatureMasterRepository;
import com.digiworldexpo.lims.lab.request.SignatureMasterRequestDTO;
import com.digiworldexpo.lims.lab.response.SignatureMasterResponseDTO;
import com.digiworldexpo.lims.lab.response.SignatureMasterSearchResponse;
import com.digiworldexpo.lims.lab.service.SignatureMasterService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SignatureMasterServiceImpl implements SignatureMasterService {

    private final SignatureMasterRepository signatureMasterRepository;
    private final LabRepository labRepository;

    public SignatureMasterServiceImpl(SignatureMasterRepository signatureMasterRepository,
			LabRepository labRepository) {
		super();
		this.signatureMasterRepository = signatureMasterRepository;
		this.labRepository = labRepository;
	}

	@Override
	public ResponseModel<SignatureMasterResponseDTO> saveSignature(UUID createdBy,
			SignatureMasterRequestDTO signatureMasterDTO) {
		log.info("Begin SignatureMasterServiceImpl -> saveSignature() method");

		ResponseModel<SignatureMasterResponseDTO> response = new ResponseModel<>();
		try {
			SignatureMaster signatureMaster = convertToEntity(signatureMasterDTO);

			signatureMaster.setCreatedBy(createdBy);
			
			if (signatureMasterDTO.getLabId() != null) {
	            Lab lab = labRepository.findById(signatureMasterDTO.getLabId())
	                    .orElseThrow(() -> new IllegalArgumentException("Invalid Lab ID"));
	            signatureMaster.setLab(lab);
	        }

			SignatureMaster savedSignature = signatureMasterRepository.save(signatureMaster);

			SignatureMasterResponseDTO savedSignatureDTO = convertToResponseDTO(savedSignature);

			response.setStatusCode(HttpStatus.OK.toString());
			response.setMessage("Signature saved successfully.");
			response.setData(savedSignatureDTO);

			log.info("Signature saved with ID {}", savedSignature.getId());
		} catch (Exception e) {
			log.error("Error occurred while saving signature: {}", e.getMessage());
			response.setData(null);
			response.setMessage("Failed to save signature: " + e.getMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End SignatureMasterServiceImpl -> saveSignature() method");
		return response;
	}

	private SignatureMaster convertToEntity(SignatureMasterRequestDTO dto) {
		SignatureMaster signatureMaster = new SignatureMaster();
		BeanUtils.copyProperties(dto, signatureMaster);
		return signatureMaster;
	}

	private SignatureMasterResponseDTO convertToResponseDTO(SignatureMaster entity) {
		SignatureMasterResponseDTO dto = new SignatureMasterResponseDTO();
		BeanUtils.copyProperties(entity, dto);
		return dto;
	}

	@Override
	public ResponseModel<SignatureMasterResponseDTO> getSignatureById(UUID id) {
	    log.info("Begin SignatureMasterServiceImpl -> getSignatureById() method");

	    ResponseModel<SignatureMasterResponseDTO> response = new ResponseModel<>();
	    try {
	        SignatureMaster signatureMaster = signatureMasterRepository.findById(id)
	                .orElseThrow(() -> new RecordNotFoundException("Signature not found with ID: " + id));

	        SignatureMasterResponseDTO signatureMasterResponseDTO = convertToResponseDTO(signatureMaster);

	        response.setStatusCode(HttpStatus.OK.toString());
	        response.setMessage("Signature found.");
	        response.setData(signatureMasterResponseDTO);

	    } catch (RecordNotFoundException e) {
	        log.error("Error while retrieving signature by id: {}", e.getMessage());
	        response.setStatusCode(HttpStatus.NOT_FOUND.toString());
	        response.setMessage(e.getMessage());
	        response.setData(null);
	    } catch (Exception e) {
	        log.error("Error while retrieving signature by id: {}", e.getMessage());
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	        response.setMessage("Failed to retrieve signature: " + e.getMessage());
	        response.setData(null);
	    }

	    log.info("End SignatureMasterServiceImpl -> getSignatureById() method");
	    return response;
	}

	@Override
	public ResponseModel<SignatureMasterResponseDTO> updateSignature(UUID id, SignatureMasterRequestDTO signatureMasterDTO) {
	    log.info("Begin SignatureMasterServiceImpl -> updateSignature() method");

	    ResponseModel<SignatureMasterResponseDTO> response = new ResponseModel<>();

	    try {
	        SignatureMaster existingSignature = signatureMasterRepository.findById(id)
	                .orElseThrow(() -> new RecordNotFoundException("Signature not found for ID: " + id));

	        SignatureMaster updatedEntity = convertToEntity(signatureMasterDTO);
	        updatedEntity.setId(existingSignature.getId());
	        updatedEntity.setCreatedBy(existingSignature.getCreatedBy());
	        updatedEntity.setCreatedOn(existingSignature.getCreatedOn());
	        updatedEntity.setModifiedBy(signatureMasterDTO.getId());
	        updatedEntity.setModifiedOn(new Timestamp(System.currentTimeMillis()));
	        updatedEntity.setLab(existingSignature.getLab());

	        SignatureMaster savedSignature = signatureMasterRepository.save(updatedEntity);
	        SignatureMasterResponseDTO updatedSignatureDTO = convertToResponseDTO(savedSignature);

	        response.setStatusCode(HttpStatus.OK.toString());
	        response.setMessage("Signature updated successfully.");
	        response.setData(updatedSignatureDTO);

	        log.info("Successfully updated signature with ID: {}", id);
	    } catch (RecordNotFoundException e) {
	        log.error("Signature update failed: {}", e.getMessage());
	        response.setStatusCode(HttpStatus.NOT_FOUND.toString());
	        response.setMessage(e.getMessage());
	        response.setData(null);
	    } catch (Exception e) {
	        log.error("Unexpected error occurred while updating signature: {}", e.getMessage(), e);
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	        response.setMessage("Failed to update signature.");
	        response.setData(null);
	    }

	    log.info("End SignatureMasterServiceImpl -> updateSignature() method");
	    return response;
	}

	@Override
	public ResponseModel<SignatureMaster> deleteSignature(UUID id) {
	    log.info("Begin SignatureMasterServiceImpl -> deleteSignature() method");

	    ResponseModel<SignatureMaster> response = new ResponseModel<>();
	    try {
	        signatureMasterRepository.findById(id)
	                .orElseThrow(() -> new RecordNotFoundException("Signature not found for ID: " + id));

	        signatureMasterRepository.deleteById(id);
	        response.setMessage("Signature deleted successfully.");
	        response.setStatusCode(HttpStatus.NO_CONTENT.toString());

	    } catch (RecordNotFoundException e) {
	        log.error("Signature deletion failed: {}", e.getMessage());
	        response.setMessage(e.getMessage());
	        response.setStatusCode(HttpStatus.NOT_FOUND.toString());
	    } catch (Exception e) {
	        log.error("Error while deleting signature: {}", e.getMessage());
	        response.setMessage("Failed to delete signature: " + e.getMessage());
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	    }

	    log.info("End SignatureMasterServiceImpl -> deleteSignature() method");
	    return response;
	}

	@Override
	public ResponseModel<List<SignatureMasterSearchResponse>> getAllSignature(UUID createdBy, String keyword, Boolean flag,
			Integer pageNumber, Integer pageSize) {
		log.info("Begin SignatureMasterServiceImpl -> getAll() method");

		ResponseModel<List<SignatureMasterSearchResponse>> response = new ResponseModel<>();

		try {

			List<SignatureMaster> signaturePage;
			if(keyword!=null && !keyword.trim().isEmpty()) {
				signaturePage = signatureMasterRepository.findByCreatedByAndActiveAndSignerName(
	                        createdBy, flag, keyword);
	            } else {
	            	signaturePage = signatureMasterRepository.findAllByCreatedByAndActive(
	                        createdBy, true);
			}
			List<SignatureMasterSearchResponse> dtoList = signaturePage.stream()
					.map(signature -> new SignatureMasterSearchResponse(signature.getId(),
							signature.getSignerName() != null ? signature.getSignerName() : "N/A"))
					.collect(Collectors.toList());

			int start = pageNumber * pageSize;
			int end = Math.min(start + pageSize, dtoList.size());

			List<SignatureMasterSearchResponse> paginatedList = dtoList.subList(start, end);
			response.setData(paginatedList);
			response.setTotalCount(signaturePage.size());
			response.setPageNumber(pageNumber);
			response.setPageSize(pageSize);
			response.setStatusCode(HttpStatus.OK.toString());
			response.setMessage("Signatures fetched successfully.");

			log.info("Successfully fetched {} signatures.", paginatedList.size());
		} catch (IllegalArgumentException e) {
			log.error("Invalid pagination parameters: {}", e.getMessage());
			response.setData(null);
			response.setMessage(e.getMessage());
			response.setStatusCode(HttpStatus.BAD_REQUEST.toString());
		} catch (Exception e) {
			log.error("Error occurred while fetching signatures: {}", e.getMessage());
			response.setData(null);
			response.setMessage("Failed to fetch signatures.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End SignatureMasterServiceImpl -> getAll() method");
		return response;
	}

}

