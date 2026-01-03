//package com.digiworldexpo.lims.master.service;
//
//import java.util.List;
//import java.util.UUID;
//
//import com.digiworldexpo.lims.entities.master.Module;
//import com.digiworldexpo.lims.master.model.response.ResponseModel;
//
//public interface ModuleService {
//
//	ResponseModel<Module> saveModule(Module module, UUID createdBy);
//
//	ResponseModel<Module> getModuleById(UUID id);
//
//	ResponseModel<List<Module>> getAllModules(String startsWith, UUID createdBy, int pageNumber, int pageSize, String sortedBy);
//
//	ResponseModel<Module> updateModuleById(UUID id, Module newModuleData, UUID modifiedBy);
//
//	ResponseModel<Module> deleteModuleById(UUID id);
//
//}
