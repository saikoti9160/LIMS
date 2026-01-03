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

import com.digiworldexpo.lims.entities.master.Cities;
import com.digiworldexpo.lims.master.exception.BadRequestException;
import com.digiworldexpo.lims.master.exception.RecordNotFoundException;
import com.digiworldexpo.lims.master.model.request.CityRequest;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.repository.CitiesRepository;
import com.digiworldexpo.lims.master.service.CitiesService;
import com.digiworldexpo.lims.master.util.MasterDataLoader;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CitiesServiceImpl implements CitiesService {

	private final CitiesRepository citiesRepository;
	private final MasterDataLoader masterDataLoader;

	public CitiesServiceImpl(CitiesRepository citiesRepository, MasterDataLoader masterDataLoader) {
		this.citiesRepository = citiesRepository;
		this.masterDataLoader = masterDataLoader;
	}

	@Override
	public ResponseModel<String> uploadCitiesFile(MultipartFile multipartFile) {
		log.info("Begin of Cities Service Implementation -> uploadCitiesFile() method");
		ResponseModel<String> responseModel = new ResponseModel<String>();

		try (Workbook workbook = new XSSFWorkbook(multipartFile.getInputStream())) {
			if (multipartFile.isEmpty()) {
				throw new IllegalArgumentException("Please select a city file to upload");
			}

			// Remove the old data of continents
			citiesRepository.deleteAll();

			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> iteratorRows = sheet.iterator();

			if (iteratorRows.hasNext()) {
				iteratorRows.next();
			}
			List<Cities> allCities = new ArrayList<Cities>();

			while (iteratorRows.hasNext()) {
				Row currentRow = iteratorRows.next();
				String stateCode = getCellValue(currentRow.getCell(0));
				String stateName = getCellValue(currentRow.getCell(1));
				String cityName = getCellValue(currentRow.getCell(2));

				Cities cities = new Cities();
				cities.setStateCode(stateCode);
				cities.setStateName(stateName);
				cities.setCityName(cityName);
				
				allCities.add(cities);
			}
			masterDataLoader.uploadCity(allCities);
			citiesRepository.saveAll(allCities);

			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Cities data has been uploaded successfully");
			responseModel.setData(null);

		}catch (BadRequestException badRequestException) {
	    	log.info("Validation failed for city input: {}", badRequestException.getMessage());
	    	responseModel.setStatusCode(HttpStatus.OK.toString());
	        responseModel.setMessage("Validation failed for city input: "+ badRequestException.getMessage());
	        responseModel.setData(null);
	    } catch (Exception exception) {
	        // Handle any other unexpected exceptions
	    	log.info("Error in uploadCitiesFile(): {}", exception.getMessage());
	        responseModel.setStatusCode(HttpStatus.OK.toString());
	        responseModel.setMessage("Error in uploadCitiesFile(): "+ exception.getMessage());
	        responseModel.setData(null);
	    }

		log.info("End of Cities Service Implementation -> uploadCitiesFile() method");
		return responseModel;
	}

	@Override
	public ResponseModel<List<Cities>> getAllCities(String startsWith, List<String> stateNames, int pageNumber, int pageSize, String sortedBy) {
		log.info("Begin of Cities Service Implementation -> getAllCities() method");
		ResponseModel<List<Cities>> responseModel = new ResponseModel<>();

		try {
			List<Cities> allCitiesInfo = masterDataLoader.getCities();
			List<Cities> filteredCities = new ArrayList<Cities>();
			
			if(stateNames != null && !stateNames.isEmpty() && startsWith != null && !startsWith.isEmpty()) {
				filteredCities = getCitiesBasedOnStateNamesAndStartsWith(allCitiesInfo, stateNames, startsWith, sortedBy);
			} else if(stateNames != null && !stateNames.isEmpty()) {
				filteredCities = getCitiesBasedOnStateNames(allCitiesInfo, stateNames, sortedBy);
			} else if(startsWith != null && !startsWith.isEmpty()) {
				filteredCities = getCitiesBasedOnStartsWith(allCitiesInfo, startsWith, sortedBy);
			} else {
				filteredCities = allCitiesInfo.stream().sorted(applyDynamicSorting(sortedBy)).collect(Collectors.toList());
			}

			// Apply pagination
			Pageable pageable = PageRequest.of(pageNumber, pageSize);
			int start = (int) pageable.getOffset();
			int end = Math.min(start + pageable.getPageSize(), filteredCities.size());
			if (start > filteredCities.size()) {
				throw new IllegalArgumentException("Page number exceeds available data.");
			}
			List<Cities> paginatedList = filteredCities.subList(start, end);

			// Set response model data
			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Cities data retrieved successfully.");
			responseModel.setData(paginatedList);
			responseModel.setTotalCount(filteredCities.size());
			responseModel.setPageNumber(pageNumber);
			responseModel.setPageSize(pageSize);
			responseModel.setSortedBy("City name ascending order");

		} catch (IllegalArgumentException illegalArgumentException) {
			log.info("Invalid input parameters: {}", illegalArgumentException.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Invalid input parameters: "+ illegalArgumentException.getMessage());
			responseModel.setData(null);
		} catch (Exception exception) {
			log.info("Error in getAllCities(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in getAllCities(): "+ exception.getMessage());
			responseModel.setData(null);
		}
		log.info("End of Cities Service Implementation -> getAllCities() method");
		return responseModel;
	}

	@Override
	public ResponseModel<Cities> saveCity(Cities cityRequest, UUID createdBy) {
		log.info("Begin of Cities Service Implementation -> saveCity() method");
		ResponseModel<Cities> responseModel = new ResponseModel<Cities>();

		try {
			if (!validationFields(cityRequest)) {
				throw new BadRequestException("Please provide all the required fields");
			}
	
			cityRequest.setCreatedBy(createdBy);
			masterDataLoader.addCity(cityRequest);

			citiesRepository.save(cityRequest); // Saving city information

			responseModel.setStatusCode(HttpStatus.CREATED.toString());
			responseModel.setMessage("City has been saved successfully.");
			responseModel.setData(cityRequest);

		} catch (BadRequestException badRequestException) {
			log.info("Validation failed for city input: {}", badRequestException.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Validation failed for city input: "+ badRequestException.getMessage());
			responseModel.setData(null);
		} catch (Exception exception) {
			log.info("Error in saveCity(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in saveCity():"+ exception.getMessage());
			responseModel.setData(null);
		}
		log.info("End of Cities Service Implementation -> saveCity() method");
		return responseModel;
	}

	@Override
	public ResponseModel<Cities> updateCityById(UUID id, Cities cityRequest, UUID modifiedBy) {
		log.info("Begin of Cities Service Implementation -> updateCityById() method");
		ResponseModel<Cities> responseModel = new ResponseModel<>();

		try {
			// Validate the required fields from cityRequest
			if (!validationFields(cityRequest)) {
				throw new BadRequestException("Please provide all the required fields");
			}

			// Find the city data by using city id
			Optional<Cities> optionalCity = citiesRepository.findById(id);
			if (optionalCity.isEmpty()) {
				throw new RecordNotFoundException("No city data found for Id: " + id);
			}

			Cities existedCity = optionalCity.get();
			
			BeanUtils.copyProperties(cityRequest, existedCity,"id", "createdBy", "createdOn");

			// Set additional fields
			existedCity.setActive(true);
			existedCity.setModifiedBy(modifiedBy);
			existedCity.setModifiedOn(new Timestamp(System.currentTimeMillis()));
			
			if (masterDataLoader != null) {
				masterDataLoader.updateCity(existedCity);
				log.info("City updated in the cache");
			} else {
				log.error("masterDataLoader is null");
				throw new Exception("Failed to update city in the cache due to internal error.");
			}

			// Save the updated entity
			citiesRepository.save(existedCity);

			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("City information has been updated successfullyfor this id: " + id);
			responseModel.setData(existedCity);

		} catch (BadRequestException badRequestException) {
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Validation failed for city input: "+ badRequestException.getMessage());
			responseModel.setData(null);
		} catch(RecordNotFoundException recordNotFoundException) {
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
			responseModel.setMessage("Fetch has failed: "+ recordNotFoundException.getMessage());
			responseModel.setData(null);
		} catch (Exception exception) {
			// Handle general errors
			log.info("Error in updateCityById(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in updateCityById(): "+ exception.getMessage());
			responseModel.setData(null);
		}

		log.info("End of Cities Service Implementation -> updateCityById() method");
		return responseModel;
	}

	@Override
	public ResponseModel<Cities> getCityById(UUID id) {
		log.info("Begin of Cities Service Implementation -> getCityById() method");
		ResponseModel<Cities> responseModel = new ResponseModel<>();

		try {
			// Find the city data by using city id
			Optional<Cities> optionalCity = citiesRepository.findById(id);
			if (optionalCity.isEmpty()) {
				throw new RecordNotFoundException("No city data found for Id: " + id);
			}

			Cities city = optionalCity.get();

			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("City info has been fetched successfully for this id: " + id);
			responseModel.setData(city);

		} catch (RecordNotFoundException recordNotFoundException) {
			log.info("No record found for the given Id: {}", recordNotFoundException.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Validation failed for city input: "+ recordNotFoundException.getMessage());
			responseModel.setData(null);
		} catch (Exception exception) {
			log.info("Error in getCityById(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in getCityById(): "+ exception.getMessage());
			responseModel.setData(null);
		}

		log.info("End of Cities Service Implementation -> getCityById() method");
		return responseModel;
	}

	@Override
	public ResponseModel<Cities> deleteCityById(UUID id) {
		log.info("Begin of Cities Service Implementation -> deleteCityById() method");
		ResponseModel<Cities> responseModel = new ResponseModel<>();

		try {
			// Find the city data by using city id
			Optional<Cities> optionalCity = citiesRepository.findById(id);
			if (optionalCity.isEmpty()) {
				throw new RecordNotFoundException("No city data found for Id: " + id);
			}

			Cities city = optionalCity.get();
			masterDataLoader.deleteCity(city);
			citiesRepository.delete(city);

			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("City info has been deleted successfully for this id: " + id);
			responseModel.setData(city);

		} catch (RecordNotFoundException recordNotFoundException) {
			log.info("No record found for the given Id: {}", recordNotFoundException.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Validation failed for city input: "+ recordNotFoundException.getMessage());
			responseModel.setData(null);
		} catch (Exception exception) {
			log.info("Error in deleteCityById(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in deleteCityById(): "+ exception.getMessage());
			responseModel.setData(null);
		}

		log.info("End of Cities Service Implementation -> deleteCityById() method");
		return responseModel;
	}

	private String getCellValue(Cell cell) {
		if (cell == null) {
			return "";
		} else {
			if (cell.getCellType() == CellType.STRING) {
				return cell.getStringCellValue();
			} else if (cell.getCellType() == CellType.NUMERIC) {
				return String.valueOf((int) cell.getNumericCellValue());
			} else {
				return "";
			}
		}
	}

	private boolean validationFields(Cities cityRequest) {
		boolean validation =(cityRequest.getStateName() != null && !cityRequest.getStateName().isEmpty())
				&& (cityRequest.getCityName() != null && !cityRequest.getCityName().isEmpty());

		log.info("Validation result: {}", validation);
		return validation;
	}

	private Cities convertDtoToEntity(Cities city, CityRequest cityRequest) {
		BeanUtils.copyProperties(cityRequest, city);
		return city;
	}
	
	private List<Cities> getCitiesBasedOnStateNamesAndStartsWith(List<Cities> allCitiesInfo, List<String> stateNames, String startsWith, String sortedBy) {
		log.info("Begin of Cities Service Implementation -> getCitiesBasedOnStateNamesAndStartsWith() method");
		List<Cities> filteredData = allCitiesInfo.stream()
				.filter(city -> city.getStateName() != null &&
				stateNames.stream().anyMatch(stateName -> stateName.equalsIgnoreCase(city.getStateName())) &&
				 city.getCityName().toLowerCase().contains(startsWith.toLowerCase()))
				.sorted(applyDynamicSorting(sortedBy))
				.collect(Collectors.toList());
		log.info("Begin of Cities Service Implementation -> getCitiesBasedOnStateNamesAndStartsWith() method");
		return filteredData;
	}
	
	private List<Cities> getCitiesBasedOnStateNames(List<Cities> allCitiesInfo, List<String> stateNames, String sortedBy) {
		log.info("Begin of Cities Service Implementation -> getCitiesBasedOnStateNames() method");
		List<Cities> filteredData = allCitiesInfo.stream()
				.filter(state -> state.getStateName() != null &&
				stateNames.stream().anyMatch(stateName -> stateName.equalsIgnoreCase(state.getStateName())))
				.sorted(applyDynamicSorting(sortedBy))
				.collect(Collectors.toList());
		log.info("End of Cities Service Implementation -> getCitiesBasedOnStateNames() method");
		return filteredData;
	}
	
	private List<Cities> getCitiesBasedOnStartsWith(List<Cities> allCitiesInfo, String startsWith, String sortedBy) {
		log.info("Begin of Cities Service Implementation -> getCitiesBasedOnStartsWith() method");
		List<Cities> filteredData = allCitiesInfo.stream()
				.filter(city -> city.getCityName().toLowerCase().contains(startsWith.toLowerCase()))
				.sorted(applyDynamicSorting(sortedBy))
				.collect(Collectors.toList());
		log.info("End of Cities Service Implementation -> getCitiesBasedOnStartsWith() method");
		return filteredData;
	}
	
	private Comparator<Cities> applyDynamicSorting(String sortedBy) {
		log.info("End of Cities Service Implementation -> applyDynamicSorting() method");
	    Map<String, Comparator<Cities>> sortingMap = new HashMap<>();

	    sortingMap.put("cityName", Comparator.comparing(Cities::getCityName , String.CASE_INSENSITIVE_ORDER));
	    sortingMap.put("stateName", Comparator.comparing(Cities::getStateName , String.CASE_INSENSITIVE_ORDER));
	    sortingMap.put("stateCode", Comparator.comparing(Cities::getStateCode , String.CASE_INSENSITIVE_ORDER));
	    sortingMap.put("createdBy", Comparator.comparing(Cities::getCreatedBy));
	    sortingMap.put("createdOn", Comparator.comparing(Cities::getCreatedOn));

	    Comparator<Cities> comparator = sortingMap.getOrDefault(sortedBy, Comparator.comparing(Cities::getCityName));
	    log.info("End of Cities Service Implementation -> applyDynamicSorting() method");
	    return comparator;
	}

}
