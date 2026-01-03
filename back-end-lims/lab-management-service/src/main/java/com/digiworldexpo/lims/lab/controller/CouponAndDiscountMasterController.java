package com.digiworldexpo.lims.lab.controller;

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

import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.request.CouponAndDiscountMasterRequestDTO;
import com.digiworldexpo.lims.lab.response.CouponAndDiscountMasterResponseDTO;
import com.digiworldexpo.lims.lab.response.CouponAndDiscountMasterSearch;
import com.digiworldexpo.lims.lab.service.CouponAndDiscountMasterService;
import com.digiworldexpo.lims.lab.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/coupon")
public class CouponAndDiscountMasterController {
		
	private final CouponAndDiscountMasterService couponAndDiscountMasterService;  
	private final HttpStatusCode httpStatusCode;
	public CouponAndDiscountMasterController(CouponAndDiscountMasterService couponAndDiscountMasterService,
			HttpStatusCode httpStatusCode) {
		super();
		this.couponAndDiscountMasterService = couponAndDiscountMasterService;
		this.httpStatusCode = httpStatusCode;
	}
	
	@PostMapping("/save")
	public ResponseEntity<ResponseModel<CouponAndDiscountMasterResponseDTO>> save(
			@RequestHeader("createdBy") UUID createdBy,
			@RequestBody CouponAndDiscountMasterRequestDTO couponAndDiscountMasterRequestDTO) {
		log.info("Begin CouponAndDiscountMaster Controller -> save() method =" + createdBy);
		ResponseModel<CouponAndDiscountMasterResponseDTO> save = couponAndDiscountMasterService.save(createdBy,
				couponAndDiscountMasterRequestDTO);
		log.info("End CouponAndDiscountMaster Controller -> save() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(save.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(save);
	}

	@GetMapping("/get-all")
	public ResponseEntity<ResponseModel<List<CouponAndDiscountMasterSearch>>> getAllCouponAndDiscount(
			@RequestParam(required = false) String searchTerm, @RequestParam(required = false) Boolean flag,
			@RequestParam(required = false) UUID createdBy, @RequestParam(defaultValue = "0") Integer pageNumber,
			@RequestParam(defaultValue = "10") Integer pageSize) {

		log.info("Begin CouponAndDiscountMaster Controller -> getAll() method");
		ResponseModel<List<CouponAndDiscountMasterSearch>> response = couponAndDiscountMasterService
				.getAllCouponAndDiscount(searchTerm, flag, createdBy, pageNumber, pageSize);
		log.info("End CouponAndDiscountMaster Controller -> getAll() method");
		HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		return ResponseEntity.status(httpStatus).body(response);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<ResponseModel<CouponAndDiscountMasterResponseDTO>> updateById(@PathVariable("id") UUID id,
			@RequestBody CouponAndDiscountMasterRequestDTO couponAndDiscountMasterDTO) {
		log.info("Begin CouponAndDiscountMaster Controller -> update() method");
		ResponseModel<CouponAndDiscountMasterResponseDTO> updateResponse = couponAndDiscountMasterService.updateById(id,
				couponAndDiscountMasterDTO);
		log.info("End CouponAndDiscountMaster Controller -> update() method");
		HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(updateResponse.getStatusCode());
		return ResponseEntity.status(httpStatus).body(updateResponse);
	}

	@GetMapping("/get/{id}")
	public ResponseEntity<ResponseModel<CouponAndDiscountMasterResponseDTO>> getById(@PathVariable("id") UUID id) {
		log.info("Begin CouponAndDiscountMaster Controller -> getById() method");
		ResponseModel<CouponAndDiscountMasterResponseDTO> response = couponAndDiscountMasterService.getById(id);
		log.info("End CouponAndDiscountMaster Controller -> getById() method");
		HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		return ResponseEntity.status(httpStatus).body(response);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResponseModel<CouponAndDiscountMasterResponseDTO>> deleteById(@PathVariable("id") UUID id) {
		log.info("Begin CouponAndDiscountMaster Controller -> delete() method");
		ResponseModel<CouponAndDiscountMasterResponseDTO> response = couponAndDiscountMasterService.deleteById(id);
		log.info("End CouponAndDiscountMaster Controller -> delete() method");
		HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		return ResponseEntity.status(httpStatus).body(response);
	}

}
