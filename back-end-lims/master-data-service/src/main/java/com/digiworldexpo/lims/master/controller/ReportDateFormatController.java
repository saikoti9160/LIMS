package com.digiworldexpo.lims.master.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.digiworldexpo.lims.master.util.HttpStatusCode;
import com.digiworldexpo.lims.entities.master.ReportDateFormat;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.service.ReportDateFormatService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/report-date-format")
public class ReportDateFormatController {

	private final ReportDateFormatService reportDateFormatService;

	private final HttpStatusCode httpStatusCode;

	ReportDateFormatController(ReportDateFormatService reportDateFormatService, HttpStatusCode httpStatusCode) {
		this.reportDateFormatService = reportDateFormatService;
		this.httpStatusCode = httpStatusCode;
	}

	@PostMapping("/save")
	public ResponseEntity<ResponseModel<ReportDateFormat>> saveReportDateFormat(@RequestBody ReportDateFormat reportDateFormat,
			@RequestHeader("createdBy") UUID createdBy) {
		log.info("Begin ReportDateFormatController -> saveReportDateFormat() method");
		ResponseModel<ReportDateFormat> responseModel = reportDateFormatService.saveReportDateFormat(reportDateFormat,
				createdBy);
		log.info("End ReportDateFormatController -> saveReportDateFormat() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(responseModel);
	}

	@PostMapping("/get-all")
	public ResponseEntity<ResponseModel<List<ReportDateFormat>>> getReportDateFormats(
			@RequestParam(required = false) String startsWith, @RequestParam(defaultValue = "0") int pageNumber,
			@RequestParam(defaultValue = "10") int pageSize, @RequestParam(defaultValue = "createdOn") String sortBy,
			@RequestHeader("createdBy") UUID createdBy) {
		log.info("Begin ReportDateFormatController -> getReportDateFormats() method");
		ResponseModel<List<ReportDateFormat>> responseModel = reportDateFormatService.getReportDateFormats(startsWith,
				pageNumber, pageSize, sortBy, createdBy);
		log.info("End ReportDateFormatController -> getReportDateFormats() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(responseModel);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ResponseModel<ReportDateFormat>> getReportDateFormatById(@PathVariable UUID id) {
		log.info("Begin ReportDateFormatController -> getReportDateFormatById() method");
		ResponseModel<ReportDateFormat> response = reportDateFormatService.getReportDateFormatById(id);
		log.info("End ReportDateFormatController -> getReportDateFormatById() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ResponseModel<ReportDateFormat>> deleteReportDateFormat(@PathVariable UUID id) {
		log.info("Begin ReportDateFormatController -> deleteReportDateFormat() method");
		ResponseModel<ReportDateFormat> response = reportDateFormatService.deleteReportDateFormat(id);
		log.info("End ReportDateFormatController -> deleteReportDateFormat() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(response);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<ResponseModel<ReportDateFormat>> updateReportDateFormat(@PathVariable UUID id,
			@RequestBody ReportDateFormat updatedReportDateFormat, @RequestHeader("userId") UUID userId) {
		log.info("Begin ReportDateFormatController -> updateReportDateFormat() method");
		ResponseModel<ReportDateFormat> responseModel = reportDateFormatService.updateReportDateFormat(id,
				updatedReportDateFormat, userId);
		log.info("End ReportDateFormatController -> updateReportDateFormat() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(responseModel);
	}
}

