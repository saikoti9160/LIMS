package com.digiworldexpo.lims.lab.service;

import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.entities.lab_management.TestConfigurationMaster;
import com.digiworldexpo.lims.lab.dto.TestConfigurationRequestDto;
import com.digiworldexpo.lims.lab.model.ResponseModel;

public interface TestConfigurationService {

	ResponseModel<TestConfigurationMaster> createTestconfiguration(UUID userId,TestConfigurationMaster testconfiguration);

	ResponseModel<List<TestConfigurationRequestDto>> getAllTestConfiguration(UUID id, Integer pageNumber, Integer pageSize, String searchText);

	ResponseModel<TestConfigurationRequestDto> getTestConfigurationById(UUID id);

	ResponseModel<TestConfigurationRequestDto> updateTestConfiguration(UUID id,TestConfigurationRequestDto updatedConfigurationDto);

	ResponseModel<TestConfigurationMaster> deleteTestConfiguration(UUID id);

} 