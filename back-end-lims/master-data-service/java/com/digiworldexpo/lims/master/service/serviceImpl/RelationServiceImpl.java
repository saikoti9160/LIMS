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

import com.digiworldexpo.lims.entities.master.Relation;
import com.digiworldexpo.lims.master.exception.BadRequestException;
import com.digiworldexpo.lims.master.exception.DuplicateRecordFoundException;
import com.digiworldexpo.lims.master.exception.RecordNotFoundException;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.repository.RelationRepository;
import com.digiworldexpo.lims.master.service.RelationService;
import com.digiworldexpo.lims.master.util.MasterDataLoader;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RelationServiceImpl implements RelationService {

	private final RelationRepository relationRepository;
	private final MasterDataLoader masterDataLoader;

	public RelationServiceImpl(RelationRepository relationRepository, MasterDataLoader masterDataLoader) {
		this.relationRepository = relationRepository;
		this.masterDataLoader = masterDataLoader;
	}

	@Override
	public ResponseModel<Relation> addRelation(Relation relation, UUID createdBy) {
		log.info("Begin of Relation Service Implementation -> addRelation() method");
		ResponseModel<Relation> responseModel = new ResponseModel<Relation>();

		try {

			if (relation.getRelationName() == null || relation.getRelationName().isEmpty()) {
				throw new BadRequestException("Please provide the Relation name field.");
			}
			
			if(relation.getRelationName().trim().isEmpty() || relation.getRelationName().charAt(0)==' ') {
				throw new IllegalArgumentException("Relation name must not contain a space as first letter");
			}
			
			Optional<Relation> optionalRelation = relationRepository.findByRelationName(relation.getRelationName());
			if(optionalRelation.isPresent()) {
				throw new DuplicateRecordFoundException("Relation Name data for this "+relation.getRelationName()+" is already exists");
			}
			
			relation.setCreatedBy(createdBy);
			masterDataLoader.addRelation(relation);
			log.info("New relation data has been added in the cache");
			
			relationRepository.save(relation);

			responseModel.setStatusCode(HttpStatus.CREATED.toString());
			responseModel.setMessage("Relation name has been added successfully");
			responseModel.setData(relation);
		} catch(IllegalArgumentException illegalArgumentException) {
			log.info("Error occured due to invaid argument {}", illegalArgumentException.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Error occured due to invaid argument "+ illegalArgumentException.getMessage());
			responseModel.setData(null);
		} catch (BadRequestException badRequestException) {
			log.info("Validation failed for relation name input: {}", badRequestException.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Validation failed for relation name input: "+ badRequestException.getMessage());
			responseModel.setData(null);
		} catch (DuplicateRecordFoundException duplicateRecordFoundException) {
			log.info("Duplicate Record found : {}", duplicateRecordFoundException.getMessage());
			responseModel.setStatusCode(HttpStatus.CONFLICT.toString());
			responseModel.setMessage("Duplicate Record found : "+ duplicateRecordFoundException.getMessage());
			responseModel.setData(null);
		} catch (Exception exception) {
			log.info("Error in addRelation(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in addRelation(): "+ exception.getMessage());
			responseModel.setData(null);
		}
		log.info("End of Relation Service Implementation -> addRelation() method");
		return responseModel;
	}

	@Override
	public ResponseModel<List<Relation>> getAllRelations(String startsWith, int pageNumber, int pageSize,
			String sortedBy) {
		log.info("Begin of Relation Service Implementation -> getAllRelations() method");
	    ResponseModel<List<Relation>> responseModel = new ResponseModel<>();
	    
	    try {
	    	  
	    	  List<Relation> allRelationsInfo = masterDataLoader.getRelations();
	    	  List<Relation> filteredRelationsData = new ArrayList<Relation>();
	        if(startsWith != null && !startsWith.isEmpty()) {
	        	filteredRelationsData = getAllRelationNamesBasedOnStartsWith(allRelationsInfo, startsWith, sortedBy);
	        } else {
	        	filteredRelationsData = allRelationsInfo.stream().filter(relation -> relation.isActive())
	        			.sorted(applyDynamicSorting(sortedBy)).collect(Collectors.toList());
	        }
	        
	        // Apply pagination
	        Pageable pageable = PageRequest.of(pageNumber, pageSize);
	        int start = (int) pageable.getOffset();
	        int end = Math.min(start + pageable.getPageSize(), filteredRelationsData.size());
	        if (start > filteredRelationsData.size()) {
	            throw new IllegalArgumentException("Page number exceeds available data.");
	        }
	        List<Relation> paginatedList = filteredRelationsData.subList(start, end);

	        // Set response model data
	        responseModel.setStatusCode(HttpStatus.OK.toString());
	        responseModel.setMessage("Relation names data retrieved successfully.");
	        responseModel.setData(paginatedList);
	        responseModel.setTotalCount(filteredRelationsData.size());
	        responseModel.setPageNumber(pageNumber);
	        responseModel.setPageSize(pageSize);
	        responseModel.setSortedBy("Relation name ascending order");

	    } catch (IllegalArgumentException illegalArgumentException) {
			log.info("Invalid input parameters: {}", illegalArgumentException.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Error occured due to invaid argument "+ illegalArgumentException.getMessage());
			responseModel.setData(null);
		} catch (Exception exception) {
			log.info("Error in getAllRelations(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in getAllRelations(): "+ exception.getMessage());
			responseModel.setData(null);
		}
	    log.info("End of Relation Service Implementation -> getAllRelations() method");
	    return responseModel;
	}

	@Override
	public ResponseModel<Relation> updateRelationById(UUID id, Relation requestRelation, UUID modifiedBy) {
		log.info("End of Relation Service Implementation -> updateRelationById() method");
		ResponseModel<Relation> responseModel = new ResponseModel<>();

		try {
			
			if (requestRelation.getRelationName() == null || requestRelation.getRelationName().isEmpty()) {
				throw new BadRequestException("Please provide the Relation name");
			}

			Optional<Relation> optionalRelation = relationRepository.findById(id);
			if (optionalRelation.isEmpty()) {
				throw new RecordNotFoundException("No Relation data found for Id: " + id);
			}
			
			Optional<Relation> optionalRelationByName = relationRepository.findByRelationName(requestRelation.getRelationName());
			if(optionalRelationByName.isPresent() && !optionalRelationByName.get().getId().equals(optionalRelation.get().getId())) {
				throw new DuplicateRecordFoundException("Relation Name data for this "+requestRelation.getRelationName()+" is already exists");
			}

			Relation existedRelation = optionalRelation.get();

			existedRelation.setRelationName(requestRelation.getRelationName());
			existedRelation.setActive(true);
			existedRelation.setModifiedBy(modifiedBy);
			existedRelation.setModifiedOn(new Timestamp(System.currentTimeMillis()));
			
			if (masterDataLoader != null) {
				masterDataLoader.updateRelation(existedRelation);
				log.info("Relation updated in the cache");
			} else {
				log.error("masterDataLoader is null");
				throw new Exception("Failed to update relation in the cache due to internal error.");
			}

			relationRepository.save(existedRelation);

			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Relation has been updated successfully for this id: " + id);
			responseModel.setData(existedRelation);

		} catch (BadRequestException badRequestException) {
			log.info("Validation failed for relation name input: {}", badRequestException.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Validation failed for relation name input: "+ badRequestException.getMessage());
			responseModel.setData(null);
		} catch (DuplicateRecordFoundException duplicateRecordFoundException) {
			log.info("Duplicate Record found : {}", duplicateRecordFoundException.getMessage());
			responseModel.setStatusCode(HttpStatus.CONFLICT.toString());
			responseModel.setMessage("Duplicate Record found : "+ duplicateRecordFoundException.getMessage());
			responseModel.setData(null);
		}catch (RecordNotFoundException recordNotFoundException) {
			log.info("No record found for the given Id: {}", recordNotFoundException.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
			responseModel.setMessage("No record found for the given Id: "+ recordNotFoundException.getMessage());
			responseModel.setData(null);
		} catch (Exception exception) {
			log.info("Error in updateRelationById(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in updateRelationById(): "+ exception.getMessage());
			responseModel.setData(null);
		}

		log.info("End of Relation Service Implementation -> updateRelationById() method");
		return responseModel;
	}

	@Override
	public ResponseModel<Relation> getRelationById(UUID id) {
		log.info("Begin of Relation Service Implementation -> getRelationById() method");
		ResponseModel<Relation> responseModel = new ResponseModel<>();

		try {
			Optional<Relation> optionalRelation = relationRepository.findById(id);
			if (optionalRelation.isEmpty()) {
				throw new RecordNotFoundException("No Relation data found for Id: " + id);
			}

			Relation Relation = optionalRelation.get();

			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Relation data has been fetched successfully for this id: " + id);
			responseModel.setData(Relation);

		} catch (RecordNotFoundException recordNotFoundException) {
			log.info("No record found for the given Id: {}", recordNotFoundException.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
			responseModel.setMessage("No record found for the given Id: "+ recordNotFoundException.getMessage());
			responseModel.setData(null);

		} catch (Exception exception) {
			log.info("Error in getRelationById(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in getRelationById(): "+ exception.getMessage());
			responseModel.setData(null);
		}

		log.info("End of Relation Service Implementation -> getRelationById() method");
		return responseModel;
	}

	@Override
	public ResponseModel<Relation> deleteRelationById(UUID id) {
		log.info("Begin of Relation Service Implementation -> deleteRelationById() method");
		ResponseModel<Relation> responseModel = new ResponseModel<>();

		try {
			Relation relation = relationRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("No Relation data found for Id: " + id));
			
			masterDataLoader.deleteRelation(relation);

			relationRepository.delete(relation);

			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Relation data has been deleted successfully for this id: " + id);
			responseModel.setData(relation);

		} catch (RecordNotFoundException recordNotFoundException) {
			log.info("No record found for the given Id: {}", recordNotFoundException.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
			responseModel.setMessage("No record found for the given Id: "+ recordNotFoundException.getMessage());
			responseModel.setData(null);

		} catch (Exception exception) {
			log.info("Error in deleteRelationById(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in deleteRelationById(): "+ exception.getMessage());
			responseModel.setData(null);
		}

		log.info("End of Relation Service Implementation -> deleteRelationById() method");
		return responseModel;
	}
	
	private List<Relation> getAllRelationNamesBasedOnStartsWith(List<Relation> allRelationsInfo, String startsWith, String sortedBy) {
		log.info("Begin of Relation Service Implementation -> getAllRelationNamesBasedOnStartsWith() method");
		List<Relation> filteredData = allRelationsInfo.stream().filter(relation -> relation.getRelationName().toLowerCase().contains(startsWith.toLowerCase()) && relation.isActive())
				.sorted(applyDynamicSorting(sortedBy)).collect(Collectors.toList());
		log.info("End of Relation Service Implementation -> getAllRelationNamesBasedOnStartsWith() method");
		return filteredData;
	}
	private Comparator<Relation> applyDynamicSorting(String sortedBy){
		log.info("Begin of Relation Service Implementation -> applyDynamicSorting() method");
		
		Map<String, Comparator<Relation>> sortMapping = new HashMap<String, Comparator<Relation>>();
		
		sortMapping.put("relationName", Comparator.comparing(Relation::getRelationName));
		sortMapping.put("createdBy", Comparator.comparing(Relation::getCreatedBy));
		sortMapping.put("createdOn", Comparator.comparing(Relation::getCreatedOn));
		
		Comparator<Relation> comparator = sortMapping.getOrDefault(sortedBy, Comparator.comparing(Relation::getRelationName));
		log.info("End of Relation Service Implementation -> applyDynamicSorting() method");
		return comparator;
	}
}
