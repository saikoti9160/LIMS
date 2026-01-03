package com.digiworldexpo.lims.lab.serviceimpl;

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
import com.digiworldexpo.lims.entities.lab_management.ReferralMaster;
import com.digiworldexpo.lims.entities.master.Account;
import com.digiworldexpo.lims.entities.master.Role;
import com.digiworldexpo.lims.lab.exception.RecordNotFoundException;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.model.UserModel;
import com.digiworldexpo.lims.lab.repository.AccountRepository;
import com.digiworldexpo.lims.lab.repository.LabRepository;
import com.digiworldexpo.lims.lab.repository.ReferralMasterRepository;
import com.digiworldexpo.lims.lab.repository.RoleRepository;
import com.digiworldexpo.lims.lab.request.ReferralMasterRequestDTO;
import com.digiworldexpo.lims.lab.response.ReferralMasterResponseDTO;
import com.digiworldexpo.lims.lab.response.ReferralMasterSearchResponse;
import com.digiworldexpo.lims.lab.service.ReferralMasterService;
import com.digiworldexpo.lims.lab.util.RestClientUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class ReferralMasterServiceImpl implements ReferralMasterService {
	
	private final ReferralMasterRepository referralMasterRepository;
	private final RestClientUtil restClientUtil;
	private final AccountRepository accountRepository;
	private final RoleRepository roleRepository;
	private final LabRepository labRepository;
	
	

	@Value("${lims.auth.signupUrl}")
    private String authSignUpUrl;

public ReferralMasterServiceImpl(ReferralMasterRepository referralMasterRepository, RestClientUtil restClientUtil,
			AccountRepository accountRepository, RoleRepository roleRepository, LabRepository labRepository) {
		super();
		this.referralMasterRepository = referralMasterRepository;
		this.restClientUtil = restClientUtil;
		this.accountRepository = accountRepository;
		this.roleRepository = roleRepository;
		this.labRepository = labRepository;
	}

	
	@Override
	public ResponseModel<ReferralMasterResponseDTO> saveReferralMaster(UUID createdBy, 
	        ReferralMasterRequestDTO referralMasterRequestDTO) {
	    log.info("Begin ReferralMasterServiceImpl -> saveReferralMaster() method");

	    ResponseModel<ReferralMasterResponseDTO> response = new ResponseModel<>();

	    try {
	        String sequenceId = "REF" + referralMasterRepository.getNextFormattedReferralMasterSequenceId();
	        log.info("Generated ReferralMaster Sequence ID: {}", sequenceId);

	        ReferralMaster referralMaster = convertToEntity(referralMasterRequestDTO);

	        referralMaster.setReferralSequenceId(sequenceId);
	        referralMaster.setCreatedBy(createdBy);

	        if (referralMasterRequestDTO.getLabId() != null) {
	            Lab lab = labRepository.findById(referralMasterRequestDTO.getLabId())
	                    .orElseThrow(() -> new IllegalArgumentException("Invalid Lab ID"));
	            referralMaster.setLab(lab);
	        }

	        if (referralMasterRequestDTO.getRoleId() != null) {
	            Role role = roleRepository.findById(referralMasterRequestDTO.getRoleId())
	                    .orElseThrow(() -> new IllegalArgumentException("Invalid Role ID"));
	            referralMaster.setRole(role);
	        }

	        ResponseModel<UUID> signupResponse = signupUser(referralMasterRequestDTO);
	        if (!HttpStatus.OK.toString().equals(signupResponse.getStatusCode()) || signupResponse.getData() == null) {
	            log.error("User signup failed: {}", signupResponse.getMessage());
	            response.setStatusCode(signupResponse.getStatusCode());
	            response.setMessage(signupResponse.getMessage());
	            response.setData(null);
	            return response;
	        }

	        UUID labUserId = signupResponse.getData();
	        log.info("Assigned User ID to Lab: {}", labUserId);

	        ReferralMaster savedReferralMaster = referralMasterRepository.save(referralMaster);
	        ReferralMasterResponseDTO savedDTO = convertToResponseDTO(savedReferralMaster);

	        response.setStatusCode(HttpStatus.OK.toString());
	        response.setMessage("ReferralMaster saved successfully.");
	        response.setData(savedDTO);
	        log.info("ReferralMaster saved with ID: {}", savedReferralMaster.getId());

	    } catch (Exception e) {
	        log.error("Error occurred while saving referral: {}", e.getMessage(), e);
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	        response.setMessage("Failed to save referral");
	        response.setData(null);
	    }

	    log.info("End ReferralMasterServiceImpl -> saveReferralMaster() method");
	    return response;
	}

	private ResponseModel<UUID> signupUser(ReferralMasterRequestDTO referralMasterDTO) {
	    ResponseModel<UUID> responseModel = new ResponseModel<>();
	    ResponseEntity<Object> responseEntity = createUserAndReturnResponseEntity(referralMasterDTO);

	    if (responseEntity.getStatusCodeValue() == 200) {
	        try {
	            JsonNode jsonNode = new ObjectMapper()
	                    .readTree(new ObjectMapper().writeValueAsString(responseEntity.getBody()));
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

	private ResponseEntity<Object> createUserAndReturnResponseEntity(ReferralMasterRequestDTO referralMasterDTO) {
	    UserModel userModel = new UserModel();
	    
	    Optional<Account> account = accountRepository.findByAccountName("LabAdmin");
	    if (!account.isPresent()) {
	        throw new IllegalStateException("Required account 'LabAdmin' not found.");
	    }

	    UUID roleId = referralMasterDTO.getRoleId();

	    Optional<Role> role = roleRepository.findById(roleId);
	    if (!role.isPresent()) {
	        throw new IllegalStateException("Required role not found.");
	    }

	    userModel.setAccountType(account.get().getId());
	    userModel.setRole(role.get().getId());
	    userModel.setEmail(referralMasterDTO.getEmail());
	    userModel.setPassword(referralMasterDTO.getPassword());
	    userModel.setLab(referralMasterDTO.getLabId());

	    return restClientUtil.postForEntity(authSignUpUrl, userModel, Object.class);
	}


	private ReferralMaster convertToEntity(ReferralMasterRequestDTO dto) {
	    ReferralMaster entity = new ReferralMaster();
	    BeanUtils.copyProperties(dto, entity);
	    return entity;
	}

	private ReferralMasterResponseDTO convertToResponseDTO(ReferralMaster entity) {
	    ReferralMasterResponseDTO dto = new ReferralMasterResponseDTO();
	    BeanUtils.copyProperties(entity, dto);
	    if (entity.getRole() != null) {
	        dto.setRoleId(entity.getRole().getId());
	    }

	    if (entity.getLab() != null) {
	        dto.setLabId(entity.getLab().getId());
	    }
	    return dto;
	}


	@Override
	public ResponseModel<List<ReferralMasterSearchResponse>> getAllReferrals(UUID createdBy, String keyword, Boolean flag,
			Integer pageNumber, Integer pageSize) {
		log.info("Begin ReferralMasterServiceImpl -> getAll() method");

		ResponseModel<List<ReferralMasterSearchResponse>> response = new ResponseModel<>();

		try {
			List<ReferralMaster> referralPage;

			if(keyword!=null && !keyword.trim().isEmpty()) {
				referralPage = referralMasterRepository.findByCreatedByAndActiveAndReferralName(
	                        createdBy, flag, keyword);
	            } else {
	            	referralPage = referralMasterRepository.findAllByCreatedByAndActive(
	                        createdBy, true);
			}

			List<ReferralMasterSearchResponse> dtoList = referralPage.stream()
					.map(referral -> new ReferralMasterSearchResponse(referral.getId(),
							referral.getReferralSequenceId(), referral.getReferralName(), referral.getEmail()))
					.collect(Collectors.toList());

			int start = pageNumber * pageSize;
			int end = Math.min(start + pageSize, dtoList.size());
			List<ReferralMasterSearchResponse> paginatedList = dtoList.subList(start, end);

			response.setData(paginatedList);
			response.setTotalCount(referralPage.size());
			response.setPageNumber(pageNumber);
			response.setPageSize(pageSize);
			response.setStatusCode(HttpStatus.OK.toString());
			response.setMessage("Referrals fetched successfully.");

			log.info("Successfully fetched {} referrals.", paginatedList.size());
		} catch (Exception e) {
			log.error("Error occurred while fetching referrals: {}", e.getMessage());
			response.setData(null);
			response.setMessage("Failed to fetch referrals.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End ReferralMasterServiceImpl -> getAll() method");
		return response;
	}

	@Override
	public ResponseModel<ReferralMasterResponseDTO> getReferralById(UUID id) {
		log.info("Begin ReferralMasterServiceImpl -> getReferralById() method");

		ResponseModel<ReferralMasterResponseDTO> response = new ResponseModel<>();

		try {
			ReferralMaster referralMaster = referralMasterRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("Referral not found for ID: " + id));

			ReferralMasterResponseDTO referralDTO = convertToResponseDTO(referralMaster);

			response.setStatusCode(HttpStatus.OK.toString());
			response.setMessage("Referral fetched successfully.");
			response.setData(referralDTO);

			log.info("Successfully fetched referral with ID: {}", id);
		} catch (RecordNotFoundException e) {
			log.error("Invalid ID provided: {}", e.getMessage());
			response.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			response.setMessage(e.getMessage());
			response.setData(null);
		} catch (Exception e) {
			log.error("Unexpected error occurred while fetching referral.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			response.setMessage("Failed to fetch referral.");
			response.setData(null);
		}

		log.info("End ReferralMasterServiceImpl -> getReferralById() method");
		return response;
	}

	@Override
	public ResponseModel<ReferralMasterResponseDTO> updateReferralMaster(UUID id, ReferralMasterRequestDTO referralMasterRequestDTO) {
	    log.info("Begin ReferralMasterServiceImpl -> updateReferralMaster() method");

	    ResponseModel<ReferralMasterResponseDTO> response = new ResponseModel<>();

	    try {
	        ReferralMaster existingEntity = referralMasterRepository.findById(id)
	                .orElseThrow(() -> new RecordNotFoundException("Referral not found for ID: " + id));

	        UUID newRoleId = referralMasterRequestDTO.getRoleId();
	        if (newRoleId != null && !newRoleId.equals(existingEntity.getRole().getId())) {
	            Optional<Role> newRole = roleRepository.findById(newRoleId);
	            if (!newRole.isPresent()) {
	                throw new IllegalStateException("Provided role ID not found.");
	            }
	            existingEntity.setRole(newRole.get()); 
	        }

	        BeanUtils.copyProperties(referralMasterRequestDTO, existingEntity); 

	        ReferralMaster updatedReferralMaster = referralMasterRepository.save(existingEntity);
	        ReferralMasterResponseDTO updatedReferralDTO = convertToResponseDTO(updatedReferralMaster);

	        response.setStatusCode(HttpStatus.OK.toString());
	        response.setMessage("Referral updated successfully.");
	        response.setData(updatedReferralDTO);

	        log.info("Successfully updated referral with ID: {}", id);

	    } catch (RecordNotFoundException e) {
	        log.error("Referral update failed: {}", e.getMessage());
	        response.setStatusCode(HttpStatus.BAD_REQUEST.toString());
	        response.setMessage(e.getMessage());
	        response.setData(null);
	    } catch (Exception e) {
	        log.error("Unexpected error occurred while updating referral: {}", e.getMessage());
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	        response.setMessage("Failed to update referral.");
	        response.setData(null);
	    }

	    log.info("End ReferralMasterServiceImpl -> updateReferralMaster() method");
	    return response;
	}



	@Override
	public ResponseModel<ReferralMasterResponseDTO> deleteReferralMaster(UUID id) {
		log.info("Begin ReferralMasterServiceImpl -> deleteReferralMaster() method");

		ResponseModel<ReferralMasterResponseDTO> response = new ResponseModel<>();

		try {
			ReferralMaster existingReferralMaster = referralMasterRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("Referral not found for ID: " + id));

			ReferralMasterResponseDTO deletedReferralDTO = convertToResponseDTO(existingReferralMaster);

			referralMasterRepository.delete(existingReferralMaster);

			response.setStatusCode(HttpStatus.OK.toString());
			response.setMessage("Referral deleted successfully.");
			response.setData(deletedReferralDTO);

			log.info("Successfully deleted referral with ID: {}", id);
		} catch (RecordNotFoundException e) {
			log.error("Referral deletion failed: {}", e.getMessage());
			response.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			response.setMessage(e.getMessage());
			response.setData(null);
		} catch (Exception e) {
			log.error("Unexpected error occurred while deleting referral.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			response.setMessage("Failed to delete referral.");
			response.setData(null);
		}

		log.info("End ReferralMasterServiceImpl -> deleteReferralMaster() method");
		return response;
	}

}
