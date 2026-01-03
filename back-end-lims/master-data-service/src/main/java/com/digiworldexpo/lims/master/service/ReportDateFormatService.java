package com.digiworldexpo.lims.master.service;

import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.entities.master.ReportDateFormat;
import com.digiworldexpo.lims.master.model.response.ResponseModel;

public interface ReportDateFormatService {
	
	ResponseModel<ReportDateFormat> saveReportDateFormat(ReportDateFormat reportDateFormat, UUID createdBy);

	ResponseModel<List<ReportDateFormat>> getReportDateFormats(String startsWith, int pageNumber, int pageSize,
			String sortBy, UUID createdBy);

	ResponseModel<ReportDateFormat> getReportDateFormatById(UUID id);

	ResponseModel<ReportDateFormat> updateReportDateFormat(UUID id, ReportDateFormat updatedReportDateFormat, UUID userId);

	ResponseModel<ReportDateFormat> deleteReportDateFormat(UUID id);


}
