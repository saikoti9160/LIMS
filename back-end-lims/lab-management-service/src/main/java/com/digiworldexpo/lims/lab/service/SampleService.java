package com.digiworldexpo.lims.lab.service;

import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.entities.lab_management.SampleMaster;
import com.digiworldexpo.lims.lab.dto.SampleMasterRequestDto;
import com.digiworldexpo.lims.lab.model.ResponseModel;

public interface SampleService {

	ResponseModel<SampleMaster> createSample(UUID userId,SampleMaster sampleMaster);

	ResponseModel<List<SampleMasterRequestDto>> getAllSamples(UUID labId, Integer pageNumber, Integer pageSize, String searchText);

	ResponseModel<SampleMasterRequestDto> updateSamples(UUID id, SampleMasterRequestDto sampleMasterDto);

	ResponseModel<SampleMaster> deleteSample(UUID id);

	ResponseModel<SampleMasterRequestDto> getSamplesById(UUID id);

	ResponseModel<List<SampleMaster>> getSamplesBySampleName(String sampleName);

}