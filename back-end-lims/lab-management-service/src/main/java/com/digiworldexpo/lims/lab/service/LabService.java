package com.digiworldexpo.lims.lab.service;

import java.util.UUID;

import com.digiworldexpo.lims.lab.dto.LabManagementDTO;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.request.LabFilterRequestDTO;
import com.digiworldexpo.lims.lab.response.LabMainResponse;

public interface LabService {

	ResponseModel<LabManagementDTO> saveLab(UUID userId, LabManagementDTO labManagementDTO);

	ResponseModel<LabManagementDTO> updateLab(UUID creatorId, UUID id, LabManagementDTO labManagementDTO);

	ResponseModel<LabManagementDTO> getLabById(UUID labId);

	ResponseModel<LabManagementDTO> deleteLabById(UUID labId);

	ResponseModel<LabMainResponse> getLabs(LabFilterRequestDTO filterRequest, int pageNumber, int pageSize,
			String sortBy);

}
