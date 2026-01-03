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

import com.digiworldexpo.lims.entities.master.PaymentMode;
import com.digiworldexpo.lims.master.exception.BadRequestException;
import com.digiworldexpo.lims.master.exception.DuplicateRecordFoundException;
import com.digiworldexpo.lims.master.exception.RecordNotFoundException;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.repository.PaymentModeRepository;
import com.digiworldexpo.lims.master.service.PaymentModeService;
import com.digiworldexpo.lims.master.util.MasterDataLoader;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentModeServiceImpl implements PaymentModeService {

	private final PaymentModeRepository paymentModeRepository;
	private final MasterDataLoader masterDataLoader;

	public PaymentModeServiceImpl(PaymentModeRepository paymentModeRepository, MasterDataLoader masterDataLoader) {
		this.paymentModeRepository = paymentModeRepository;
		this.masterDataLoader = masterDataLoader;
	}

	@Override
	public ResponseModel<PaymentMode> addPaymentMode(PaymentMode paymentMode, UUID createdBy) {
		log.info("Begin of Payment Mode Service Implementation -> addPaymentMode() method");
		ResponseModel<PaymentMode> responseModel = new ResponseModel<PaymentMode>();

		try {

			if (paymentMode.getPaymentModeName() == null || paymentMode.getPaymentModeName().isEmpty()) {
				throw new BadRequestException("Please provide the payment mode name");
			}
			
			if(paymentMode.getPaymentModeName().trim().isEmpty() || paymentMode.getPaymentModeName().charAt(0)==' ') {
				throw new IllegalArgumentException("Payment mode name must not contain a space as first letter");
			}
			
			Optional<PaymentMode> optionalPaymentMode = paymentModeRepository.findByPaymentModeName(paymentMode.getPaymentModeName().toLowerCase());
			if(optionalPaymentMode.isPresent()) {
				throw new DuplicateRecordFoundException("Payment mode data for this "+paymentMode.getPaymentModeName()+" is already exists");
			}

			paymentMode.setCreatedBy(createdBy);
			
			masterDataLoader.addPaymentMode(paymentMode);
			log.info("New payment mode has been added in the cache.");
			
			paymentModeRepository.save(paymentMode);

			responseModel.setStatusCode(HttpStatus.CREATED.toString());
			responseModel.setMessage("Payment mode name has been added successfully");
			responseModel.setData(paymentMode);
		} catch(IllegalArgumentException illegalArgumentException) {
			log.info("Error occured due to invaid argument {}", illegalArgumentException.getMessage());
			
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Error occured due to invaid argument "+ illegalArgumentException.getMessage());
			responseModel.setData(null);
		} catch (BadRequestException badRequestException) {
			log.info("Validation failed for payment mode name input: {}", badRequestException.getMessage());
			
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Validation failed for payment mode name input: "+ badRequestException.getMessage());
			responseModel.setData(null);
		} catch (DuplicateRecordFoundException duplicateRecordFoundException) {
			log.info("Duplicate Record found : {}", duplicateRecordFoundException.getMessage());
			
			responseModel.setStatusCode(HttpStatus.CONFLICT.toString());
			responseModel.setMessage("Duplicate Record found : "+ duplicateRecordFoundException.getMessage());
			responseModel.setData(null);
		} catch (Exception exception) {
			log.info("Error in addPaymentMode(): {}", exception.getMessage());
			
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in addPaymentMode(): "+ exception.getMessage());
			responseModel.setData(null);
		}

		log.info("End of Payment Mode Service Implementation -> addPaymentMode() method");
		return responseModel;
	}

	@Override
	public ResponseModel<List<PaymentMode>> getAllPaymentModes(String startsWith, int pageNumber, int pageSize,
			String sortedBy) {
		log.info("Begin of Payment Mode Service Implementation -> getAllPaymentModes() method");
		ResponseModel<List<PaymentMode>> responseModel = new ResponseModel<>();

		try {
		
			List<PaymentMode> allPaymentModesInfo = masterDataLoader.getPaymentModes();
			List<PaymentMode> filteredPaymentModeData = new ArrayList<>();
			if (startsWith != null && !startsWith.isEmpty()) {
				filteredPaymentModeData = getPaymentModeBasedOnStartsWith(allPaymentModesInfo, startsWith, sortedBy);
			} else {
				filteredPaymentModeData = allPaymentModesInfo.stream().filter(paymentMode -> paymentMode.isActive())
						.sorted(dynamicSorting(sortedBy)).collect(Collectors.toList());
			}

			// Apply pagination
			Pageable pageable = PageRequest.of(pageNumber, pageSize);
			int start = (int) pageable.getOffset();
			int end = Math.min(start + pageable.getPageSize(), filteredPaymentModeData.size());
			if (start > filteredPaymentModeData.size()) {
				throw new IllegalArgumentException("Page number exceeds available data.");
			}
			List<PaymentMode> paginatedList = filteredPaymentModeData.subList(start, end);

			// Set response model data
			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Payment Mode names data retrieved successfully.");
			responseModel.setData(paginatedList);
			responseModel.setTotalCount(filteredPaymentModeData.size());
			responseModel.setPageNumber(pageNumber);
			responseModel.setPageSize(pageSize);
			responseModel.setSortedBy("Sorted by "+sortedBy+" in ascending order");

		} catch (IllegalArgumentException illegalArgumentException) {
			log.info("Invalid input parameters: {}", illegalArgumentException.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Invalid input parameters: "+ illegalArgumentException.getMessage());
			responseModel.setData(null);
		} catch (Exception exception) {
			log.info("Error in getAllPaymentModes(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in getAllPaymentModes(): "+ exception.getMessage());
			responseModel.setData(null);
		}
		log.info("End of Payment Mode Service Implementation -> getAllPaymentModes() method");
		return responseModel;
	}

	@Override
	public ResponseModel<PaymentMode> updatePaymentModeById(UUID id, PaymentMode requestPaymentMode, UUID modifiedBy) {
		log.info("End of Payment Mode Service Implementation -> updatePaymentModeById() method");
		ResponseModel<PaymentMode> responseModel = new ResponseModel<>();

		try {
			if (requestPaymentMode.getPaymentModeName() == null || requestPaymentMode.getPaymentModeName().isEmpty()) {
				throw new BadRequestException("Please provide the payment mode name");
			}

			Optional<PaymentMode> optionalPaymentMode = paymentModeRepository.findById(id);
			if (optionalPaymentMode.isEmpty()) {
				throw new RecordNotFoundException("No payment mode data found for Id: " + id);
			}

			PaymentMode existedPaymentMode = optionalPaymentMode.get();
			
			Optional<PaymentMode> optionalPaymentModeUsingName = paymentModeRepository.findByPaymentModeName(requestPaymentMode.getPaymentModeName());
			if(optionalPaymentModeUsingName.isPresent() && !optionalPaymentModeUsingName.get().getId().equals(existedPaymentMode.getId())) {
				throw new DuplicateRecordFoundException("Payment mode data for this "+optionalPaymentModeUsingName.get().getPaymentModeName()+" is already exists");
			}

			existedPaymentMode.setPaymentModeName(requestPaymentMode.getPaymentModeName());
			existedPaymentMode.setActive(true);
			existedPaymentMode.setModifiedBy(modifiedBy);
			existedPaymentMode.setModifiedOn(new Timestamp(System.currentTimeMillis()));
			
			if (masterDataLoader != null) {
				masterDataLoader.updatePaymentMode(existedPaymentMode);
				log.info("Payment mode updated in the cache");
			} else {
				log.error("masterDataLoader is null");
				throw new Exception("Failed to update payment mode in the cache due to internal error.");
			}
			
			paymentModeRepository.save(existedPaymentMode);

			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Payment Mode has been updated successfully for this id: " + id);
			responseModel.setData(existedPaymentMode);

		} catch (BadRequestException badRequestException) {
			log.info("Validation failed for payment mode name input: {}", badRequestException.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			responseModel.setMessage("Validation failed for payment mode name input: "+ badRequestException.getMessage());
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
			log.info("Error in updatePaymentModeById(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in updatePaymentModeById(): "+ exception.getMessage());
			responseModel.setData(null);
		}

		log.info("End of Payment Mode Service Implementation -> updatePaymentModeById() method");
		return responseModel;
	}

	@Override
	public ResponseModel<PaymentMode> getPaymentModeById(UUID id) {
		log.info("Begin of Payment Mode Service Implementation -> getPaymentModeById() method");
		ResponseModel<PaymentMode> responseModel = new ResponseModel<>();

		try {
			Optional<PaymentMode> optionalPaymentMode = paymentModeRepository.findById(id);
			if (optionalPaymentMode.isEmpty()) {
				throw new RecordNotFoundException("No payment mode data found for Id: " + id);
			}

			PaymentMode paymentMode = optionalPaymentMode.get();

			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Payment mode data has been fetched successfully for this id: " + id);
			responseModel.setData(paymentMode);

		} catch (RecordNotFoundException recordNotFoundException) {
			log.info("No record found for the given Id: {}", recordNotFoundException.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
			responseModel.setMessage("Fetch has failed: "+ recordNotFoundException.getMessage());
			responseModel.setData(null);
		} catch (Exception exception) {
			log.info("Error in getPaymentModeById(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in getPaymentModeById(): "+ exception.getMessage());
			responseModel.setData(null);
		}

		log.info("End of Payment Mode Service Implementation -> getPaymentModeById() method");
		return responseModel;
	}

	@Override
	public ResponseModel<PaymentMode> deletePaymentModeById(UUID id) {
		log.info("Begin of Payment Mode Service Implementation -> deletePaymentModeById() method");
		ResponseModel<PaymentMode> responseModel = new ResponseModel<>();

		try {
			PaymentMode paymentMode = paymentModeRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("No payment mode data found for Id: " + id));
			
			masterDataLoader.deletePaymentMode(paymentMode);
			log.info("Payment mode has been deleted in the cache");

			paymentModeRepository.delete(paymentMode);

			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Payment mode data has been deleted successfully for this id: " + id);
			responseModel.setData(paymentMode);

		}	catch (RecordNotFoundException recordNotFoundException) {
			log.info("No record found for the given Id: {}", recordNotFoundException.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
			responseModel.setMessage("Fetch has failed: "+ recordNotFoundException.getMessage());
			responseModel.setData(null);
		} catch (Exception exception) {
			log.info("Error in deletePaymentModeById(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in deletePaymentModeById(): "+ exception.getMessage());
			responseModel.setData(null);
		}
		log.info("End of Payment Mode Service Implementation -> deletePaymentModeById() method");
		return responseModel;
	}
	
	
	private List<PaymentMode> getPaymentModeBasedOnStartsWith(List<PaymentMode> allPaymentModes, String startsWith, String sortedBy){
		log.info("Begin of Payment Mode Service Implementation -> getPaymentModeBasedOnStartsWith() method");
		
		List<PaymentMode> filteredData = allPaymentModes.stream()
				.filter(paymentMode -> paymentMode.getPaymentModeName().toLowerCase().contains(startsWith.toLowerCase()) &&
						paymentMode.isActive())
				.sorted(dynamicSorting(sortedBy)).collect(Collectors.toList());
		
		log.info("End of Payment Mode Service Implementation -> getPaymentModeBasedOnStartsWith() method");
		return filteredData;
	}

	
	private Comparator<PaymentMode> dynamicSorting(String sortedBy){
		log.info("Begin of Payment Mode Service Implementation -> dynamicSorting() method");
		
		Map<String, Comparator<PaymentMode>> sortMapping = new HashMap<String, Comparator<PaymentMode>>();
		
		sortMapping.put("paymentModeName", Comparator.comparing(PaymentMode::getPaymentModeName));
		sortMapping.put("createdBy", Comparator.comparing(PaymentMode::getCreatedBy));
		sortMapping.put("createdOn", Comparator.comparing(PaymentMode::getCreatedOn));
		
		Comparator<PaymentMode> comparator = sortMapping.getOrDefault(sortedBy, Comparator.comparing(PaymentMode::getPaymentModeName));
		log.info("End of Payment Mode Service Implementation -> dynamicSorting() method");
		return comparator;
	}
}
