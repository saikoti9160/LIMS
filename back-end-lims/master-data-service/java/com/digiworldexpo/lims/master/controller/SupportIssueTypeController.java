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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digiworldexpo.lims.entities.master.SupportIssueType;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.service.SupportIssueTypeService;
import com.digiworldexpo.lims.master.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/support-issue-types")
@CrossOrigin(origins = "*")
public class SupportIssueTypeController {

    private final SupportIssueTypeService supportIssueTypeService;
    
    private final HttpStatusCode httpStatusCode;

    public SupportIssueTypeController(SupportIssueTypeService supportIssueTypeService, HttpStatusCode httpStatusCode) {
        this.supportIssueTypeService = supportIssueTypeService;
        this.httpStatusCode = httpStatusCode;
    }

    @PostMapping("/save")
    public ResponseEntity<ResponseModel<SupportIssueType>> saveSupportIssueType(@RequestBody SupportIssueType supportIssueType) {
       
    	log.info("Begin SupportIssueType Controller -> saveSupportIssueType() method");
        ResponseModel<SupportIssueType> response = supportIssueTypeService.saveSupportIssueType(supportIssueType);
        log.info("End SupportIssueType Controller -> saveSupportIssueType() method");
        HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(httpStatusFromCode).body(response);
    }
    
    @PostMapping("/get-all")
    public ResponseEntity<ResponseModel<List<SupportIssueType>>> getSupportIssueTypes(
            @RequestParam(required = false) String startsWith,
            @RequestParam(required = false, defaultValue = "0") int pageNumber,
            @RequestParam(required = false, defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "name") String sortBy) {

        log.info("Begin SupportIssueType Controller -> getSupportIssueTypes() method");
        ResponseModel<List<SupportIssueType>> response = supportIssueTypeService.getSupportIssueTypes(startsWith, pageNumber, pageSize, sortBy);
        log.info("End SupportIssueType Controller -> getSupportIssueTypes() method");
        HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(httpStatusFromCode).body(response);
    }




    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<SupportIssueType>> getSupportIssueTypeById(@PathVariable UUID id) {
    	 log.info("Begin SupportIssueType Controller -> getSupportIssueTypeById() method for ID: {}", id);
         ResponseModel<SupportIssueType> response = supportIssueTypeService.getSupportIssueTypeById(id);
         log.info("End SupportIssueType Controller -> getSupportIssueTypeById() method");
         HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
         return ResponseEntity.status(httpStatusFromCode).body(response);
    }
    
    
    @PutMapping("/{id}")
    public ResponseEntity<ResponseModel<SupportIssueType>> updateSupportIssueType(@PathVariable UUID id, @RequestBody SupportIssueType updatedSupportIssueType) {
    	 log.info("Begin SupportIssueType Controller -> updateSupportIssueType() method for ID: {}", id);
         ResponseModel<SupportIssueType> response = supportIssueTypeService.updateSupportIssueType(id, updatedSupportIssueType);
         log.info("End SupportIssueType Controller -> updateSupportIssueType() method");
         HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
         return ResponseEntity.status(httpStatusFromCode).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<SupportIssueType>> deleteSupportIssueType(@PathVariable UUID id) {
    	 log.info("Begin SupportIssueType Controller -> deleteSupportIssueType() method for ID: {}", id);
         ResponseModel<SupportIssueType> response = supportIssueTypeService.deleteSupportIssueType(id);
         log.info("End SupportIssueType Controller -> deleteSupportIssueType() method");
         HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
         return ResponseEntity.status(httpStatusFromCode).body(response);
    }
}