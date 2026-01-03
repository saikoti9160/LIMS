package com.digiworldexpo.lims.lab.service;

import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.request.OrganizationRequestDTO;
import com.digiworldexpo.lims.lab.response.OrganizationResponseDTO;
import com.digiworldexpo.lims.lab.response.OrganizationSearchResponseDTO;

public interface OrganizationService {
	
    ResponseModel<OrganizationResponseDTO> saveOrganization(UUID createdBy, OrganizationRequestDTO organizationDTO);
    
    ResponseModel<List<OrganizationSearchResponseDTO>> getAllOrganization(UUID createdBy, String keyword, Boolean flag, Integer pageNumber, Integer pageSize);
    
    ResponseModel<OrganizationResponseDTO> getOrganizationById(UUID id);
    
    ResponseModel<OrganizationResponseDTO> updateOrganizationById(UUID id, OrganizationRequestDTO organizationRequestDTO);
    
    ResponseModel<OrganizationResponseDTO> deleteOrganizationById(UUID id);


}
