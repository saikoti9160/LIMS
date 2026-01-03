package com.digiworldexpo.lims.lab.serviceimpl;

import java.sql.Timestamp;
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

import com.digiworldexpo.lims.constants.WeekDay;
import com.digiworldexpo.lims.entities.lab_management.Lab;
import com.digiworldexpo.lims.entities.lab_management.PhlebotomistAvailability;
import com.digiworldexpo.lims.entities.lab_management.PhlebotomistMaster;
import com.digiworldexpo.lims.entities.master.Account;
import com.digiworldexpo.lims.entities.master.Role;
import com.digiworldexpo.lims.lab.dto.DoctorAvailabilityDTO;
import com.digiworldexpo.lims.lab.dto.PhlebotomistAvailabilityDTO;
import com.digiworldexpo.lims.lab.exception.RecordNotFoundException;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.model.UserModel;
import com.digiworldexpo.lims.lab.repository.AccountRepository;
import com.digiworldexpo.lims.lab.repository.LabRepository;
import com.digiworldexpo.lims.lab.repository.PhlebotomistMasterRepository;
import com.digiworldexpo.lims.lab.repository.RoleRepository;
import com.digiworldexpo.lims.lab.request.PhlebotomistMasterRequestDTO;
import com.digiworldexpo.lims.lab.response.PhlebotomistMasterResponseDTO;
import com.digiworldexpo.lims.lab.response.PhlebotomistMasterSearchResponse;
import com.digiworldexpo.lims.lab.service.PhlebotomistMasterService;
import com.digiworldexpo.lims.lab.util.RestClientUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PhlebotomistMasterServiceImpl implements PhlebotomistMasterService {

	private final PhlebotomistMasterRepository phlebotomistRepository;
	private final RoleRepository roleRepository;
	private final LabRepository labRepository;
	private final AccountRepository accountRepository;
	 private final RestClientUtil restClientUtil;
	
	@Value("${lims.auth.signupUrl}")
	private String authSignUpUrl;
	    


	public PhlebotomistMasterServiceImpl(PhlebotomistMasterRepository phlebotomistRepository,
			RoleRepository roleRepository, LabRepository labRepository, AccountRepository accountRepository,
			RestClientUtil restClientUtil) {
		super();
		this.phlebotomistRepository = phlebotomistRepository;
		this.roleRepository = roleRepository;
		this.labRepository = labRepository;
		this.accountRepository = accountRepository;
		this.restClientUtil = restClientUtil;
	}

	@Override
	public ResponseModel<PhlebotomistMasterResponseDTO> savePhlebotomistMaster(UUID createdBy, PhlebotomistMasterRequestDTO phlebotomistMasterDTO) {
	    log.info("Begin PhlebotomistMasterService -> save() method");
	    ResponseModel<PhlebotomistMasterResponseDTO> response = new ResponseModel<>();

	    try {
	    	 String sequenceId = "Phle" + phlebotomistRepository.getNextFormattedPhlebotomistSequenceId();
           log.info("Generated Phlebotomist Sequence ID: {}", sequenceId);
	    	 
	        PhlebotomistMaster phlebotomistEntity = mapToEntity(phlebotomistMasterDTO, createdBy);
	        phlebotomistEntity.setCreatedBy(createdBy);
	        phlebotomistEntity.setPhlebotomistSequenceId(sequenceId);

	        ResponseModel<UUID> signupResponse = signupUser(phlebotomistMasterDTO);
	        if (!HttpStatus.OK.toString().equals(signupResponse.getStatusCode()) || signupResponse.getData() == null) {
	            log.error("User signup failed: {}", signupResponse.getMessage());
	            response.setStatusCode(signupResponse.getStatusCode());
	            response.setMessage(signupResponse.getMessage());
	            return response;
	        }

	        if (phlebotomistMasterDTO.getAvailabilities() != null && !phlebotomistMasterDTO.getAvailabilities().isEmpty()) {
	            List<PhlebotomistAvailability> availabilities = phlebotomistMasterDTO.getAvailabilities().stream()
	                .map(dto -> mapToAvailabilityEntity(dto, createdBy))
	                .collect(Collectors.toList());
	            
	            availabilities.forEach(availability -> {
	                availability.setPhlebotomistMaster(phlebotomistEntity);
	            });
	            phlebotomistEntity.setAvailabilities(availabilities);
	        } else {
	            List<PhlebotomistAvailability> defaultAvailabilities = createDefaultAvailabilities(phlebotomistEntity, createdBy);
	            phlebotomistEntity.setAvailabilities(defaultAvailabilities);
	        }

	        PhlebotomistMaster savedEntity = phlebotomistRepository.save(phlebotomistEntity);
	        PhlebotomistMasterResponseDTO savedDTO = mapToResponseDTO(savedEntity);

	        response.setStatusCode(HttpStatus.OK.toString());
	        response.setMessage("Phlebotomist saved successfully.");
	        response.setData(savedDTO);

	        log.info("Phlebotomist saved successfully with ID: {}", savedDTO.getId());
	    } catch (Exception e) {
	        log.error("Error occurred while saving Phlebotomist: {}", e.getMessage(), e);
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	        response.setMessage("An error occurred while saving the Phlebotomist.");
	    }

	    return response;
	}

	private PhlebotomistAvailability mapToAvailabilityEntity(PhlebotomistAvailabilityDTO dto, UUID createdBy) {
	    PhlebotomistAvailability availability = new PhlebotomistAvailability();
	    availability.setWeekDay(dto.getWeekDay());
	    availability.setStartTime(dto.getStartTime());
	    availability.setEndTime(dto.getEndTime());
	    availability.setAvailable(dto.isAvailable());
	    availability.setCreatedBy(createdBy);
	    return availability;
	}
    
	private List<PhlebotomistAvailability> createDefaultAvailabilities(PhlebotomistMaster phlebotomistEntity, UUID createdBy) {
	    List<PhlebotomistAvailability> defaultAvailabilities = new ArrayList<>();
		WeekDay[] weekDays = WeekDay.values();
	    
	    for (WeekDay weekDay : weekDays) {
	        PhlebotomistAvailability availability = new PhlebotomistAvailability();
	        availability.setWeekDay(weekDay);
	        availability.setStartTime(new Timestamp(System.currentTimeMillis()));
	        availability.setEndTime(new Timestamp(System.currentTimeMillis()));
	        availability.setAvailable(true);
	        availability.setPhlebotomistMaster(phlebotomistEntity);
	        availability.setCreatedBy(createdBy);
	        defaultAvailabilities.add(availability);
	    }

	    return defaultAvailabilities;
	}



    private ResponseModel<UUID> signupUser(PhlebotomistMasterRequestDTO phlebotomistMasterDTO) {
        ResponseModel<UUID> responseModel = new ResponseModel<>();
        ResponseEntity<Object> responseEntity = createUserAndReturnResponseEntity(phlebotomistMasterDTO);

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

    private ResponseEntity<Object> createUserAndReturnResponseEntity(PhlebotomistMasterRequestDTO phlebotomistMasterDTO) {
        UserModel userModel = new UserModel();

        Optional<Account> account = accountRepository.findByAccountName("LabAdmin");
        if (!account.isPresent()) {
            throw new IllegalStateException("Required account 'LabAdmin' not found.");
        }

        UUID roleId = phlebotomistMasterDTO.getRoleId();
        Optional<Role> role = roleRepository.findById(roleId);

        if (!role.isPresent()) {
            throw new IllegalStateException("Required role not found.");
        }

        userModel.setAccountType(account.get().getId());
        userModel.setRole(role.get().getId());
        userModel.setEmail(phlebotomistMasterDTO.getEmail());
        userModel.setPassword(phlebotomistMasterDTO.getSetPassword());
        userModel.setLab(phlebotomistMasterDTO.getLabId());

        return restClientUtil.postForEntity(authSignUpUrl, userModel, Object.class);
    }


    private PhlebotomistMaster mapToEntity(PhlebotomistMasterRequestDTO dto, UUID createdBy) {
        PhlebotomistMaster entity = new PhlebotomistMaster();
        BeanUtils.copyProperties(dto, entity);
        
        if (dto.getRoleId() != null) {
            Optional<Role> role = roleRepository.findById(dto.getRoleId());
            role.ifPresent(entity::setRole);
        }

        if (dto.getLabId() != null) {
            Optional<Lab> lab = labRepository.findById(dto.getLabId());
            lab.ifPresent(entity::setLab);
        }

        return entity;
    }

    private PhlebotomistMasterResponseDTO mapToResponseDTO(PhlebotomistMaster entity) {
        PhlebotomistMasterResponseDTO dto = new PhlebotomistMasterResponseDTO();
        BeanUtils.copyProperties(entity, dto);

        if (entity.getRole() != null) {
            dto.setRoleId(entity.getRole().getId());
        }

        if (entity.getLab() != null) {
            dto.setLabId(entity.getLab().getId());
        }
        
        if (entity.getAvailabilities() != null) {
            List<PhlebotomistAvailabilityDTO> availabilityDTOs = entity.getAvailabilities().stream()
                    .map(this::mapToAvailabilityDTO) 
                    .collect(Collectors.toList());
            dto.setAvailabilities(availabilityDTOs);
        }

        return dto;
    }
    
    private PhlebotomistAvailabilityDTO mapToAvailabilityDTO(PhlebotomistAvailability entity) {
        PhlebotomistAvailabilityDTO dto = new PhlebotomistAvailabilityDTO();
       BeanUtils.copyProperties(entity, dto);
        return dto;
    }


	@Override
	public ResponseModel<List<PhlebotomistMasterSearchResponse>> getAllPhlebotomist(
	        UUID createdBy, String keyword, Boolean flag, Integer pageNumber, Integer pageSize) {
	    log.info("Begin PhlebotomistMasterService -> getAll() method");

	    ResponseModel<List<PhlebotomistMasterSearchResponse>> response = new ResponseModel<>();

	    try {
	        List<PhlebotomistMasterSearchResponse> phlebotomistPage = new ArrayList<>(); 

	        if (keyword != null && !keyword.trim().isEmpty()) {
	            phlebotomistPage = phlebotomistRepository.findByCreatedByAndActiveAndKeyword(
	                    createdBy, flag, keyword);
	        } else {
	            List<PhlebotomistMaster> phlebotomistMasterPage = phlebotomistRepository.findAllByCreatedByAndActive(
	                    createdBy, true);
	            phlebotomistPage = phlebotomistMasterPage.stream()
	                    .map(phlebotomist -> new PhlebotomistMasterSearchResponse(
	                            phlebotomist.getId(),
	                            phlebotomist.getPhlebotomistSequenceId(),
	                            phlebotomist.getEmail(), // Corrected: email should be here
	                            phlebotomist.getName())) 
	                    .collect(Collectors.toList());
	        }

	        int start = pageNumber * pageSize;
	        int end = Math.min(start + pageSize, phlebotomistPage.size());
	        List<PhlebotomistMasterSearchResponse> subList = phlebotomistPage.subList(start, end);

	        response.setData(subList);
	        response.setTotalCount(phlebotomistPage.size());
	        response.setPageNumber(pageNumber);
	        response.setPageSize(pageSize);
	        response.setStatusCode(HttpStatus.OK.toString());
	        response.setMessage("Phlebotomists fetched successfully.");

	    } catch (Exception e) {
	        log.error("Error occurred while fetching phlebotomists: {}", e.getMessage());
	        response.setData(null);
	        response.setMessage("Failed to fetch phlebotomists.");
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	    }

	    log.info("End PhlebotomistMasterService -> getAll() method");
	    return response;
	}

	@Override
	public ResponseModel<PhlebotomistMasterResponseDTO> updatePhlebotomist(UUID id, PhlebotomistMasterRequestDTO phlebotomistMasterDTO) {
	    log.info("Begin PhlebotomistMasterService -> updatePhlebotomist() method");

	    ResponseModel<PhlebotomistMasterResponseDTO> response = new ResponseModel<>();

	    try {
	        PhlebotomistMaster existingPhlebotomist = phlebotomistRepository.findById(id)
	                .orElseThrow(() -> new RecordNotFoundException("Phlebotomist not found for the given ID."));

	        UUID createdBy = existingPhlebotomist.getCreatedBy();
	        if (createdBy == null) {
	            throw new IllegalStateException("CreatedBy cannot be null when updating Phlebotomist.");
	        }

	        PhlebotomistMaster updatedEntity = mapToEntity(phlebotomistMasterDTO, createdBy);
	        updatedEntity.setId(existingPhlebotomist.getId());
	        updatedEntity.setCreatedBy(existingPhlebotomist.getCreatedBy());
	        updatedEntity.setCreatedOn(existingPhlebotomist.getCreatedOn());
	        updatedEntity.setModifiedBy(createdBy);
	        updatedEntity.setModifiedOn(new Timestamp(System.currentTimeMillis()));
	        updatedEntity.setPhlebotomistSequenceId(existingPhlebotomist.getPhlebotomistSequenceId());

	        log.info("Updating Phlebotomist - ModifiedBy: {}, ModifiedOn: {}", createdBy, updatedEntity.getModifiedOn());

	        if (phlebotomistMasterDTO.getRoleId() != null) {
	            Role newRole = roleRepository.findById(phlebotomistMasterDTO.getRoleId())
	                    .orElseThrow(() -> new RecordNotFoundException("Role not found for the given ID."));
	            updatedEntity.setRole(newRole);
	        } else {
	            updatedEntity.setRole(existingPhlebotomist.getRole()); 
	        }

	        if (phlebotomistMasterDTO.getAvailabilities() != null && !phlebotomistMasterDTO.getAvailabilities().isEmpty()) {
	            List<PhlebotomistAvailability> availabilities = phlebotomistMasterDTO.getAvailabilities().stream()
	                    .map(dto -> mapToAvailabilityEntity(dto, createdBy))
	                    .collect(Collectors.toList());

	            availabilities.forEach(availability -> availability.setPhlebotomistMaster(updatedEntity));
	            updatedEntity.setAvailabilities(availabilities);
	        } else {
	            List<PhlebotomistAvailability> defaultAvailabilities = createDefaultAvailabilities(updatedEntity, createdBy);
	            updatedEntity.setAvailabilities(defaultAvailabilities);
	        }

	        if (existingPhlebotomist.getLab() != null) {
	            updatedEntity.setLab(existingPhlebotomist.getLab());
	        }

	        PhlebotomistMaster savedEntity = phlebotomistRepository.save(updatedEntity);
	        PhlebotomistMasterResponseDTO savedDTO = mapToResponseDTO(savedEntity);

	        response.setStatusCode(HttpStatus.OK.toString());
	        response.setMessage("Phlebotomist updated successfully.");
	        response.setData(savedDTO);

	        log.info("Phlebotomist updated successfully with ID: {}", savedDTO.getId());
	    } catch (Exception e) {
	        log.error("Error occurred while updating Phlebotomist: {}", e.getMessage(), e);
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	        response.setMessage("An error occurred while updating the Phlebotomist.");
	    }

	    return response;
	}


	@Override
	public ResponseModel<PhlebotomistMasterResponseDTO> getPhlebotomistById(UUID id) {
	    log.info("Begin PhlebotomistMasterService -> getById() method");

	    ResponseModel<PhlebotomistMasterResponseDTO> response = new ResponseModel<>();

	    try {
	        PhlebotomistMaster phlebotomistMaster = phlebotomistRepository.findById(id)
	                .orElseThrow(() -> new RecordNotFoundException("Phlebotomist not found with ID: " + id));

	        PhlebotomistMasterResponseDTO phlebotomistMasterDTO = mapToResponseDTO(phlebotomistMaster);

	        response.setStatusCode(HttpStatus.OK.toString()); 
	        response.setMessage("Phlebotomist fetched successfully.");
	        response.setData(phlebotomistMasterDTO);

	    } catch (RecordNotFoundException e) {
	        log.error("Record not found: {}", e.getMessage());
	        response.setStatusCode(HttpStatus.NOT_FOUND.toString()); 
	        response.setMessage(e.getMessage());
	        response.setData(null);
	    } catch (Exception e) {
	        log.error("Unexpected error occurred while fetching phlebotomist by ID.", e);
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	        response.setMessage("Failed to fetch phlebotomist.");
	        response.setData(null);
	    }

	    log.info("End PhlebotomistMasterService -> getById() method");
	    return response;
	}

	@Override
	public ResponseModel<PhlebotomistMasterResponseDTO> deletePhlebotomistById(UUID id) {
	    log.info("Begin PhlebotomistMasterService -> delete() method");

	    ResponseModel<PhlebotomistMasterResponseDTO> response = new ResponseModel<>();

	    try {
	        PhlebotomistMaster phlebotomistMaster = phlebotomistRepository.findById(id)
	                .orElseThrow(() -> new RecordNotFoundException("Phlebotomist not found with ID: " + id));

	        PhlebotomistMasterResponseDTO phlebotomistMasterResponseDTO = mapToResponseDTO(phlebotomistMaster);
	        phlebotomistRepository.deleteById(id);

	        response.setStatusCode(HttpStatus.OK.toString());
	        response.setMessage("Phlebotomist deleted successfully.");
	        response.setData(phlebotomistMasterResponseDTO);
	    } catch (RecordNotFoundException e) {
	        log.error("Invalid ID parameter: {}", e.getMessage());
	        response.setStatusCode(HttpStatus.NOT_FOUND.toString());
	        response.setMessage(e.getMessage());
	        response.setData(null);
	    } catch (Exception e) {
	        log.error("Unexpected error occurred while deleting phlebotomist.");
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	        response.setMessage("Failed to delete phlebotomist.");
	        response.setData(null);
	    }

	    log.info("End PhlebotomistMasterService -> delete() method");
	    return response;
	}


}


