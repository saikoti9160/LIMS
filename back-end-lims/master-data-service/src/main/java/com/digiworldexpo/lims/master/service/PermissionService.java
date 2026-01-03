package com.digiworldexpo.lims.master.service;
import java.util.List;

import com.digiworldexpo.lims.entities.master.Permission;

public interface PermissionService {

    Permission savePermission(Permission permission);

    List<Permission> savePermissions(List<Permission> permissions);
}