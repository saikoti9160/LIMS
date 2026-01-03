import { createContext, useState } from "react";

const PermissionsContext = createContext();

export const PermissionProvider = ({ children}) => {
    const [permissions, setPermissions] = useState([]);

    const hasPermission = (requiredPermission) => {
        return permissions.includes(requiredPermission);
    }

    const hasAnyPermission = (requiredPermissions) => {
        return requiredPermissions.some(permission => permissions.includes(permission));
    }

    const setUserPermissions = (newPermissions) => {
        setPermissions(newPermissions);
    }

    return (
        <PermissionsContext.Provider value={{hasPermission, hasAnyPermission, permissions, setUserPermissions}}>
            {children}
        </PermissionsContext.Provider>
    )
}