//package com.digiworldexpo.lims.master.controller;
//
//import java.util.List;
//import java.util.UUID;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.DeleteMapping;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.digiworldexpo.lims.entities.master.Module;
//import com.digiworldexpo.lims.master.model.response.ResponseModel;
//import com.digiworldexpo.lims.master.service.ModuleService;
//import com.digiworldexpo.lims.master.util.HttpStatusCode;
//
//import lombok.extern.slf4j.Slf4j;
//
//@RestController
//@RequestMapping("/module")
//@Slf4j
//public class ModuleController {
//	
//	private final ModuleService moduleService;
//	private final HttpStatusCode httpStatusCode;
//	
//	public ModuleController(ModuleService moduleService, HttpStatusCode httpStatusCode) {
//		this.moduleService = moduleService;
//		this.httpStatusCode = httpStatusCode;
//	}
//
//	@PostMapping("/save")
//	public ResponseEntity<ResponseModel<Module>> saveModule(@RequestBody Module module, @RequestParam(required = false, name="createdBy") UUID createdBy){
//		log.info("Begin of Module Controller -> saveModule() method");
//		ResponseModel<Module> responseModel = moduleService.saveModule(module, createdBy);
//		log.info("End of Module Controller -> saveModule() method");
//		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
//	}
//	
//	@GetMapping("/get/{id}")
//	public ResponseEntity<ResponseModel<Module>> getModuleById(@PathVariable UUID id){
//		log.info("Begin of Module Controller -> getModuleById() method");
//		ResponseModel<Module> responseModel = moduleService.getModuleById(id);
//		log.info("End of Module Controller -> getModuleById() method");
//		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
//	}
//	
//	@PostMapping("/get-all")
//	public ResponseEntity<ResponseModel<List<Module>>> getAllModules(@RequestParam(required = false) String startsWith, @RequestParam(required = false) UUID createdBy,
//			@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize, 
//			@RequestParam(required = false, defaultValue = "moduleName") String sortedBy){
//		log.info("Begin of Module Controller -> getAllModules() method");
//		ResponseModel<List<Module>> responseModel = moduleService.getAllModules(startsWith, createdBy, pageNumber, pageSize, sortedBy);
//		log.info("End of Module Controller -> getAllModules() method");
//		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
//	}
//	
//	@PutMapping("/update/{id}")
//	public ResponseEntity<ResponseModel<Module>> updateModuleById(@PathVariable UUID id, @RequestBody Module newModuleData, @RequestParam(required = false, name="modifiedBy") UUID modifiedBy){
//		log.info("Begin of Module Controller -> updateModuleById() method");
//		ResponseModel<Module> responseModel = moduleService.updateModuleById(id, newModuleData, modifiedBy);
//		log.info("End of Module Controller -> updateModuleById() method");
//		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
//	}
//	
//	@DeleteMapping("/delete/{id}")
//	public ResponseEntity<ResponseModel<Module>> deleteModuleById(@PathVariable UUID id){
//		log.info("Begin of Module Controller -> deleteModuleById() method");
//		ResponseModel<Module> responseModel = moduleService.deleteModuleById(id);
//		log.info("End of Module Controller -> deleteModuleById() method");
//		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
//	}
//}
