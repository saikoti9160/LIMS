package com.digiworldexpo.lims.master.service.serviceImpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.digiworldexpo.lims.entities.master.ReportPaperSize;
import com.digiworldexpo.lims.master.exception.BadRequestException;
import com.digiworldexpo.lims.master.exception.DuplicateRecordFoundException;
import com.digiworldexpo.lims.master.exception.RecordNotFoundException;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.repository.ReportPaperSizeRepository;
import com.digiworldexpo.lims.master.service.ReportPaperSizeService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReportPaperSizeServiceImpl implements ReportPaperSizeService {

	private final ReportPaperSizeRepository reportPaperSizeRepository;

	ReportPaperSizeServiceImpl(ReportPaperSizeRepository reportPaperSizeRepository) {
		this.reportPaperSizeRepository = reportPaperSizeRepository;
	}

	@Override
	public ResponseModel<ReportPaperSize> saveReportPaperSize(ReportPaperSize reportPaperSize, UUID createdBy) {
		log.info("Begin saveReportPaperSize()...");
		ResponseModel<ReportPaperSize> responseModel = new ResponseModel<>();

		try {
			if (reportPaperSize.getPaperSize() == null || reportPaperSize.getPaperSize().isEmpty()) {
				throw new BadRequestException("Paper size cannot be null or empty");
			}

			Optional<ReportPaperSize> existingPaperSize = reportPaperSizeRepository
					.findByPaperSizeAndCreatedBy(reportPaperSize.getPaperSize(), createdBy);

			if (existingPaperSize.isPresent()) {
				throw new DuplicateRecordFoundException("Paper size already exists for this user.");
			}

			reportPaperSize.setCreatedBy(createdBy);
			ReportPaperSize savedPaperSize = reportPaperSizeRepository.save(reportPaperSize);

			responseModel.setData(savedPaperSize);
			responseModel.setMessage("Paper size saved successfully");
			responseModel.setStatusCode(HttpStatus.OK.toString());
		} catch (Exception e) {
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			log.error("Error saving ReportPaperSize: {}", e.getMessage());
		}

		log.info("End saveReportPaperSize()...");
		return responseModel;
	}

	@Override
	public ResponseModel<List<ReportPaperSize>> getReportPaperSizes(String startsWith, int pageNumber, int pageSize,
	        String sortBy, UUID createdBy) {
	    log.info("Begin getReportPaperSizes()...");
	    ResponseModel<List<ReportPaperSize>> responseModel = new ResponseModel<>();

	    try {
	        // Step 1: Fetch all records for createdBy
	        List<ReportPaperSize> reportPaperSizeList = reportPaperSizeRepository.findByCreatedBy(createdBy);

	        // Step 2: Apply 'startsWith' filter if provided
	        if (startsWith != null && !startsWith.trim().isEmpty()) {
	        	reportPaperSizeList = reportPaperSizeList.stream()
	                    .filter(rps -> rps.getPaperSize() != null && rps.getPaperSize().toLowerCase().contains(startsWith.toLowerCase()))
	                    .collect(Collectors.toList());
	        }

	        // Step 3: Apply dynamic sorting with nullsLast
	        Comparator<ReportPaperSize> comparator;
	        if ("paperSize".equalsIgnoreCase(sortBy)) {
	            comparator = Comparator.comparing(ReportPaperSize::getPaperSize, Comparator.nullsLast(String::compareToIgnoreCase));
	        } else if ("createdOn".equalsIgnoreCase(sortBy)) {
	            comparator = Comparator.comparing(ReportPaperSize::getCreatedOn, Comparator.nullsLast(Comparator.naturalOrder()));
	        } else {
	            throw new IllegalArgumentException("Unexpected value for sortBy: " + sortBy);
	        }

	        reportPaperSizeList.sort(comparator);

	        // Step 4: Apply pagination
	        int totalRecords = reportPaperSizeList.size();
	        int fromIndex = pageNumber * pageSize;
	        int toIndex = Math.min(fromIndex + pageSize, totalRecords);
	        List<ReportPaperSize> paginatedList = (fromIndex < totalRecords)
	                ? reportPaperSizeList.subList(fromIndex, toIndex)
	                : new ArrayList<>();

	        // Step 5: Build response
	        responseModel.setData(paginatedList);
	        responseModel.setMessage("Paper size retrieved successfully.");
	        responseModel.setStatusCode(HttpStatus.OK.toString());
	        responseModel.setTotalCount(totalRecords);
	        responseModel.setPageNumber(pageNumber);
	        responseModel.setPageSize(pageSize);
	        responseModel.setSortedBy(sortBy);

	    } catch (IllegalArgumentException e) {
	        responseModel.setData(null);
	        responseModel.setMessage(e.getMessage());
	        responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
	        log.error("Invalid input parameters: {}", e.getMessage());
	    } catch (Exception e) {
	        responseModel.setData(null);
	        responseModel.setMessage("Failed to retrieve page size.");
	        responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	        log.error("Error occurred while retrieving paper sizes: {}", e.getMessage());
	    }

	    log.info("End getReportPaperSizes()...");
	    return responseModel;
	}


	@Override
	public ResponseModel<ReportPaperSize> getReportPaperSizeById(UUID id) {
		log.info("Begin getReportPaperSizeById()...");
		ResponseModel<ReportPaperSize> responseModel = new ResponseModel<>();
		try {
			ReportPaperSize paperSize = reportPaperSizeRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("Paper size not found"));
			responseModel.setData(paperSize);
			responseModel.setMessage("Paper size retrieved successfully");
			responseModel.setStatusCode(HttpStatus.OK.toString());
		} catch (RecordNotFoundException e) {
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
		} catch (Exception e) {
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}
		log.info("End getReportPaperSizeById()...");
		return responseModel;
	}
	
	
	

	@Override
	@Transactional
	public ResponseModel<ReportPaperSize> updateReportPaperSize(UUID id, ReportPaperSize updatedPaperSize, UUID userId) {
		log.info("Begin updateReportPaperSize()...");
		ResponseModel<ReportPaperSize> responseModel = new ResponseModel<>();

		try {
			// Step 1: Find existing record by ID
			ReportPaperSize existingPaperSize = reportPaperSizeRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("Page size not found"));

			// Step 2: Fetch all records for the given createdBy (userId)
	        List<ReportPaperSize> existingPaperSizes = reportPaperSizeRepository.findByCreatedBy(userId);

	        // Step 3: Check if the updated page size already exists in the user's list
	        boolean isDuplicate = existingPaperSizes.stream()
	                .anyMatch(rps -> rps.getPaperSize().equalsIgnoreCase(updatedPaperSize.getPaperSize())
	                        && !rps.getId().equals(id));

	        if (isDuplicate) {
	            throw new DuplicateRecordFoundException("Page size already exists");
	        }

			// Step 4: Update allowed fields
			existingPaperSize.setPaperSize(updatedPaperSize.getPaperSize());
			existingPaperSize.setModifiedBy(userId);
			existingPaperSize.setModifiedOn(new Timestamp(System.currentTimeMillis()));

			// Step 5: Save updated entity
			ReportPaperSize savedPaperSize = reportPaperSizeRepository.save(existingPaperSize);

			// Step 6: Build response
			responseModel.setData(savedPaperSize);
			responseModel.setMessage("Paper size updated successfully");
			responseModel.setStatusCode(HttpStatus.OK.toString());

		} catch (RecordNotFoundException e) {
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
			log.error("Validation error: {}", e.getMessage());
		} catch (DuplicateRecordFoundException e) {
			responseModel.setData(null);
			responseModel.setMessage("Failed to update paper size");
			responseModel.setStatusCode(HttpStatus.CONFLICT.toString());
			log.error("Error occurred while updating paper size: {}", e.getMessage());
		} catch (Exception e) {
			responseModel.setData(null);
			responseModel.setMessage("Failed to update paper size");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			log.error("Error occurred while updating paper size: {}", e.getMessage());
		}

		log.info("End updateReportPaperSize()...");
		return responseModel;
	}

	@Override
	public ResponseModel<ReportPaperSize> deleteReportPaperSize(UUID id) {
		log.info("Begin deleteReportPaperSize()...");
		ResponseModel<ReportPaperSize> responseModel = new ResponseModel<>();

		try {
			ReportPaperSize existingPaperSize = reportPaperSizeRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("Paper size not found"));

			reportPaperSizeRepository.deleteById(id);
			responseModel.setMessage("Paper size deleted successfully");
			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setData(existingPaperSize);
		} catch (Exception e) {
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End deleteReportPaperSize()...");
		return responseModel;
	}

	
}
