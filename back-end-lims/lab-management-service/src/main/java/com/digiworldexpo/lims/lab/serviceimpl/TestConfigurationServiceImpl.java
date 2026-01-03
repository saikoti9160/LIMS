package com.digiworldexpo.lims.lab.serviceimpl;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.digiworldexpo.lims.entities.lab_management.LabDepartment;
import com.digiworldexpo.lims.entities.lab_management.ReportParameter;
import com.digiworldexpo.lims.entities.lab_management.SampleMapping;
import com.digiworldexpo.lims.entities.lab_management.TestConfigurationMaster;
import com.digiworldexpo.lims.lab.dto.TestConfigurationRequestDto;
import com.digiworldexpo.lims.lab.exception.RecordNotFoundException;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.repository.LabDepertmentRepo;
import com.digiworldexpo.lims.lab.repository.LabRepository;
import com.digiworldexpo.lims.lab.repository.ReportParameterRepository;
import com.digiworldexpo.lims.lab.repository.SampleMappingRepository;
import com.digiworldexpo.lims.lab.repository.TestConfigurationRepository;
import com.digiworldexpo.lims.lab.service.TestConfigurationService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TestConfigurationServiceImpl implements TestConfigurationService {

	private final TestConfigurationRepository testConfigurationRepository;

	private final SampleMappingRepository sampleMappingRepository;

	private final LabDepertmentRepo departmentRepository;

	private final LabRepository labRepository;

	private final ReportParameterRepository parameterRepository;

	public TestConfigurationServiceImpl(TestConfigurationRepository testConfigurationRepository,
			SampleMappingRepository sampleMappingRepository, LabDepertmentRepo departmentRepository,
			LabRepository labRepository, ReportParameterRepository parameterRepository) {
		super();
		this.testConfigurationRepository = testConfigurationRepository;
		this.sampleMappingRepository = sampleMappingRepository;
		this.departmentRepository = departmentRepository;
		this.labRepository = labRepository;
		this.parameterRepository = parameterRepository;
	}

	@Override
	public ResponseModel<TestConfigurationMaster> createTestconfiguration(UUID userId,
			TestConfigurationMaster testConfiguration) {
		log.info("Begin TestConfigurationServiceImpl -> createTestconfiguration() ....!");
		ResponseModel<TestConfigurationMaster> responseModel = new ResponseModel<>();
		try {
			log.info("Setting createdBy and createdOn for TestConfiguration.");
			testConfiguration.setCreatedBy(userId);
			testConfiguration.setCreatedOn(new Timestamp(System.currentTimeMillis()));

			// Fetch the LabDepartment
			log.info("Fetching LabDepartment with ID: {}", testConfiguration.getLabDepartment().getId());
			LabDepartment department = departmentRepository.findById(testConfiguration.getLabDepartment().getId())
					.orElseThrow(() -> new RecordNotFoundException(
							"Department not found with ID: " + testConfiguration.getLabDepartment().getId()));

			// Set the LabDepartment and Samples
			log.info("Setting LabDepartment and Samples in TestConfiguration.");
			testConfiguration.setLabDepartment(department);

			SampleMapping sampleMapping = sampleMappingRepository.findById(testConfiguration.getSampleMapping().getId())
					.orElseThrow(() -> new RecordNotFoundException(
							"sample data not found with ID: " + testConfiguration.getSampleMapping().getId()));
			testConfiguration.setSampleMapping(sampleMapping);
			// Save the TestConfigurationMaster

			log.info("Saving TestConfigurationMaster.");
			TestConfigurationMaster testConfigurationMasterEntity = testConfigurationRepository.save(testConfiguration);

			// Save ReportParameters if present
			if (testConfiguration.getReportParameters() != null) {
				log.info("Saving ReportParameter.");
				ReportParameter reportParameter = testConfiguration.getReportParameters();
				// Set the reference to the TestConfigurationMaster entity
				reportParameter.setTestConfiguration(testConfiguration);
				reportParameter.setCreatedBy(userId);
				reportParameter.setCreatedOn(new Timestamp(System.currentTimeMillis()));

				// Save the ReportParameter
				parameterRepository.save(reportParameter);
			}
			// Set the response model
			responseModel.setData(testConfigurationMasterEntity);
			responseModel.setMessage("Test configuration created successfully.");
			responseModel.setStatusCode(HttpStatus.OK.toString());

			log.info("TestConfigurationMaster created successfully with ID: {}", testConfigurationMasterEntity.getId());
		} catch (Exception e) {
			log.error("Error occurred while creating test configuration: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Failed to create test configuration due to an internal error.");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End TestConfigurationServiceImpl -> createTestconfiguration() ....!");
		return responseModel;
	}

	@Transactional
	@Override
	public ResponseModel<List<TestConfigurationRequestDto>> getAllTestConfiguration(UUID labId, Integer pageNumber,
			Integer pageSize, String searchText) {
		log.info("Begin TestConfigurationServiceImpl -> getAllTestConfigurations() ....!");
		ResponseModel<List<TestConfigurationRequestDto>> responseModel = new ResponseModel<>();

		try {
			if (pageNumber < 0 || pageSize <= 0) {
				log.error("Invalid page number or size. Page number: {}, Page size: {}", pageNumber, pageSize);
				responseModel.setMessage("Page number and size must be positive.");
				responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
				return responseModel;
			}

			Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Direction.ASC, "sampleMapping.testName"));
			Page<TestConfigurationMaster> testConfigurationPage;

			if (searchText != null && !searchText.isEmpty()) {
				log.info("Searching test configurations by testName containing: {}", searchText);
				testConfigurationPage = testConfigurationRepository.findByTestName(searchText, labId, pageable);
			} else {
				testConfigurationPage = testConfigurationRepository.findByLabId(labId, pageable);
			}

			if (testConfigurationPage.isEmpty()) {
				log.warn("No test configurations found.");
				responseModel.setMessage("No test configurations found.");
				responseModel.setStatusCode(HttpStatus.NO_CONTENT.toString());
			} else {
				List<TestConfigurationRequestDto> testConfigurationDtoList = testConfigurationPage.getContent().stream()
						.map(TestConfigurationServiceImpl::convertEntityToDto).collect(Collectors.toList());

				responseModel.setData(testConfigurationDtoList);
				responseModel.setMessage("Test configurations retrieved successfully.");
				responseModel.setStatusCode(HttpStatus.OK.toString());
				responseModel.setTotalCount((int) testConfigurationPage.getTotalElements());
				responseModel.setPageNumber(testConfigurationPage.getNumber());
				responseModel.setPageSize(testConfigurationPage.getSize());
			}
		} catch (Exception e) {
			log.error("Error occurred while fetching test configurations: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Failed to retrieve test configurations due to an internal error.");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End TestConfigurationServiceImpl -> getAllTestConfigurations() ....!");
		return responseModel;
	}

	@Transactional
	@Override
	public ResponseModel<TestConfigurationRequestDto> getTestConfigurationById(UUID id) {
		log.info("Begin TestConfigurationServiceImpl -> getTestConfigurationById() ....!");
		ResponseModel<TestConfigurationRequestDto> responseModel = new ResponseModel<>();

		try {
			// Validate the input ID
			if (id == null) {
				log.error("Test Configuration ID is null.");
				responseModel.setData(null);
				responseModel.setMessage("Test Configuration ID cannot be null.");
				responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
				return responseModel;
			}

			log.info("Fetching TestConfigurationMaster with ID: {}", id);
			TestConfigurationMaster testConfigurationEntity = testConfigurationRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("Test Configuration not found with ID: " + id));

			log.info("Successfully retrieved TestConfigurationMaster with ID: {}", id);

			TestConfigurationRequestDto testConfigurationDto = convertEntityToDto(testConfigurationEntity);

			responseModel.setData(testConfigurationDto);
			responseModel.setMessage("Test configuration retrieved successfully for ID: " + id);
			responseModel.setStatusCode(HttpStatus.OK.toString());

		} catch (RecordNotFoundException e) {
			log.error("Record not found: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
		} catch (IllegalArgumentException e) {
			log.error("Invalid argument: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
		} catch (Exception e) {
			log.error("Error occurred while fetching test configuration: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Failed to retrieve test configuration due to an internal error.");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End TestConfigurationServiceImpl -> getTestConfigurationById() ....!");
		return responseModel;
	}

	@Transactional
	@Override
	public ResponseModel<TestConfigurationRequestDto> updateTestConfiguration(UUID id,
			TestConfigurationRequestDto testConfigurationDto) {
		log.info("Begin TestConfigurationServiceImpl -> updateTestConfiguration() ....!");
		ResponseModel<TestConfigurationRequestDto> responseModel = new ResponseModel<>();

		try {
			if (id == null || testConfigurationDto == null) {
				log.error("ID or updated configuration is null.");
				throw new IllegalArgumentException("ID or updated configuration cannot be null.");
			}

			log.info("Fetching TestConfigurationMaster with ID: {}", id);
			TestConfigurationMaster existingEntity = testConfigurationRepository.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("TestConfiguration not found"));

			log.info("Successfully retrieved existing TestConfigurationMaster with ID: {}", id);

			// Ensure ID is not altered
			testConfigurationDto.setId(existingEntity.getId());

			// Update SampleMapping if provided
			if (testConfigurationDto.getSampleMapping().getId() != null) {
				log.info("Fetching SampleMapping with ID: {}", testConfigurationDto.getSampleMapping().getId());
				SampleMapping sampleMapping = sampleMappingRepository
						.findById(testConfigurationDto.getSampleMapping().getId())
						.orElseThrow(() -> new EntityNotFoundException(
								"SampleMapping not found with ID: " + testConfigurationDto.getSampleMapping().getId()));

				existingEntity.setSampleMapping(sampleMapping);
			}

			// Update LabDepartment if provided
			if (testConfigurationDto.getLabDepartment().getId() != null) {
				log.info("Fetching Report parameter with ID: {}", testConfigurationDto.getReportParameters());
				LabDepartment department = departmentRepository
						.findById(testConfigurationDto.getLabDepartment().getId())
						.orElseThrow(() -> new EntityNotFoundException("Department not found"));
				existingEntity.setLabDepartment(department);

			}
			if (testConfigurationDto.getReportParameters() != null) {
				log.info("Updating ReportParameter for TestConfiguration ID: {}", id);

				ReportParameter param = testConfigurationDto.getReportParameters();
				ReportParameter reportParameter;

				if (param.getId() != null) {
					log.info("Fetching existing ReportParameter with ID: {}", param.getId());
					reportParameter = parameterRepository.findById(param.getId()).orElseThrow(
							() -> new EntityNotFoundException("ReportParameter not found with ID: " + param.getId()));
				} else {
					reportParameter = new ReportParameter();
				}
				BeanUtils.copyProperties(param, reportParameter, "id");

				reportParameter.setTestConfiguration(existingEntity);

				parameterRepository.save(reportParameter);
			}
			BeanUtils.copyProperties(testConfigurationDto, existingEntity, "id", "sampleMapping", "labDepartment",
					"reportParameters");

			log.info("Saving updated TestConfigurationMaster with ID: {}", id);
			TestConfigurationMaster savedEntity = testConfigurationRepository.save(existingEntity);

			TestConfigurationRequestDto savedConfigurationDto = convertEntityToDto(savedEntity);

			responseModel.setData(savedConfigurationDto);
			responseModel.setMessage("Test configuration updated successfully for ID: " + id);
			responseModel.setStatusCode(HttpStatus.OK.toString());

		} catch (RecordNotFoundException e) {
			log.error("Record not found: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
		} catch (IllegalArgumentException e) {
			log.error("Invalid argument: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
		} catch (Exception e) {
			log.error("Error occurred while updating test configuration: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Failed to update test configuration due to an internal error.");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End TestConfigurationServiceImpl -> updateTestConfiguration() ....!");
		return responseModel;
	}

	@Transactional
	@Override
	public ResponseModel<TestConfigurationMaster> deleteTestConfiguration(UUID id) {
		log.info("Begin TestConfigurationServiceImpl -> deleteTestConfiguration() ....!");
		ResponseModel<TestConfigurationMaster> responseModel = new ResponseModel<>();

		try {
			if (id == null) {
				log.error("Test Configuration ID is null.");
				throw new IllegalArgumentException("ID cannot be null.");
			}

			log.info("Fetching TestConfigurationMaster with ID: {}", id);
			TestConfigurationMaster existingConfiguration = testConfigurationRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("Test Configuration not found with ID: " + id));

			// Remove mappings before deletion
			log.info("Removing associations for TestConfigurationMaster ID: {}", id);
			testConfigurationRepository.removeMappingsBeforeDelete(id);

			log.info("Deleting TestConfigurationMaster with ID: {}", id);
			testConfigurationRepository.deleteById(id);

			responseModel.setData(null);
			responseModel.setMessage("Test configuration deleted successfully for ID: " + id);
			responseModel.setStatusCode(HttpStatus.OK.toString());

			log.info("Test Configuration deleted successfully for ID: {}", id);

		} catch (RecordNotFoundException e) {
			log.error("Record not found: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
		} catch (IllegalArgumentException e) {
			log.error("Invalid argument: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
		} catch (Exception e) {
			log.error("Error occurred while deleting test configuration: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Failed to delete test configuration due to an internal error.");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End TestConfigurationServiceImpl -> deleteTestConfiguration() ....!");
		return responseModel;
	}

	// Convert DTO to Entity
	public static TestConfigurationMaster convertDtoToEntity(TestConfigurationRequestDto testConfigurationDto) {
		TestConfigurationMaster testConfigurationMaster = new TestConfigurationMaster();

		BeanUtils.copyProperties(testConfigurationDto, testConfigurationMaster, "samples", "department");

		// Map Department
		if (testConfigurationDto.getLabDepartment() != null) {
			LabDepartment department = new LabDepartment();
			department.setId(testConfigurationDto.getLabDepartment().getId());
			testConfigurationMaster.setLabDepartment(department);
		}

		if (testConfigurationDto.getSampleMapping() != null) {
			SampleMapping sampleMapping = new SampleMapping();
			sampleMapping.setId(testConfigurationDto.getSampleMapping().getId());
			testConfigurationMaster.setSampleMapping(sampleMapping);
		}

		if (testConfigurationDto.getReportParameters() != null) {
			ReportParameter reportParameter = new ReportParameter();
			BeanUtils.copyProperties(testConfigurationDto.getReportParameters(), reportParameter);
			testConfigurationMaster.setReportParameters(reportParameter);
		}

		return testConfigurationMaster;
	}

	// Convert Entity to DTO
	public static TestConfigurationRequestDto convertEntityToDto(TestConfigurationMaster testConfigurationMaster) {
		TestConfigurationRequestDto responseDto = new TestConfigurationRequestDto();

		BeanUtils.copyProperties(testConfigurationMaster, responseDto, "samplesMapping", "department",
				"reportParameter");

		if (testConfigurationMaster.getSampleMapping() != null) {
			SampleMapping sampleMapping = new SampleMapping();
			BeanUtils.copyProperties(testConfigurationMaster.getSampleMapping(), sampleMapping);
			responseDto.setSampleMapping(sampleMapping);
		}

		// Map Department
		if (testConfigurationMaster.getLabDepartment() != null) {
			LabDepartment departmentDto = new LabDepartment();
			BeanUtils.copyProperties(testConfigurationMaster.getLabDepartment(), departmentDto);
			responseDto.setLabDepartment(departmentDto);
		}

		if (testConfigurationMaster.getReportParameters() != null) {
			ReportParameter reportParameterDto = new ReportParameter();
			BeanUtils.copyProperties(testConfigurationMaster.getReportParameters(), reportParameterDto);
			responseDto.setReportParameters(reportParameterDto);
		}
		return responseDto;
	}

}
