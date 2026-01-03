package com.digiworldexpo.lims.lab.serviceimpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
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
import com.digiworldexpo.lims.entities.lab_management.DoctorAvailability;
import com.digiworldexpo.lims.entities.lab_management.DoctorMaster;
import com.digiworldexpo.lims.entities.lab_management.Lab;
import com.digiworldexpo.lims.entities.lab_management.LabDepartment;
import com.digiworldexpo.lims.entities.master.Account;
import com.digiworldexpo.lims.entities.master.Role;
import com.digiworldexpo.lims.lab.dto.DoctorAvailabilityDTO;
import com.digiworldexpo.lims.lab.exception.RecordNotFoundException;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.model.UserModel;
import com.digiworldexpo.lims.lab.repository.AccountRepository;
import com.digiworldexpo.lims.lab.repository.DepartmentRepository;
import com.digiworldexpo.lims.lab.repository.DoctorMasterRepository;
import com.digiworldexpo.lims.lab.repository.LabRepository;
import com.digiworldexpo.lims.lab.repository.RoleRepository;
import com.digiworldexpo.lims.lab.request.DoctorMasterRequestDTO;
import com.digiworldexpo.lims.lab.response.DoctorMasterResponseDTO;
import com.digiworldexpo.lims.lab.response.DoctorSearchResponseDTO;
import com.digiworldexpo.lims.lab.service.DoctorMasterService;
import com.digiworldexpo.lims.lab.util.RestClientUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DoctorMasterServiceImpl implements DoctorMasterService {
	
	private final DoctorMasterRepository doctorMasterRepository;
	private final DepartmentRepository departmentRepository;
	private final AccountRepository accountRepository;
	private final RoleRepository roleRepository;
	private final LabRepository labRepository;
	private final RestClientUtil restClientUtil;
	
	@Value("${lims.auth.signupUrl}")
    private String authSignUpUrl;
	

	

public DoctorMasterServiceImpl(DoctorMasterRepository doctorMasterRepository,
			DepartmentRepository departmentRepository, AccountRepository accountRepository,
			RoleRepository roleRepository, LabRepository labRepository, RestClientUtil restClientUtil) {
		super();
		this.doctorMasterRepository = doctorMasterRepository;
		this.departmentRepository = departmentRepository;
		this.accountRepository = accountRepository;
		this.roleRepository = roleRepository;
		this.labRepository = labRepository;
		this.restClientUtil = restClientUtil;
	}


	@Override
	public ResponseModel<DoctorMasterResponseDTO> saveDoctorMaster(UUID createdBy,
			DoctorMasterRequestDTO doctorMasterDTO) {
		log.info("Begin DoctorMasterService -> save() method");
		ResponseModel<DoctorMasterResponseDTO> response = new ResponseModel<>();

		try {
			String sequenceId = "DOC" + doctorMasterRepository.getNextFormattedDoctorSequenceId();
			log.info("Generated Doctor Sequence ID: {}", sequenceId);

			DoctorMaster doctorEntity = mapToEntity(doctorMasterDTO, createdBy);
			doctorEntity.setCreatedBy(createdBy);
			doctorEntity.setDoctorSequenceId(sequenceId);
			doctorEntity.setShowOnAppointment(Boolean.TRUE.equals(doctorMasterDTO.getShowOnAppointment()));

			ResponseModel<UUID> signupResponse = signupUser(doctorMasterDTO);
			if (!HttpStatus.OK.toString().equals(signupResponse.getStatusCode()) || signupResponse.getData() == null) {
				log.error("User signup failed: {}", signupResponse.getMessage());
				response.setStatusCode(signupResponse.getStatusCode());
				response.setMessage(signupResponse.getMessage());
				return response;
			}

			if (doctorMasterDTO.getAvailabilities() != null && !doctorMasterDTO.getAvailabilities().isEmpty()
					&& Boolean.TRUE.equals(doctorMasterDTO.getShowOnAppointment())) {
				List<DoctorAvailability> availabilities = doctorMasterDTO.getAvailabilities().stream()
						.map(dto -> mapToAvailabilityEntity(dto, createdBy)).collect(Collectors.toList());

				availabilities.forEach(availability -> {
					availability.setDoctorMaster(doctorEntity);
				});
				doctorEntity.setAvailabilities(availabilities);
			} else if (Boolean.TRUE.equals(doctorMasterDTO.getShowOnAppointment())) {
				List<DoctorAvailability> defaultAvailabilities = createDefaultAvailabilities(doctorEntity, createdBy);
				doctorEntity.setAvailabilities(defaultAvailabilities);
			}

			DoctorMaster savedEntity = doctorMasterRepository.save(doctorEntity);
			DoctorMasterResponseDTO savedDTO = mapToResponseDTO(savedEntity);

			response.setStatusCode(HttpStatus.OK.toString());
			response.setMessage("Doctor saved successfully.");
			response.setData(savedDTO);

			log.info("Doctor saved successfully with ID: {}", savedDTO.getId());
		} catch (Exception e) {
			log.error("Error occurred while saving Doctor: {}", e.getMessage(), e);
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			response.setMessage("An error occurred while saving the Doctor.");
		}

		return response;
	}

	private DoctorAvailability mapToAvailabilityEntity(DoctorAvailabilityDTO dto, UUID createdBy) {
		DoctorAvailability availability = new DoctorAvailability();
		availability.setWeekDay(dto.getWeekDay());
		availability.setStartTime(dto.getStartTime());
		availability.setEndTime(dto.getEndTime());
		availability.setAvailable(dto.isAvailable());
		availability.setCreatedBy(createdBy);
		return availability;
	}

	private List<DoctorAvailability> createDefaultAvailabilities(DoctorMaster doctorEntity, UUID createdBy) {
		List<DoctorAvailability> defaultAvailabilities = new ArrayList<>();
		WeekDay[] weekDays = WeekDay.values();

		for (WeekDay weekDay : weekDays) {
			DoctorAvailability availability = new DoctorAvailability();
			availability.setWeekDay(weekDay);
			availability.setStartTime(new Timestamp(System.currentTimeMillis()));
			availability.setEndTime(new Timestamp(System.currentTimeMillis()));
			availability.setAvailable(true);
			availability.setDoctorMaster(doctorEntity);
			availability.setCreatedBy(createdBy);
			defaultAvailabilities.add(availability);
		}

		return defaultAvailabilities;
	}

	private ResponseModel<UUID> signupUser(DoctorMasterRequestDTO doctorMasterDTO) {
		ResponseModel<UUID> responseModel = new ResponseModel<>();
		ResponseEntity<Object> responseEntity = createUserAndReturnResponseEntity(doctorMasterDTO);

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
		}

		log.error("Error while signing up user in auth service - {}", responseEntity.getBody());
		responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		responseModel.setMessage("User signup failed due to an unexpected error.");
		return responseModel;
	}

	private ResponseEntity<Object> createUserAndReturnResponseEntity(DoctorMasterRequestDTO doctorMasterDTO) {
		Optional<Account> account = accountRepository.findByAccountName("LabAdmin");
		if (!account.isPresent()) {
			throw new IllegalStateException("Required account 'LabAdmin' not found.");
		}
		Optional<Role> role = roleRepository.findById(doctorMasterDTO.getRoleId());
		if (!role.isPresent()) {
			throw new IllegalStateException("Required role not found.");
		}
		UserModel userModel = new UserModel().builder().accountType(account.get().getId())
				.role(role.get().getId()).email(doctorMasterDTO.getEmail()).password(doctorMasterDTO.getSetPassword()).lab(doctorMasterDTO.getLabId()).build();
		return restClientUtil.postForEntity(authSignUpUrl, userModel, Object.class);
	}

	private DoctorMaster mapToEntity(DoctorMasterRequestDTO dto, UUID createdBy) {
		DoctorMaster entity = new DoctorMaster();
		BeanUtils.copyProperties(dto, entity);

		if (dto.getRoleId() != null) {
			Optional<Role> role = roleRepository.findById(dto.getRoleId());
			role.ifPresent(entity::setRole);
		}

		if (dto.getLabId() != null) {
			Optional<Lab> lab = labRepository.findById(dto.getLabId());
			lab.ifPresent(entity::setLab);
		}

		if (dto.getDepartmentId() != null) {
			Optional<LabDepartment> department = departmentRepository.findById(dto.getDepartmentId());
			department.ifPresent(entity::setDepartment);
		}

		return entity;
	}

	private DoctorMasterResponseDTO mapToResponseDTO(DoctorMaster entity) {
		DoctorMasterResponseDTO dto = new DoctorMasterResponseDTO();
		BeanUtils.copyProperties(entity, dto);

		if (entity.getRole() != null) {
			dto.setRoleId(entity.getRole().getId());
		}

		if (entity.getLab() != null) {
			dto.setLabId(entity.getLab().getId());
		}

		if (entity.getDepartment() != null) {
			dto.setDepartmentId(entity.getDepartment().getId());
		}

		if (entity.getAvailabilities() != null) {
			List<DoctorAvailabilityDTO> availabilityDTOs = entity.getAvailabilities().stream()
					.map(this::mapToAvailabilityDTO).collect(Collectors.toList());
			dto.setAvailabilities(availabilityDTOs);
		}

		return dto;
	}

	private DoctorAvailabilityDTO mapToAvailabilityDTO(DoctorAvailability entity) {
		DoctorAvailabilityDTO dto = new DoctorAvailabilityDTO();
		BeanUtils.copyProperties(entity, dto);
		return dto;
	}
			


	@Override
	public ResponseModel<DoctorMasterResponseDTO> getDoctorById(UUID id) {
	    log.info("Begin DoctorMasterServiceImpl -> getDoctorById() method with ID: {}", id);

	    ResponseModel<DoctorMasterResponseDTO> response = new ResponseModel<>();

	    try {
	        DoctorMaster doctorMaster = doctorMasterRepository.findById(id)
	                .orElseThrow(() -> new RecordNotFoundException("Doctor not found with ID: " + id));

	        DoctorMasterResponseDTO doctorDTO = mapToResponseDTO(doctorMaster);

	        response.setData(doctorDTO);
	        response.setStatusCode(HttpStatus.OK.toString());
	        response.setMessage("Doctor found successfully");
	        log.info("Doctor with ID: {} found and converted to DTO", id);

	    } catch (RecordNotFoundException e) {
	        log.error("Doctor not found: {}", e.getMessage());
	        response.setStatusCode(HttpStatus.NOT_FOUND.toString());
	        response.setMessage(e.getMessage());
	        response.setData(null);
	    } catch (Exception e) {
	        log.error("Error occurred while fetching doctor with ID {}: {}", id, e.getMessage(), e);
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	        response.setMessage("Failed to fetch doctor details");
	        response.setData(null);
	    }

	    log.info("End DoctorMasterServiceImpl -> getDoctorById() method");
	    return response;
	}

	@Override
	public ResponseModel<DoctorMasterResponseDTO> deleteDoctorById(UUID id) {
	    log.info("Begin DoctorServiceImpl -> deleteDoctorMaster() method with ID: {}", id);

	    ResponseModel<DoctorMasterResponseDTO> response = new ResponseModel<>();
	    try {
	        DoctorMaster doctorMaster = doctorMasterRepository.findById(id)
	                .orElseThrow(() -> new RecordNotFoundException("Doctor not found with ID: " + id));

	        doctorMasterRepository.deleteById(id);

	        DoctorMasterResponseDTO doctorMasterResponseDTO = mapToResponseDTO(doctorMaster);

	        response.setStatusCode(HttpStatus.OK.toString());
	        response.setMessage("Doctor deleted successfully with ID: " + id);
	        response.setData(doctorMasterResponseDTO);
	        log.info("Doctor with ID: {} deleted successfully.", id);

	    } catch (RecordNotFoundException e) {
	        log.error("Doctor deletion failed: {}", e.getMessage());
	        response.setStatusCode(HttpStatus.NOT_FOUND.toString());
	        response.setMessage(e.getMessage());
	        response.setData(null);
	    } catch (Exception e) {
	        log.error("Error occurred while deleting doctor with ID {}: {}", id, e.getMessage());
	        response.setMessage("Failed to delete doctor: " + e.getMessage());
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	        response.setData(null);
	    }

	    log.info("End DoctorServiceImpl -> deleteDoctorMaster() method");
	    return response;
	}



	@Override
	public ResponseModel<DoctorMasterResponseDTO> updateDoctorById(UUID id, DoctorMasterRequestDTO doctorMasterDTO) {
	    log.info("Begin DoctorMasterServiceImpl -> updateDoctorById() method");

	    ResponseModel<DoctorMasterResponseDTO> response = new ResponseModel<>();

	    try {
	        DoctorMaster existingDoctor = doctorMasterRepository.findById(id)
	                .orElseThrow(() -> new RecordNotFoundException("Doctor not found with ID: " + id));

	        UUID createdBy = existingDoctor.getCreatedBy();

	        DoctorMaster updatedDoctor = mapToEntity(doctorMasterDTO, createdBy);
	        updatedDoctor.setId(existingDoctor.getId());
	        updatedDoctor.setCreatedBy(existingDoctor.getCreatedBy());
	        updatedDoctor.setCreatedOn(existingDoctor.getCreatedOn());
	        updatedDoctor.setModifiedBy(createdBy);
	        updatedDoctor.setModifiedOn(new Timestamp(System.currentTimeMillis()));
	        updatedDoctor.setDoctorSequenceId(existingDoctor.getDoctorSequenceId());

	        log.info("Updating Doctor - ModifiedBy: {}, ModifiedOn: {}", createdBy, updatedDoctor.getModifiedOn());

	        if (doctorMasterDTO.getRoleId() != null) {
	            Role newRole = roleRepository.findById(doctorMasterDTO.getRoleId())
	                    .orElseThrow(() -> new RecordNotFoundException("Role not found for the given ID."));
	            updatedDoctor.setRole(newRole);
	        } else {
	            updatedDoctor.setRole(existingDoctor.getRole()); 
	        }

	        if (doctorMasterDTO.getDepartmentId() != null) {
	            LabDepartment department = departmentRepository.findById(doctorMasterDTO.getDepartmentId())
	                    .orElseThrow(() -> new RecordNotFoundException("Department not found with ID: " + doctorMasterDTO.getDepartmentId()));
	            updatedDoctor.setDepartment(department);
	        } else {
	            updatedDoctor.setDepartment(existingDoctor.getDepartment()); 
	        }

	        if (existingDoctor.getLab() != null) {
	            updatedDoctor.setLab(existingDoctor.getLab());
	        }

	        updatedDoctor.setShowOnAppointment(Boolean.TRUE.equals(doctorMasterDTO.getShowOnAppointment()));

	        if (Boolean.TRUE.equals(doctorMasterDTO.getShowOnAppointment())) {
	            if (doctorMasterDTO.getAvailabilities() != null && !doctorMasterDTO.getAvailabilities().isEmpty()) {
	                List<DoctorAvailability> availabilities = doctorMasterDTO.getAvailabilities().stream()
	                        .map(dto -> mapToAvailabilityEntity(dto, createdBy))
	                        .collect(Collectors.toList());

	                availabilities.forEach(availability -> availability.setDoctorMaster(updatedDoctor));
	                updatedDoctor.setAvailabilities(availabilities);
	            } else {
	                List<DoctorAvailability> defaultAvailabilities = createDefaultAvailabilities(updatedDoctor, createdBy);
	                updatedDoctor.setAvailabilities(defaultAvailabilities);
	            }
	        } else {
	            updatedDoctor.setAvailabilities(Collections.emptyList());
	        }

	        DoctorMaster savedDoctor = doctorMasterRepository.save(updatedDoctor);
	        DoctorMasterResponseDTO savedDoctorDTO = mapToResponseDTO(savedDoctor);

	        response.setStatusCode(HttpStatus.OK.toString());
	        response.setMessage("Doctor updated successfully.");
	        response.setData(savedDoctorDTO);

	        log.info("Doctor updated successfully with ID: {}", savedDoctorDTO.getId());
	    } catch (RecordNotFoundException e) {
	        log.error("Doctor not found: {}", e.getMessage());
	        response.setStatusCode(HttpStatus.NOT_FOUND.toString());
	        response.setMessage(e.getMessage());
	        response.setData(null);
	    } catch (Exception e) {
	        log.error("Error while updating doctor: {}", e.getMessage(), e);
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	        response.setMessage("An error occurred while updating the Doctor.");
	    }

	    return response;
	}


	@Override
	public ResponseModel<List<DoctorSearchResponseDTO>> getAllDoctors(
	        UUID createdBy, String keyword, Boolean flag, Integer pageNumber, Integer pageSize) {
	    log.info("Begin DoctorMasterServiceImpl -> getAllDoctors() method");

	    ResponseModel<List<DoctorSearchResponseDTO>> response = new ResponseModel<>();

	    try {
	        List<DoctorSearchResponseDTO> doctorDTOs = new ArrayList<>(); 
	        List<DoctorMaster> doctors;

	        if (keyword != null && !keyword.trim().isEmpty()) {
	            doctors = doctorMasterRepository.findByCreatedByAndActiveByDoctor(
	                    createdBy, flag, keyword);
	        } else {
	            doctors = doctorMasterRepository.findAllByCreatedByAndActive(
	                    createdBy, true);
	        }

	        doctorDTOs = doctors.stream()
	                .map(doctor -> new DoctorSearchResponseDTO(
	                        doctor.getId(),
	                        doctor.getDoctorSequenceId(),
	                        doctor.getDoctorName(),
	                        doctor.getDepartment() != null ? doctor.getDepartment().getDepartmentName() : null,
	                        doctor.getEmail(),
	                        doctor.getIsReportApprover()))
	                	 .collect(Collectors.toList());

	        int start = pageNumber * pageSize;
	        int end = Math.min(start + pageSize, doctorDTOs.size());
	        List<DoctorSearchResponseDTO> subList = doctorDTOs.subList(start, end);

	        response.setData(subList);
	        response.setTotalCount(doctorDTOs.size());
	        response.setPageNumber(pageNumber);
	        response.setPageSize(pageSize);
	        response.setStatusCode(HttpStatus.OK.toString());
	        response.setMessage("Doctors fetched successfully.");

	    } catch (Exception e) {
	        log.error("Error in DoctorMasterServiceImpl -> getAllDoctors() method: {}", e.getMessage());
	        response.setData(null);
	        response.setMessage("Failed to fetch doctors.");
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	    }

	    log.info("End DoctorMasterServiceImpl -> getAllDoctors() method");
	    return response;
	}

}