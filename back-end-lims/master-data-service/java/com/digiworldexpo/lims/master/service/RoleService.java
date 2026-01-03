package com.digiworldexpo.lims.master.service;

import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.entities.master.Role;
import com.digiworldexpo.lims.master.model.response.ResponseModel;

public interface RoleService {

	ResponseModel<Role> saveRole(Role role, UUID createdBy);

	ResponseModel<Role> getRoleById(UUID id);

	ResponseModel<List<Role>> getAllRoles(String startsWith, Boolean status, UUID labId, int pageNumber, int pageSize, String sortedBy);

	ResponseModel<Role> updateRoleById(UUID id, Role newRoleData, UUID modifiedBy);

	ResponseModel<Role> deleteRoleById(UUID id);

}
