import React, { useEffect, useState } from "react";
import InputField from "../../Homepage/InputField"
import DropDown from "../../Re-usable-components/DropDown";
import './AddRole.css'
import { useLocation, useNavigate } from "react-router-dom";
import { addRole, updateRole } from '../../../services/RoleService';
import Swal from "../../Re-usable-components/Swal";
import Checkbox from "../../Re-usable-components/Checkbox";


const AddRole = () => {
    const [Role, setRole] = useState({
        roleName: "",
        active: "",
        preferences: []

    });
    const location = useLocation();
    const navigate = useNavigate();
    const [popupConfig, setPopupConfig] = useState(null);


    const options = [
        { label: "Active", status: true },
        { label: "Inactive", status: false },
    ]
    const modules = [
        "Dashboard",
        "Department Master",
        "Role Master",
        "Payment Mode",
        "Relation",
        "Designation",
        "Location Master",
        "Support Issue Type",
        "Support Priority",
        "Lab Type",
        "Lab Management",
        "Staff Management",
        "Packages",
        "Notification",
        "Support Tickets",
    ];

    const MODULE_ENUM = {
        "Dashboard": "Dashboard",
        "Department Master": "DepartmentMaster",  
        "Role Master": "RoleMaster",
        "Payment Mode": "PaymentMode",
        "Relation": "Relation",
        "Location Master": "LocationMaster",
        "Lab Management": "LabManagement",
        "Packages": "Packages",
        "Notification": "Notification",
        "Support Tickets": "SupportTickets",
    };
    

    const actions = ["Manage", "View", "Create", "Edit", "Delete"];

    const [permissions, setPermissions] = useState(
        modules.reduce((acc, module) => {
            acc[module] = {
                Manage: false,
                View: false,
                Create: false,
                Edit: false,
                Delete: false
            };
            return acc;
        }, {})
    );

    const handleChange = (checkedValue, module, action) => {
        const updatedPermissions = { ...permissions };

        if (action === "Manage") {
            updatedPermissions[module] = {
                Manage: checkedValue,
                View: checkedValue,
                Create: checkedValue,
                Edit: checkedValue,
                Delete: checkedValue
            };
        } else {
            updatedPermissions[module][action] = checkedValue;

            const allChecked =
                updatedPermissions[module].View &&
                updatedPermissions[module].Create &&
                updatedPermissions[module].Edit &&
                updatedPermissions[module].Delete;

            updatedPermissions[module].Manage = allChecked;
        }

        setPermissions(updatedPermissions);
    };

    const generatePreferences = () => {
        const preferences = [];
        for (const module in permissions) {
            const selectedActions = {};
            Object.keys(permissions[module]).forEach(action => {
                if (permissions[module][action] && action !== "Manage") {
                    selectedActions[`can${action.charAt(0).toUpperCase() + action.slice(1)}`] = true;  
                }
            });
    
            if (Object.keys(selectedActions).length > 0) {
                const moduleEnum = MODULE_ENUM[module]; 
    
                if (moduleEnum) {
                    preferences.push({
                        active: true, 
                        moduleName: moduleEnum,  
                        ...selectedActions,  
                        createdBy: createdBy,  
                        modifiedBy: modifiedBy,  
                    });
                } else {
                    console.warn(`Invalid module name: ${module}`); 
                }
            }
        }
    
        return preferences;
    };
    const [createdBy, setCreatedBy] = useState('3fa85f64-5717-4562-b3fc-2c963f66afa6');
    const handlesave = async () => {
        const preferences = generatePreferences();
        console.log("Generated Preferences:", preferences);
    
     
        const role = {
            active: Role.active,
            createdBy: createdBy,
            roleName: Role.roleName,
            lab: "", // Example lab ID
            permission: modules
                .filter(module => {
                    const modulePermissions = permissions[module];
                    return Object.values(modulePermissions).some(permission => permission === true);
                })
                .map(module => {
                    const modulePermissions = permissions[module];
                    const moduleEnum = MODULE_ENUM[module] || module; 
    
                    return {
                        active: true,
                        moduleName: moduleEnum,  
                        canCreate: modulePermissions.Create,
                        canRead: modulePermissions.View,
                        canUpdate: modulePermissions.Edit,
                        canDelete: modulePermissions.Delete,
                        createdBy: createdBy,
                        modifiedBy: createdBy 
                    };
                })
        };
    
        try {
            const response = await addRole(role, createdBy);
            if (response.statusCode === "201 CREATED") {
                setPopupConfig({
                    icon: 'success',
                    title: 'Added Successfully',
                    text: '',
                    onClose: () => navigate('/masters/role'),
                });
                
            }
            else
                setPopupConfig({
                    icon: 'delete',
                    title: 'Failed to add role.',
                    text: 'Please try again.',
                    onClose: () => navigate('/masters/role'),
            })
        } catch (error) {
            console.error("Error while saving role", error);
        }
    };
    


    const handleBack = () => {
        navigate('/masters/role')
    }

    const [modifiedBy, setModifiedBy] = useState('3fa85f64-5717-4562-b3fc-2c963f66afa6');
    const handleUpdate = async () => {
        const updatedPermissions = modules
            .filter(module => {
                const modulePermissions = permissions[module];
                return Object.values(modulePermissions).some(permission => permission === true);
            })
            .map(module => {
                const modulePermissions = permissions[module];
                const moduleEnum = MODULE_ENUM[module] || module; 
        
                return {
                    active: true,
                    moduleName: moduleEnum,  
                    canCreate: modulePermissions.Create,
                    canRead: modulePermissions.View,
                    canUpdate: modulePermissions.Edit,
                    canDelete: modulePermissions.Delete,
                    createdBy: createdBy,
                    modifiedBy: modifiedBy, 
                };
            });
    
        const role = {
            roleName: Role.roleName,
            active: Role.active,
            permission: updatedPermissions, 
        };
    
        try {
            const response = await updateRole(location.state.roleData.id, role, modifiedBy);
            if (response.statusCode === "200 OK") {
                setPopupConfig({
                    icon: 'success',
                    title: 'Updated Successfully',
                    text: '',
                    onClose: () => navigate('/masters/role'),
                });
            }else{
                setPopupConfig({
                    icon: 'delete',
                    title: 'Failed to update role.',
                    text: 'Please try again.',
                    onClose: () => navigate('/masters/role'),
                });
            }
        } catch (error) {
            console.error("Error while updating role", error);
        }
    };
    
    const mode = location.state?.mode;
    const [viewMode, setViewMode] = useState(false);
    const roleData = location.state?.roleData;
    useEffect(() => {
        if (mode === "view") {
            setViewMode(true);
        }
        
        if (roleData) {
            console.log("roleData", roleData.active);
            setRole({
                roleName: roleData.roleName || "",
                active: roleData.active,
            });
    
            const loadedPermissions = modules.reduce((acc, module) => {
                acc[module] = {
                    Manage: false,
                    View: false,
                    Create: false,
                    Edit: false,
                    Delete: false,
                };
                return acc;
            }, {});
    
            roleData.permission?.forEach((perm) => {
                const frontendModuleName = Object.keys(MODULE_ENUM).find(key => MODULE_ENUM[key] === perm.moduleName);
    
                if (frontendModuleName && loadedPermissions[frontendModuleName]) {
                    if (perm.canCreate) loadedPermissions[frontendModuleName].Create = true;
                    if (perm.canRead) loadedPermissions[frontendModuleName].View = true;
                    if (perm.canUpdate) loadedPermissions[frontendModuleName].Edit = true;
                    if (perm.canDelete) loadedPermissions[frontendModuleName].Delete = true;
    
                    loadedPermissions[frontendModuleName].Manage = (
                        loadedPermissions[frontendModuleName].View &&
                        loadedPermissions[frontendModuleName].Create &&
                        loadedPermissions[frontendModuleName].Edit &&
                        loadedPermissions[frontendModuleName].Delete
                    );
                }
            });
    
            setPermissions(loadedPermissions);
          
        }
        console.log("role",Role)
    }, [roleData]);
    const handleInputChange = (e) => {
        setRole({ ...Role, [e.target.name]: e.target.value });
    }
    const handleDropDownChange = (selectedValue) => {
        console.log(selectedValue.target.value.status);
        if (mode === "view") return;
        setRole({ ...Role, active: selectedValue.target.value.status });
    };



    return (
        <div className="role-container">
            <span className="role-head"> {viewMode ? "View Role" : location.state?.mode === "edit" ? "Edit Role" : "Add Role"}</span>

            <div className="role-input-div">
                <div className="role-name">
                    <InputField
                        placeholder="Enter Here"
                        type="text"
                        label="Role Name"
                        name="roleName"
                        value={Role.roleName}
                        onChange={handleInputChange}
                        disabled={viewMode}
                        required
                    />
                </div>

                <span className="role-status">
                    <DropDown
                        options={options}
                        label="Status"
                        name="active"
                        value={
                            roleData ? (Role.active ? "Active" : "Inactive") : "" 
                        }
                        onChange={(selectedOption) => handleDropDownChange(selectedOption)}
                        disabled={viewMode}
                      fieldName={"label"}
                      required
                    />

                </span>


            </div>

            <div className="role-div-table">
                <table className="role-table" rules="none">

                    <thead>
                        <tr className="head-tr" >
                            <th  >Module</th>
                            {actions.map((action) => (
                                <th key={action} >{action}</th>
                            ))}
                        </tr>

                    </thead>

                    <tbody>
                        {modules.map((module) => (
                            <tr key={module} className="body-tr">
                                <td>{module}</td>
                                {actions.map((action) => (
                                    <td key={action}>
                                        <Checkbox
                                            borderColor="#E1E1E1"
                                            checked={permissions[module][action]} 
                                            onChange={(newCheckedValue) => handleChange(newCheckedValue, module, action)}
                                            disabled={viewMode} 
                                        />
                                    </td>
                                ))}
                            </tr>
                        ))}

                    </tbody>
                </table>
            </div>
            <div className="botton-div">
                {viewMode ? (<button className="btn-secondary" onClick={handleBack}>Back</button>) : location.state?.mode === "edit" ? (
                    <div><button className="btn-secondary" onClick={handleBack}>Back</button> <button className="btn-primary" onClick={handleUpdate}>update</button></div>
                ) : (<div><button className="btn-secondary" onClick={handleBack}>Back</button>     <button className="btn-primary" onClick={handlesave}>Save</button></div>)}
            </div>

            {popupConfig && (
                <Swal
                    icon={popupConfig.icon}
                    title={popupConfig.title}
                    text={popupConfig.text}
                    onButtonClick={popupConfig.onButtonClick}
                    onClose={popupConfig.onClose}
                />
            )}
        </div>
    )
}
export default AddRole;