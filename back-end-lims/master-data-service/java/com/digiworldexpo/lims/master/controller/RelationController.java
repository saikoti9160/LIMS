package com.digiworldexpo.lims.master.controller;


import java.util.List;
import java.util.UUID;

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

import com.digiworldexpo.lims.entities.master.Relation;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.service.RelationService;
import com.digiworldexpo.lims.master.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/relation")
@Slf4j
@CrossOrigin
public class RelationController {
	private final RelationService relationService;
	private final HttpStatusCode httpStatusCode;

	public RelationController(RelationService relationService, HttpStatusCode httpStatusCode) {
		this.relationService = relationService;
		this.httpStatusCode = httpStatusCode;
	}

	@PostMapping("/save")
	public ResponseEntity<ResponseModel<Relation>> addRelation(@RequestBody Relation relation, @RequestParam("createdBy") UUID createdBy) {
	    log.info("Begin of User Type Controller -> addRelation() method");
	    ResponseModel<Relation> responseModel = relationService.addRelation(relation, createdBy);
	    log.info("End of User Type Controller -> addRelation() method");
	    return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode()))
	                         .body(responseModel);
	}

	@PostMapping("/get-all")
	public ResponseEntity<ResponseModel<List<Relation>>> getAllRelations(@RequestParam(required = false) String startsWith, 
			@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize, 
			@RequestParam(required = false, defaultValue = "relationName") String sortedBy) {
		
	    log.info("Begin of User Type Controller -> getAllRelations() method");
	    ResponseModel<List<Relation>> responseModel = relationService.getAllRelations(startsWith, pageNumber, pageSize, sortedBy);
	    log.info("End of User Type Controller -> getAllRelations() method");
	    return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode()))
	                         .body(responseModel);
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<ResponseModel<Relation>> updateRelationById(@PathVariable UUID id, @RequestBody Relation relation, @RequestParam("modifiedBy") UUID modifiedBy){
		log.info("Begin of User Type Controller -> updateRelationById() method");
		log.info("dfhjkhas {}", relation.getRelationName());
		ResponseModel<Relation> responseModel = relationService.updateRelationById(id, relation, modifiedBy);
		log.info("Begin of User Type Controller -> updateRelationById() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
	
	@GetMapping("/get/{id}")
	public ResponseEntity<ResponseModel<Relation>> getRelationById(@PathVariable UUID id){
		log.info("Begin of User Type Controller -> getRelationById() method");
		ResponseModel<Relation> responseModel = relationService.getRelationById(id);
		log.info("Begin of User Type Controller -> getRelationById() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResponseModel<Relation>> deleteRelationById(@PathVariable UUID id){
		log.info("Begin of User Type Controller -> deleteRelationById() method");
		ResponseModel<Relation> responseModel = relationService.deleteRelationById(id);
		log.info("Begin of User Type Controller -> deleteRelationById() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
}
