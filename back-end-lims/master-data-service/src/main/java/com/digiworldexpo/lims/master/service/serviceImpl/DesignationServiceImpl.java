package com.digiworldexpo.lims.master.service.serviceImpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.digiworldexpo.lims.entities.master.Designation;
import com.digiworldexpo.lims.master.exception.BadRequestException;
import com.digiworldexpo.lims.master.exception.DuplicateRecordFoundException;
import com.digiworldexpo.lims.master.exception.RecordNotFoundException;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.repository.DesignationRepository;
import com.digiworldexpo.lims.master.service.DesignationService;
import com.digiworldexpo.lims.master.util.MasterDataLoader;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DesignationServiceImpl implements DesignationService {
	
	private final DesignationRepository designationRepository;
	private final MasterDataLoader masterDataLoader;
	
	public DesignationServiceImpl(DesignationRepository designationRepository, MasterDataLoader masterDataLoader) {
		this.designationRepository = designationRepository;
		this.masterDataLoader = masterDataLoader;
	}

	@Override
	public ResponseModel<Designation> addDesignation(Designation designation, UUID createdBy) {
		log.info("Begin of Designation Implementation Implementation -> addDesignation() method");
		ResponseModel<Designation> responseModel = new ResponseModel<Designation>();

		try {
			if (designation.getDesignationName() == null || designation.getDesignationName().isEmpty()) {
				throw new BadRequestException("Please provide the designation name field.");
			}
			
			if(designation.getDesignationName().trim().isEmpty() || designation.getDesignationName().charAt(0)==' ') {
				throw new IllegalArgumentException("Designation name must not contain a space as first letter");
			}
			
			Optional<Designation> optionalDesignation = designationRepository.findByDesignationNameAndCreatedBy(createdBy, designation.getDesignationName());
			if(optionalDesignation.isPresent()) {
				throw new DuplicateRecordFoundException("Designation mode data for this "+designation.getDesignationName()+" is already exists");
			}

			designation.setCreatedBy(createdBy);
			masterDataLoader.addDesignation(designation);
			log.info("New designation has added in the cache");
			
			designationRepository.save(designation);
			responseModel.setStatusCode(HttpStatus.CREATED.toString());
			responseModel.setMessage("Designation name has been added successfully");
			responseModel.setData(designation);
		} catch(IllegalArgumentException illegalArgumentException) {
			log.info("Error occured due to invaid argument {}", illegalArgumentException.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Error occured due to invaid argument "+ illegalArgumentException.getMessage());
			responseModel.setData(null);
		} catch (BadRequestException badRequestException) {
			log.info("Validation failed for designation name input: {}", badRequestException.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Validation failed for designation name input: "+ badRequestException.getMessage());
			responseModel.setData(null);
		} catch (DuplicateRecordFoundException duplicateRecordFoundException) {
			log.info("Duplicate Record found : {}", duplicateRecordFoundException.getMessage());
			responseModel.setStatusCode(HttpStatus.CONFLICT.toString());
			responseModel.setMessage("Duplicate Record found : "+ duplicateRecordFoundException.getMessage());
			responseModel.setData(null);
		} catch (Exception exception) {
			log.info("Error in addDesignation(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in addDesignation(): "+ exception.getMessage());
			responseModel.setData(null);
		}

		log.info("End of Designation Implementation Implementation -> addDesignation() method");
		return responseModel;
	}

	@Override
	public ResponseModel<List<Designation>> getAllDesignations(String startsWith, UUID createdBy, int pageNumber,
			int pageSize, String sortedBy) {
		log.info("Begin of Designation Service Implementation -> getAllDesignations() method");
		ResponseModel<List<Designation>> responseModel = new ResponseModel<>();

		try {
			List<Designation> allDesignationsInfo = masterDataLoader.getDesignations();
			List<Designation> filteredDesignationData = new ArrayList<>();
			if (startsWith != null && !startsWith.isEmpty()) {
				filteredDesignationData = getDesignationBasedOnStartsWith(allDesignationsInfo, startsWith, sortedBy);
			} else {
				filteredDesignationData = allDesignationsInfo.stream().filter(designation -> designation.isActive())
						.sorted(dynamicSorting(sortedBy)).collect(Collectors.toList());
			}

			// Apply pagination
			Pageable pageable = PageRequest.of(pageNumber, pageSize);
			int start = (int) pageable.getOffset();
			int end = Math.min(start + pageable.getPageSize(), filteredDesignationData.size());
			if (start > filteredDesignationData.size()) {
				throw new IllegalArgumentException("Page number exceeds available data.");
			}
			List<Designation> paginatedList = filteredDesignationData.subList(start, end);

			// Set response model data
			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Designation names data retrieved successfully.");
			responseModel.setData(paginatedList);
			responseModel.setTotalCount(filteredDesignationData.size());
			responseModel.setPageNumber(pageNumber);
			responseModel.setPageSize(pageSize);
			responseModel.setSortedBy("Designation name ascending order");

		} catch (IllegalArgumentException illegalArgumentException) {
			log.info("Invalid input parameters: {}", illegalArgumentException.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Error occured due to invaid argument "+ illegalArgumentException.getMessage());
			responseModel.setData(null);
		} catch (Exception exception) {
			log.info("Error in getAllDesignations(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in getAllDesignations(): "+ exception.getMessage());
			responseModel.setData(null);
		}
		log.info("End of Designation Service Implementation -> getAllDesignations() method");
		return responseModel;
	}

	@Override
	public ResponseModel<Designation> updateDesignationById(UUID id, Designation newDesignationData, UUID modifiedBy) {
		log.info("End of Designation Service Implementation -> updateDesignationById() method");
		ResponseModel<Designation> responseModel = new ResponseModel<>();

		try {
			if (newDesignationData.getDesignationName() == null || newDesignationData.getDesignationName().isEmpty()) {
				throw new BadRequestException("Please provide the Designation name");
			}

			Optional<Designation> optionalDesignation = designationRepository.findById(id);
			if (optionalDesignation.isEmpty()) {
				throw new RecordNotFoundException("No designation data found for Id: " + id);
			}

			Designation updatingExistedDesignation = optionalDesignation.get();
			Optional<Designation> optionalDesignationUsingName = designationRepository.findByDesignationName(newDesignationData.getDesignationName());
			if(optionalDesignationUsingName.isPresent() && !optionalDesignationUsingName.get().getId().equals(updatingExistedDesignation.getId())) {
				throw new DuplicateRecordFoundException("Designation data for this "+optionalDesignationUsingName.get().getDesignationName()+" is already exists");
			}
			
			updatingExistedDesignation.setDesignationName(newDesignationData.getDesignationName());
			updatingExistedDesignation.setActive(true);
			updatingExistedDesignation.setModifiedBy(modifiedBy);
			updatingExistedDesignation.setModifiedOn(new Timestamp(System.currentTimeMillis()));
			
			if (masterDataLoader != null) {
				masterDataLoader.updateDesignation(updatingExistedDesignation);
				log.info("Designation updated in the cache");
			} else {
				log.error("masterDataLoader is null");
				throw new Exception("Failed to update designation in the cache due to internal error.");
			}

			designationRepository.save(updatingExistedDesignation);

			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Designation has been updated successfully for this id: " + id);
			responseModel.setData(updatingExistedDesignation);

		} catch (BadRequestException badRequestException) {
			log.info("Validation failed for designation name input: {}", badRequestException.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Validation failed for designation name input: "+ badRequestException.getMessage());
			responseModel.setData(null);
		} catch (DuplicateRecordFoundException duplicateRecordFoundException) {
			log.info("Duplicate Record found : {}", duplicateRecordFoundException.getMessage());
			responseModel.setStatusCode(HttpStatus.CONFLICT.toString());
			responseModel.setMessage("Duplicate Record found : "+ duplicateRecordFoundException.getMessage());
			responseModel.setData(null);
		} catch (RecordNotFoundException recordNotFoundException) {
			log.info("No record found for the given Id: {}", recordNotFoundException.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
			responseModel.setMessage("No record found for the given Id: "+ recordNotFoundException.getMessage());
			responseModel.setData(null);
		} catch (Exception exception) {
			// Handle general errors
			log.info("Error in updateDesignationById(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in updateDesignationById(): "+ exception.getMessage());
			responseModel.setData(null);
		}

		log.info("End of Designation Service Implementation -> updateDesignationById() method");
		return responseModel;
	}

	@Override
	public ResponseModel<Designation> getDesignationById(UUID id) {
		log.info("Begin of Designation Service Implementation -> getDesignationById() method");
		ResponseModel<Designation> responseModel = new ResponseModel<>();

		try {
			Optional<Designation> optionalDesignation = designationRepository.findById(id);
			if (optionalDesignation.isEmpty()) {
				throw new RecordNotFoundException("No Designation data found for Id: " + id);
			}

			Designation designation = optionalDesignation.get();

			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Designation data has been fetched successfully for this id: " + id);
			responseModel.setData(designation);

		} catch (RecordNotFoundException recordNotFoundException) {
			log.info("No record found for the given Id: {}", recordNotFoundException.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
			responseModel.setMessage("No record found for the given Id: "+ recordNotFoundException.getMessage());
			responseModel.setData(null);

		} catch (Exception exception) {
			log.info("Error in getDesignationById(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in getDesignationById(): "+ exception.getMessage());
			responseModel.setData(null);
		}

		log.info("End of Designation Service Implementation -> getDesignationById() method");
		return responseModel;
	}

	@Override
	public ResponseModel<Designation> deleteDesignationById(UUID id) {
		log.info("Begin of Designation Service Implementation -> deleteDesignationById() method");
		ResponseModel<Designation> responseModel = new ResponseModel<>();

		try {
			Optional<Designation> optionalDesignation = designationRepository.findById(id);
			if (optionalDesignation.isEmpty()) {
				throw new RecordNotFoundException("No Designation data found for Id: " + id);
			}

			Designation designation = optionalDesignation.get();
			
			masterDataLoader.deleteDesignation(designation);
			log.info("Designation has been deleted in the cache");
			
			designationRepository.delete(designation);

			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Designation data has been deleted successfully for this id: " + id);
			responseModel.setData(designation);

		} catch (RecordNotFoundException recordNotFoundException) {
			log.info("No record found for the given Id: {}", recordNotFoundException.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
			responseModel.setMessage("No record found for the given Id: "+ recordNotFoundException.getMessage());
			responseModel.setData(null);

		} catch (Exception exception) {
			log.info("Error in deleteDesignationById(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in deleteDesignationById(): "+ exception.getMessage());
			responseModel.setData(null);
		}

		log.info("End of Designation Service Implementation -> deleteDesignationById() method");
		return responseModel;
	}
	
	private List<Designation> getDesignationBasedOnStartsWith(List<Designation> allDesignationsInfo, String startsWith, String sortedBy){
		log.info("Begin of Designation Service Implementation -> getDesignationBasedOnStartsWith() method");
		
		List<Designation> filteredData = allDesignationsInfo.stream()
				.filter(designation -> designation.getDesignationName().toLowerCase().contains(startsWith.toLowerCase()) &&
						designation.isActive())
				.sorted(dynamicSorting(sortedBy)).collect(Collectors.toList());
		
		log.info("End of Designation Service Implementation -> getDesignationBasedOnStartsWith() method");
		return filteredData;
	}

	
	private Comparator<Designation> dynamicSorting(String sortedBy){
		log.info("Begin of Designation Service Implementation -> dynamicSorting() method");
		
		Map<String, Comparator<Designation>> sortMapping = new HashMap<String, Comparator<Designation>>();
		
		sortMapping.put("designationName", Comparator.comparing(Designation::getDesignationName));
		sortMapping.put("createdBy", Comparator.comparing(Designation::getCreatedBy));
		sortMapping.put("createdOn", Comparator.comparing(Designation::getCreatedOn));
		
		Comparator<Designation> comparator = sortMapping.getOrDefault(sortedBy, Comparator.comparing(Designation::getDesignationName));
		log.info("End of Designation Service Implementation -> dynamicSorting() method");
		return comparator;
	}

}
