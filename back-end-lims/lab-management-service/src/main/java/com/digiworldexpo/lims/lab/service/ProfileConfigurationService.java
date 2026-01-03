package com.digiworldexpo.lims.lab.service;

import java.util.UUID;

import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.request.ProfileConfigurationRequestDTO;
import com.digiworldexpo.lims.lab.response.ProfileConfigurationResponseDTO;

public interface ProfileConfigurationService {
	
	 ResponseModel<ProfileConfigurationResponseDTO> saveProfileConfiguration(UUID createdBy, ProfileConfigurationRequestDTO requestDTO);

}
