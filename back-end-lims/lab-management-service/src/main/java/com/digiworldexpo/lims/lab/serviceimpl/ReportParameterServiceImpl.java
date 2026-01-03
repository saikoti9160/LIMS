fpackage com.digiworldexpo.lims.lab.serviceimpl;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.digiworldexpo.lims.entities.lab_management.ReportParameter;
import com.digiworldexpo.lims.lab.dto.ReportParameterRequestDto;
import com.digiworldexpo.lims.lab.exception.RecordNotFoundException;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.repository.LabRepository;
import com.digiworldexpo.lims.lab.repository.ReportParameterRepository;
import com.digiworldexpo.lims.lab.service.ReportParameterService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportParameterServiceImpl implements ReportParameterService {

	private final ReportParameterRepository testParameterRepository;

	private final LabRepository labRepository;
	
	public ReportParameterServiceImpl(ReportParameterRepository testParameterRepository, LabRepository labRepository) {
		super();
		this.testParameterRepository = testParameterRepository;
		this.labRepository = labRepository;
	}

	@Transactional
	@Override
	public ResponseModel<ReportParameterRequestDto> updateTestParameter(UUID id,
			ReportParameterRequestDto testParameterDto) {
		log.info("Begin TestConfigurationServiceImpl -> updateTestParameter() method");

		ResponseModel<ReportParameterRequestDto> responseModel = new ResponseModel<>();
		try {
			// Validate input DTO
			if (testParameterDto == null) {
			    responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			    responseModel.setMessage("Test parameter object data cannot be null.");
			    responseModel.setData(null);
			    log.error("Input TestParameterRequestDto is null.");
			 
			}

			log.info("Fetching existing TestParameter with ID: {}", id);
			ReportParameter existingTestParameter = testParameterRepository.findById(id)
			        .orElseThrow(() -> {
			            log.error("Test parameter not found with ID: {}", id);
			            return new RecordNotFoundException("Test parameter not found with ID: " + id);
			        });

			log.info("Converting DTO to entity for update.");
			ReportParameter updatePerameter = convertDtoToEntity(testParameterDto);
			updatePerameter.setId(id);
			updatePerameter.setModifiedBy(id);
			updatePerameter.setModifiedOn(new Timestamp(System.currentTimeMillis()));
			
			log.info("Saving updated TestParameter entity with ID: {}", id);
			ReportParameter saveUpdate = testParameterRepository.save(updatePerameter);

			log.info("Converting updated entity to DTO.");
			ReportParameterRequestDto updatedTestParameterDto = convertEntityToDto(saveUpdate);

			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Test parameter updated successfully.");
			responseModel.setData(updatedTestParameterDto);

			log.info("Successfully updated TestParameter with ID: {}", id);
		} catch (RecordNotFoundException e) {
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
			responseModel.setMessage(e.getMessage());
			responseModel.setData(null);
			log.error("Test parameter not found: {}", e.getMessage());

		} catch (Exception e) {
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Failed to update test parameter: " + e.getMessage());
			responseModel.setData(null);
			log.error("Error while updating test parameter: {}", e.getMessage());
		}

		log.info("End TestConfigurationServiceImpl -> updateTestParameter() method");
		return responseModel;
	}

	@Transactional
	@Override
	public ResponseModel<ReportParameter> deleteTestParameterById(UUID id) {
		log.info("Begin TestConfigurationServiceImpl -> deleteTestParameterById() method");

		ResponseModel<ReportParameter> responseModel = new ResponseModel<>();
		try {

			if (id == null) {
			    responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			    responseModel.setMessage("Test parameter ID cannot be null.");
			    log.error("Test parameter ID is null.");
			}

			log.info("Checking if TestParameter exists with ID: {}", id);
					ReportParameter parameter = testParameterRepository.findById(id).orElseThrow(()->new RecordNotFoundException("No record found with this ID: " + id));
					parameter.setActive(false);
					testParameterRepository.save(parameter);
					responseModel.setData(parameter);
			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Test parameter deleted successfully with ID: " + id);
			log.info("Successfully deleted TestParameter with ID: {}", id);
			
		} catch (Exception e) {

			log.error("Error while deleting Test parameter with ID {}: {}", id, e.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Failed to delete Test parameter: " + e.getMessage());
			responseModel.setData(null);
		}

		log.info("End TestConfigurationServiceImpl -> deleteTestParameterById() method");
		return responseModel;
	}

	// Method to convert entity to DTO
	private ReportParameterRequestDto convertEntityToDto(ReportParameter testParameter) {
		ReportParameterRequestDto testParameterRequestDto = new ReportParameterRequestDto();
		BeanUtils.copyProperties(testParameter, testParameterRequestDto);			 
		return testParameterRequestDto;
	}

	// Method to convert DTO to entity
	private ReportParameter convertDtoToEntity(ReportParameterRequestDto testparameterDto) {
		ReportParameter testParameterEntity = new ReportParameter();
		BeanUtils.copyProperties(testparameterDto, testParameterEntity);
		return testParameterEntity;
	}

}
