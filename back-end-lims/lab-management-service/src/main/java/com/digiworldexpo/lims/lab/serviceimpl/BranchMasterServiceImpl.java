package com.digiworldexpo.lims.lab.serviceimpl;

import java.sql.Timestamp;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.digiworldexpo.lims.constants.WeekDay;
import com.digiworldexpo.lims.entities.lab_management.BranchMaster;
import com.digiworldexpo.lims.entities.lab_management.BranchMasterTimings;
import com.digiworldexpo.lims.entities.master.BranchType;
import com.digiworldexpo.lims.entities.master.Role;
import com.digiworldexpo.lims.lab.dto.BranchMasterTimingsDTO;
import com.digiworldexpo.lims.lab.exception.DuplicateRecordFoundException;
import com.digiworldexpo.lims.lab.exception.RecordNotFoundException;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.repository.BranchMasterRepository;
import com.digiworldexpo.lims.lab.repository.BranchTypeRepository;
import com.digiworldexpo.lims.lab.repository.RoleRepository;
import com.digiworldexpo.lims.lab.request.BranchMasterRequestDTO;
import com.digiworldexpo.lims.lab.response.BranchMasterResponseDTO;
import com.digiworldexpo.lims.lab.service.BranchMasterService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BranchMasterServiceImpl implements BranchMasterService {

	private final BranchMasterRepository branchMasterRepository;
	private final RoleRepository roleRepository;
	private final BranchTypeRepository branchTypeRepository;

	public BranchMasterServiceImpl(BranchMasterRepository branchMasterRepository, RoleRepository roleRepository,
			BranchTypeRepository branchTypeRepository) {
		this.branchMasterRepository = branchMasterRepository;
		this.roleRepository = roleRepository;
		this.branchTypeRepository = branchTypeRepository;
	}

	@Override
	@Transactional
	public ResponseModel<BranchMasterResponseDTO> saveBranchMaster(UUID createdBy,
	        BranchMasterRequestDTO branchMasterRequestDTO) {
	    log.info("Begin BranchMasterService -> save() method");
	    ResponseModel<BranchMasterResponseDTO> response = new ResponseModel<>();
	    try {
	        if (createdBy == null || branchMasterRequestDTO.getEmail() == null
	                || branchMasterRequestDTO.getEmail().isEmpty() || branchMasterRequestDTO.getBranchType() == null) {
	            throw new IllegalArgumentException("Invalid input: Required fields are missing.");
	        }
	        UUID branchTypeId = branchMasterRequestDTO.getBranchType();
	        Optional<BranchType> branchTypeOpt = branchTypeRepository.findById(branchTypeId);
	        if (branchTypeOpt.isEmpty()) {
	            throw new IllegalArgumentException("Invalid Branch Type: No branch type found with the given ID.");
	        }

	        BranchType branchType = branchTypeOpt.get(); // Get the entity
	        String branchTypeName = branchType.getBranchTypeName();
	        log.info("Branch Type Name: {}", branchTypeName);
	        // Check for Duplicate Email
	        Optional<BranchMaster> existingEmail = branchMasterRepository.findByEmail(branchMasterRequestDTO.getEmail());
	        if (existingEmail.isPresent()) {
	            throw new DuplicateRecordFoundException(
	                    "A branch with this email already exists: " + branchMasterRequestDTO.getEmail());
	        }

	        // Check for Duplicate Branch Type for the Same Branch Name
	        Optional<BranchMaster> existingBranch = branchMasterRepository
	                .findByBranchNameAndBranchType(branchMasterRequestDTO.getBranchName(), branchType);

	        if (existingBranch.isPresent()) {
	            throw new DuplicateRecordFoundException("A branch with the same name and type already exists: "
	                    + branchMasterRequestDTO.getBranchName());
	        }

	        // ✅ Generate Branch Sequence ID
	        String sequenceId = branchMasterRepository.getNextFormattedBranchSequenceId();
	        log.info("Generated Branch Sequence ID: {}", sequenceId);

	        // ✅ Map DTO to Entity
	        BranchMaster branchEntity = mapToEntity(branchMasterRequestDTO, createdBy);
	        branchEntity.setBranchSequenceId(sequenceId);
	        branchEntity.setCreatedBy(createdBy);
	        branchEntity.setCreatedOn(new Timestamp(System.currentTimeMillis()));
	        branchEntity.setBranchType(branchType); // Set the resolved BranchType entity

	        // ✅ Prepare Branch Timings
	        List<BranchMasterTimings> timings;
	        if (branchMasterRequestDTO.getAvailabilities() != null
	                && !branchMasterRequestDTO.getAvailabilities().isEmpty()) {
	            timings = branchMasterRequestDTO.getAvailabilities().stream().map(dto -> {
	                BranchMasterTimings timing = mapToBranchTimingsEntity(dto, createdBy);
	                timing.setBranchMaster(branchEntity); // Associate with branch
	                return timing;
	            }).collect(Collectors.toList());
	        } else {
	            timings = createDefaultBranchTimings(branchEntity, createdBy);
	        }

	        // ✅ Set timings in branchEntity BEFORE saving it
	        System.out.println("timingssss"+timings);
	        branchEntity.setBranchTime(timings);

	        // ✅ Save Branch along with Timings
	        System.out.println("entitytimings"+branchEntity);
	        BranchMaster savedBranch = branchMasterRepository.save(branchEntity);
	        log.info("BranchMaster saved with ID: {}", savedBranch.getId());

	        // ✅ Prepare Response
	        BranchMasterResponseDTO savedDTO = mapToResponseDTO(savedBranch);
	        response.setStatusCode(HttpStatus.OK.toString());
	        response.setMessage("Branch saved successfully.");
	        response.setData(savedDTO);

	        log.info("Branch saved successfully with ID: {}", savedDTO.getId());

	    } catch (IllegalArgumentException e) {
	        log.warn("Validation Error: {}", e.getMessage());
	        response.setStatusCode(HttpStatus.BAD_REQUEST.toString());
	        response.setMessage(e.getMessage());

	    } catch (DuplicateRecordFoundException e) {
	        log.warn("Duplicate Record Error: {}", e.getMessage());
	        response.setStatusCode(HttpStatus.CONFLICT.toString()); // 409 Conflict
	        response.setMessage(e.getMessage());

	    } catch (Exception e) {
	        log.error("Unexpected Error: {}", e.getMessage(), e);
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	        response.setMessage("An unexpected error occurred while saving the Branch.");
	    }

	    return response;
	}

	private BranchMaster mapToEntity(BranchMasterRequestDTO dto, UUID createdBy) {
		BranchMaster branchMaster = new BranchMaster();

		// ✅ Copy properties from DTO to Entity
		BeanUtils.copyProperties(dto, branchMaster);

		// ✅ Manually set fields that require special handling
		branchMaster.setCreatedBy(createdBy);

		if (dto.getRoleId() != null) {
			Role role = roleRepository.findById(dto.getRoleId())
					.orElseThrow(() -> new IllegalArgumentException("Role ID not found: " + dto.getRoleId()));
			branchMaster.setRole(role);
		}
		return branchMaster;
	}

	private List<BranchMasterTimings> createDefaultBranchTimings(BranchMaster branchEntity, UUID createdBy) {
		List<BranchMasterTimings> defaultTimings = new ArrayList<>();
		WeekDay[] weekDays = WeekDay.values();

		for (WeekDay weekDay : weekDays) {
			BranchMasterTimings timing = new BranchMasterTimings();
			timing.setWeekDay(weekDay);
			timing.setStartTime(Timestamp.valueOf("09:00:00")); // Default 9 AM
			timing.setEndTime(Timestamp.valueOf("18:00:00")); // Default 6 PM
			timing.setAvailable(true);
			timing.setBranchMaster(branchEntity);
			timing.setCreatedBy(createdBy);
			defaultTimings.add(timing);
		}
		return defaultTimings;
	}

	private BranchMasterResponseDTO mapToResponseDTO(BranchMaster branchMaster) {
		BranchMasterResponseDTO dto = new BranchMasterResponseDTO();

		// ✅ Copy common properties
		BeanUtils.copyProperties(branchMaster, dto);

		// ✅ Convert Timings to DTO List manually
		if (branchMaster.getBranchTime() != null) {
			List<BranchMasterTimingsDTO> timingDTOs = branchMaster.getBranchTime().stream()
					.map(this::mapToBranchMasterTimingsDTO).collect(Collectors.toList());
			dto.setAvailabilities(timingDTOs);
		}
		System.out.println("hscf"+dto);
		

		return dto;
	}

	private BranchMasterTimings mapToBranchTimingsEntity(BranchMasterTimingsDTO dto, UUID createdBy) {
		BranchMasterTimings timing = new BranchMasterTimings();
//		timing.setId(dto.getId());
		timing.setWeekDay(dto.getWeekDay());
		timing.setStartTime(dto.getStartTime());
		timing.setEndTime(dto.getEndTime());
		timing.setAvailable(dto.isAvailable());
		timing.setCreatedBy(createdBy);
		return timing;
	}

	private BranchMasterTimingsDTO mapToBranchMasterTimingsDTO(BranchMasterTimings timing) {
		BranchMasterTimingsDTO dto = new BranchMasterTimingsDTO();
		dto.setId(timing.getId());
		dto.setWeekDay(timing.getWeekDay());
		dto.setStartTime(timing.getStartTime());
		dto.setEndTime(timing.getEndTime());
		dto.setAvailable(timing.isAvailable());
		return dto;
	}

	@Override
	@Transactional
	public ResponseModel<BranchMasterResponseDTO> getBranchById(UUID id) {
		log.info("Begin BranchMasterServiceImpl -> getBranchById() method...");

		ResponseModel<BranchMasterResponseDTO> responseModel = new ResponseModel<>();

		try {
			// Fetch BranchMaster by ID
			Optional<BranchMaster> optionalBranch = branchMasterRepository.findById(id);

			if (optionalBranch.isEmpty()) {
				throw new RecordNotFoundException("Branch not found with ID: " + id);
			}

			// Convert to DTO
			BranchMaster branchMaster = optionalBranch.get();
			BranchMasterResponseDTO branchMasterDTO = mapToResponseDTO(branchMaster);

			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setData(branchMasterDTO);
			responseModel.setMessage("Branch retrieved successfully.");
			log.info("Branch found: {}", branchMasterDTO);
		} catch (RecordNotFoundException e) {
			log.warn(e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
		} catch (Exception e) {
			log.error("Error occurred while fetching branch by ID: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Failed to retrieve branch");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}
		log.info("End BranchMasterServiceImpl -> getBranchById() method...");
		return responseModel;
	}

	@Override
	@Transactional
	public ResponseModel<BranchMasterResponseDTO> deleteBranch(UUID id) {
		log.info("Begin BranchMasterServiceImpl -> deleteBranch() method with ID: {}", id);

		ResponseModel<BranchMasterResponseDTO> responseModel = new ResponseModel<>();

		try {
			Optional<BranchMaster> branchOptional = branchMasterRepository.findById(id);
			if (branchOptional.isEmpty()) {
				log.warn("Branch not found with ID: {}", id);
				throw new RecordNotFoundException("Branch not found with ID: " + id);
			}

			BranchMaster branchMaster = branchOptional.get();
//	        branchMaster.setActive(false);
//	        branchMasterRepository.save(branchMaster);
			branchMasterRepository.delete(branchMaster);

			log.info("Branch deleted successfully with ID: {}", id);
			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Branch deleted successfully");
			responseModel.setData(null);

		} catch (RecordNotFoundException e) {
			log.warn("Record not found exception: {}", e.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
			responseModel.setMessage(e.getMessage());
			responseModel.setData(null);
		} catch (Exception e) {
			log.error("Error in deleteBranch(): {}", e.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error occurred while deleting branch: " + e.getMessage());
			responseModel.setData(null);
		}

		log.info("End BranchMasterServiceImpl -> deleteBranch() method with ID: {}", id);
		return responseModel;
	}

	@Override
	@Transactional
	public ResponseModel<BranchMasterResponseDTO> updateBranch(UUID updatedBy, UUID id, 
	        BranchMasterRequestDTO updatedBranchDTO) {
	    
	    log.info("Begin updateBranch() with ID: {}", id);
	    ResponseModel<BranchMasterResponseDTO> responseModel = new ResponseModel<>();

	    try {
	        // ✅ Fetch Existing Branch
	        BranchMaster existingBranch = branchMasterRepository.findById(id)
	                .orElseThrow(() -> new RecordNotFoundException("Branch not found for the given ID: " + id));

	        UUID createdBy = existingBranch.getCreatedBy();
	        if (createdBy == null) {
	            throw new IllegalStateException("CreatedBy cannot be null when updating Branch.");
	        }
	        

	        // ✅ Update Basic Fields
//	        existingBranch.setBranchName(updatedBranchDTO.getBranchName());
//	        existingBranch.setPhoneCode(updatedBranchDTO.getPhoneCode());
//	        existingBranch.setPhoneNumber(updatedBranchDTO.getPhoneNumber());
//	        existingBranch.setCountryName(updatedBranchDTO.getCountryName());
//	        existingBranch.setStateName(updatedBranchDTO.getStateName());
//	        existingBranch.setCityName(updatedBranchDTO.getCityName());
//	        existingBranch.setPincode(updatedBranchDTO.getPincode());
//	        existingBranch.setAddress(updatedBranchDTO.getAddress());
//	        existingBranch.setReportHeader(updatedBranchDTO.getReportHeader());
//	        existingBranch.setReportFooter(updatedBranchDTO.getReportFooter());
//	        existingBranch.setBillHeader(updatedBranchDTO.getBillHeader());
//	        existingBranch.setBillFooter(updatedBranchDTO.getBillFooter());
//	        existingBranch.setEmail(updatedBranchDTO.getEmail());
//	        BeanUtils.copyProperties(branchMaster, branchMasterDTO); // Copy properties
	     // ✅ Copy Basic Properties (Avoid Null Overwrites)
	        BeanUtils.copyProperties(updatedBranchDTO, existingBranch,"roleId","bracnType","availabilities");
	        
	        

	        // ✅ Preserve Modification Info
	        existingBranch.setModifiedBy(updatedBy);
	        existingBranch.setModifiedOn(new Timestamp(System.currentTimeMillis()));

	        log.info("Updating Branch - ModifiedBy: {}, ModifiedOn: {}", updatedBy, existingBranch.getModifiedOn());

	        // ✅ Update Role (Only If Provided)
	        if (updatedBranchDTO.getRoleId() != null) {
	            Role newRole = roleRepository.findById(updatedBranchDTO.getRoleId())
	                    .orElseThrow(() -> new RecordNotFoundException("Role not found for the given ID."));
	            existingBranch.setRole(newRole);
	        }

	        // ✅ Update Branch Type (Only If Provided)
	        if (updatedBranchDTO.getBranchType() != null) {
	            BranchType newBranchType = branchTypeRepository.findById(updatedBranchDTO.getBranchType())
	                    .orElseThrow(() -> new RecordNotFoundException("Branch Type not found for the given ID."));
	            existingBranch.setBranchType(newBranchType);
	        }

	        // ✅ Update Branch Timings (Only If Provided)
	        if (updatedBranchDTO.getAvailabilities() != null) {
	            existingBranch.getBranchTime().clear();  // Clear only if new data is provided
	            List<BranchMasterTimings> updatedTimings = updatedBranchDTO.getAvailabilities().stream()
	                .map(dto -> {
	                    BranchMasterTimings timing = mapToBranchTimingsEntity(dto, createdBy);
	                    timing.setBranchMaster(existingBranch);
	                    return timing;
	                }).collect(Collectors.toList());

	            existingBranch.getBranchTime().addAll(updatedTimings);
	        }

	        // ✅ Save Updated Entity
	        System.out.println("hgsdgv"+existingBranch);
	        BranchMaster savedBranch = branchMasterRepository.save(existingBranch);
	        BranchMasterResponseDTO savedBranchDTO = mapToResponseDTO(savedBranch);

	        responseModel.setStatusCode(HttpStatus.OK.toString());
	        responseModel.setMessage("Branch updated successfully.");
	        responseModel.setData(savedBranchDTO);

	        log.info("Branch updated successfully with ID: {}", savedBranch.getId());

	    } catch (RecordNotFoundException e) {
	        log.warn("Record Not Found: {}", e.getMessage());
	        responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
	        responseModel.setMessage(e.getMessage());
	    } catch (IllegalStateException e) {
	        log.warn("Illegal State Error: {}", e.getMessage());
	        responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
	        responseModel.setMessage(e.getMessage());
	    } catch (Exception e) {
	        log.error("Unexpected Error: {}", e.getMessage(), e);
	        responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	        responseModel.setMessage("An unexpected error occurred while updating the Branch.");
	    }

	    log.info("End updateBranch() with ID: {}", id);
	    return responseModel;
	}


	@Override
	@Transactional
	public ResponseModel<List<BranchMasterResponseDTO>> getAllBranches(String searchBy, int pageNumber, int pageSize,
	                                                                   String sortBy, UUID createdBy) {
	    log.info("Begin BranchMasterServiceImpl -> getAllBranches() method...");

	    ResponseModel<List<BranchMasterResponseDTO>> responseModel = new ResponseModel<>();

	    try {
	        if (pageNumber < 0 || pageSize < 1) {
	            throw new IllegalArgumentException("Invalid pagination parameters.");
	        }

	        Sort sort = Sort.by(Sort.Direction.ASC, sortBy);
	        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

	        long totalCount = branchMasterRepository.countByCreatedBy(createdBy);

	        Page<BranchMaster> branchPage;
	        if (searchBy != null && !searchBy.trim().isEmpty()) {
	            log.info("Searching branches with keyword '{}' for createdBy '{}'", searchBy, createdBy);
	            branchPage = branchMasterRepository.searchBranches(createdBy, searchBy.toLowerCase(), pageable);
	            totalCount = branchPage.getTotalElements();
	        } else {
	            branchPage = branchMasterRepository.findByCreatedBy(createdBy, pageable);
	            log.info("Retrieving all branches for createdBy '{}'", createdBy);
	        }

	        // ✅ Convert Entity to DTO
	        List<BranchMasterResponseDTO> branchDTOList = branchPage.getContent()
	                .stream()
	                .map(this::mapToResponseDTO)
	                .toList();

	        responseModel.setData(branchDTOList);
	        responseModel.setMessage("Branches retrieved successfully.");
	        responseModel.setStatusCode(String.valueOf(HttpStatus.OK.value()));
	        responseModel.setTotalCount((int) totalCount);
	        responseModel.setPageNumber(pageNumber);
	        responseModel.setPageSize(pageSize);

	    } catch (IllegalArgumentException e) {
	        responseModel.setData(null);
	        responseModel.setMessage(e.getMessage());
	        responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
	        log.error("Invalid input parameters: {}", e.getMessage());
	    } catch (Exception e) {
	        responseModel.setData(null);
	        responseModel.setMessage("Failed to retrieve branches.");
	        responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	        log.error("Error occurred while retrieving branches: {}", e.getMessage());
	    }

	    log.info("End BranchMasterServiceImpl -> getAllBranches() method...");
	    return responseModel;
	}


}
