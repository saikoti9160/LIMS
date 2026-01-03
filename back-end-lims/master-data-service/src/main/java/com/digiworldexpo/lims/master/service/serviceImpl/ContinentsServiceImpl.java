package com.digiworldexpo.lims.master.service.serviceImpl;



import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.digiworldexpo.lims.entities.master.Continents;
import com.digiworldexpo.lims.master.exception.BadRequestException;
import com.digiworldexpo.lims.master.exception.RecordNotFoundException;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.repository.ContinentsRepository;
import com.digiworldexpo.lims.master.service.ContinentsService;
import com.digiworldexpo.lims.master.util.MasterDataLoader;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ContinentsServiceImpl implements ContinentsService {

	private final ContinentsRepository continentsRepository;
	private final MasterDataLoader masterDataLoader;
	
	public ContinentsServiceImpl(ContinentsRepository continentsRepository, MasterDataLoader masterDataLoader) {
		this.continentsRepository = continentsRepository;
		this.masterDataLoader = masterDataLoader;
	}

	@Override
	public ResponseModel<String> uploadContinentsFile(MultipartFile multipartFile) {
	    log.info("Begin of Continents Service Implementation -> uploadContinentsFile() method");
	    ResponseModel<String> responseModel = new ResponseModel<>();

	    try (Workbook workbook = new XSSFWorkbook(multipartFile.getInputStream())) {
	        if (multipartFile.isEmpty()) {
	            // Throw custom exception
	            throw new BadRequestException("Please select a continent file to upload");
	        }

	        // Remove old data of continents
	        continentsRepository.deleteAll();

	        Sheet sheet = workbook.getSheetAt(0);
	        Iterator<Row> iteratorRows = sheet.iterator();
	        if (iteratorRows.hasNext()) {
	            iteratorRows.next(); // Skip the first row (header)
	        }
	        
	        List<Continents> allContinents = new ArrayList<Continents>();
	        while (iteratorRows.hasNext()) {
	            Row currentRow = iteratorRows.next();
	            String cellContinentName = getCellValue(currentRow.getCell(0));
	            String cellContinentCode = getCellValue(currentRow.getCell(1));

	            Continents continents = new Continents();
	            continents.setContinentName(cellContinentName);
	            continents.setContinentCode(cellContinentCode);
	            
	            allContinents.add(continents);
	        }
	        
	        masterDataLoader.uploadContinent(allContinents);
	        
	        continentsRepository.saveAll(allContinents);

	        // Success response
	        responseModel.setStatusCode(HttpStatus.OK.toString());
	        responseModel.setMessage("Continents Data have been uploaded successfully");
	        responseModel.setData(null);

	    } catch (BadRequestException badRequestException) {
	    	log.info("Validation failed for continent input: {}", badRequestException.getMessage());
	    	responseModel.setStatusCode(HttpStatus.OK.toString());
	        responseModel.setMessage("Validation failed for continent input: "+ badRequestException.getMessage());
	        responseModel.setData(null);
	    } catch (Exception exception) {
	        // Handle any other unexpected exceptions
	    	log.info("Error in uploadContinentsFile(): {}", exception.getMessage());
	        responseModel.setStatusCode(HttpStatus.OK.toString());
	        responseModel.setMessage("Error in uploadContinentsFile(): "+ exception.getMessage());
	        responseModel.setData(null);
	    }

	    log.info("End of Continents Service Implementation -> uploadContinentsFile() method");
	    return responseModel;
	}

	@Override
	public ResponseModel<List<Continents>> getAllContinents(String startsWith, int pageNumber, int pageSize,
			String sortedBy) {
		log.info("Begin of Continents Service Implementation -> getAllContinents() method");
		ResponseModel<List<Continents>> responseModel = new ResponseModel<>();

		try {

			List<Continents> allContinentsInfo = masterDataLoader.getContinents();
			List<Continents> filteredContinentsData = new ArrayList<Continents>();
			
			if (startsWith != null && !startsWith.isEmpty()) {
				filteredContinentsData = getContinentsBasedOnStartsWith(allContinentsInfo, startsWith, sortedBy);
			} else {
				filteredContinentsData = allContinentsInfo.stream().sorted(applyDynamicSorting(sortedBy)).collect(Collectors.toList());
			}

			// Apply pagination
			Pageable pageable = PageRequest.of(pageNumber, pageSize);
			int start = (int) pageable.getOffset();
			int end = Math.min(start + pageable.getPageSize(), filteredContinentsData.size());
			if (start > filteredContinentsData.size()) {
				throw new IllegalArgumentException("Page number exceeds available data.");
			}
			List<Continents> paginatedList = filteredContinentsData.subList(start, end);

			// Set response model data
			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Continents data retrieved successfully.");
			responseModel.setData(paginatedList);
			responseModel.setTotalCount(filteredContinentsData.size());
			responseModel.setPageNumber(pageNumber);
			responseModel.setPageSize(pageSize);
			responseModel.setSortedBy("Sorted in ascending order based on: " + sortedBy);

		} catch (IllegalArgumentException illegalArgumentException) {
			log.info("Invalid input parameters: {}", illegalArgumentException.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Invalid input parameters: "+ illegalArgumentException.getMessage());
			responseModel.setData(null);
		} catch (Exception exception) {
			log.info("Error in getAllContinents(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in getAllContinents(): "+ exception.getMessage());
			responseModel.setData(null);
		}
		log.info("End of Continents Service Implementation -> getAllContinents() method");
		return responseModel;
	}

	@Override
	public ResponseModel<Continents> saveContinent(Continents continentRequest, UUID createdBy) {
		log.info("Begin of Continents Service Implementation -> saveContinent() method");
		ResponseModel<Continents> responseModel = new ResponseModel<Continents>();

		try {
			if (!validationFields(continentRequest)) {
				throw new BadRequestException("Please provide all the required fields");
			}
			
			continentRequest.setCreatedBy(createdBy);
			
			masterDataLoader.addContinent(continentRequest);
			log.info("New continent is added in the cache");
			
			continentsRepository.save(continentRequest); // Saving Continent information

			responseModel.setStatusCode(HttpStatus.CREATED.toString());
			responseModel.setMessage("Continent information has been saved successfully.");
			responseModel.setData(continentRequest);

		} catch (BadRequestException badRequestException) {
			log.info("Validation failed for continent input: {}", badRequestException.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Validation failed for continent input: "+ badRequestException.getMessage());
			responseModel.setData(null);
		} catch (Exception exception) {
			log.info("Error in saveContinent(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in saveContinent():"+ exception.getMessage());
			responseModel.setData(null);
		}
		log.info("End of Continents Service Implementation -> saveContinent() method");
		return responseModel;
	}

	@Override
	public ResponseModel<Continents> updateContinentById(UUID id, Continents continentRequest, UUID modifiedBy) {
		log.info("Begin of Continents Service Implementation -> updateContinentById() method");
		ResponseModel<Continents> responseModel = new ResponseModel<>();

		try {
			// Validate the required fields from continentRequest
			if (!validationFields(continentRequest)) {
				throw new BadRequestException("Please provide all the required fields");
			}

			Continents existedContinent = continentsRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("No continent data found for Id: " + id));

			// Set additional fields
			existedContinent.setContinentName(continentRequest.getContinentName());
			existedContinent.setContinentCode(continentRequest.getContinentCode());
			existedContinent.setModifiedBy(modifiedBy);
			existedContinent.setActive(true);
			existedContinent.setModifiedOn(new Timestamp(System.currentTimeMillis()));
			
			if (masterDataLoader != null) {
		        masterDataLoader.updateContinent(existedContinent);
		        log.info("Continent updated in the cache");
		    } else {
		        log.error("masterDataLoader is null");
		        throw new Exception("Failed to update continent in the cache due to internal error.");
		    }

			// Save the updated entity
			continentsRepository.save(existedContinent);

			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Continent has been updated successfully for this id: " + id);
			responseModel.setData(existedContinent);

		} catch (BadRequestException badRequestException) {
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Validation failed for continent input: "+ badRequestException.getMessage());
			responseModel.setData(null);
		} catch(RecordNotFoundException recordNotFoundException) {
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
			responseModel.setMessage("Fetch has failed: "+ recordNotFoundException.getMessage());
			responseModel.setData(null);
		} catch (Exception exception) {
			// Handle general errors
			log.info("Error in updateContinentById(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in updateContinentById(): "+ exception.getMessage());
			responseModel.setData(null);
		}

		log.info("End of Continents Service Implementation -> updateContinentById() method");
		return responseModel;
	}

	@Override
	public ResponseModel<Continents> getContinentById(UUID id) {
		log.info("Begin of Continents Service Implementation -> getContinentById() method");
		ResponseModel<Continents> responseModel = new ResponseModel<>();

		try {
			Continents continent = continentsRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("No continent data found for Id: " + id));

			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Continent data has been fetched successfully for this id: " + id);
			responseModel.setData(continent);

		} catch (RecordNotFoundException recordNotFoundException) {
			log.info("No record found for the given Id: {}", recordNotFoundException.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Validation failed for continent input: "+ recordNotFoundException.getMessage());
			responseModel.setData(null);
		} catch (Exception exception) {
			log.info("Error in getContinentById(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in getContinentById(): "+ exception.getMessage());
			responseModel.setData(null);
		}

		log.info("End of Continents Service Implementation -> getContinentById() method");
		return responseModel;
	}
	
	@Override
	public ResponseModel<Continents> deleteContinentById(UUID id) {
		log.info("Begin of Continents Service Implementation -> deleteContinentById() method");
		ResponseModel<Continents> responseModel = new ResponseModel<>();

		try {
			Continents continent = continentsRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("No continent data found for Id: " + id));
			
			masterDataLoader.deleteContinent(continent);
			log.info("Continent data is deleted in the cache");

			continentsRepository.delete(continent);
			
			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Continent data has been deleted successfully for this id: " + id);
			responseModel.setData(continent);

		} catch (RecordNotFoundException recordNotFoundException) {
			log.info("No record found for the given Id: {}", recordNotFoundException.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Validation failed for continent input: "+ recordNotFoundException.getMessage());
			responseModel.setData(null);
		} catch (Exception exception) {
			log.info("Error in deleteContinentById(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in deleteContinentById(): "+ exception.getMessage());
			responseModel.setData(null);
		}

		log.info("End of Continents Service Implementation -> deleteContinentById() method");
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

	private boolean validationFields(Continents continentRequest) {
		boolean validation = (continentRequest.getContinentName() != null
				&& !continentRequest.getContinentName().isEmpty())
				&& (continentRequest.getContinentCode() != null && !continentRequest.getContinentCode().isEmpty());

		log.info("Validation result: {}", validation);
		return validation;
	}
	
	private List<Continents> getContinentsBasedOnStartsWith(List<Continents> allContinentsInfo, String startsWith, String sortedBy) {
		log.info("End of Continents Service Implementation -> getContinentsBasedOnStartsWith() method");
		List<Continents> filteredData = allContinentsInfo.stream()
				.filter(continent -> continent.getContinentName().toLowerCase().contains(startsWith.toLowerCase()))
				.sorted(applyDynamicSorting(sortedBy))
				.collect(Collectors.toList());
		log.info("End of Continents Service Implementation -> getContinentsBasedOnStartsWith() method");
		return filteredData;
	}
	
	private Comparator<Continents> applyDynamicSorting(String sortedBy) {
		log.info("Begin of Continents Service Implementation -> applyDynamicSorting() method");
	    Map<String, Comparator<Continents>> sortingMap = new HashMap<>();

	    sortingMap.put("continentName", Comparator.comparing(Continents::getContinentName,String.CASE_INSENSITIVE_ORDER));
	    sortingMap.put("continentCode", Comparator.comparing(Continents::getContinentCode,String.CASE_INSENSITIVE_ORDER));
	    sortingMap.put("createdBy", Comparator.comparing(Continents::getCreatedBy));
	    sortingMap.put("createdOn", Comparator.comparing(Continents::getCreatedOn));

	    // Default sorting: By continentName
	    Comparator<Continents> comparator = sortingMap.getOrDefault(sortedBy, Comparator.comparing(Continents::getContinentName));

	    log.info("End of Continents Service Implementation -> applyDynamicSorting() method");
	    return comparator;
	}

}
