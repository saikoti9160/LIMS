package com.digiworldexpo.lims.lab.serviceimpl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.digiworldexpo.lims.entities.lab_management.Lab;
import com.digiworldexpo.lims.entities.lab_management.ProfileConfiguration;
import com.digiworldexpo.lims.entities.lab_management.TestParameter;
//import com.digiworldexpo.lims.lab.dto.TestParameterDTO;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.repository.LabRepository;
import com.digiworldexpo.lims.lab.repository.ProfileConfigurationRepository;
import com.digiworldexpo.lims.lab.request.ProfileConfigurationRequestDTO;
import com.digiworldexpo.lims.lab.response.ProfileConfigurationResponseDTO;
import com.digiworldexpo.lims.lab.service.ProfileConfigurationService;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class ProfileConfigurationServiceImpl implements ProfileConfigurationService {
	
	private final ProfileConfigurationRepository profileConfigurationRepository;
	private final LabRepository labRepository;
	
	
	public ProfileConfigurationServiceImpl(ProfileConfigurationRepository profileConfigurationRepository,
			LabRepository labRepository) {
		super();
		this.profileConfigurationRepository = profileConfigurationRepository;
		this.labRepository = labRepository;
	}


	@Override
	public ResponseModel<ProfileConfigurationResponseDTO> saveProfileConfiguration(UUID createdBy,
	        ProfileConfigurationRequestDTO requestDTO) {
	    log.info("Begin ProfileConfigurationServiceImpl -> saveProfileConfiguration() method");
	    ResponseModel<ProfileConfigurationResponseDTO> response = new ResponseModel<>();
	    
	    try {
	        ProfileConfiguration profileConfiguration = convertToEntity(requestDTO);
	        profileConfiguration.setCreatedBy(createdBy);
	        
	        if (requestDTO.getTests() == null || requestDTO.getTests().isEmpty()) {
	            throw new IllegalArgumentException("Tests list cannot be empty");
	        }
	        
	        if (requestDTO.getLabId() != null) {
	            Lab lab = labRepository.findById(requestDTO.getLabId())
	                    .orElseThrow(() -> new IllegalArgumentException("Invalid Lab ID"));
	            profileConfiguration.setLab(lab);
	        }
	        
//	        // Convert and set test parameters
//	        if (requestDTO.getTestParameters() != null) {
//	            List<TestParameter> testParameters = requestDTO.getTestParameters().stream()
//	                .map(paramDTO -> {
//	                    TestParameter param = new TestParameter();
//	                    param.setParameterName(paramDTO.getParameterName());
//	                    param.setResultType(paramDTO.getResultType());
//	                    param.setReferenceResult(paramDTO.getReferenceResult());
//	                    param.setTestResult(paramDTO.getTestResult());
//	                    param.setProfileConfiguration(profileConfiguration);
//	                    return param;
//	                })
//	                .collect(Collectors.toList());
//	            profileConfiguration.setTestParameters(testParameters);
//	        }
//	        
//	        profileConfiguration.setRemarks(requestDTO.getRemarks());
	        
	        ProfileConfiguration savedProfile = profileConfigurationRepository.save(profileConfiguration);
	        ProfileConfigurationResponseDTO responseDTO = convertToResponseDTO(savedProfile);
	        
	        response.setStatusCode(HttpStatus.OK.toString());
	        response.setMessage("Profile Configuration saved successfully");
	        response.setData(responseDTO);
	        
	        log.info("ProfileConfiguration saved with ID: {}", savedProfile.getId());
	    } catch (Exception e) {
	        log.error("Error occurred while saving profile configuration: {}", e.getMessage(), e);
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	        response.setMessage("Failed to save profile configuration");
	        response.setData(null);
	    }
	    
	    return response;
	}

	    
	    private ProfileConfiguration convertToEntity(ProfileConfigurationRequestDTO dto) {
	        ProfileConfiguration entity = new ProfileConfiguration();
	        BeanUtils.copyProperties(dto, entity);
	        return entity;
	    }
	    
	    private ProfileConfigurationResponseDTO convertToResponseDTO(ProfileConfiguration entity) {
	        ProfileConfigurationResponseDTO dto = new ProfileConfigurationResponseDTO();
	        BeanUtils.copyProperties(entity, dto);
	        
//	        // Convert test parameters
//	        if (entity.getTestParameters() != null) {
//	            List<TestParameterDTO> parameterDTOs = entity.getTestParameters().stream()
//	                .map(param -> {
//	                    TestParameterDTO paramDTO = new TestParameterDTO();
//	                    paramDTO.setParameterName(param.getParameterName());
//	                    paramDTO.setResultType(param.getResultType());
//	                    paramDTO.setReferenceResult(param.getReferenceResult());
//	                    paramDTO.setTestResult(param.getTestResult());
//	                    return paramDTO;
//	                })
//	                .collect(Collectors.toList());
//	            dto.setTestParameters(parameterDTOs);
//	        }
	        
	        return dto;
	    }
}
