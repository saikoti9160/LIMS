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

import com.digiworldexpo.lims.entities.master.ReportDateFormat;
import com.digiworldexpo.lims.master.exception.BadRequestException;
import com.digiworldexpo.lims.master.exception.DuplicateRecordFoundException;
import com.digiworldexpo.lims.master.exception.RecordNotFoundException;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.repository.ReportDateFormatRepository;
import com.digiworldexpo.lims.master.service.ReportDateFormatService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReportDateFormatServiceImpl implements ReportDateFormatService {
	
	private final ReportDateFormatRepository reportDateFormatRepository;

	ReportDateFormatServiceImpl(ReportDateFormatRepository reportDateFormatRepository) {
		this.reportDateFormatRepository = reportDateFormatRepository;
	}
	
	@Override
	public ResponseModel<ReportDateFormat> saveReportDateFormat(ReportDateFormat reportDateFormat, UUID createdBy) {
		log.info("Begin saveReportDateFormat()...");
		ResponseModel<ReportDateFormat> responseModel = new ResponseModel<>();

		try {
			if (reportDateFormat.getDateFormat() == null || reportDateFormat.getDateFormat().isEmpty()) {
				new BadRequestException("Date Format cannot be null or empty");
			}

			Optional<ReportDateFormat> existingDateFormat= reportDateFormatRepository
					.findByDateFormatAndCreatedBy(reportDateFormat.getDateFormat(), createdBy);

			if (existingDateFormat.isPresent()) {
				throw new DuplicateRecordFoundException("Date Format already exists for this user.");
			}

			reportDateFormat.setCreatedBy(createdBy);
			ReportDateFormat savedDateFormat = reportDateFormatRepository.save(reportDateFormat);

			responseModel.setData(savedDateFormat);
			responseModel.setMessage("Date Format saved successfully");
			responseModel.setStatusCode(HttpStatus.OK.toString());
		}catch (DuplicateRecordFoundException e) {
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.CONFLICT.toString());
			log.error("Error saving ReportDateFormat: {}", e.getMessage());
		}
		catch (Exception e) {
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			log.error("Error saving ReportDateFormat: {}", e.getMessage());
		}

		log.info("End saveReportDateFormat()...");
		return responseModel;
	}

	@Override
	public ResponseModel<List<ReportDateFormat>> getReportDateFormats(String startsWith, int pageNumber, int pageSize,
			String sortBy, UUID createdBy) {
		log.info("Begin getReportDateFormats()...");
		ResponseModel<List<ReportDateFormat>> responseModel = new ResponseModel<>();

		try {
			List<ReportDateFormat> reportDateFormatList = reportDateFormatRepository.findByCreatedBy(createdBy);

			if (startsWith != null && !startsWith.trim().isEmpty()) {
				reportDateFormatList = reportDateFormatList.stream()
						.filter(rps -> rps.getDateFormat().toLowerCase().startsWith(startsWith.toLowerCase()))
						.collect(Collectors.toList());
			}

			 Comparator<ReportDateFormat> comparator;
		        if ("dateFormat".equalsIgnoreCase(sortBy)) {
		            comparator = Comparator.comparing(ReportDateFormat::getDateFormat, Comparator.nullsLast(String::compareToIgnoreCase));
		        } else if ("createdOn".equalsIgnoreCase(sortBy)) {
		            comparator = Comparator.comparing(ReportDateFormat::getCreatedOn, Comparator.nullsLast(Comparator.naturalOrder()));
		        } else {
		            throw new IllegalArgumentException("Unexpected value for sortBy: " + sortBy);
		        }
	 
		        reportDateFormatList.sort(comparator);

			int totalRecords = reportDateFormatList.size();
			int fromIndex = pageNumber * pageSize;
			int toIndex = Math.min(fromIndex + pageSize, totalRecords);
			List<ReportDateFormat> paginatedList = (fromIndex < totalRecords)
					? reportDateFormatList.subList(fromIndex, toIndex)
					: new ArrayList<>();

			responseModel.setData(paginatedList);
			responseModel.setMessage("Date Format retrieved successfully.");
			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setTotalCount(totalRecords);
			responseModel.setPageNumber(pageNumber);
			responseModel.setPageSize(pageSize);
			responseModel.setSortedBy(sortBy);

		} catch (Exception e) {
			responseModel.setData(null);
			responseModel.setMessage("Failed to retrieve Date Format.");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			log.error("Error occurred while retrieving date Format: {}", e.getMessage());
		}

		log.info("End getReportDateFormats()...");
		return responseModel;
	}
	
	@Override
	public ResponseModel<ReportDateFormat> getReportDateFormatById(UUID id) {
		log.info("Begin getReportDateFormatById()...");
		ResponseModel<ReportDateFormat> responseModel = new ResponseModel<>();
		try {
			ReportDateFormat dateFormat = reportDateFormatRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("Date format not found"));
			responseModel.setData(dateFormat);
			responseModel.setMessage("Date format retrieved successfully");
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
		log.info("End getReportDateFormatById()...");
		return responseModel;
	}

	@Override
	@Transactional
	public ResponseModel<ReportDateFormat> updateReportDateFormat(UUID id, ReportDateFormat updatedDateFormat, UUID userId) {
		log.info("Begin updateReportDateFormat()...");
		ResponseModel<ReportDateFormat> responseModel = new ResponseModel<>();

		try {
			ReportDateFormat existingDateFormat = reportDateFormatRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("Date format not found"));

			List<ReportDateFormat> existingDateFormats = reportDateFormatRepository.findByCreatedBy(userId);

			boolean isDuplicate = existingDateFormats.stream()
					.anyMatch(rdf -> rdf.getDateFormat().equalsIgnoreCase(updatedDateFormat.getDateFormat())
							&& !rdf.getId().equals(id));

			if (isDuplicate) {
				throw new DuplicateRecordFoundException("Date format already exists");
			}

			existingDateFormat.setDateFormat(updatedDateFormat.getDateFormat());
			existingDateFormat.setModifiedBy(userId);
			existingDateFormat.setModifiedOn(new Timestamp(System.currentTimeMillis()));

			ReportDateFormat savedDateFormat = reportDateFormatRepository.save(existingDateFormat);

			responseModel.setData(savedDateFormat);
			responseModel.setMessage("Date format updated successfully");
			responseModel.setStatusCode(HttpStatus.OK.toString());

		} catch (RecordNotFoundException e) {
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
			log.error("Validation error: {}", e.getMessage());
		} catch (DuplicateRecordFoundException e) {
			responseModel.setData(null);
			responseModel.setMessage("Date Format already exist to update");
			responseModel.setStatusCode(HttpStatus.CONFLICT.toString());
			log.error("date format already exist : {}", e.getMessage());
		} catch (Exception e) {
			responseModel.setData(null);
			responseModel.setMessage("Failed to update date format");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			log.error("Error occurred while updating date format: {}", e.getMessage());
		}

		log.info("End updateReportDateFormat()...");
		return responseModel;
	}

	@Override
	public ResponseModel<ReportDateFormat> deleteReportDateFormat(UUID id) {
		log.info("Begin deleteReportDateFormat()...");
		ResponseModel<ReportDateFormat> responseModel = new ResponseModel<>();

		try {
			ReportDateFormat existingDateFormat = reportDateFormatRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("Date format not found"));

			reportDateFormatRepository.deleteById(id);
			responseModel.setMessage("Date format deleted successfully");
			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setData(existingDateFormat);
		} catch (Exception e) {
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End deleteReportDateFormat()...");
		return responseModel;
	}

}
