package com.digiworldexpo.lims.master.service;

import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.entities.master.Designation;
import com.digiworldexpo.lims.master.model.response.ResponseModel;

public interface DesignationService {
	
	ResponseModel<Designation> addDesignation(Designation designation, UUID createdBy);

	ResponseModel<List<Designation>> getAllDesignations(String startsWith, UUID createdBy, int pageNumber, int pageSize,
			String sortedBy);

	ResponseModel<Designation> updateDesignationById(UUID id, Designation designation, UUID modifiedBy);

	ResponseModel<Designation> getDesignationById(UUID id);

	ResponseModel<Designation> deleteDesignationById(UUID id);


}
