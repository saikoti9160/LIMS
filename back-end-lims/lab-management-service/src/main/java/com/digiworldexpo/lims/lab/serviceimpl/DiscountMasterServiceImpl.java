package com.digiworldexpo.lims.lab.serviceimpl;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.digiworldexpo.lims.entities.lab_management.DiscountMaster;
import com.digiworldexpo.lims.entities.lab_management.Lab;
import com.digiworldexpo.lims.lab.exception.RecordNotFoundException;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.repository.DiscountMasterRepository;
import com.digiworldexpo.lims.lab.repository.LabRepository;
import com.digiworldexpo.lims.lab.request.DiscountMasterRequestDTO;
import com.digiworldexpo.lims.lab.response.DiscountMasterResponseDTO;
import com.digiworldexpo.lims.lab.response.DiscountMasterSearch;
import com.digiworldexpo.lims.lab.service.DiscountMasterService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DiscountMasterServiceImpl implements DiscountMasterService {
	
	private final DiscountMasterRepository discountMasterRepository;
	private final LabRepository labRepository;
	
	public DiscountMasterServiceImpl(DiscountMasterRepository discountMasterRepository, LabRepository labRepository) {
		super();
		this.discountMasterRepository = discountMasterRepository;
		this.labRepository = labRepository;
	}

	@Override
	@Transactional
	public ResponseModel<DiscountMasterResponseDTO> saveDiscountMaster(UUID createdBy, DiscountMasterRequestDTO discountMasterDTO) {
	    log.info("Begin DiscountMasterServiceImpl -> saveDiscountMaster() method");

	    ResponseModel<DiscountMasterResponseDTO> response = new ResponseModel<>();
	    try {
	        DiscountMaster discountMaster = convertToEntity(discountMasterDTO);
	        discountMaster.setCreatedBy(createdBy);

	        if (discountMasterDTO.getLabId() != null) {
	            Lab lab = labRepository.findById(discountMasterDTO.getLabId())
	                    .orElseThrow(() -> new IllegalArgumentException("Invalid Lab ID"));
	            discountMaster.setLab(lab);
	        } 

	        DiscountMaster savedDiscountMaster = discountMasterRepository.saveAndFlush(discountMaster);

	        DiscountMasterResponseDTO savedDiscountMasterResponseDTO = convertToResponseDTO(savedDiscountMaster);
	        savedDiscountMasterResponseDTO.setLabId(savedDiscountMaster.getLab().getId());

	        response.setStatusCode(HttpStatus.OK.toString());
	        response.setMessage("DiscountMaster saved successfully.");
	        response.setData(savedDiscountMasterResponseDTO);
	        log.info("DiscountMaster saved with ID {}", savedDiscountMaster.getId());
	    } catch (Exception e) {
	        log.error("Error while saving DiscountMaster: {}", e.getMessage());
	        response.setStatusCode(HttpStatus.BAD_REQUEST.toString());
	        response.setMessage("Failed to save DiscountMaster: An unexpected error occurred.");
	        response.setData(null);
	    }

	    log.info("End DiscountMasterServiceImpl -> saveDiscountMaster() method");
	    return response;
	}

	private DiscountMaster convertToEntity(DiscountMasterRequestDTO dto) {
		DiscountMaster discountMaster = new DiscountMaster();
		BeanUtils.copyProperties(dto, discountMaster);
		return discountMaster;
	}

	private DiscountMasterResponseDTO convertToResponseDTO(DiscountMaster entity) {
		DiscountMasterResponseDTO dto = new DiscountMasterResponseDTO();
		BeanUtils.copyProperties(entity, dto);
		return dto;
	}

	@Override
	public ResponseModel<List<DiscountMasterSearch>> getAllDiscounts(UUID createdBy, String keyword, Boolean flag,
	        Integer pageNumber, Integer pageSize) {
	    log.info("Begin DiscountMasterServiceImpl -> getAllDiscounts() method");

	    ResponseModel<List<DiscountMasterSearch>> response = new ResponseModel<>();

	    try {
	        List<DiscountMaster> discountList;

	        if (keyword != null && !keyword.trim().isEmpty()) {
	            discountList = discountMasterRepository.findByCreatedByAndActiveAndDiscountName(
	                    createdBy, flag, keyword);
	        } else {
	            discountList = discountMasterRepository.findAllByCreatedByAndActive(
	                    createdBy, true);
	        }

	        List<DiscountMasterSearch> dtoList = discountList.stream()
	                .map(discount -> new DiscountMasterSearch(
	                        discount.getId(),
	                        discount.getDiscountName() != null ? discount.getDiscountName() : "N/A",
	                        discount.getDiscountType() != null ? discount.getDiscountType() : "N/A"))
	                .collect(Collectors.toList());

	        int start = pageNumber * pageSize;
	        int end = Math.min(start + pageSize, dtoList.size());
	        List<DiscountMasterSearch> paginatedList = dtoList.subList(start, end);

	        response.setData(paginatedList);
	        response.setTotalCount(discountList.size());
	        response.setPageNumber(pageNumber);
	        response.setPageSize(pageSize);
	        response.setStatusCode(HttpStatus.OK.toString());
	        response.setMessage("Discounts fetched successfully.");

	        log.info("Successfully fetched {} discounts.", paginatedList.size());
	    } catch (IllegalArgumentException e) {
	        log.error("Invalid pagination parameters: {}", e.getMessage());
	        response.setData(null);
	        response.setMessage(e.getMessage());
	        response.setStatusCode(HttpStatus.BAD_REQUEST.toString());
	    } catch (Exception e) {
	        log.error("Error occurred while fetching discounts: {}", e.getMessage());
	        response.setData(null);
	        response.setMessage("Failed to fetch discounts.");
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	    }

	    log.info("End DiscountMasterServiceImpl -> getAllDiscounts() method");
	    return response;
	}
	
	@Override
	public ResponseModel<DiscountMasterResponseDTO> getDiscountById(UUID id) {
		log.info("Begin DiscountServiceImpl -> getDiscountById() method");

		ResponseModel<DiscountMasterResponseDTO> response = new ResponseModel<>();
		try {
			DiscountMaster discount = discountMasterRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("Discount not found with ID: " + id));

			DiscountMasterResponseDTO discountMasterResponseDTO = convertToResponseDTO(discount);

			response.setStatusCode(HttpStatus.OK.toString());
			response.setMessage("Discount fetched successfully.");
			response.setData(discountMasterResponseDTO);

		} catch (RecordNotFoundException e) {
			log.error("Record not found: {}", e.getMessage());
			response.setStatusCode(HttpStatus.NOT_FOUND.toString());
			response.setMessage(e.getMessage());
			response.setData(null);

		} catch (Exception e) {
			log.error("Error while fetching discount: {}", e.getMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			response.setMessage("Failed to fetch discount: " + e.getMessage());
			response.setData(null);
		}

		log.info("End DiscountServiceImpl -> getDiscountById() method");
		return response;
	}

	@Override
	public ResponseModel<DiscountMasterResponseDTO> updateDiscount(UUID id, DiscountMasterRequestDTO discountMasterRequestDto) {
	    log.info("Begin DiscountServiceImpl -> updateDiscount() method");

	    ResponseModel<DiscountMasterResponseDTO> response = new ResponseModel<>();
	    try {
	        DiscountMaster existingDiscount = discountMasterRepository.findById(id)
	                .orElseThrow(() -> new RecordNotFoundException("Discount not found with ID: " + id));

	        DiscountMaster updatedDiscountEntity = convertToEntity(discountMasterRequestDto);

	        updatedDiscountEntity.setId(existingDiscount.getId());
	        updatedDiscountEntity.setCreatedBy(existingDiscount.getCreatedBy());
	        updatedDiscountEntity.setCreatedOn(existingDiscount.getCreatedOn());
	        updatedDiscountEntity.setModifiedOn(existingDiscount.getModifiedOn());
	        updatedDiscountEntity.setModifiedBy(existingDiscount.getModifiedBy());

	        if (discountMasterRequestDto.getLabId() != null) {
	            Lab lab = labRepository.findById(discountMasterRequestDto.getLabId())
	                    .orElseThrow(() -> new RecordNotFoundException("Lab not found with ID: " + discountMasterRequestDto.getLabId()));
	            updatedDiscountEntity.setLab(lab);
	        } else {
	            updatedDiscountEntity.setLab(existingDiscount.getLab());
	        }
	        updatedDiscountEntity.setCreatedBy(existingDiscount.getCreatedBy());
	        updatedDiscountEntity.setModifiedOn(new Timestamp(System.currentTimeMillis()));
	        DiscountMaster updatedDiscount = discountMasterRepository.save(updatedDiscountEntity);

	        DiscountMasterResponseDTO discountMasterResponseDTO = convertToResponseDTO(updatedDiscount);

	        response.setStatusCode(HttpStatus.OK.toString());
	        response.setMessage("Discount updated successfully.");
	        response.setData(discountMasterResponseDTO);
	    } catch (RecordNotFoundException e) {
	        log.error("Error: {}", e.getMessage());
	        response.setStatusCode(HttpStatus.NOT_FOUND.toString());
	        response.setMessage(e.getMessage());
	        response.setData(null);
	    } catch (Exception e) {
	        log.error("Error while updating discount: {}", e.getMessage());
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	        response.setMessage("Failed to update discount: " + e.getMessage());
	        response.setData(null);
	    }

	    log.info("End DiscountServiceImpl -> updateDiscount() method");
	    return response;
	}




	@Override
	public ResponseModel<DiscountMasterResponseDTO> deleteDiscount(UUID id) {
	    log.info("Begin DiscountServiceImpl -> deleteDiscount() method");

	    ResponseModel<DiscountMasterResponseDTO> response = new ResponseModel<>();
	    try {
	        DiscountMaster discountMaster = discountMasterRepository.findById(id)
	                .orElseThrow(() -> new RecordNotFoundException("Discount not found with ID: " + id));

	        discountMasterRepository.deleteById(id);

	        DiscountMasterResponseDTO discountMasterResponseDTO = convertToResponseDTO(discountMaster);

	        response.setStatusCode(HttpStatus.NO_CONTENT.toString());
	        response.setMessage("Discount deleted successfully with ID: " + id);
	        response.setData(discountMasterResponseDTO);
	        log.info("Successfully deleted discount with ID: {}", id);

	    } catch (RecordNotFoundException e) {
	        log.error("Discount deletion failed: {}", e.getMessage());
	        response.setStatusCode(HttpStatus.NOT_FOUND.toString());
	        response.setMessage(e.getMessage());
	        response.setData(null);
	    } catch (Exception e) {
	        log.error("Error while deleting discount with ID {}: {}", id, e.getMessage());
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	        response.setMessage("Failed to delete discount: " + e.getMessage());
	        response.setData(null);
	    }

	    log.info("End DiscountServiceImpl -> deleteDiscount() method");
	    return response;
	}



}