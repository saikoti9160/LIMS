package com.digiworldexpo.lims.authentication.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digiworldexpo.lims.authentication.model.ResponseModel;
import com.digiworldexpo.lims.authentication.model.UserDTO;
import com.digiworldexpo.lims.authentication.model.UserModel;
import com.digiworldexpo.lims.authentication.service.AuthService;
import com.digiworldexpo.lims.authentication.service.StaffService;
import com.digiworldexpo.lims.authentication.service.UserAuthenticationService;
import com.digiworldexpo.lims.authentication.util.HttpStatusCode;
import com.digiworldexpo.lims.entities.User;


import lombok.extern.slf4j.Slf4j;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/user/staff")
@Slf4j
public class StaffController {
	   @Autowired
	   private StaffService staffService;
	   
	   @Autowired
		HttpStatusCode httpStatusCode;
	   
	    @PostMapping("/add")
	    public ResponseEntity<ResponseModel<User>> addStaff(@RequestBody UserModel userModel,@RequestHeader("createdBy") UUID createdBy) {
	        // Call the service to add the staff member
	    	 log.info("Begin addStaff controller -> addStaff() method", userModel);
	        ResponseModel<User> response = staffService.addStaff(userModel,createdBy);
	        log.info("End addStaff controller -> addStaff() method", userModel);
	        HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
			return ResponseEntity.status(httpStatus).body(response);
	    }
	    
	 // Get staff by ID
	    @GetMapping("/{id}")
	    public ResponseEntity<ResponseModel<UserDTO>> getStaffById(@PathVariable UUID id) {
	    	 log.info("Fetching staff details for ID: {}", id);
	        ResponseModel<UserDTO> response = staffService.getStaffById(id);
	        HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
	        log.info("Fetched staff details: {}", response);
			return ResponseEntity.status(httpStatus).body(response);
	    }

	    // Update staff details
	    @PutMapping("/{id}")
	    public ResponseEntity<ResponseModel<UserDTO>> updateStaff(@PathVariable UUID id, @RequestBody UserDTO updatedUser,@RequestHeader("userId") UUID userId) {
	    	  log.info("Updating staff with ID: {}, Updated Data: {}", id, updatedUser);
	        ResponseModel<UserDTO> response = staffService.updateStaff(id, userId,updatedUser);
	        HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
	        log.info("Staff updated successfully with response: {}", response);
			return ResponseEntity.status(httpStatus).body(response);
	    }

	    // Delete staff by ID
	    @DeleteMapping("/{id}")
	    public ResponseEntity<ResponseModel<UserDTO>> deleteStaff(@PathVariable UUID id) {
	    	 log.info("Received request to delete staff with ID: {}", id);
	        ResponseModel<UserDTO> response = staffService.deleteStaff(id);
	        HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
	        log.info("Staff deleted successfully with response: {}", response);
			return ResponseEntity.status(httpStatus).body(response);
	    }
	    @PostMapping("/get-all")
	    public ResponseEntity<ResponseModel<List<UserDTO>>> getAllStaff(
	            @RequestParam(required = false) String searchBy,
	            @RequestParam(defaultValue = "0") int pageNumber,
	            @RequestParam(defaultValue = "10") int pageSize,
	            @RequestParam(defaultValue = "userSequenceId") String sortBy,
	            @RequestHeader UUID createdBy) {
	    	 log.info("Fetching all staff with filters - startsWith: {}, pageNumber: {}, pageSize: {}, sortBy: {}, createdBy: {}", 
	    			 searchBy, pageNumber, pageSize, sortBy, createdBy);
	        ResponseModel<List<UserDTO>> response = staffService.getAllStaff(searchBy, pageNumber, pageSize, sortBy, createdBy);
	        HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
	        log.info("Fetched all staff with response: {}", response);
	        return ResponseEntity.status(httpStatus).body(response);
	    }
}
