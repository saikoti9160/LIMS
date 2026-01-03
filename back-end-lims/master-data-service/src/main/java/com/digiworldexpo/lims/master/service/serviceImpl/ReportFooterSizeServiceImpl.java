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

import com.digiworldexpo.lims.entities.master.ReportFooterSize;
import com.digiworldexpo.lims.master.exception.BadRequestException;
import com.digiworldexpo.lims.master.exception.DuplicateRecordFoundException;
import com.digiworldexpo.lims.master.exception.RecordNotFoundException;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.repository.ReportFooterSizeRepository;
import com.digiworldexpo.lims.master.service.ReportFooterSizeService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReportFooterSizeServiceImpl implements ReportFooterSizeService {

	private final ReportFooterSizeRepository reportFooterSizeRepository;

	ReportFooterSizeServiceImpl(ReportFooterSizeRepository reportFooterSizeRepository) {
		this.reportFooterSizeRepository = reportFooterSizeRepository;
	}

	@Override
	public ResponseModel<ReportFooterSize> saveReportFooterSize(ReportFooterSize reportFooterSize, UUID createdBy) {
		log.info("Begin saveReportFooterSize()...");
		ResponseModel<ReportFooterSize> responseModel = new ResponseModel<>();

		try {
			if (reportFooterSize.getFooterSize() == null || reportFooterSize.getFooterSize().isEmpty()) {
				throw new BadRequestException("Footer size cannot be null or empty");
			}

			Optional<ReportFooterSize> existingFooterSize = reportFooterSizeRepository
					.findByFooterSizeAndCreatedBy(reportFooterSize.getFooterSize(), createdBy);

			if (existingFooterSize.isPresent()) {
				throw new DuplicateRecordFoundException("Footer size already exists for this user.");
			}

			reportFooterSize.setCreatedBy(createdBy);
			ReportFooterSize savedFooterSize = reportFooterSizeRepository.save(reportFooterSize);

			responseModel.setData(savedFooterSize);
			responseModel.setMessage("Footer size saved successfully");
			responseModel.setStatusCode(HttpStatus.OK.toString());
		} catch (Exception e) {
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			log.error("Error saving ReportFooterSize: {}", e.getMessage());
		}

		log.info("End saveReportFooterSize()...");
		return responseModel;
	}

	@Override
	public ResponseModel<List<ReportFooterSize>> getReportFooterSizes(String startsWith, int pageNumber, int pageSize,
			String sortBy, UUID createdBy) {
		log.info("Begin getReportFooterSizes()...");
		ResponseModel<List<ReportFooterSize>> responseModel = new ResponseModel<>();

		try {
			// Step 1: Fetch all records for createdBy
			List<ReportFooterSize> reportFooterSizeList = reportFooterSizeRepository.findByCreatedBy(createdBy);

			// Step 2: Apply 'startsWith' filter if provided
			if (startsWith != null && !startsWith.trim().isEmpty()) {
				reportFooterSizeList = reportFooterSizeList.stream()
						.filter(rfs -> rfs.getFooterSize().toLowerCase().contains(startsWith.toLowerCase()))
						.collect(Collectors.toList());
			}

			// Step 3: Apply dynamic sorting with nullsLast
	        Comparator<ReportFooterSize> comparator;
	        if ("footerSize".equalsIgnoreCase(sortBy)) {
	            comparator = Comparator.comparing(ReportFooterSize::getFooterSize, Comparator.nullsLast(String::compareToIgnoreCase));
	        } else if ("createdOn".equalsIgnoreCase(sortBy)) {
	            comparator = Comparator.comparing(ReportFooterSize::getCreatedOn, Comparator.nullsLast(Comparator.naturalOrder()));
	        } else {
	            throw new IllegalArgumentException("Unexpected value for sortBy: " + sortBy);
	        }

	        reportFooterSizeList.sort(comparator);

	        // Step 4: Apply pagination
	        int totalRecords = reportFooterSizeList.size();
	        int fromIndex = pageNumber * pageSize;
	        int toIndex = Math.min(fromIndex + pageSize, totalRecords);
	        List<ReportFooterSize> paginatedList = (fromIndex < totalRecords)
	                ? reportFooterSizeList.subList(fromIndex, toIndex)
	                : new ArrayList<>();
			// Step 5: Build response
			responseModel.setData(paginatedList);
			responseModel.setMessage("Footer size retrieved successfully.");
			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setTotalCount(totalRecords);
			responseModel.setPageNumber(pageNumber);
			responseModel.setPageSize(pageSize);

		} catch (IllegalArgumentException e) {
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			log.error("Invalid input parameters: {}", e.getMessage());
		} catch (Exception e) {
			responseModel.setData(null);
			responseModel.setMessage("Failed to retrieve footer size.");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			log.error("Error occurred while retrieving footer sizes: {}", e.getMessage());
		}

		log.info("End getReportFooterSizes()...");
		return responseModel;
	}

	@Override
	public ResponseModel<ReportFooterSize> getReportFooterSizeById(UUID id) {
		log.info("Begin getReportFooterSizeById()...");
		ResponseModel<ReportFooterSize> responseModel = new ResponseModel<>();
		try {
			ReportFooterSize footerSize = reportFooterSizeRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("Footer size not found"));
			responseModel.setData(footerSize);
			responseModel.setMessage("Footer size retrieved successfully");
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
		log.info("End getReportFooterSizeById()...");
		return responseModel;
	}

	@Override
	@Transactional
	public ResponseModel<ReportFooterSize> updateReportFooterSize(UUID id, ReportFooterSize updatedFooterSize, UUID userId) {
		log.info("Begin updateReportFooterSize()...");
		ResponseModel<ReportFooterSize> responseModel = new ResponseModel<>();

		try {
			// Step 1: Find existing record by ID
			ReportFooterSize existingFooterSize = reportFooterSizeRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("Footer size not found"));

			// Step 2: Fetch all records for the given createdBy (userId)
			List<ReportFooterSize> existingFooterSizes = reportFooterSizeRepository.findByCreatedBy(userId);

			// Step 3: Check if the updated page size already exists in the user's list
			// (excluding itself)
			boolean isDuplicate = existingFooterSizes.stream()
					.anyMatch(rfs -> rfs.getFooterSize().equalsIgnoreCase(updatedFooterSize.getFooterSize())
							&& !rfs.getId().equals(id));

			if (isDuplicate) {
				throw new DuplicateRecordFoundException("Footer size already exists");
			}

			// Step 4: Update allowed fields
			existingFooterSize.setFooterSize(updatedFooterSize.getFooterSize());
			existingFooterSize.setModifiedBy(userId);
			existingFooterSize.setModifiedOn(new Timestamp(System.currentTimeMillis()));

			// Step 5: Save updated entity
			ReportFooterSize savedFooterSize = reportFooterSizeRepository.save(existingFooterSize);

			// Step 6: Build response
			responseModel.setData(savedFooterSize);
			responseModel.setMessage("Footer size updated successfully");
			responseModel.setStatusCode(HttpStatus.OK.toString());

		} catch (RecordNotFoundException e) {
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
			log.error("Validation error: {}", e.getMessage());
		} catch (DuplicateRecordFoundException e) {
			responseModel.setData(null);
			responseModel.setMessage("Failed to update footer size");
			responseModel.setStatusCode(HttpStatus.CONFLICT.toString());
			log.error("Error occurred while updating footer size: {}", e.getMessage());
		} catch (Exception e) {
			responseModel.setData(null);
			responseModel.setMessage("Failed to update footer size");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			log.error("Error occurred while updating footer size: {}", e.getMessage());
		}

		log.info("End updateReportFooterSize()...");
		return responseModel;
	}

	@Override
	public ResponseModel<ReportFooterSize> deleteReportFooterSize(UUID id) {
		log.info("Begin deleteReportFooterSize()...");
		ResponseModel<ReportFooterSize> responseModel = new ResponseModel<>();

		try {
			ReportFooterSize existingFooterSize = reportFooterSizeRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("Footer size not found"));

			reportFooterSizeRepository.deleteById(id);
			responseModel.setMessage("Footer size deleted successfully");
			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setData(existingFooterSize);
		} catch (Exception e) {
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End deleteReportFooterSize()...");
		return responseModel;
	}
}
