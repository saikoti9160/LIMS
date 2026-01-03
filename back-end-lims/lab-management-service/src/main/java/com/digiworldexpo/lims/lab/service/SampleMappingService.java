package com.digiworldexpo.lims.lab.service;

import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.entities.lab_management.SampleMapping;
import com.digiworldexpo.lims.lab.model.ResponseModel;

public interface SampleMappingService {
	ResponseModel<SampleMapping> saveSampleMapping(UUID userId, SampleMapping sampleMapping);

	ResponseModel<List<SampleMapping>> getAllSampleMapping(UUID ladId, Integer pageNumber, Integer pageSize,
			String searchText);

	ResponseModel<SampleMapping> updateSampleMapping(UUID id,SampleMapping sampleMapping);

	ResponseModel<SampleMapping> deleteSampleMapping(UUID id);

	ResponseModel<SampleMapping> getByIdSampleMapping(UUID id);
}
