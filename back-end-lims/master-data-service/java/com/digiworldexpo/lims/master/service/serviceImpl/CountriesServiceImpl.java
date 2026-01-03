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

import com.digiworldexpo.lims.entities.master.Countries;
import com.digiworldexpo.lims.master.exception.BadRequestException;
import com.digiworldexpo.lims.master.exception.RecordNotFoundException;
import com.digiworldexpo.lims.master.model.request.CountryRequest;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.repository.CountriesRepository;
import com.digiworldexpo.lims.master.service.CountriesService;
import com.digiworldexpo.lims.master.util.MasterDataLoader;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CountriesServiceImpl implements CountriesService {

	private final CountriesRepository countriesRepository;
	private final MasterDataLoader masterDataLoader;

	public CountriesServiceImpl(CountriesRepository countriesRepository, MasterDataLoader masterDataLoader) {
		this.countriesRepository = countriesRepository;
		this.masterDataLoader = masterDataLoader;
	}

	@Override
	public ResponseModel<String> uploadCountriesFile(MultipartFile multipartFile) {

		log.info("Begin of Countries Service Implementation -> uploadCountriesFile() method");
		ResponseModel<String> responseModel = new ResponseModel<String>();

		try (Workbook workbook = new XSSFWorkbook(multipartFile.getInputStream())) {
			if (multipartFile.isEmpty()) {
				throw new BadRequestException("Please select a country file to upload");
			}

			// Remove the old data of countries
			countriesRepository.deleteAll();

			Sheet sheet = workbook.getSheetAt(0);
			Iterator<Row> iteratorRows = sheet.iterator();

			if (iteratorRows.hasNext()) {
				iteratorRows.next();
			}
			
			List<Countries> allCountries = new ArrayList<Countries>();

			while (iteratorRows.hasNext()) {
				Row currentRow = iteratorRows.next();
				String continentCode = getCellValue(currentRow.getCell(0));
				String countryName = getCellValue(currentRow.getCell(1));
				String countryCode = getCellValue(currentRow.getCell(2));
				String phoneCode = getCellValue(currentRow.getCell(3));
				String currency = getCellValue(currentRow.getCell(4));
				String currencySymbol = getCellValue(currentRow.getCell(5));
				String continentName = getCellValue(currentRow.getCell(6));

				Countries countries = new Countries();
				countries.setContinentCode(continentCode);
				countries.setCountryName(countryName);
				countries.setCountryCode(countryCode);
				countries.setPhoneCode(phoneCode);
				countries.setCurrency(currency);
				countries.setCurrencySymbol(currencySymbol);
				countries.setContinentName(continentName);

				allCountries.add(countries);
			}
	        masterDataLoader.uploadCountry(allCountries);

			countriesRepository.saveAll(allCountries);

			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Countries Data have been uploaded successfully");
			responseModel.setData(null);

		} catch (BadRequestException badRequestException) {
	    	log.info("Validation failed for country input: {}", badRequestException.getMessage());
	    	responseModel.setStatusCode(HttpStatus.OK.toString());
	        responseModel.setMessage("Validation failed for country input: "+ badRequestException.getMessage());
	        responseModel.setData(null);
	    } catch (Exception exception) {
	    	log.info("Error in uploadCountriesFile(): {}", exception.getMessage());
	        responseModel.setStatusCode(HttpStatus.OK.toString());
	        responseModel.setMessage("Error in uploadCountriesFile(): "+ exception.getMessage());
	        responseModel.setData(null);
	    }
		log.info("End of Countries Service Implementation -> uploadCountriesFile() method");
		return responseModel;
	}

	@Override
	public ResponseModel<List<Countries>> getAllCountries(String startsWith,List<String> continentNames, int pageNumber, int pageSize,
			String sortedBy) {
		log.info("Begin of Countries Service Implementation -> getAllCountries() method");
		ResponseModel<List<Countries>> responseModel = new ResponseModel<>();

		try {
			
			List<Countries> allCountriesInfo = masterDataLoader.getCountries();
			List<Countries> filteredCountries = new ArrayList<Countries>();
			
			if(continentNames != null && !continentNames.isEmpty() && startsWith != null && !startsWith.isEmpty()) {
				filteredCountries = getCountriesBasedOnContinentNamesAndStartsWith(allCountriesInfo, continentNames, startsWith, sortedBy);
			} else if(continentNames != null && !continentNames.isEmpty()) {
				filteredCountries = getCountriesBasedOnContientNames(allCountriesInfo, continentNames, sortedBy);
			} else if(startsWith != null && !startsWith.isEmpty()) {
				filteredCountries = getCountriesBasedOnStartsWith(allCountriesInfo, startsWith, sortedBy);
			} else {
				filteredCountries = allCountriesInfo.stream().sorted(applyDynamicSorting(sortedBy)).collect(Collectors.toList());
			}

			// Apply pagination
			Pageable pageable = PageRequest.of(pageNumber, pageSize);
			int start = (int) pageable.getOffset();
			int end = Math.min(start + pageable.getPageSize(), filteredCountries.size());
			if (start > filteredCountries.size()) {
				throw new IllegalArgumentException("Page number exceeds available data.");
			}
			List<Countries> paginatedList = filteredCountries.subList(start, end);

			// Set response model data
			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Countries data retrieved successfully.");
			responseModel.setData(paginatedList);
			responseModel.setTotalCount(filteredCountries.size());
			responseModel.setPageNumber(pageNumber);
			responseModel.setPageSize(pageSize);
			responseModel.setSortedBy("Country name ascending order");

		} catch (IllegalArgumentException illegalArgumentException) {
			log.info("Invalid input parameters: {}", illegalArgumentException.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Invalid input parameters: "+ illegalArgumentException.getMessage());
			responseModel.setData(null);
		} catch (Exception exception) {
			log.info("Error in getAllCountries(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in getAllCountries(): "+ exception.getMessage());
			responseModel.setData(null);
		}
		log.info("End of Countries Service Implementation -> getAllCountries() method");
		return responseModel;
	}

	@Override
	public ResponseModel<Countries> saveCountry(Countries country, UUID createdBy) {
		log.info("Begin of Countries Service Implementation -> saveCountry() method");
		ResponseModel<Countries> responseModel = new ResponseModel<Countries>();

		try {
			if (!validationFields(country)) {
				throw new BadRequestException("Please provide all the required fields");
			}

			country.setCreatedBy(createdBy);
			masterDataLoader.addCountry(country);
			log.info("New country is added in the cache");
			countriesRepository.save(country); // Saving country information

			responseModel.setStatusCode(HttpStatus.CREATED.toString());
			responseModel.setMessage("Country has been saved successfully.");
			responseModel.setData(country);

		} catch (BadRequestException badRequestException) {
			log.info("Validation failed for country input: {}", badRequestException.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Validation failed for country input: "+ badRequestException.getMessage());
			responseModel.setData(null);
		} catch (Exception exception) {
			log.info("Error in saveCountry(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in saveCountry():"+ exception.getMessage());
			responseModel.setData(null);
		}
		log.info("End of Countries Service Implementation -> saveCountry() method");
		return responseModel;
	}

	@Override
	public ResponseModel<Countries> updateCountryById(UUID id, Countries newCountryData, UUID modifiedBy) {
		log.info("Begin of Countries Service Implementation -> updateCountryById() method");
		ResponseModel<Countries> responseModel = new ResponseModel<>();

		try {
			// Validate the required fields from countryRequest
			if (!validationFields(newCountryData)) {
				throw new BadRequestException("Please provide all the required fields");
			}

			// Find the country data by using country id
			Optional<Countries> optionalCountry = countriesRepository.findById(id);
			if (optionalCountry.isEmpty()) {
				throw new RecordNotFoundException("No country data found for Id: " + id);
			}

			Countries existingCountry = optionalCountry.get();

			// Convert DTO to Entity
			existingCountry.setContinentCode(newCountryData.getContinentCode());
			existingCountry.setContinentName(newCountryData.getContinentName());
			existingCountry.setCountryCode(newCountryData.getCountryCode());
			existingCountry.setCountryName(newCountryData.getCountryName());
			existingCountry.setPhoneCode(newCountryData.getPhoneCode());
			existingCountry.setCurrency(newCountryData.getCurrency());
			existingCountry.setCurrencySymbol(newCountryData.getCurrencySymbol());

			// Set additional fields
			existingCountry.setActive(true);
			existingCountry.setModifiedBy(modifiedBy);
			existingCountry.setModifiedOn(new Timestamp(System.currentTimeMillis()));
			
		    if (masterDataLoader != null) {
		        masterDataLoader.updateCountry(existingCountry);
		        log.info("Country updated in the cache");
		    } else {
		        log.error("masterDataLoader is null");
		        throw new Exception("Failed to update country in the cache due to internal error.");
		    }

			// Save the updated entity
			countriesRepository.save(existingCountry);

			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Country has been updated successfully.");
			responseModel.setData(existingCountry);

		} catch (BadRequestException badRequestException) {
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Validation failed for country input: "+ badRequestException.getMessage());
			responseModel.setData(null);
		} catch(RecordNotFoundException recordNotFoundException) {
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
			responseModel.setMessage("Fetch has failed: "+ recordNotFoundException.getMessage());
			responseModel.setData(null);
		} catch (Exception exception) {
			// Handle general errors
			log.info("Error in updateCountryById(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in updateCountryById(): "+ exception.getMessage());
			responseModel.setData(null);
		}

		log.info("End of Countries Service Implementation -> updateCountryById() method");
		return responseModel;
	}

	@Override
	public ResponseModel<Countries> getCountryById(UUID id) {
		log.info("Begin of Countries Service Implementation -> getCountryById() method");
		ResponseModel<Countries> responseModel = new ResponseModel<>();

		try {
			// Find the country data by using country id
			Optional<Countries> optionalCountry = countriesRepository.findById(id);
			if (optionalCountry.isEmpty()) {
				throw new RecordNotFoundException("No country data found for Id: " + id);
			}

			Countries country = optionalCountry.get();

			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Country data has been fetched successfully for this id: " + id);
			responseModel.setData(country);

		} catch (RecordNotFoundException recordNotFoundException) {
			log.info("No record found for the given Id: {}", recordNotFoundException.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Validation failed for country input: "+ recordNotFoundException.getMessage());
			responseModel.setData(null);
		} catch (Exception exception) {
			log.info("Error in getCountryById(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in getCountryById(): "+ exception.getMessage());
			responseModel.setData(null);
		}

		log.info("End of Countries Service Implementation -> getCountryById() method");
		return responseModel;
	}

	@Override
	public ResponseModel<Countries> deleteCountryById(UUID id) {
		log.info("Begin of Countries Service Implementation -> deleteCountryById() method");
		ResponseModel<Countries> responseModel = new ResponseModel<>();

		try {
			// Find the country data by using country id
			Optional<Countries> optionalCountry = countriesRepository.findById(id);
			if (optionalCountry.isEmpty()) {
				throw new RecordNotFoundException("No country data found for Id: " + id);
			}

			Countries country = optionalCountry.get();
			masterDataLoader.deleteCountry(country);
			log.info("Country data is deleted in the cache");
			countriesRepository.delete(country);

			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Country data has been deleted successfully for this id: " + id);
			responseModel.setData(country);

		} catch (RecordNotFoundException recordNotFoundException) {
			log.info("No record found for the given Id: {}", recordNotFoundException.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Validation failed for country input: "+ recordNotFoundException.getMessage());
			responseModel.setData(null);
		} catch (Exception exception) {
			log.info("Error in deleteCountryById(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in deleteCountryById(): "+ exception.getMessage());
			responseModel.setData(null);
		}
		log.info("End of Countries Service Implementation -> deleteCountryById() method");
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

	private boolean validationFields(Countries country) {
		boolean validation = (country.getCountryName() != null && !country.getCountryName().isEmpty())
				&& (country.getCountryCode() != null && !country.getCountryCode().isEmpty())
				&& (country.getCurrencySymbol() != null && !country.getCurrencySymbol().isEmpty())
				&& (country.getContinentCode() != null && !country.getContinentCode().isEmpty())
				&& (country.getContinentName() != null && !country.getContinentName().isEmpty())
				&& (country.getPhoneCode() != null && !country.getPhoneCode().isEmpty())
				&& (country.getCurrency() != null && !country.getCurrency().isEmpty());

		log.info("Validation result: {}", validation);
		return validation;
	}

	private Countries convertDtoToEntity(Countries country, CountryRequest countryRequest) {
		BeanUtils.copyProperties(countryRequest, country);
		return country;
	}
	
	private List<Countries> getCountriesBasedOnContinentNamesAndStartsWith(List<Countries> allCountriesInfo, List<String> continentNames, String startsWith, String sortedBy) {
		log.info("Begin of Countries Service Implementation -> getCountriesBasedOnContinentNamesAndStartsWith() method");
		List<Countries> filteredData = allCountriesInfo.stream()
				.filter(country -> country.getContinentName() != null &&
				 continentNames.stream().anyMatch(continentName -> continentName.equalsIgnoreCase(country.getContinentName())) &&
				 country.getCountryName().toLowerCase().contains(startsWith.toLowerCase()))
				.sorted(applyDynamicSorting(sortedBy))
				.collect(Collectors.toList());
		log.info("End of Countries Service Implementation -> getCountriesBasedOnContinentNamesAndStartsWith() method");
		return filteredData;
	}
	
	private List<Countries> getCountriesBasedOnContientNames(List<Countries> allCountriesInfo, List<String> continentNames, String sortedBy) {
		log.info("Begin of Countries Service Implementation -> getCountriesBasedOnContientNames() method");
		List<Countries> filteredData = allCountriesInfo.stream()
				.filter(country -> country.getContinentName() != null && 
				continentNames.stream().anyMatch(continentName -> continentName.equalsIgnoreCase(country.getContinentName())))
				.sorted(applyDynamicSorting(sortedBy))
				.collect(Collectors.toList());
		log.info("End of Countries Service Implementation -> getCountriesBasedOnContientNames() method");
		return filteredData;
	}
	
	private List<Countries> getCountriesBasedOnStartsWith(List<Countries> allCountriesInfo, String startsWith, String sortedBy) {
		log.info("Begin of Countries Service Implementation -> getCountriesBasedOnStartsWith() method");
		List<Countries> filteredData = allCountriesInfo.stream()
				.filter(country -> country.getCountryName().toLowerCase().contains(startsWith.toLowerCase()))
				.sorted(applyDynamicSorting(sortedBy))
				.collect(Collectors.toList());
		log.info("End of Countries Service Implementation -> getCountriesBasedOnStartsWith() method");
		return filteredData;
	}
	
	private Comparator<Countries> applyDynamicSorting(String sortedBy) {
		log.info("Begin of Countries Service Implementation -> applyDynamicSorting() method");
	    Map<String, Comparator<Countries>> sortingMap = new HashMap<>();

	    sortingMap.put("countryName", Comparator.comparing(Countries::getCountryName,String.CASE_INSENSITIVE_ORDER));
	    sortingMap.put("continentName", Comparator.comparing(Countries::getContinentName,String.CASE_INSENSITIVE_ORDER));
	    sortingMap.put("countryCode", Comparator.comparing(Countries::getCountryCode,String.CASE_INSENSITIVE_ORDER));
	    sortingMap.put("continentCode", Comparator.comparing(Countries::getContinentCode,String.CASE_INSENSITIVE_ORDER));
	    sortingMap.put("phoneCode", Comparator.comparing(Countries::getPhoneCode));
	    sortingMap.put("currency", Comparator.comparing(Countries::getCurrency));
	    sortingMap.put("currencySymbol", Comparator.comparing(Countries::getCurrencySymbol));
	    sortingMap.put("createdBy", Comparator.comparing(Countries::getCreatedBy));

	    Comparator<Countries> comparator = sortingMap.getOrDefault(sortedBy, Comparator.comparing(Countries::getCountryName));
	    log.info("End of Countries Service Implementation -> applyDynamicSorting() method");
	    return comparator;
	}
	
}
