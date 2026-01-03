
package com.digiworldexpo.lims.master.service;



import com.digiworldexpo.lims.entities.master.ReportPaperSize;
import com.digiworldexpo.lims.master.model.response.ResponseModel;

import java.util.List;
import java.util.UUID;

public interface ReportPaperSizeService {

	ResponseModel<ReportPaperSize> saveReportPaperSize(ReportPaperSize reportPaperSize, UUID createdBy);

	ResponseModel<List<ReportPaperSize>> getReportPaperSizes(String startsWith, int pageNumber, int pageSize,
			String sortBy, UUID createdBy);

	ResponseModel<ReportPaperSize> getReportPaperSizeById(UUID id);

	ResponseModel<ReportPaperSize> updateReportPaperSize(UUID id, ReportPaperSize updatedReportPaperSize, UUID userId);

	ResponseModel<ReportPaperSize> deleteReportPaperSize(UUID id);
}
