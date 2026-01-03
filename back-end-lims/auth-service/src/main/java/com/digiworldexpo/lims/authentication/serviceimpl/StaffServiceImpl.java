package com.digiworldexpo.lims.authentication.serviceimpl;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.digiworldexpo.lims.authentication.exceptions.DuplicateRecordFoundException;
import com.digiworldexpo.lims.authentication.exceptions.RecordNotFoundException;
import com.digiworldexpo.lims.authentication.model.ResponseModel;
import com.digiworldexpo.lims.authentication.model.UserDTO;
import com.digiworldexpo.lims.authentication.model.UserModel;
import com.digiworldexpo.lims.authentication.repository.DepartmentRepository;
import com.digiworldexpo.lims.authentication.repository.RoleRepository;
import com.digiworldexpo.lims.authentication.repository.StaffRepository;
import com.digiworldexpo.lims.authentication.repository.UserAuthenticationRepository;
import com.digiworldexpo.lims.authentication.service.StaffService;
import com.digiworldexpo.lims.entities.User;
import com.digiworldexpo.lims.entities.master.Department;
import com.digiworldexpo.lims.entities.master.Role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;



import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StaffServiceImpl implements StaffService{
	@Autowired
    private StaffRepository staffRepository;
	
	@Autowired
    private UserAuthenticationRepository userAuthenticationRepository;

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserAuthenticationServiceImpl userAuthenticationServiceImpl;
    
    @Override
    @Transactional
    public ResponseModel<User> addStaff(UserModel userModel, UUID createdBy) {
        log.info("Begin StaffServiceImpl -> addStaff() method...");
        ResponseModel<User> responseModel = new ResponseModel<>();

        try {
            // Null Check for Required Fields
            if (userModel == null || createdBy == null || userModel.getEmail() == null || userModel.getEmail().isEmpty()) {
                throw new IllegalArgumentException("Invalid input: Required fields are missing.");
            }

            // Check for Duplicate Email or Username
            Optional<User> existingUser = userAuthenticationRepository.findByEmail(userModel.getEmail());
            if (existingUser.isPresent()) {
                throw new DuplicateRecordFoundException("A user with this email already exists: " + userModel.getEmail());
            }
            
            // Register the user in Cognito (or another external service)
            ResponseModel<User> response = userAuthenticationServiceImpl.registerUserInCognito(userModel);
            log.info("User registration response: {}", response);

            if (response == null || response.getData() == null) {
                responseModel.setData(null);
                responseModel.setMessage("User registration failed or returned null data");
                responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
                return responseModel;
            }
            String sequenceId = "USER" + userAuthenticationRepository.getNextFormattedUserSequenceId();
            log.info("Generated User Sequence ID: {}", sequenceId);

            if (HttpStatus.OK.toString().equalsIgnoreCase(response.getStatusCode())) {

                User savedUser = response.getData();
                savedUser.setCreatedBy(createdBy);
                savedUser.setUserSequenceId(sequenceId);

                Optional<Department> department = departmentRepository.findById(userModel.getDepartment());
                if (department.isPresent()) {
                    savedUser.setDepartment(department.get());
                } else {
                    responseModel.setData(null);
                    responseModel.setMessage("Department not found");
                    responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
                    return responseModel;
                }

                // Set additional details with default values
                savedUser.setDateOfBirth(userModel.getDateOfBirth() != null ? userModel.getDateOfBirth() : "");
                savedUser.setPosition(userModel.getPosition() != null ? userModel.getPosition() : "");
                savedUser.setStatus(userModel.getStatus() != null ? userModel.getStatus() : "");
                savedUser.setProfilePic(userModel.getProfilePic() != null ? userModel.getProfilePic() : "");

                // Save user in database
         
                User finalSavedUser = userAuthenticationRepository.save(savedUser);
                log.info("User saved successfully: {}", finalSavedUser);

                responseModel.setData(finalSavedUser);
                responseModel.setMessage("User added successfully");
                responseModel.setStatusCode(HttpStatus.OK.toString());

            } else {
                responseModel.setData(null);
                responseModel.setMessage("User registration failed");
                responseModel.setStatusCode(response.getStatusCode());
            }

        } catch (IllegalArgumentException e) {
            log.warn("Invalid input: {}", e.getMessage());
            responseModel.setData(null);
            responseModel.setMessage("Invalid input: " + e.getMessage());
            responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
        } catch (DuplicateRecordFoundException e) {
            log.warn("Duplicate user found: {}", e.getMessage());
            responseModel.setData(null);
            responseModel.setMessage(e.getMessage());
            responseModel.setStatusCode(HttpStatus.CONFLICT.toString());
        } catch (Exception e) {
            log.error("Error occurred while adding staff: {}", e.getMessage());
            responseModel.setData(null);
            responseModel.setMessage("Failed to add staff");
            responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }

        log.info("End StaffServiceImpl -> addStaff() method...");
        return responseModel;
    }


    @Override
    @Transactional
    public ResponseModel<UserDTO> getStaffById(UUID userId) {
        log.info("Begin of User Service Implementation -> getStaffById() method");

        ResponseModel<UserDTO> responseModel = new ResponseModel<>();

        try {
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }

            User user = userAuthenticationRepository.findById(userId)
                    .orElseThrow(() -> new RecordNotFoundException("User not found with ID: " + userId));

            log.info("User fetched from DB: {}", user);

            ResponseModel<UserDTO> conversionResponse = convertToUserDTO(user);
            if (!"200 OK".equals(conversionResponse.getStatusCode())) {
                log.error("User conversion failed: {}", conversionResponse.getMessage());
                return conversionResponse; // Return error if conversion fails
            }

            responseModel.setStatusCode(HttpStatus.OK.toString());
            responseModel.setMessage("User retrieved successfully");
            responseModel.setData(conversionResponse.getData());

            log.info("User retrieval success: {}", userId);

        } catch (IllegalArgumentException e) {
            log.warn("Invalid input: {}", e.getMessage());
            responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
            responseModel.setMessage("Invalid input: " + e.getMessage());
            responseModel.setData(null);
        } catch (RecordNotFoundException e) {
            log.warn("User not found: {}", e.getMessage());
            responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
            responseModel.setMessage(e.getMessage());
            responseModel.setData(null);
        } catch (Exception e) {
            log.error("Error in getStaffById(): {}", e.getMessage());
            responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            responseModel.setMessage("An unexpected error occurred while retrieving the user: " + e.getMessage());
            responseModel.setData(null);
        }

        log.info("End of User Service Implementation -> getStaffById() method");
        return responseModel;
    }


    private ResponseModel<UserDTO> convertToUserDTO(User user) {
        log.info("Begin of User Service Implementation -> convertToUserDTO() method");

        ResponseModel<UserDTO> responseModel = new ResponseModel<>();

        try {
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(user, userDTO, "password"); // Exclude sensitive data
            
            responseModel.setStatusCode(HttpStatus.OK.toString());
            responseModel.setMessage("User converted successfully");
            responseModel.setData(userDTO);

            log.info("User conversion success for ID: {}", user.getId());

        } catch (Exception e) {
            log.error("Error in convertToUserDTO(): {}", e.getMessage());
            responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            responseModel.setMessage("Error occurred while converting user: " + e.getMessage());
            responseModel.setData(null);
        }

        log.info("End of User Service Implementation -> convertToUserDTO() method");
        return responseModel;
    }
  
    @Override
    @Transactional
    public ResponseModel<UserDTO> updateStaff(UUID id, UUID creatorId, UserDTO updatedUserDTO) {
        log.info("Begin UserServiceImpl -> updateStaff() method");

        ResponseModel<UserDTO> responseModel = new ResponseModel<>();

        try {
            if (id == null || creatorId == null || updatedUserDTO == null) {
                throw new IllegalArgumentException("Invalid input: ID, Creator ID, and UserDTO must not be null");
            }

            // Check if the user exists
            User user = userAuthenticationRepository.findById(id)
                    .orElseThrow(() -> new RecordNotFoundException("User not found with ID: " + id));

            // Check for duplicate records (e.g., username or email already exists)
            Optional<User> existingUser = userAuthenticationRepository.findByEmail(updatedUserDTO.getEmail());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                throw new DuplicateRecordFoundException("Email already exists: " + updatedUserDTO.getEmail());
            }

            // Copy properties from DTO to entity, excluding sensitive fields
            BeanUtils.copyProperties(updatedUserDTO, user, "id", "password");

            // Set modification details
            user.setModifiedBy(creatorId);
            user.setModifiedOn(new Timestamp(System.currentTimeMillis()));

            // Save updated user
            user = userAuthenticationRepository.save(user);

            // Convert updated user to DTO
            ResponseModel<UserDTO> conversionResponse = convertToUserDTO(user);
            if (!"200 OK".equals(conversionResponse.getStatusCode())) {
                return conversionResponse;
            }

            responseModel.setStatusCode(HttpStatus.OK.toString());
            responseModel.setMessage("User updated successfully");
            responseModel.setData(conversionResponse.getData());

        } catch (IllegalArgumentException e) {
            log.warn("Invalid input provided: {}", e.getMessage());
            responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
            responseModel.setMessage("Invalid input: " + e.getMessage());
            responseModel.setData(null);
        } catch (RecordNotFoundException e) {
            log.info("No record found for the given ID: {}", e.getMessage());
            responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
            responseModel.setMessage(e.getMessage());
            responseModel.setData(null);
        } catch (DuplicateRecordFoundException e) {
            log.warn("Duplicate record found: {}", e.getMessage());
            responseModel.setStatusCode(HttpStatus.CONFLICT.toString());
            responseModel.setMessage(e.getMessage());
            responseModel.setData(null);
        } catch (Exception e) {
            log.error("Error in updateStaff(): {}", e.getMessage());
            responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            responseModel.setMessage("An unexpected error occurred while updating the user: " + e.getMessage());
            responseModel.setData(null);
        }

        log.info("End UserServiceImpl -> updateStaff() method");
        return responseModel;
    }


    @Override
    @Transactional
    public ResponseModel<List<UserDTO>> getAllStaff(String keyword, int pageNumber, int pageSize,
                                                    String sortBy, UUID createdBy) {
        log.info("Begin StaffServiceImpl -> getAllStaff() method...");
        ResponseModel<List<UserDTO>> responseModel = new ResponseModel<>();

        try {
            if (pageNumber < 0 || pageSize < 1) {
                throw new IllegalArgumentException("Invalid pagination parameters.");
            }

            Sort sort = Sort.by(Sort.Direction.ASC, sortBy);
            Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

            long totalCount = userAuthenticationRepository.countByCreatedByAndActive(createdBy, true);

            Page<User> staffPage;
            if (keyword != null && !keyword.trim().isEmpty()) {
                staffPage = userAuthenticationRepository.searchByKeyword(createdBy, keyword, pageable);
                totalCount = staffPage.getTotalElements();
                log.info("Searching active staff with keyword '{}' for createdBy '{}'", keyword, createdBy);
            } else {
                staffPage = userAuthenticationRepository.findByCreatedByAndActive(createdBy, true, pageable);
                log.info("Retrieving all active staff for createdBy '{}'", createdBy);
            }

            // Convert List<User> to List<UserDTO>
            List<UserDTO> userDTOList = staffPage.getContent().stream()
                    .map(user -> convertToUserDTO(user).getData()) // Convert each User to UserDTO
                    .toList();

            responseModel.setData(userDTOList);
            responseModel.setMessage("Active staff retrieved successfully.");
            responseModel.setStatusCode(String.valueOf(HttpStatus.OK.value()));
            responseModel.setTotalCount((int) totalCount);
            responseModel.setPageNumber(pageNumber);
            responseModel.setPageSize(pageSize);
            responseModel.setTimestamp(String.valueOf(System.currentTimeMillis())); // Timestamp

        } catch (IllegalArgumentException e) {
            responseModel.setData(null);
            responseModel.setMessage(e.getMessage());
            responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
            log.info("Invalid input parameters: {}", e.getMessage());
        } catch (Exception e) {
            responseModel.setData(null);
            responseModel.setMessage("Failed to retrieve staff.");
            responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            log.info("Error occurred while retrieving staff: {}", e.getMessage());
        }

        log.info("End StaffServiceImpl -> getAllStaff() method...");
        return responseModel;
    }


	@Override
	@Transactional
	public ResponseModel<UserDTO> deleteStaff(UUID id) {
	    ResponseModel<UserDTO> responseModel = new ResponseModel<>();
	    
	    try {
	        User user = userAuthenticationRepository.findById(id)
	                .orElseThrow(() -> new RecordNotFoundException("User not found with ID: " + id));

	        // Assuming there's a method to convert User to DTO, similar to convertToLab()
	        ResponseModel<UserDTO> conversionResponse = convertToUserDTO(user);
	        if (!"200 OK".equals(conversionResponse.getStatusCode())) {
	            return conversionResponse; // Return error if conversion fails
	        }

	        // Perform soft delete (deactivating user instead of deleting)
	        user.setActive(false);
	        userAuthenticationRepository.save(user);

	        // Prepare success response
	        responseModel.setStatusCode(String.valueOf(HttpStatus.OK.value()));
	        responseModel.setMessage("User deactivated successfully");
	        responseModel.setData(conversionResponse.getData());
	        responseModel.setTimestamp(String.valueOf(System.currentTimeMillis()));

	    } catch (RecordNotFoundException e) {
	        responseModel.setStatusCode(String.valueOf(HttpStatus.NOT_FOUND.value()));
	        responseModel.setMessage("User not found: " + e.getMessage());
	        responseModel.setData(null);
	        responseModel.setTimestamp(String.valueOf(System.currentTimeMillis()));
	    } catch (Exception e) {
	        log.error("Error while deactivating staff", e);
	        responseModel.setStatusCode(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
	        responseModel.setMessage("An error occurred while deactivating the staff: " + e.getMessage());
	        responseModel.setData(null);
	        responseModel.setTimestamp(String.valueOf(System.currentTimeMillis()));
	    }
	    
	    return responseModel;
	}





   
}

