package com.digiworldexpo.lims.master.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import com.digiworldexpo.lims.entities.master.ReportFooterSize;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.service.ReportFooterSizeService;
import com.digiworldexpo.lims.master.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/report-footer-size")
public class ReportFooterSizeController {

	private final ReportFooterSizeService reportFooterSizeService;

	private final HttpStatusCode httpStatusCode;

	ReportFooterSizeController(ReportFooterSizeService reportFooterSizeService, HttpStatusCode httpStatusCode) {
		this.reportFooterSizeService = reportFooterSizeService;
		this.httpStatusCode = httpStatusCode;
	}

	@PostMapping("/save")
	public ResponseEntity<ResponseModel<ReportFooterSize>> saveReportFooterSize(@RequestBody ReportFooterSize reportFooterSize,
			@RequestHeader("createdBy") UUID createdBy) {
		log.info("Begin ReportFooterSizeController -> saveReportFooterSize() method");
		ResponseModel<ReportFooterSize> responseModel = reportFooterSizeService.saveReportFooterSize(reportFooterSize,
				createdBy);
		log.info("End ReportFooterSizeController -> saveReportFooterSize() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(responseModel);
	}

	@PostMapping("/get-all")
	public ResponseEntity<ResponseModel<List<ReportFooterSize>>> getReportFooterSizes(
			@RequestParam(required = false) String startsWith, @RequestParam(defaultValue = "0") int pageNumber,
			@RequestParam(defaultValue = "10") int pageSize, @RequestParam(defaultValue = "footerSize") String sortBy,
			@RequestHeader("createdBy") UUID createdBy) {
		log.info("Begin ReportFooterSizeController -> getReportFooterSizes() method");
		ResponseModel<List<ReportFooterSize>> responseModel = reportFooterSizeService.getReportFooterSizes(startsWith,
				pageNumber, pageSize, sortBy, createdBy);
		log.info("End ReportFooterSizeController -> getReportFooterSizes() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(responseModel);
	}

	@GetMapping("/get/{id}")
	public ResponseEntity<ResponseModel<ReportFooterSize>> getReportFooterSizeById(@PathVariable UUID id) {
		log.info("Begin ReportFooterSizeController -> getReportFooterSizeById() method");
		ResponseModel<ReportFooterSize> response = reportFooterSizeService.getReportFooterSizeById(id);
		log.info("End ReportFooterSizeController -> getReportFooterSizeById() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(response);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResponseModel<ReportFooterSize>> deleteReportFooterSize(@PathVariable UUID id) {
		log.info("Begin ReportFooterSizeController -> deleteReportFooterSize() method");
		ResponseModel<ReportFooterSize> response = reportFooterSizeService.deleteReportFooterSize(id);
		log.info("End ReportFooterSizeController -> deleteReportFooterSize() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(response);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<ResponseModel<ReportFooterSize>> updateReportFooterSize(@PathVariable UUID id,
			@RequestBody ReportFooterSize updatedReportFooterSize, @RequestHeader("userId") UUID userId) {
		log.info("Begin ReportFooterSizeController -> updateReportFooterSize() method");
		ResponseModel<ReportFooterSize> responseModel = reportFooterSizeService.updateReportFooterSize(id,
				updatedReportFooterSize, userId);
		log.info("End ReportFooterSizeController -> updateReportFooterSize() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(responseModel);
	}
}