package com.digiworldexpo.lims.master.service;

import java.util.List;

import java.util.UUID;

import com.digiworldexpo.lims.entities.master.LabType;
import com.digiworldexpo.lims.master.model.response.ResponseModel;

public interface LabTypeService {

	ResponseModel<LabType> saveLabType(LabType labType, UUID createdBy);

	ResponseModel<List<LabType>> getLabTypes(String startsWith, int pageNumber, int pageSize, String sortBy,
			UUID createdBy);

	ResponseModel<LabType> getLabTypeById(UUID id);

	ResponseModel<LabType> updateLabType(UUID id, LabType updatedLabType, UUID userId);

	ResponseModel<LabType> deleteLabType(UUID id);
}
