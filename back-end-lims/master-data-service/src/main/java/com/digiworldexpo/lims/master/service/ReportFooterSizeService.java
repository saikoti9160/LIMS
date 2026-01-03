
package com.digiworldexpo.lims.master.service;

import com.digiworldexpo.lims.entities.master.ReportFooterSize;
import com.digiworldexpo.lims.master.model.response.ResponseModel;

import java.util.List;
import java.util.UUID;

public interface ReportFooterSizeService {

	ResponseModel<ReportFooterSize> saveReportFooterSize(ReportFooterSize reportFooterSize, UUID createdBy);

	ResponseModel<List<ReportFooterSize>> getReportFooterSizes(String startsWith, int pageNumber, int pageSize,
			String sortBy, UUID createdBy);

	ResponseModel<ReportFooterSize> getReportFooterSizeById(UUID id);

	ResponseModel<ReportFooterSize> updateReportFooterSize(UUID id, ReportFooterSize updatedReportFooterSize, UUID userId);

	ResponseModel<ReportFooterSize> deleteReportFooterSize(UUID id);
}
