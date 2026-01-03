import { useContext } from "react"
import { PermissionContext } from './PermissionContext';

export const usePermissions = () => {
    return useContext(PermissionContext);
}