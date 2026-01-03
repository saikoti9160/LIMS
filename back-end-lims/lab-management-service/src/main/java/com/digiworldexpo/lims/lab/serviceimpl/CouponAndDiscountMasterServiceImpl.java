package com.digiworldexpo.lims.lab.serviceimpl;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.digiworldexpo.lims.entities.lab_management.CouponAndDiscountMaster;
import com.digiworldexpo.lims.entities.lab_management.DiscountMaster;
import com.digiworldexpo.lims.entities.lab_management.Lab;
import com.digiworldexpo.lims.lab.exception.RecordNotFoundException;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.repository.CouponAndDiscountMasterRepository;
import com.digiworldexpo.lims.lab.repository.DiscountMasterRepository;
import com.digiworldexpo.lims.lab.repository.LabRepository;
import com.digiworldexpo.lims.lab.request.CouponAndDiscountMasterRequestDTO;
import com.digiworldexpo.lims.lab.response.CouponAndDiscountMasterResponseDTO;
import com.digiworldexpo.lims.lab.response.CouponAndDiscountMasterSearch;
import com.digiworldexpo.lims.lab.service.CouponAndDiscountMasterService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CouponAndDiscountMasterServiceImpl implements CouponAndDiscountMasterService {
	
	private final CouponAndDiscountMasterRepository couponAndDiscountMasterRepository;
	private final LabRepository labRepository;
	private final DiscountMasterRepository discountMasterRepository;
	
	public CouponAndDiscountMasterServiceImpl(CouponAndDiscountMasterRepository couponAndDiscountMasterRepository,
			LabRepository labRepository, DiscountMasterRepository discountMasterRepository) {
		super();
		this.couponAndDiscountMasterRepository = couponAndDiscountMasterRepository;
		this.labRepository = labRepository;
		this.discountMasterRepository = discountMasterRepository;
	}

	@Override
	public ResponseModel<CouponAndDiscountMasterResponseDTO> save(UUID createdBy,
	        CouponAndDiscountMasterRequestDTO couponAndDiscountMasterRequestDTO) {
	    log.info("Begin CouponAndDiscountMasterServiceImpl -> save() method");

	    ResponseModel<CouponAndDiscountMasterResponseDTO> response = new ResponseModel<>();

	    try {
	        String sequenceId = "CDM" + couponAndDiscountMasterRepository.getNextFormattedSequenceId();
	        log.info("Generated Coupon and Discount Master Sequence ID: {}", sequenceId);

	        CouponAndDiscountMaster couponAndDiscountMaster = convertToEntity(couponAndDiscountMasterRequestDTO);
	        couponAndDiscountMaster.setCouponSequenceId(sequenceId);
	        couponAndDiscountMaster.setCreatedBy(createdBy);

	        DiscountMaster discountMaster = discountMasterRepository.findById(couponAndDiscountMasterRequestDTO.getDiscountId())
	                .orElseThrow(() -> new IllegalArgumentException("Invalid Discount ID"));
	        Lab lab = labRepository.findById(couponAndDiscountMasterRequestDTO.getLabId())
	                .orElseThrow(() -> new IllegalArgumentException("Invalid Lab ID"));

	        couponAndDiscountMaster.setDiscountMaster(discountMaster);
	        couponAndDiscountMaster.setLab(lab);

	        if (couponAndDiscountMasterRequestDTO.getVisitFrequency() != null && !couponAndDiscountMasterRequestDTO.getVisitFrequency().isEmpty()) {
	            List<String> visitFrequencyList = couponAndDiscountMasterRequestDTO.getVisitFrequency();

	            if (visitFrequencyList.contains("Specific Number")) {
	                if (couponAndDiscountMasterRequestDTO.getSpecificNumber() == null) {
	                    throw new IllegalArgumentException("Specific Number must be provided when selecting 'Specific Number' in visit frequency.");
	                }
	            }

	            if (visitFrequencyList.contains("Others")) {
	                if (couponAndDiscountMasterRequestDTO.getOthers() == null || couponAndDiscountMasterRequestDTO.getOthers().trim().isEmpty()) {
	                    throw new IllegalArgumentException("Details must be provided when selecting 'Others' in visit frequency.");
	                }
	            }

	            couponAndDiscountMaster.setVisitFrequency(visitFrequencyList);
	        }

	        CouponAndDiscountMaster savedCouponAndDiscountMaster = couponAndDiscountMasterRepository.save(couponAndDiscountMaster);
	        CouponAndDiscountMasterResponseDTO savedDTO = convertToResponseDTO(savedCouponAndDiscountMaster);

	        response.setStatusCode(HttpStatus.OK.toString());
	        response.setMessage("Coupon and Discount saved successfully");
	        response.setData(savedDTO);

	        log.info("Coupon and Discount saved with ID: {}", savedCouponAndDiscountMaster.getId());

	    } catch (IllegalArgumentException e) {
	        log.error("Error occurred while saving coupon and discount: {}", e.getMessage());
	        response.setStatusCode(HttpStatus.BAD_REQUEST.toString());
	        response.setMessage("Invalid input: " + e.getMessage());
	        response.setData(null);
	    } catch (Exception e) {
	        log.error("Error occurred while saving coupon and discount: {}", e.getMessage());
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	        response.setMessage("Failed to save coupon and discount");
	        response.setData(null);
	    }

	    log.info("End CouponAndDiscountMasterServiceImpl -> save() method");
	    return response;
	}





	private CouponAndDiscountMaster convertToEntity(CouponAndDiscountMasterRequestDTO dto) {
	    CouponAndDiscountMaster entity = new CouponAndDiscountMaster();
	    BeanUtils.copyProperties(dto, entity);

	    if (dto.getDiscountId() != null) {
	        DiscountMaster discountMaster = discountMasterRepository.findById(dto.getDiscountId())
	                .orElseThrow(() -> new RecordNotFoundException("DiscountMaster not found with ID: " + dto.getDiscountId()));
	        entity.setDiscountMaster(discountMaster);
	    }

	    if (dto.getLabId() != null) {
	        Lab lab = labRepository.findById(dto.getLabId())
	                .orElseThrow(() -> new RecordNotFoundException("Lab not found with ID: " + dto.getLabId()));
	        entity.setLab(lab);
	    }

	    return entity;
	}


	private CouponAndDiscountMasterResponseDTO convertToResponseDTO(CouponAndDiscountMaster couponAndDiscountMaster) {
		CouponAndDiscountMasterResponseDTO dto = new CouponAndDiscountMasterResponseDTO();
		
	    if (couponAndDiscountMaster.getDiscountMaster() != null) {
	        dto.setDiscountId(couponAndDiscountMaster.getDiscountMaster().getId());
	    }
	    
	    if (couponAndDiscountMaster.getLab() != null) {
	        dto.setLabId(couponAndDiscountMaster.getLab().getId());
	    }
		BeanUtils.copyProperties(couponAndDiscountMaster, dto);
		return dto;
	}

	@Override
	public ResponseModel<List<CouponAndDiscountMasterSearch>> getAllCouponAndDiscount(
	        String searchTerm, Boolean flag, UUID createdBy, Integer pageNumber, Integer pageSize) {

	    log.info("Begin CouponAndDiscountMasterServiceImpl -> getAllCouponAndDiscount()");
	    ResponseModel<List<CouponAndDiscountMasterSearch>> response = new ResponseModel<>();

	    try {
	        List<CouponAndDiscountMaster> couponList;

	        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
	            couponList = couponAndDiscountMasterRepository.findByCouponNameAndCreatedByAndActive(
	                    searchTerm, createdBy, flag);
	        } else {
	            couponList = couponAndDiscountMasterRepository.findAllByCreatedByAndActive(createdBy, true);
	        }

	        List<CouponAndDiscountMasterSearch> dtoList = couponList.stream()
	                .map(coupon -> new CouponAndDiscountMasterSearch(
	                        coupon.getId(),
	                        coupon.getCouponName() != null ? coupon.getCouponName() : "N/A",
	                        coupon.getStartDate(),
	                        coupon.getEndDate()))
	                .collect(Collectors.toList());

	        int start = pageNumber * pageSize;
	        int end = Math.min(start + pageSize, dtoList.size());
	        List<CouponAndDiscountMasterSearch> paginatedList = dtoList.subList(start, end);

	        response.setData(paginatedList);
	        response.setTotalCount(couponList.size());
	        response.setPageNumber(pageNumber);
	        response.setPageSize(pageSize);
	        response.setStatusCode(HttpStatus.OK.toString());
	        response.setMessage("Coupons fetched successfully.");

	        log.info("Successfully fetched {} coupons.", paginatedList.size());
	    } catch (IllegalArgumentException e) {
	        log.error("Invalid pagination parameters: {}", e.getMessage());
	        response.setData(null);
	        response.setMessage(e.getMessage());
	        response.setStatusCode(HttpStatus.BAD_REQUEST.toString());
	    } catch (Exception e) {
	        log.error("Error occurred while fetching coupons: {}", e.getMessage());
	        response.setData(null);
	        response.setMessage("Failed to fetch coupons.");
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	    }

	    log.info("End CouponAndDiscountMasterServiceImpl -> getAllCouponAndDiscount()");
	    return response;
	}

	@Override
	public ResponseModel<CouponAndDiscountMasterResponseDTO> updateById(UUID id, CouponAndDiscountMasterRequestDTO couponAndDiscountMasterRequestDTO) {
	    log.info("Begin CouponAndDiscountMasterServiceImpl -> update() method");

	    ResponseModel<CouponAndDiscountMasterResponseDTO> response = new ResponseModel<>();

	    try {
	        CouponAndDiscountMaster existingEntity = couponAndDiscountMasterRepository.findById(id)
	                .orElseThrow(() -> new RecordNotFoundException("Coupon and Discount not found with ID: " + id));

	        CouponAndDiscountMaster updatedEntity = convertToEntity(couponAndDiscountMasterRequestDTO);

	        updatedEntity.setCreatedBy(existingEntity.getCreatedBy());
	        updatedEntity.setCreatedOn(existingEntity.getCreatedOn());

	        updatedEntity.setModifiedOn(new Timestamp(System.currentTimeMillis())); 

	        updatedEntity.setId(existingEntity.getId());
	        CouponAndDiscountMaster updated = couponAndDiscountMasterRepository.save(updatedEntity);
	        CouponAndDiscountMasterResponseDTO updatedResponseDTO = convertToResponseDTO(updated);

	        response.setStatusCode(HttpStatus.OK.toString());
	        response.setMessage("Coupon and Discount updated successfully.");
	        response.setData(updatedResponseDTO);

	        log.info("Coupon and Discount updated with ID: {}", updated.getId());
	    } catch (RecordNotFoundException e) {
	        log.error("Coupon and Discount not found: {}", e.getMessage());
	        response.setStatusCode(HttpStatus.NOT_FOUND.toString());
	        response.setMessage(e.getMessage());
	        response.setData(null);
	    } catch (Exception e) {
	        log.error("Error occurred while updating coupon and discount: {}", e.getMessage(), e);
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	        response.setMessage("Failed to update coupon and discount: " + e.getMessage());
	        response.setData(null);
	    }

	    log.info("End CouponAndDiscountMasterServiceImpl -> update() method");
	    return response;
	}




	@Override
	public ResponseModel<CouponAndDiscountMasterResponseDTO> getById(UUID id) {
	    log.info("Begin CouponAndDiscountMasterServiceImpl -> getById() method");

	    ResponseModel<CouponAndDiscountMasterResponseDTO> response = new ResponseModel<>();

	    try {
	        CouponAndDiscountMaster couponAndDiscountMaster = couponAndDiscountMasterRepository
	                .findById(id)
	                .orElseThrow(() -> new RecordNotFoundException("CouponAndDiscountMaster not found with ID: " + id));
	        CouponAndDiscountMasterResponseDTO couponAndDiscountMasterResponseDTO = convertToResponseDTO(couponAndDiscountMaster);

	        response.setStatusCode(HttpStatus.OK.toString());
	        response.setMessage("Coupon and Discount Master fetched successfully.");
	        response.setData(couponAndDiscountMasterResponseDTO);

	        log.info("Successfully fetched Coupon and Discount Master for ID: {}", id);

	    } catch (RecordNotFoundException e) {
	        log.error("Record not found: {}", e.getMessage());
	        response.setStatusCode(HttpStatus.NOT_FOUND.toString());
	        response.setMessage(e.getMessage());
	        response.setData(null);

	    } catch (Exception e) {
	        log.error("Unexpected error occurred while fetching Coupon and Discount Master for ID: {}", id, e);
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	        response.setMessage("Failed to fetch Coupon and Discount Master.");
	        response.setData(null);
	    }

	    log.info("End CouponAndDiscountMasterServiceImpl -> getById() method");
	    return response;
	}


	@Override
	public ResponseModel<CouponAndDiscountMasterResponseDTO> deleteById(UUID id) {
		log.info("Begin CouponAndDiscountMasterServiceImpl -> delete() method");

		ResponseModel<CouponAndDiscountMasterResponseDTO> response = new ResponseModel<>();

		try {
			CouponAndDiscountMaster existingEntity = couponAndDiscountMasterRepository.findById(id).orElseThrow(
					() -> new RecordNotFoundException("Coupon and Discount with ID " + id + " not found."));

			CouponAndDiscountMasterResponseDTO deletedDTO = convertToResponseDTO(existingEntity);

			couponAndDiscountMasterRepository.delete(existingEntity);

			response.setStatusCode(HttpStatus.OK.toString());
			response.setMessage("Coupon and Discount deleted successfully");
			response.setData(deletedDTO);

			log.info("Coupon and Discount with ID {} deleted successfully", id);

		} catch (RecordNotFoundException e) {
			log.error("Validation error while deleting coupon and discount: {}", e.getMessage());
			response.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			response.setMessage(e.getMessage());
			response.setData(null);

		} catch (Exception e) {
			log.error("Error occurred while deleting coupon and discount: {}", e.getMessage(), e);
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			response.setMessage("Failed to delete coupon and discount");
			response.setData(null);
		}

		log.info("End CouponAndDiscountMasterServiceImpl -> delete() method");
		return response;
	}
    

}
