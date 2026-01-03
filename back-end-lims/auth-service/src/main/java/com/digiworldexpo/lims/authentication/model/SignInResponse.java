package com.digiworldexpo.lims.authentication.model;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.digiworldexpo.lims.entities.User;
import com.digiworldexpo.lims.entities.master.Permission;
import com.digiworldexpo.lims.entities.master.Module;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignInResponse {
	private UUID userId;
	private String email;
	private String accessToken;
	private String idToken;
	private String refreshToken;	
	private String userName;
	private String accountType;
	private String roleName;
	private long expiresIn; 
//	private Object preferences;
	
	// Use Map<Module, Map<String, Boolean>> to store permissions
    private Map<Module, Map<String, Boolean>> permissions; 

    // Constructor to initialize SignInResponse from User
    public SignInResponse(User user) {
        this.userId = user.getId();
        this.email = user.getEmail();
        this.userName = user.getFirstName() + " " + user.getLastName();
        this.accountType = user.getAccount().getAccountName().toString();
        this.roleName = user.getRole().getRoleName();

        // Initialize the permissions map
        this.permissions = new HashMap<>();

        // Check if the role and permissions exist
        if (user.getRole() != null && user.getRole().getPermission() != null) {
            for (Permission permission : user.getRole().getPermission()) {
                // Create a map to hold the CRUD permission booleans
                Map<String, Boolean> permissionMap = new HashMap<>();
                permissionMap.put("canCreate", permission.isCanCreate());
                permissionMap.put("canRead", permission.isCanRead());
                permissionMap.put("canUpdate", permission.isCanUpdate());
                permissionMap.put("canDelete", permission.isCanDelete());

                // Get the Module from the Permission and add it to the permissions map
                Module module = permission.getModuleName();
                permissions.put(module, permissionMap);  // Map Module to its permissions
            }
        }
    }
}
