package com.digiworldexpo.lims.lab.controller;

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

import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.request.DiscountMasterRequestDTO;
import com.digiworldexpo.lims.lab.response.DiscountMasterResponseDTO;
import com.digiworldexpo.lims.lab.response.DiscountMasterSearch;
import com.digiworldexpo.lims.lab.service.DiscountMasterService;
import com.digiworldexpo.lims.lab.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/discount")
@CrossOrigin
public class DiscountMasterController {

	private final DiscountMasterService discountMasterService;
	private final HttpStatusCode httpStatusCode;

	public DiscountMasterController(DiscountMasterService discountMasterService, HttpStatusCode httpStatusCode) {
		super();
		this.discountMasterService = discountMasterService;
		this.httpStatusCode = httpStatusCode;
	}

	@PostMapping("/save")
	public ResponseEntity<ResponseModel<DiscountMasterResponseDTO>> saveDiscountMaster(@RequestHeader("createdBy") UUID createdBy,
			@RequestBody DiscountMasterRequestDTO discountMasterDTO) {
		log.info("Begin DiscountMasterController -> saveDiscountMaster() method ");
		ResponseModel<DiscountMasterResponseDTO> saveDiscountMaster = discountMasterService
				.saveDiscountMaster(createdBy,discountMasterDTO);
		log.info("End DiscountMasterController -> saveDiscountMaster() method ");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(saveDiscountMaster.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(saveDiscountMaster);
	}

	@GetMapping("/get/all")
	public ResponseEntity<ResponseModel<List<DiscountMasterSearch>>> getAllDiscounts(
			@RequestParam(required = true) UUID createdBy,
	        @RequestParam(required = false) String keyword,
	        @RequestParam(required = false) Boolean flag,
	        @RequestParam(defaultValue = "0") Integer pageNumber,
	        @RequestParam(defaultValue = "10") Integer pageSize) {
	    log.info("Begin DiscountMaster Controller -> getAllDiscounts() method");
	    ResponseModel<List<DiscountMasterSearch>> responseModel = discountMasterService.getAllDiscounts(createdBy,keyword,flag, pageNumber, pageSize);
	    log.info("End DiscountMaster Controller -> getAllDiscounts() method");
	    HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
	    return ResponseEntity.status(httpStatusFromCode).body(responseModel);
	}


	@GetMapping("/get/{id}")
	public ResponseEntity<ResponseModel<DiscountMasterResponseDTO>> getDiscountById(@PathVariable("id") UUID id) {
		log.info("Begin DiscountMaster Controller -> getDiscountById() method");
		ResponseModel<DiscountMasterResponseDTO> discountMaster = discountMasterService.getDiscountById(id);
		log.info("End DiscountMaster Controller -> getDiscountById() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(discountMaster.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(discountMaster);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<ResponseModel<DiscountMasterResponseDTO>> updateDiscount(@PathVariable("id") UUID id,
			@RequestBody DiscountMasterRequestDTO discountMasterRequestDto) {
		log.info("Begin DiscountMaster Controller -> updateDiscount() method");
		ResponseModel<DiscountMasterResponseDTO> updatedDiscount = discountMasterService.updateDiscount(id, discountMasterRequestDto);
		log.info("End DiscountMaster Controller -> updateDiscount() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(updatedDiscount.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(updatedDiscount);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResponseModel<DiscountMasterResponseDTO>> deleteDiscount(@PathVariable("id") UUID id) {
		log.info("Begin DiscountMaster Controller -> deleteDiscount() method");
		ResponseModel<DiscountMasterResponseDTO> response = discountMasterService.deleteDiscount(id);
		log.info("End DiscountMaster Controller -> deleteDiscount() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(response);
	}
}
