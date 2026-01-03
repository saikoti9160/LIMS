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
import com.digiworldexpo.lims.entities.master.ReportPaperSize;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.service.ReportPaperSizeService;
import com.digiworldexpo.lims.master.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/report-paper-size")
public class ReportPaperSizeController {

	private final ReportPaperSizeService reportPaperSizeService;

	private final HttpStatusCode httpStatusCode;

	ReportPaperSizeController(ReportPaperSizeService reportPaperSizeService, HttpStatusCode httpStatusCode) {
		this.reportPaperSizeService = reportPaperSizeService;
		this.httpStatusCode = httpStatusCode;
	}

	@PostMapping("/save")
	public ResponseEntity<ResponseModel<ReportPaperSize>> saveReportPaperSize(@RequestBody ReportPaperSize reportPaperSize,
			@RequestHeader("createdBy") UUID createdBy) {
		log.info("Begin ReportPaperSizeController -> saveReportPaperSize() method");
		ResponseModel<ReportPaperSize> responseModel = reportPaperSizeService.saveReportPaperSize(reportPaperSize,
				createdBy);
		log.info("End ReportPaperSizeController -> saveReportPaperSize() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(responseModel);
	}

	@PostMapping("/get-all")
	public ResponseEntity<ResponseModel<List<ReportPaperSize>>> getReportPaperSizes(
			@RequestParam(required = false) String startsWith,
			@RequestParam(defaultValue = "0") int pageNumber,
			@RequestParam(defaultValue = "10") int pageSize, @RequestParam(defaultValue = "paperSize") String sortBy,
			@RequestHeader("createdBy") UUID createdBy) {
		log.info("Begin ReportPaperSizeController -> getReportPaperSizes() method");
		ResponseModel<List<ReportPaperSize>> responseModel = reportPaperSizeService.getReportPaperSizes(startsWith,
				pageNumber, pageSize, sortBy, createdBy);
		log.info("End ReportPaperSizeController -> getReportPaperSizes() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(responseModel);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ResponseModel<ReportPaperSize>> getReportPaperSizeById(@PathVariable UUID id) {
		log.info("Begin ReportPaperSizeController -> getReportPaperSizeById() method");
		ResponseModel<ReportPaperSize> response = reportPaperSizeService.getReportPaperSizeById(id);
		log.info("End ReportPaperSizeController -> getReportPaperSizeById() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ResponseModel<ReportPaperSize>> deleteReportPaperSize(@PathVariable UUID id) {
		log.info("Begin ReportPaperSizeController -> deleteReportPaperSize() method");
		ResponseModel<ReportPaperSize> response = reportPaperSizeService.deleteReportPaperSize(id);
		log.info("End ReportPaperSizeController -> deleteReportPaperSize() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(response);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<ResponseModel<ReportPaperSize>> updateReportPaperSize(@PathVariable UUID id,
			@RequestBody ReportPaperSize updatedReportPaperSize, @RequestHeader("userId") UUID userId) {
		log.info("Begin ReportPaperSizeController -> updateReportPaperSize() method");
		ResponseModel<ReportPaperSize> responseModel = reportPaperSizeService.updateReportPaperSize(id,
				updatedReportPaperSize, userId);
		log.info("End ReportPaperSizeController -> updateReportPaperSize() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(responseModel);
	}
}
