package com.digiworldexpo.lims.master.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.digiworldexpo.lims.entities.master.ReportFontType;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.service.ReportFontTypeService;
import com.digiworldexpo.lims.master.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/font-type")
public class ReportFontTypeController {

	private final ReportFontTypeService reportFontTypeService;
	private final HttpStatusCode httpStatusCode;

	public ReportFontTypeController(ReportFontTypeService reportFontTypeService, HttpStatusCode httpStatusCode) {
		this.reportFontTypeService = reportFontTypeService;
		this.httpStatusCode = httpStatusCode;
	}

	@PostMapping("/save")
	public ResponseEntity<ResponseModel<ReportFontType>> saveFontType(@RequestBody ReportFontType fontType,
			@RequestHeader("createdBy") UUID createdBy) {
		log.info("Begin ReportFontTypeController -> saveFontType() method");
		ResponseModel<ReportFontType> responseModel = reportFontTypeService.saveFontType(fontType, createdBy);
		log.info("End ReportFontTypeController -> saveFontType() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(responseModel);
	}

	@PostMapping("/get-all")
	public ResponseEntity<ResponseModel<List<ReportFontType>>> getFontTypes(@RequestParam(required = false) String searchTearm,
			@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize,
			@RequestParam(defaultValue = "fontType") String sortBy, @RequestHeader("createdBy") UUID createdBy) {
		log.info("Begin ReportFontTypeController -> getFontTypes() method");
		ResponseModel<List<ReportFontType>> responseModel = reportFontTypeService.getFontTypes(searchTearm, pageNumber, pageSize,
				sortBy, createdBy);
		log.info("End ReportFontTypeController -> getFontTypes() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(responseModel);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ResponseModel<ReportFontType>> getFontTypeById(@PathVariable UUID id) {
		log.info("Begin ReportFontTypeController -> getFontTypeById() method");
		ResponseModel<ReportFontType> response = reportFontTypeService.getFontTypeById(id);
		log.info("End ReportFontTypeController -> getFontTypeById() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(response);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResponseModel<ReportFontType>> deleteFontType(@PathVariable UUID id) {
		log.info("Begin ReportFontTypeController -> deleteFontType() method");
		ResponseModel<ReportFontType> response = reportFontTypeService.deleteFontType(id);
		log.info("End ReportFontTypeController -> deleteFontType() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(response);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<ResponseModel<ReportFontType>> updateFontType(@PathVariable UUID id,
			@RequestBody ReportFontType updatedFontType, @RequestHeader("userId") UUID userId) {
		log.info("Begin ReportFontTypeController -> updateFontType() method");
		ResponseModel<ReportFontType> responseModel = reportFontTypeService.updateFontType(id, updatedFontType, userId);
		log.info("End ReportFontTypeController -> updateFontType() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(responseModel);
	}
}
