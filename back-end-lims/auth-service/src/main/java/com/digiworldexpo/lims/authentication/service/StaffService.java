package com.digiworldexpo.lims.authentication.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.digiworldexpo.lims.authentication.model.ResponseModel;
import com.digiworldexpo.lims.authentication.model.UserDTO;
import com.digiworldexpo.lims.authentication.model.UserModel;
import com.digiworldexpo.lims.entities.User;

@Service
public interface StaffService {

	ResponseModel<User> addStaff(UserModel userModel,UUID createdBy);
	
  ResponseModel<List<UserDTO>> getAllStaff(String searchBy, int pageNumber, int pageSize, String sortBy,UUID createdBy);

  ResponseModel<UserDTO> getStaffById(UUID id);

//  ResponseModel<UserDTO> updateStaff(UUID id, User updatedUser,UUID userId);

  ResponseModel<UserDTO> deleteStaff(UUID id);

ResponseModel<UserDTO> updateStaff(UUID creatorId, UUID id, UserDTO updatedUserDTO);
}
