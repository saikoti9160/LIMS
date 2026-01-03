package com.digiworldexpo.lims.lab.service;


import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.entities.lab_management.ReportParameter;
import com.digiworldexpo.lims.lab.dto.ReportParameterRequestDto;
import com.digiworldexpo.lims.lab.model.ResponseModel;

public interface ReportParameterService {

//	ResponseModel<ReportParameter> createTestParameter(UUID userId,ReportParameter testparameterDto);
//
//	ResponseModel<List<ReportParameterRequestDto>> getAllTestParameters(UUID labId,String searchText, Integer pageNumber, Integer pageSize);
//
//	ResponseModel<ReportParameterRequestDto> getTestParameterById(UUID id);

	ResponseModel<ReportParameterRequestDto> updateTestParameter(UUID id, ReportParameterRequestDto testParameterDto);

	ResponseModel<ReportParameter> deleteTestParameterById(UUID id);

}
 