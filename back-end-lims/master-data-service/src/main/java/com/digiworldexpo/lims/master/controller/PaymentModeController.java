package com.digiworldexpo.lims.master.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digiworldexpo.lims.entities.master.PaymentMode;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.service.PaymentModeService;
import com.digiworldexpo.lims.master.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/paymentMode")
@Slf4j
public class PaymentModeController {
	
	private final PaymentModeService paymentModeService;
	private final HttpStatusCode httpStatusCode;
	
	public PaymentModeController(PaymentModeService paymentModeService, HttpStatusCode httpStatusCode) {
		this.paymentModeService = paymentModeService;
		this.httpStatusCode = httpStatusCode; 
	}

	@PostMapping("/save")
	public ResponseEntity<ResponseModel<PaymentMode>> addPaymentMode(@RequestBody PaymentMode paymentMode, @RequestParam("createdBy") UUID createdBy ) {
	    log.info("Begin of Payment Mode Controller -> addPaymentMode() method");
	    ResponseModel<PaymentMode> responseModel = paymentModeService.addPaymentMode(paymentMode, createdBy);
	    log.info("End of Payment Mode Controller -> addPaymentMode() method");
	    return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode()))
	                         .body(responseModel);
	}

	@PostMapping("/get-all")
	public ResponseEntity<ResponseModel<List<PaymentMode>>> getAllPaymentModes(@RequestParam(required = false) String startsWith, 
			@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize, @RequestParam(required = false, defaultValue = "paymentModeName") String sortedBy){
	    log.info("Begin of Payment Mode Controller -> getAllPaymentModes() method");
	    ResponseModel<List<PaymentMode>> responseModel = paymentModeService.getAllPaymentModes(startsWith, pageNumber, pageSize, sortedBy);
	    log.info("End of Payment Mode Controller -> getAllPaymentModes() method");
	    return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode()))
	                         .body(responseModel);
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<ResponseModel<PaymentMode>> updatePaymentModeById(@PathVariable UUID id, @RequestBody PaymentMode paymentMode, @RequestParam("modifiedBy") UUID modifiedBy){
		log.info("Begin of Payment Mode Controller -> updatePaymentModeById() method");
		ResponseModel<PaymentMode> responseModel = paymentModeService.updatePaymentModeById(id, paymentMode, modifiedBy);
		log.info("Begin of Payment Mode Controller -> updatePaymentModeById() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
	
	@GetMapping("/get/{id}")
	public ResponseEntity<ResponseModel<PaymentMode>> getPaymentModeById(@PathVariable UUID id){
		log.info("Begin of Payment Mode Controller -> getPaymentModeById() method");
		ResponseModel<PaymentMode> responseModel = paymentModeService.getPaymentModeById(id);
		log.info("Begin of Payment Mode Controller -> getPaymentModeById() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResponseModel<PaymentMode>> deletePaymentModeById(@PathVariable UUID id){
		log.info("Begin of Payment Mode Controller -> deletePaymentModeById() method");
		ResponseModel<PaymentMode> responseModel = paymentModeService.deletePaymentModeById(id);
		log.info("Begin of Payment Mode Controller -> deletePaymentModeById() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
}