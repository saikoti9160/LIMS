package com.digiworldexpo.lims.lab.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.digiworldexpo.lims.entities.lab_management.Lab;
import com.digiworldexpo.lims.entities.lab_management.OutsourceMaster;
import com.digiworldexpo.lims.entities.master.Account;
import com.digiworldexpo.lims.entities.master.Role;
import com.digiworldexpo.lims.lab.exception.RecordNotFoundException;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.model.UserModel;
import com.digiworldexpo.lims.lab.repository.AccountRepository;
import com.digiworldexpo.lims.lab.repository.LabRepository;
import com.digiworldexpo.lims.lab.repository.OutsourceMasterRepository;
import com.digiworldexpo.lims.lab.repository.RoleRepository;
import com.digiworldexpo.lims.lab.request.OutsourceMasterRequestDTO;
import com.digiworldexpo.lims.lab.response.OutsourceMasterResponseDTO;
import com.digiworldexpo.lims.lab.response.OutsourceSearchResponse;
import com.digiworldexpo.lims.lab.service.OutsourceMasterService;
import com.digiworldexpo.lims.lab.util.RestClientUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OutsourceMasterServiceImpl implements OutsourceMasterService {
    
    private final OutsourceMasterRepository outsourceMasterRepository;
    private final RestClientUtil restClientUtil;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final LabRepository labRepository;
    
    @Value("${lims.auth.signupUrl}")
    private String authSignUpUrl;
    
  

    public OutsourceMasterServiceImpl(OutsourceMasterRepository outsourceMasterRepository,
			RestClientUtil restClientUtil, AccountRepository accountRepository, RoleRepository roleRepository,
			LabRepository labRepository) {
		super();
		this.outsourceMasterRepository = outsourceMasterRepository;
		this.restClientUtil = restClientUtil;
		this.accountRepository = accountRepository;
		this.roleRepository = roleRepository;
		this.labRepository = labRepository;
	}

    @Override
    public ResponseModel<OutsourceMasterResponseDTO> saveOutsourceMaster(UUID createdBy, OutsourceMasterRequestDTO outsourceMasterDTO) {
        log.info("Begin OutsourceMasterServiceImpl -> saveOutsourceMaster() method");

        ResponseModel<OutsourceMasterResponseDTO> responseModel = new ResponseModel<>();
        try {
            // Generate Outsource Sequence ID
            String sequenceId = "OR-" + outsourceMasterRepository.getNextFormattedOutsourceSequenceId();
            log.info("Generated Outsource Sequence ID: {}", sequenceId);

            // Convert DTO to Entity and Set Required Fields
            OutsourceMaster outsourceMaster = convertToEntity(outsourceMasterDTO);
            outsourceMaster.setCreatedBy(createdBy);
            outsourceMaster.setOutsourceSequenceId(sequenceId);
            outsourceMaster.setActive(true);

            // Sign Up User
            ResponseModel<UUID> signupResponse = signupUser(outsourceMasterDTO);
            if (!HttpStatus.OK.toString().equals(signupResponse.getStatusCode()) || signupResponse.getData() == null) {
                log.error("User signup failed: {}", signupResponse.getMessage());
                responseModel.setStatusCode(signupResponse.getStatusCode());
                responseModel.setMessage(signupResponse.getMessage());
                responseModel.setData(null);
                return responseModel;
            }

            UUID labUserId = signupResponse.getData();
            log.info("Assigned User ID to Lab: {}", labUserId);

            // Save OutsourceMaster to Repository
            OutsourceMaster savedOutsourceMaster = outsourceMasterRepository.save(outsourceMaster);
            OutsourceMasterResponseDTO savedOutsourceMasterDTO = convertToResponseDTO(savedOutsourceMaster);

            responseModel.setStatusCode(String.valueOf(HttpStatus.OK.value()));
            responseModel.setMessage("Outsource master saved successfully.");
            responseModel.setData(savedOutsourceMasterDTO);

            log.info("Outsource master saved with ID {}", savedOutsourceMaster.getId());
        } catch (Exception e) {
            log.error("Error occurred while saving outsource master: {}", e.getMessage());
            responseModel.setData(null);
            responseModel.setMessage("Failed to save outsource master: " + e.getMessage());
            responseModel.setStatusCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }

        log.info("End OutsourceMasterServiceImpl -> saveOutsourceMaster() method");
        return responseModel;
    }


    private ResponseModel<UUID> signupUser(OutsourceMasterRequestDTO outsourceMasterDTO) {
        ResponseModel<UUID> responseModel = new ResponseModel<>();
        ResponseEntity<Object> responseEntity = createUserAndReturnResponseEntity(outsourceMasterDTO);

        if (responseEntity.getStatusCodeValue() == 200) {
            try {
                JsonNode jsonNode = new ObjectMapper().readTree(new ObjectMapper().writeValueAsString(responseEntity.getBody()));
                log.info("User response - {}", jsonNode);

                JsonNode userData = jsonNode.get("data");
                if (userData != null && userData.has("id")) {
                    UUID userId = UUID.fromString(userData.get("id").asText());
                    log.info("User signed up successfully with ID - {}", userId);
                    responseModel.setData(userId);
                    responseModel.setMessage("User saved successfully");
                    responseModel.setStatusCode(HttpStatus.OK.toString());
                    return responseModel;
                }
            } catch (JsonProcessingException e) {
                log.error("Error while parsing user response", e);
                responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
                responseModel.setMessage("Failed to process user response data.");
                return responseModel;
            }
        } else if (responseEntity.getStatusCodeValue() == 400) {
            log.error("Failed to save Lab in Auth service.");
            responseModel.setMessage("Failed to save Lab in Auth service.");
            responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
            return responseModel;
        }

        log.error("Error while signing up user in auth service - {}", responseEntity.getBody());
        responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        responseModel.setMessage("User signup failed due to an unexpected error.");
        return responseModel;
    }


    private ResponseEntity<Object> createUserAndReturnResponseEntity(OutsourceMasterRequestDTO outsourceMasterDTO) {
        UserModel userModel = new UserModel();
        Optional<Account> account = accountRepository.findByAccountName("LabAdmin");
        if (!account.isPresent()) {
            throw new IllegalStateException("Required account 'LabAdmin' not found.");
        }

        UUID roleId = outsourceMasterDTO.getRoleId();
        Optional<Role> role = roleRepository.findById(roleId);
        if (!role.isPresent()) {
            throw new IllegalStateException("Required role not found.");
        }

        userModel.setAccountType(account.get().getId());
        userModel.setRole(role.get().getId());
        userModel.setEmail(outsourceMasterDTO.getEmail());
        userModel.setPassword(outsourceMasterDTO.getPassword());
        userModel.setLab(outsourceMasterDTO.getLabId());

        return restClientUtil.postForEntity(authSignUpUrl, userModel, Object.class);
    }


    
    	private OutsourceMaster convertToEntity(OutsourceMasterRequestDTO dto) {
    	    OutsourceMaster outsourceMaster = new OutsourceMaster();
    	    BeanUtils.copyProperties(dto, outsourceMaster);

    	    if (dto.getLabId() != null) {
    	        Optional<Lab> lab = labRepository.findById(dto.getLabId());
    	        lab.ifPresent(outsourceMaster::setLab);
    	    }

    	    if (dto.getRoleId() != null) {
    	        Optional<Role> role = roleRepository.findById(dto.getRoleId());
    	        role.ifPresent(outsourceMaster::setRole);
    	    }

    	    outsourceMaster.setProfiles(new ArrayList<>());
    	    outsourceMaster.setTests(new ArrayList<>());

    	    return outsourceMaster;
    	}

    	private OutsourceMasterResponseDTO convertToResponseDTO(OutsourceMaster entity) {
    	    OutsourceMasterResponseDTO dto = new OutsourceMasterResponseDTO();
    	    BeanUtils.copyProperties(entity, dto);

    	    if (entity.getRole() != null) {
    	        dto.setRoleId(entity.getRole().getId());
    	    }

    	    if (entity.getLab() != null) {
    	        dto.setLabId(entity.getLab().getId());
    	    }

    	    dto.setTests(new ArrayList<>(entity.getTests()));
    	    dto.setProfiles(new ArrayList<>(entity.getProfiles()));

    	    return dto;
    	}


    	@Override
    	public ResponseModel<List<OutsourceSearchResponse>> getAllOutsources(UUID createdBy, String keyword, Boolean flag, Integer pageNumber, Integer pageSize) {
    	    log.info("Begin OutsourceMasterServiceImpl -> getAllOutsources() method");

    	    ResponseModel<List<OutsourceSearchResponse>> response = new ResponseModel<>();

    	    try {
    	        List<OutsourceMaster> outsourceMasters;

    	        if (keyword != null && !keyword.trim().isEmpty()) {
    	            outsourceMasters = outsourceMasterRepository.findByCreatedByAndKeyword(createdBy, keyword);
    	        } else {
    	            outsourceMasters = outsourceMasterRepository.findByCreatedBy(createdBy);
    	        }

    	        List<OutsourceSearchResponse> outsourceSearchResponses = outsourceMasters.stream()
    	                .map(outsourceMaster -> new OutsourceSearchResponse(
    	                        outsourceMaster.getId(),
    	                        outsourceMaster.getOutsourceCenterName(),
    	                        outsourceMaster.getOutsourceSequenceId(),
    	                        outsourceMaster.getEmail(),
    	                        outsourceMaster.isActive()))
    	                .collect(Collectors.toList());


    	        int start = pageNumber * pageSize;
    	        int end = Math.min(start + pageSize, outsourceSearchResponses.size());
    	        List<OutsourceSearchResponse> subList = outsourceSearchResponses.subList(start, end);

    	        response.setData(subList);
    	        response.setTotalCount(outsourceSearchResponses.size());
    	        response.setPageNumber(pageNumber);
    	        response.setPageSize(pageSize);
    	        response.setStatusCode("200");
    	        response.setMessage("Outsource masters fetched successfully.");

    	    } catch (Exception e) {
    	        log.error("Error occurred while fetching outsource master: {}", e.getMessage());
    	        response.setData(null);
    	        response.setMessage("Failed to fetch outsource masters.");
    	        response.setStatusCode("500");
    	    }

    	    log.info("End OutsourceMasterServiceImpl -> getAllOutsources() method");
    	    return response;
    	}

    
    @Override
    public ResponseModel<OutsourceMasterResponseDTO> getOutsourceById(UUID id) {
        log.info("Begin OutsourceMasterServiceImpl -> getOutsourceById() method");

        ResponseModel<OutsourceMasterResponseDTO> responseModel = new ResponseModel<>();
        try {
            OutsourceMaster outsourceMaster = outsourceMasterRepository.findById(id)
                .orElseThrow(() -> new RecordNotFoundException("Outsource not found with ID: " + id));

            OutsourceMasterResponseDTO responseDTO = convertToResponseDTO(outsourceMaster);

            responseModel.setStatusCode(String.valueOf(HttpStatus.OK.value()));
            responseModel.setMessage("Outsource master fetched successfully.");
            responseModel.setData(responseDTO);

        } catch (RecordNotFoundException e) {
            log.error("Record not found: {}", e.getMessage());
            responseModel.setStatusCode(String.valueOf(HttpStatus.NOT_FOUND.value()));
            responseModel.setMessage(e.getMessage());
            responseModel.setData(null);

        } catch (Exception e) {
            log.error("Error occurred while fetching outsource master: {}", e.getMessage());
            responseModel.setStatusCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
            responseModel.setMessage("Failed to fetch outsource master: " + e.getMessage());
            responseModel.setData(null);
        }

        log.info("End OutsourceMasterServiceImpl -> getOutsourceById() method");
        return responseModel;
    }
    
    @Override
    public ResponseModel<OutsourceMasterResponseDTO> updateOutsourceById(UUID id, OutsourceMasterRequestDTO outsourceMasterDTO) {
        log.info("Begin OutsourceMasterServiceImpl -> updateOutsourceById() method");

        ResponseModel<OutsourceMasterResponseDTO> responseModel = new ResponseModel<>();
        try {
            Optional<OutsourceMaster> existingOutsourceMasterOptional = outsourceMasterRepository.findById(id);
            if (!existingOutsourceMasterOptional.isPresent()) {
                throw new RecordNotFoundException("OutsourceMaster with id " + id + " not found");
            }

            OutsourceMaster existingOutsourceMaster = existingOutsourceMasterOptional.get();

            OutsourceMaster updatedOutsourceMaster = convertToEntity(outsourceMasterDTO);
            
            updatedOutsourceMaster.setId(existingOutsourceMaster.getId());
            updatedOutsourceMaster.setCreatedBy(existingOutsourceMaster.getCreatedBy());
            updatedOutsourceMaster.setOutsourceSequenceId(existingOutsourceMaster.getOutsourceSequenceId());

            updatedOutsourceMaster = outsourceMasterRepository.save(updatedOutsourceMaster);
            
            OutsourceMasterResponseDTO updatedOutsourceMasterDTO = convertToResponseDTO(updatedOutsourceMaster);

            responseModel.setStatusCode(String.valueOf(HttpStatus.OK.value()));
            responseModel.setMessage("OutsourceMaster updated successfully.");
            responseModel.setData(updatedOutsourceMasterDTO);

            log.info("OutsourceMaster updated with ID {}", updatedOutsourceMaster.getId());
        } catch (RecordNotFoundException e) {
            log.error("OutsourceMaster not found: {}", e.getMessage());
            responseModel.setStatusCode(String.valueOf(HttpStatus.NOT_FOUND.value()));
            responseModel.setMessage(e.getMessage());
            responseModel.setData(null);
        } catch (Exception e) {
            log.error("Error occurred while updating OutsourceMaster: {}", e.getMessage());
            responseModel.setStatusCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
            responseModel.setMessage("Failed to update OutsourceMaster: " + e.getMessage());
            responseModel.setData(null);
        }

        log.info("End OutsourceMasterServiceImpl -> updateOutsourceById() method");
        return responseModel;
    }


    
    @Override
    public ResponseModel<OutsourceMasterResponseDTO> deleteOutsourceById(UUID id) {
        log.info("Begin OutsourceMasterService -> deleteOutsourceById() method");

        ResponseModel<OutsourceMasterResponseDTO> response = new ResponseModel<>();

        try {
            OutsourceMaster outsourceMaster = outsourceMasterRepository.findById(id)
                    .orElseThrow(() -> new RecordNotFoundException("Outsource record not found with ID: " + id));

            OutsourceMasterResponseDTO outsourceMasterResponseDTO = convertToResponseDTO(outsourceMaster);

            outsourceMasterRepository.deleteById(id);

            response.setStatusCode(HttpStatus.OK.toString());
            response.setMessage("Outsource record deleted successfully.");
            response.setData(outsourceMasterResponseDTO);
        } catch (RecordNotFoundException e) {
            log.error("Invalid ID parameter: {}", e.getMessage());
            response.setStatusCode(HttpStatus.NOT_FOUND.toString());
            response.setMessage(e.getMessage());
            response.setData(null);
        } catch (Exception e) {
            log.error("Unexpected error occurred while deleting outsource record with ID: {}", id, e);
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.setMessage("Failed to delete outsource record.");
            response.setData(null);
        }

        log.info("End OutsourceMasterService -> deleteOutsourceById() method");
        return response;
    }
    
    
}
