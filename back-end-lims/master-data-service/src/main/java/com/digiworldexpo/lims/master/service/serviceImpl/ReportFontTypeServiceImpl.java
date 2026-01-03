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

import com.digiworldexpo.lims.entities.master.ReportFontType;
import com.digiworldexpo.lims.master.exception.BadRequestException;
import com.digiworldexpo.lims.master.exception.DuplicateRecordFoundException;
import com.digiworldexpo.lims.master.exception.RecordNotFoundException;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.repository.ReportFontTypeRepository;
import com.digiworldexpo.lims.master.service.ReportFontTypeService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ReportFontTypeServiceImpl implements ReportFontTypeService {

	private final ReportFontTypeRepository reportFontTypeRepository;

	public ReportFontTypeServiceImpl(ReportFontTypeRepository fontTypeRepository) {
		this.reportFontTypeRepository = fontTypeRepository;
	}

	@Override
	public ResponseModel<ReportFontType> saveFontType(ReportFontType fontType, UUID createdBy) {
		log.info("Begin saveFontType()...");
		ResponseModel<ReportFontType> responseModel = new ResponseModel<>();

		try {
			if (fontType.getFontType() == null || fontType.getFontType().isEmpty()) {
				throw new BadRequestException("Font name cannot be null or empty");
			}

			Optional<ReportFontType> existingFont = reportFontTypeRepository.findByFontTypeAndCreatedBy(fontType.getFontType(),
					createdBy);
			if (existingFont.isPresent()) {
				throw new DuplicateRecordFoundException("Font type already exists for this user.");
			}

			fontType.setCreatedBy(createdBy);
			ReportFontType savedFontType = reportFontTypeRepository.save(fontType);

			responseModel.setData(savedFontType);
			responseModel.setMessage("Font type saved successfully");
			responseModel.setStatusCode(HttpStatus.OK.toString());
		} catch (Exception e) {
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			log.error("Error saving FontType: {}", e.getMessage());
		}

		log.info("End saveFontType()...");
		return responseModel;
	}

	@Override
	public ResponseModel<List<ReportFontType>> getFontTypes(String searchTearm, int pageNumber, int pageSize, String sortBy,
			UUID createdBy) {
		log.info("Begin getFontTypes()...");
		ResponseModel<List<ReportFontType>> responseModel = new ResponseModel<>();

		try {
			List<ReportFontType> fontTypeList = reportFontTypeRepository.findByCreatedBy(createdBy);

			if (searchTearm != null && !searchTearm.trim().isEmpty()) {
				fontTypeList = fontTypeList.stream()
						.filter(ft -> ft.getFontType().toLowerCase().contains(searchTearm.toLowerCase()))
						.collect(Collectors.toList());
			}

			fontTypeList.sort(Comparator.comparing(ReportFontType::getFontType));

			int totalRecords = fontTypeList.size();
			int fromIndex = pageNumber * pageSize;
			int toIndex = Math.min(fromIndex + pageSize, totalRecords);
			List<ReportFontType> paginatedList = (fromIndex < totalRecords) ? fontTypeList.subList(fromIndex, toIndex)
					: new ArrayList<>();

			responseModel.setData(paginatedList);
			responseModel.setMessage("Font types retrieved successfully.");
			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setTotalCount(totalRecords);
			responseModel.setPageNumber(pageNumber);
			responseModel.setPageSize(pageSize);
			responseModel.setSortedBy(sortBy);
		} catch (Exception e) {
			responseModel.setData(null);
			responseModel.setMessage("Failed to retrieve font types.");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			log.error("Error occurred while retrieving font types: {}", e.getMessage());
		}

		log.info("End getFontTypes()...");
		return responseModel;
	}

	@Override
	public ResponseModel<ReportFontType> getFontTypeById(UUID id) {
		log.info("Begin getFontTypeById()...");
		ResponseModel<ReportFontType> responseModel = new ResponseModel<>();
		try {
			ReportFontType fontType = reportFontTypeRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("Font type not found"));
			responseModel.setData(fontType);
			responseModel.setMessage("Font type retrieved successfully");
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
		log.info("End getFontTypeById()...");
		return responseModel;
	}

	@Override
	@Transactional
	public ResponseModel<ReportFontType> updateFontType(UUID id, ReportFontType updatedFontType, UUID userId) {
		log.info("Begin updateFontType()...");
		ResponseModel<ReportFontType> responseModel = new ResponseModel<>();

		try {
			ReportFontType existingFontType = reportFontTypeRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("Font type not found"));

			existingFontType.setFontType(updatedFontType.getFontType());
			existingFontType.setModifiedBy(userId);
			existingFontType.setModifiedOn(new Timestamp(System.currentTimeMillis()));

			ReportFontType savedFontType = reportFontTypeRepository.save(existingFontType);
			responseModel.setData(savedFontType);
			responseModel.setMessage("Font type updated successfully");
			responseModel.setStatusCode(HttpStatus.OK.toString());
		} catch (RecordNotFoundException e) {
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
		} catch (Exception e) {
			responseModel.setData(null);
			responseModel.setMessage("Failed to update font type");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			log.error("Error occurred while updating font type: {}", e.getMessage());
		}

		log.info("End updateFontType()...");
		return responseModel;
	}

	@Override
	public ResponseModel<ReportFontType> deleteFontType(UUID id) {
		log.info("Begin deleteFontType()...");
		ResponseModel<ReportFontType> responseModel = new ResponseModel<>();

		try {
			ReportFontType existingFontType = reportFontTypeRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("Font type not found"));

			reportFontTypeRepository.deleteById(id);
			responseModel.setMessage("Font type deleted successfully");
			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setData(existingFontType);
		} catch (RecordNotFoundException e) {
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
		} catch (Exception e) {
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End deleteFontType()...");
		return responseModel;
	}
}
