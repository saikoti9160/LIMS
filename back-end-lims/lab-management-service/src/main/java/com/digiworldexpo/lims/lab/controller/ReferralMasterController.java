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
import com.digiworldexpo.lims.lab.request.ReferralMasterRequestDTO;
import com.digiworldexpo.lims.lab.response.ReferralMasterResponseDTO;
import com.digiworldexpo.lims.lab.response.ReferralMasterSearchResponse;
import com.digiworldexpo.lims.lab.service.ReferralMasterService;
import com.digiworldexpo.lims.lab.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/referral")
public class ReferralMasterController {

    private final ReferralMasterService referralMasterService;
    private final HttpStatusCode httpStatusCode;

    public ReferralMasterController(ReferralMasterService referralMasterService, HttpStatusCode httpStatusCode) {
        this.referralMasterService = referralMasterService;
        this.httpStatusCode = httpStatusCode;
    }

    @PostMapping("/save")
    public ResponseEntity<ResponseModel<ReferralMasterResponseDTO>> saveReferralMaster(
            @RequestHeader("createdBy") UUID createdBy,
            @RequestBody ReferralMasterRequestDTO referralMasterRequestDTO) {
        log.info("Begin ReferralMasterController -> saveReferralMaster() method userId = {}", createdBy);
        ResponseModel<ReferralMasterResponseDTO> response = referralMasterService.saveReferralMaster(createdBy, referralMasterRequestDTO);
        log.info("End ReferralMasterController -> saveReferralMaster() method");	
        HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(httpStatus).body(response);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseModel<ReferralMasterResponseDTO>> getReferralById(@PathVariable("id") UUID id) {
        log.info("Begin ReferralMasterController -> getReferralById() method");
        ResponseModel<ReferralMasterResponseDTO> response = referralMasterService.getReferralById(id);
        log.info("End ReferralMasterController -> getReferralById() method");
        HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(httpStatusFromCode).body(response);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseModel<ReferralMasterResponseDTO>> updateReferralMaster(
            @PathVariable("id") UUID id,
            @RequestBody ReferralMasterRequestDTO referralMasterRequestDTO) {
        log.info("Begin ReferralMasterController -> updateReferralMaster() method");
        ResponseModel<ReferralMasterResponseDTO> response = referralMasterService.updateReferralMaster(id, referralMasterRequestDTO);
        log.info("End ReferralMasterController -> updateReferralMaster() method");
        HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(httpStatusFromCode).body(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseModel<ReferralMasterResponseDTO>> deleteReferralMaster(@PathVariable("id") UUID id) {
        log.info("Begin ReferralMasterController -> deleteReferralMaster() method");
        ResponseModel<ReferralMasterResponseDTO> response = referralMasterService.deleteReferralMaster(id);
        log.info("End ReferralMasterController -> deleteReferralMaster() method");
        HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(httpStatusFromCode).body(response);
    }

    @GetMapping("/get-All")
    public ResponseEntity<ResponseModel<List<ReferralMasterSearchResponse>>> getAllReferrals(
    		    @RequestParam(required = true) UUID createdBy,
		        @RequestParam(required = false) String keyword,
		        @RequestParam(required = false) Boolean flag,
		        @RequestParam(defaultValue = "0") Integer pageNumber,
		        @RequestParam(defaultValue = "10") Integer pageSize) {

        log.info("Begin ReferralMasterController -> getAllReferrals() method");
        ResponseModel<List<ReferralMasterSearchResponse>> response = referralMasterService.getAllReferrals(createdBy, keyword, flag, pageNumber, pageSize);
        log.info("End ReferralMasterController -> getAllReferrals() method");
        HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(httpStatusFromCode).body(response);
    }


}
