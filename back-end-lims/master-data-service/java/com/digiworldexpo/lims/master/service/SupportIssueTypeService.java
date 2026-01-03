package com.digiworldexpo.lims.master.service;

import java.util.List;

import java.util.UUID;

import com.digiworldexpo.lims.entities.master.SupportIssueType;
import com.digiworldexpo.lims.master.model.response.ResponseModel;

public interface SupportIssueTypeService {

	ResponseModel<SupportIssueType> saveSupportIssueType(SupportIssueType supportIssueType);

	ResponseModel<List<SupportIssueType>> getSupportIssueTypes(String startsWith, int pageNumber, int pageSize,
			String sortBy);

	ResponseModel<SupportIssueType> getSupportIssueTypeById(UUID id);

	ResponseModel<SupportIssueType> updateSupportIssueType(UUID id, SupportIssueType updatedSupportIssueType);

	ResponseModel<SupportIssueType> deleteSupportIssueType(UUID id);

}