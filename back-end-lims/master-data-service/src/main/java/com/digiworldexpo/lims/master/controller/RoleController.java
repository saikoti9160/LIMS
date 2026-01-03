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

import com.digiworldexpo.lims.entities.master.Role;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.service.RoleService;
import com.digiworldexpo.lims.master.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/role")
@Slf4j
@CrossOrigin(origins ="*")
public class RoleController {

	private final RoleService roleService;
	private final HttpStatusCode httpStatusCode;
	
	public RoleController(RoleService roleService, HttpStatusCode httpStatusCode) {
		this.roleService = roleService;
		this.httpStatusCode = httpStatusCode;
	}
	
	@PostMapping("/save")
	public ResponseEntity<ResponseModel<Role>> saveRole(@RequestBody Role role, @RequestParam("createdBy") UUID createdBy){
		log.info("Begin of Role Controller -> saveRole() method");
		ResponseModel<Role> responseModel = roleService.saveRole(role, createdBy);
		log.info("End of Role Controller -> saveRole() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
	
	@GetMapping("/get/{id}")
	public ResponseEntity<ResponseModel<Role>> getRoleById(@PathVariable UUID id){
		log.info("Begin of Role Controller -> getRoleById() method");
		ResponseModel<Role> responseModel = roleService.getRoleById(id);
		log.info("End of Role Controller -> getRoleById() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
	
	@PostMapping("/get-all")
	public ResponseEntity<ResponseModel<List<Role>>> getAllRoles(@RequestParam(required = false) String startsWith, @RequestParam(required = false) Boolean status,
			@RequestParam(required = false) UUID labId,
			@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize, 
			@RequestParam(required = false, defaultValue = "roleName") String sortedBy){
		log.info("Begin of Role Controller -> getAllRoles() method");
		ResponseModel<List<Role>> responseModel = roleService.getAllRoles(startsWith, status, labId, pageNumber, pageSize, sortedBy);
		log.info("End of Role Controller -> getAllRoles() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<ResponseModel<Role>> updateRoleById(@PathVariable UUID id, @RequestBody Role newRoleData, @RequestParam("modifiedBy") UUID modifiedBy){
		log.info("Begin of Role Controller -> updateRoleById() method");
		ResponseModel<Role> responseModel = roleService.updateRoleById(id, newRoleData, modifiedBy);
		log.info("End of Role Controller -> updateRoleById() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResponseModel<Role>> deleteRoleById(@PathVariable UUID id){
		log.info("Begin of Role Controller -> deleteRoleById() method");
		ResponseModel<Role> responseModel = roleService.deleteRoleById(id);
		log.info("End of Role Controller -> deleteRoleById() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
}