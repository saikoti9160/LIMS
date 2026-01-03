package com.digiworldexpo.lims.master.service.serviceImpl;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digiworldexpo.lims.entities.master.Permission;
import com.digiworldexpo.lims.master.repository.PermissionRepository;
import com.digiworldexpo.lims.master.service.PermissionService;
import com.digiworldexpo.lims.entities.master.Module;

@Service
@Transactional  // Transactional at the service level is generally good practice
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public Permission savePermission(Permission permission) {
        validateModuleName(permission.getModuleName());

        if (permission.getId() == null) {
            permission.setId(UUID.randomUUID()); // Or let the DB generate if configured
        }

        return permissionRepository.save(permission);
    }

    @Override
    public List<Permission> savePermissions(List<Permission> permissions) {
        permissions.forEach(permission -> {
            validateModuleName(permission.getModuleName());
            if (permission.getId() == null) {
                permission.setId(UUID.randomUUID()); // Or let the DB generate if configured
            }
        });

        return permissionRepository.saveAll(permissions);
    }

    private void validateModuleName(Module moduleName) {
        if (moduleName == null) {
            throw new IllegalArgumentException("Module name cannot be null. Valid modules are: " +
                String.join(", ", getAvailableModules()));
        }
    }

    private List<String> getAvailableModules() {
        return List.of( Module.Dashboard.name(),Module.DepartmentMaster.name(),
                       Module.LabManagement.name(), Module.LocationMaster.name(),
                       Module.RoleMaster.name(), Module.PaymentMode.name(),
                       Module.Packages.name(), Module.Relation.name(),
                       Module.Notification.name(),Module.SupportTickets.name());
    }
}