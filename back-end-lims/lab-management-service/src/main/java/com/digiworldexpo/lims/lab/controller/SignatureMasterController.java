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

import com.digiworldexpo.lims.entities.lab_management.SignatureMaster;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.request.SignatureMasterRequestDTO;
import com.digiworldexpo.lims.lab.response.SignatureMasterResponseDTO;
import com.digiworldexpo.lims.lab.response.SignatureMasterSearchResponse;
import com.digiworldexpo.lims.lab.service.SignatureMasterService;
import com.digiworldexpo.lims.lab.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/signature")
public class SignatureMasterController {

    private final SignatureMasterService signatureMasterService;
    private final HttpStatusCode httpStatusCode;

    public SignatureMasterController(SignatureMasterService signatureMasterService, HttpStatusCode httpStatusCode) {
        this.signatureMasterService = signatureMasterService;
        this.httpStatusCode = httpStatusCode;
    }

    @PostMapping("/save")
    public ResponseEntity<ResponseModel<SignatureMasterResponseDTO>> saveSignature(
    		@RequestHeader("createdBy") UUID createdBy,
            @RequestBody SignatureMasterRequestDTO signatureMasterDTO) {
        log.info("Begin SignatureMasterController -> saveSignature() method");
        ResponseModel<SignatureMasterResponseDTO> saveSignature = signatureMasterService.saveSignature(createdBy, signatureMasterDTO);
        log.info("End SignatureMasterController -> saveSignature() method");
        HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(saveSignature.getStatusCode());
        return ResponseEntity.status(httpStatusFromCode).body(saveSignature);
    }

    
    @GetMapping("/get/{id}")
    public ResponseEntity<ResponseModel<SignatureMasterResponseDTO>> getSignatureById(@PathVariable("id") UUID id) {
        log.info("Begin SignatureMaster Controller -> getSignatureById() method");
        ResponseModel<SignatureMasterResponseDTO> signatureMaster = signatureMasterService.getSignatureById(id);
        log.info("End SignatureMaster Controller -> getSignatureById() method");
        HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(signatureMaster.getStatusCode());
        return ResponseEntity.status(httpStatusFromCode).body(signatureMaster);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseModel<SignatureMasterResponseDTO>> updateSignature(@PathVariable("id") UUID id, @RequestBody SignatureMasterRequestDTO signatureMasterDTO) {
        log.info("Begin SignatureMaster Controller -> updateSignature() method");
        ResponseModel<SignatureMasterResponseDTO> updatedSignature = signatureMasterService.updateSignature(id, signatureMasterDTO);
        log.info("End SignatureMaster Controller -> updateSignature() method");
        HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(updatedSignature.getStatusCode());
        return ResponseEntity.status(httpStatusFromCode).body(updatedSignature);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseModel<SignatureMaster>> deleteSignature(@PathVariable("id") UUID id) {
        log.info("Begin SignatureMaster Controller -> deleteSignature() method");
        ResponseModel<SignatureMaster> response = signatureMasterService.deleteSignature(id);
        log.info("End SignatureMaster Controller -> deleteSignature() method");
        HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(httpStatusFromCode).body(response);
    }
    
    @GetMapping("/get-all")
    public ResponseEntity<ResponseModel<List<SignatureMasterSearchResponse>>> getAllSignatures(
    		    @RequestParam(required = true) UUID createdBy,
		        @RequestParam(required = false) String keyword,
		        @RequestParam(required = false) Boolean flag,
		        @RequestParam(defaultValue = "0") Integer pageNumber,
		        @RequestParam(defaultValue = "10") Integer pageSize) {

        log.info("Begin SignatureMasterController -> getAllSignatures() method");
        ResponseModel<List<SignatureMasterSearchResponse>> response = signatureMasterService.getAllSignature(createdBy, keyword, flag, pageNumber, pageSize);
        log.info("End SignatureMasterController -> getAllSignatures() method");
        HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(httpStatusFromCode).body(response);
    }

}
