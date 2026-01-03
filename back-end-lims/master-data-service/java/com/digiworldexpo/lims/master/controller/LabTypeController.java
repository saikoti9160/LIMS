package com.digiworldexpo.lims.master.controller;

import java.util.List;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.digiworldexpo.lims.entities.master.LabType;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.service.LabTypeService;
import com.digiworldexpo.lims.master.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/lab-type")
@CrossOrigin(origins = "*")
public class LabTypeController {

    private final LabTypeService labTypeService;
    private final HttpStatusCode httpStatusCode;

    public LabTypeController(LabTypeService labTypeService, HttpStatusCode httpStatusCode) {
        this.labTypeService = labTypeService;
        this.httpStatusCode = httpStatusCode;
    }

    @PostMapping("/save")
    public ResponseEntity<ResponseModel<LabType>> saveLabType(@RequestBody LabType labType,
            @RequestHeader("createdBy") UUID createdBy) {
        log.info("Begin LabTypeController -> saveLabType() method");
        ResponseModel<LabType> responseModel = labTypeService.saveLabType(labType, createdBy);
        log.info("End LabTypeController -> saveLabType() method");
        HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
        return ResponseEntity.status(httpStatusFromCode).body(responseModel);
    }
    
    @PostMapping("/get-all")
    public ResponseEntity<ResponseModel<List<LabType>>> getLabTypes(
            @RequestParam(required = false) String startsWith, 
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize, 
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestHeader("createdBy") UUID createdBy) {
        log.info("Begin LabTypeController -> getLabTypes() method");
        ResponseModel<List<LabType>> responseModel = labTypeService.getLabTypes(startsWith, pageNumber,
                pageSize, sortBy, createdBy);
        log.info("End LabTypeController -> getLabTypes() method");
        HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
        return ResponseEntity.status(httpStatusFromCode).body(responseModel);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseModel<LabType>> updateLabType(@PathVariable UUID id,
            @RequestBody LabType updatedLabType, @RequestHeader("userId") UUID userId) {
        log.info("Begin LabTypeController -> updateLabType() method");
        ResponseModel<LabType> responseModel = labTypeService.updateLabType(id, updatedLabType, userId);
        log.info("End LabTypeController -> updateLabType() method");
        HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
        return ResponseEntity.status(httpStatusFromCode).body(responseModel);
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<ResponseModel<LabType>> getLabTypeById(@PathVariable UUID id) {
        log.info("Begin LabType Controller -> getLabTypeById() method");
        ResponseModel<LabType> response = labTypeService.getLabTypeById(id);
        log.info("End LabType Controller -> getLabTypeById() method");
        HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(httpStatusFromCode).body(response);
    }

  
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseModel<LabType>> deleteLabType(@PathVariable UUID id) {
        log.info("Begin LabType Controller -> deleteLabType() method");
        ResponseModel<LabType> response = labTypeService.deleteLabType(id);
        log.info("End LabType Controller -> deleteLabType() method");
        HttpStatus httpStatusFromCode = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
        return ResponseEntity.status(httpStatusFromCode).body(response);
    }
}
