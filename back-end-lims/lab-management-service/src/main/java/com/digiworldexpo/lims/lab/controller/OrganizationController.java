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
import com.digiworldexpo.lims.lab.request.OrganizationRequestDTO;
import com.digiworldexpo.lims.lab.response.DoctorMasterResponseDTO;
import com.digiworldexpo.lims.lab.response.OrganizationResponseDTO;
import com.digiworldexpo.lims.lab.response.OrganizationSearchResponseDTO;
import com.digiworldexpo.lims.lab.service.OrganizationService;
import com.digiworldexpo.lims.lab.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/organization")
public class OrganizationController {
    private final OrganizationService organizationService;
    private final HttpStatusCode httpStatusCode;

    public OrganizationController(OrganizationService organizationService, HttpStatusCode httpStatusCode) {
        super();
        this.organizationService = organizationService;
        this.httpStatusCode = httpStatusCode;
    }

    @PostMapping("/save")
    public ResponseEntity<ResponseModel<OrganizationResponseDTO>> saveOrganization(
            @RequestHeader("createdBy") UUID createdBy,
            @RequestBody OrganizationRequestDTO organizationDTO) {
        log.info("Begin Organization Controller -> save() method");
        ResponseModel<OrganizationResponseDTO> save = organizationService.saveOrganization(createdBy, organizationDTO);
        log.info("End Organization Controller -> save() method");
        HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(save.getStatusCode());
        return ResponseEntity.status(httpStatus).body(save);
    }
    
    @GetMapping("/get-all")
	 public ResponseEntity<ResponseModel<List<OrganizationSearchResponseDTO>>> getAllOrganization(
			    @RequestParam(required = false) UUID createdBy,
		        @RequestParam(required = false) String keyword,
		        @RequestParam(required = false) Boolean flag,
		        @RequestParam(defaultValue = "0") Integer pageNumber,
		        @RequestParam(defaultValue = "10") Integer pageSize) {
	     log.info("Begin Organization Controller -> getAllOrganization() method");

	     ResponseModel<List<OrganizationSearchResponseDTO>> response = organizationService.getAllOrganization(createdBy,keyword, flag, pageNumber, pageSize);

	     log.info("End Organization Controller -> getAllOrganization() method");
	     HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
	     return ResponseEntity.status(httpStatusFromCode).body(response);
	 }
    
    @GetMapping("/get/{id}")
	public ResponseEntity<ResponseModel<OrganizationResponseDTO>> getOrganizationById(@PathVariable("id") UUID id) {
	    log.info("Begin Organization -> getOrganizationById() method");
	    ResponseModel<OrganizationResponseDTO> response = organizationService.getOrganizationById(id);
	    log.info("End Organization -> getOrganizationById() method");
	    HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
	    return ResponseEntity.status(httpStatusFromCode).body(response);
	}
    
    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseModel<OrganizationResponseDTO>> updateOrganizationById(@PathVariable("id") UUID id, 
                                                                          @RequestBody OrganizationRequestDTO organizationRequestDTO) {
        log.info("Begin Organization Controller -> updateOrganizationById() method");
        ResponseModel<OrganizationResponseDTO> response = organizationService.updateOrganizationById(id,organizationRequestDTO);
        log.info("End Organization Controller -> updateOrganizationById() method");
        HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(httpStatusFromCode).body(response);
    }
    
    
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResponseModel<OrganizationResponseDTO>> deleteOrganizationById(@PathVariable("id") UUID id) {
		log.info("Begin Organization Controller -> deleteOrganizationById() method");
		ResponseModel<OrganizationResponseDTO> response = organizationService.deleteOrganizationById(id);
		log.info("End Organization Controller -> deleteOrganizationById() method");
		HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		return ResponseEntity.status(httpStatusFromCode).body(response);
	}
    
    

    
    
}
