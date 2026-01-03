package com.digiworldexpo.lims.master.service;

import com.digiworldexpo.lims.entities.master.ReportFontType;
import com.digiworldexpo.lims.master.model.response.ResponseModel;

import java.util.List;
import java.util.UUID;

public interface ReportFontTypeService {

	ResponseModel<ReportFontType> saveFontType(ReportFontType fontType, UUID createdBy);

	ResponseModel<List<ReportFontType>> getFontTypes(String searchTearm, int pageNumber, int pageSize, String sortBy,
			UUID createdBy);

	ResponseModel<ReportFontType> getFontTypeById(UUID id);

	ResponseModel<ReportFontType> updateFontType(UUID id, ReportFontType updatedFontType, UUID userId);

	ResponseModel<ReportFontType> deleteFontType(UUID id);
}
