package com.digiworldexpo.lims.master.service.serviceImpl;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.digiworldexpo.lims.entities.master.States;
import com.digiworldexpo.lims.master.exception.BadRequestException;
import com.digiworldexpo.lims.master.exception.RecordNotFoundException;
import com.digiworldexpo.lims.master.model.request.StateRequest;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.repository.StatesRepository;
import com.digiworldexpo.lims.master.service.StatesService;
import com.digiworldexpo.lims.master.util.MasterDataLoader;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StatesServiceImpl implements StatesService {
	
	private final StatesRepository statesRepository;
	private final MasterDataLoader masterDataLoader;
	
	public StatesServiceImpl(StatesRepository statesRepository, MasterDataLoader masterDataLoader) {
		this.statesRepository = statesRepository;
		this.masterDataLoader = masterDataLoader;
	}

	@Override
	public ResponseModel<String> uploadStatesFile(MultipartFile multipartFile) {
		log.info("Begin of States Service Implementation -> uploadStatesFile() method");
		ResponseModel<String> responseModel = new ResponseModel<String>();

		try (Workbook workbook = new XSSFWorkbook(multipartFile.getInputStream())) {
			if (multipartFile.isEmpty()) {
				throw new BadRequestException("Please select a state file to upload");
			}

			statesRepository.deleteAll();

			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> iteratorRows = sheet.iterator();

			if (iteratorRows.hasNext()) {
				iteratorRows.next();
			}

			List<States> allStates = new ArrayList<States>();
			while (iteratorRows.hasNext()) {
				Row currentRow = iteratorRows.next();
				String stateName = getCellValue(currentRow.getCell(0));
				String stateCode = getCellValue(currentRow.getCell(1));
				String countryName = getCellValue(currentRow.getCell(2));
				String countryCode = getCellValue(currentRow.getCell(3));

				States states = new States();
				states.setStateName(stateName);
				states.setStateCode(stateCode);
				states.setCountryName(countryName);
				states.setCountryCode(countryCode);

				allStates.add(states);
			}

			masterDataLoader.uploadState(allStates);
			statesRepository.saveAll(allStates);
			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("States Data has been uploaded successfully");
			responseModel.setData(null);

		} catch (BadRequestException badRequestException) {
	    	log.info("Validation failed for country input: {}", badRequestException.getMessage());
	    	responseModel.setStatusCode(HttpStatus.OK.toString());
	        responseModel.setMessage("Validation failed for state input: "+ badRequestException.getMessage());
	        responseModel.setData(null);
	    } catch (Exception exception) {
	    	log.info("Error in uploadStatesFile(): {}", exception.getMessage());
	        responseModel.setStatusCode(HttpStatus.OK.toString());
	        responseModel.setMessage("Error in uploadStatesFile(): "+ exception.getMessage());
	        responseModel.setData(null);
	    }
		log.info("End of States Service Implementation -> uploadStatesFile() method");
		return responseModel;
	}
	
	@Override
	public ResponseModel<List<States>> getAllStates(String startsWith, List<String> countryNames, int pageNumber, int pageSize, String sortedBy) {
		log.info("Begin of States Service Implementation -> getAllStates() method");
	    ResponseModel<List<States>> responseModel = new ResponseModel<>();
	    
	    try {
	    	List<States> allStatesInfo = masterDataLoader.getStates();
			List<States> filteredStates = new ArrayList<States>();
			
			if(countryNames != null && !countryNames.isEmpty() && startsWith != null && !startsWith.isEmpty()) {
				filteredStates = getStatesBasedOnCountryNamesAndStartsWith(allStatesInfo, countryNames, startsWith, sortedBy);
			} else if(countryNames != null && !countryNames.isEmpty()) {
				filteredStates = getStatesBasedOnCountryNames(allStatesInfo, countryNames, sortedBy);
			} else if(startsWith != null && !startsWith.isEmpty()) {
				filteredStates = getStatesBasedOnStartsWith(allStatesInfo, startsWith, sortedBy);
			} else {
				filteredStates = allStatesInfo.stream().sorted(applyDynamicSorting(sortedBy)).collect(Collectors.toList());
			}
	        
	        Pageable pageable = PageRequest.of(pageNumber, pageSize);
	        int start = (int) pageable.getOffset();
	        int end = Math.min(start + pageable.getPageSize(), filteredStates.size());
	        
	        if(start > filteredStates.size()) {
	        	throw new IllegalArgumentException("Page number exceeds available data.");
	        }
	        List<States> paginatedList = filteredStates.subList(start, end);

	        // Set response model data
	        responseModel.setStatusCode(HttpStatus.OK.toString());
	        responseModel.setMessage("States data has been retrieved successfully.");
	        responseModel.setData(paginatedList);
	        responseModel.setTotalCount(filteredStates.size());
	        responseModel.setPageNumber(pageNumber);
	        responseModel.setPageSize(pageSize);
	        responseModel.setSortedBy("State name ascending order");

	    } catch (IllegalArgumentException illegalArgumentException) {
			log.info("Invalid input parameters: {}", illegalArgumentException.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Invalid input parameters: "+ illegalArgumentException.getMessage());
			responseModel.setData(null);
		} catch (Exception exception) {
			log.info("Error in getAllStates(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in getAllStates(): "+ exception.getMessage());
			responseModel.setData(null);
		}
	    log.info("End of States Service Implementation -> getAllStates() method");
	    return responseModel;
	}
	
	@Override
	public ResponseModel<States> saveState(States stateRequest, UUID createdBy) {
		log.info("Begin of States Service Implementation -> saveState() method");
		ResponseModel<States> responseModel = new ResponseModel<States>();
		
		try {
			if(!validationFields(stateRequest)) {
				throw new BadRequestException("Please provide all the required fields");
			}
			
			stateRequest.setCreatedBy(createdBy);
			masterDataLoader.addState(stateRequest);
			
			statesRepository.save(stateRequest); // Saving state information
			
			responseModel.setStatusCode(HttpStatus.CREATED.toString());
		    responseModel.setMessage("State has been saved successfully.");
		    responseModel.setData(stateRequest);	
			
		} catch (BadRequestException badRequestException) {
			log.info("Validation failed for state input: {}", badRequestException.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Validation failed for state input: "+ badRequestException.getMessage());
			responseModel.setData(null);
		} catch (Exception exception) {
			log.info("Error in saveState(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in saveState():"+ exception.getMessage());
			responseModel.setData(null);
		}
		log.info("End of States Service Implementation -> saveState() method");
		return responseModel;
	}
	
	@Override
	public ResponseModel<States> updateStateById(UUID id, States stateRequest, UUID modifiedBy) {
		log.info("Begin of States Service Implementation -> updateStateById() method");
		ResponseModel<States> responseModel = new ResponseModel<>();

		try {
			// Validate the required fields from stateRequest
			if (!validationFields(stateRequest)) {
				throw new IllegalArgumentException("Please provide all the required fields");
			}

			// Find the country data by using state id
			Optional<States> optionalState = statesRepository.findById(id);
			if (optionalState.isEmpty()) {
				throw new RecordNotFoundException("State data is not found for this id: " + id);
			}
			
			States existedState = optionalState.get();

			BeanUtils.copyProperties(stateRequest, existedState,"id", "createdBy", "createdOn");
			// Set additional fields
			existedState.setActive(true);
			existedState.setModifiedBy(modifiedBy);
			existedState.setModifiedOn(new Timestamp(System.currentTimeMillis()));
			
			if (masterDataLoader != null) {
				masterDataLoader.updateState(existedState);
				log.info("State updated in the cache");
			} else {
				log.error("masterDataLoader is null");
				throw new Exception("Failed to update state in the cache due to internal error.");
			}

			// Save the updated entity
			statesRepository.save(existedState);

			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("State has been updated successfully for this id: "+ id);
			responseModel.setData(existedState);

		} catch (BadRequestException badRequestException) {
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Validation failed for state input: "+ badRequestException.getMessage());
			responseModel.setData(null);
		} catch(RecordNotFoundException recordNotFoundException) {
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
			responseModel.setMessage("Fetch has failed: "+ recordNotFoundException.getMessage());
			responseModel.setData(null);
		} catch (Exception exception) {
			// Handle general errors
			log.info("Error in updateStateById(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in updateStateById(): "+ exception.getMessage());
			responseModel.setData(null);
		}

		log.info("End of States Service Implementation -> updateStateById() method");
		return responseModel;
	}
	
	@Override
	public ResponseModel<States> getStateById(UUID id) {
		log.info("Begin of States Service Implementation -> getStateById() method");
		ResponseModel<States> responseModel = new ResponseModel<>();

		try {
			// Find the country data by using state id
			Optional<States> optionalState = statesRepository.findById(id);
			if (optionalState.isEmpty()) {
				throw new RecordNotFoundException("State data is not found for this id: " + id);
			}

			States state = optionalState.get();

			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("State data has been fetched successfully for this id: "+ id);
			responseModel.setData(state);

		} catch (RecordNotFoundException recordNotFoundException) {
			log.info("No record found for the given Id: {}", recordNotFoundException.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Validation failed for state input: "+ recordNotFoundException.getMessage());
			responseModel.setData(null);
		} catch (Exception exception) {
			log.info("Error in getStateById(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in getStateById(): "+ exception.getMessage());
			responseModel.setData(null);
		}

		log.info("End of States Service Implementation -> getStateById() method");
		return responseModel;
	}

	@Override
	public ResponseModel<States> deleteStateById(UUID id) {
		log.info("Begin of States Service Implementation -> deleteStateById() method");
		ResponseModel<States> responseModel = new ResponseModel<>();

		try {
			// Find the country data by using state id
			Optional<States> optionalState = statesRepository.findById(id);
			if (optionalState.isEmpty()) {
				throw new RecordNotFoundException("State data is not found for this id: " + id);
			}

			States state = optionalState.get();
			
			masterDataLoader.deleteState(state);
			
			statesRepository.delete(state);

			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("State data has been deleted successfully for this id: "+ id);
			responseModel.setData(state);

		} catch (RecordNotFoundException recordNotFoundException) {
			log.info("No record found for the given Id: {}", recordNotFoundException.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Validation failed for state input: "+ recordNotFoundException.getMessage());
			responseModel.setData(null);
		} catch (Exception exception) {
			log.info("Error in deleteStateById(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in deleteStateById(): "+ exception.getMessage());
			responseModel.setData(null);
		}
		log.info("End of States Service Implementation -> deleteStateById() method");
		return responseModel;
	}
	
	private String getCellValue(Cell cell) {
		if(cell == null) {
			return "";
		} else {
			if(cell.getCellType() == CellType.STRING) {
				return cell.getStringCellValue();
			}
			else if(cell.getCellType() == CellType.NUMERIC) {
				return String.valueOf((int) cell.getNumericCellValue());
			}
			else {
				return "";
			}
		}
	}
	
	private boolean validationFields(States stateRequest) {
	    boolean validation = (stateRequest.getCountryName() != null && !stateRequest.getCountryName().isEmpty()) 
	            && (stateRequest.getCountryCode() != null && !stateRequest.getCountryCode().isEmpty())
	            && (stateRequest.getStateName() != null && !stateRequest.getStateName().isEmpty()) 
	            && (stateRequest.getStateCode() != null && !stateRequest.getStateCode().isEmpty());
	    
	    log.info("Validation result: {}", validation);
	    return validation;
	}

	
	private States convertDtoToEntity(States state, StateRequest stateRequest) {
		BeanUtils.copyProperties(stateRequest, state);
		return state;
	}

	private List<States> getStatesBasedOnCountryNamesAndStartsWith(List<States> allStatesInfo, List<String> countryNames, String startsWith, String sortedBy) {
		log.info("Begin of States Service Implementation -> getStatesBasedOnCountryNamesAndStartsWith() method");
		List<States> filteredData =  allStatesInfo.stream()
				.filter(state -> state.getCountryName() != null &&
				countryNames.stream().anyMatch(countryName -> countryName.equalsIgnoreCase(state.getCountryName())) &&
				 state.getStateName().toLowerCase().contains(startsWith.toLowerCase()))
				.sorted(applyDynamicSorting(sortedBy))
				.collect(Collectors.toList());
		log.info("End of States Service Implementation -> getStatesBasedOnCountryNamesAndStartsWith() method");
		return filteredData;
	}
	
	private List<States> getStatesBasedOnCountryNames(List<States> allStatesInfo, List<String> countryNames, String sortedBy) {
		log.info("Begin of States Service Implementation -> getStatesBasedOnCountryNames() method");
		List<States> filteredData =  allStatesInfo.stream()
				.filter(state -> state.getCountryName() != null &&
				 countryNames.stream().anyMatch(countryName -> countryName.equalsIgnoreCase(state.getCountryName())))
				.sorted(applyDynamicSorting(sortedBy))
				.collect(Collectors.toList());
		log.info("End of States Service Implementation -> getStatesBasedOnCountryNames() method");
		return filteredData;
	}
	
	private List<States> getStatesBasedOnStartsWith(List<States> allStatesInfo, String startsWith, String sortedBy) {
		log.info("Begin of States Service Implementation -> getStatesBasedOnStartsWith() method");
		List<States> filteredData = allStatesInfo.stream()
				.filter(state -> state.getStateName().toLowerCase().contains(startsWith.toLowerCase()))
				.sorted(applyDynamicSorting(sortedBy))
				.collect(Collectors.toList());
		log.info("End of States Service Implementation -> getStatesBasedOnStartsWith() method");
		return filteredData;
	}
	
	private Comparator<States> applyDynamicSorting(String sortedBy) {
		log.info("Begin of States Service Implementation -> applyDynamicSorting() method");
	    Map<String, Comparator<States>> sortingMap = new HashMap<>();

	    sortingMap.put("countryName", Comparator.comparing(States::getCountryName ,String.CASE_INSENSITIVE_ORDER));
	    sortingMap.put("countryCode", Comparator.comparing(States::getCountryCode,String.CASE_INSENSITIVE_ORDER));
	    sortingMap.put("stateName", Comparator.comparing(States::getStateName,String.CASE_INSENSITIVE_ORDER));
	    sortingMap.put("stateCode", Comparator.comparing(States::getStateCode,String.CASE_INSENSITIVE_ORDER));
	    sortingMap.put("createdBy", Comparator.comparing(States::getCreatedBy));
	    sortingMap.put("createdOn", Comparator.comparing(States::getCreatedOn));

	    Comparator<States> comparator = sortingMap.getOrDefault(sortedBy, Comparator.comparing(States::getStateName));
	    log.info("End of States Service Implementation -> applyDynamicSorting() method");
	    return comparator;
	}
}
