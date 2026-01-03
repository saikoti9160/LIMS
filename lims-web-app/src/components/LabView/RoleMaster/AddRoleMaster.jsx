import React, { useEffect, useState } from "react";
import InputField from "../../Homepage/InputField";
import Checkbox from "../../Re-usable-components/Checkbox";
import DropDown from "../../Re-usable-components/DropDown";
import "../RoleMaster/Rolemaster.css";
import { addRole, updateRole } from "../../../services/RoleService";
import { useLocation, useNavigate } from "react-router-dom";
import Swal from "../../Re-usable-components/Swal";

const AddRoleMaster = () => {

    const [role, setRole] = useState({
        roleName: "",
        active: "",

        labId: "0e681d43-a8d2-47fd-9298-31c7872c8a59",
        selectedPermissions: {}
    });
    const [popupConfig, setPopupConfig] = useState(null);
    const navigate = useNavigate();
    const location = useLocation();
    const [selectedPermissions, setSelectedPermissions] = useState({});
    const actions = ["Manage", "Add", "Edit", "Delete", "View"];
    const options = [
        { label: "Active", status: true },
        { label: "Inactive", status: false },
    ]

    const roleData = [
        {
            category: "Dashboard",
            items: [{ name: "Dashboard", permissions: {} }],
        },
        {
            category: "Masters",
            items: [
                { name: "Role Master", permissions: {} },
                { name: "Doctor Master", permissions: {} },
                { name: "Department Master", permissions: {} },
                { name: "Sample Master", permissions: {} },
                { name: "Sample Test Mapping", permissions: {} },
                { name: "Test Configuration Master", permissions: {} },
                { name: "Profile Master", permissions: {} },
                { name: "Coupon and Discount Master", permissions: {} },
                { name: "Signature Master", permissions: {} },
                { name: "Referral Master", permissions: {} },
                { name: "Branch Master", permissions: {} },
                { name: "Phlebotomist Master", permissions: {} },
                { name: "Discount Type Master", permissions: {} },
                { name: "Warehouse Master", permissions: {} },
                { name: "Center Master", permissions: {} },
                { name: "Storage Master", permissions: {} },
                { name: "Report Settings Master", permissions: {} },
                { name: "Expense Category Master", permissions: {} },
                { name: "Expense Master", permissions: {} },
                { name: "Outsource Center Master", permissions: {} },
                { name: "Organisation Master", permissions: {} },
                { name: "Inventory Master", permissions: {} },
                { name: "Supplier Master", permissions: {} },
                { name: "Equipment Master", permissions: {} },
            ],
        },
        {
            category: "Price List",
            items: [{ name: "Price List", permissions: {} }],
        },
        {
            category: "Staff Management",
            items: [{ name: "Staff Management", permissions: {} }],
        },
        {
            category: "Registration",
            items: [
                { name: "Patient Registration", permissions: {} },
                { name: "Appointment Management", permissions: {} },
                { name: "Home Collection Management", permissions: {} },
            ],
        },
        {
            category: "Reports",
            items: [{ name: "Reports", permissions: {} }],
        },
        {
            category: "Acession Management",
            items: [
                { name: "BarCode Management", permissions: {} },
                { name: "Sample Collection", permissions: {} },
                { name: "Sample Receive", permissions: {} },
                { name: "Acession", permissions: {} },
                { name: "Organization Request Entries", permissions: {} },
                { name: "Organization Request Log", permissions: {} },
            ],
        },
        {
            category: "Billing & Invoice",
            items: [
                { name: "Billing", permissions: {} },
                { name: "All Bills View", permissions: {} },
                { name: "Patient Billing Settlements", permissions: {} },
                { name: "Organization Invoice Settlements", permissions: {} },
                { name: "Invoice Management Organization", permissions: {} },
            ],
        },
        {
            category: "Commission Management",
            items: [
                { name: "Referral Commission Mapping", permissions: {} },
                { name: "Organisation Commission Mapping", permissions: {} },
            ],
        },
        {
            category: "Approver",
            items: [{ name: "Report Management", permissions: {} }],
        },
        {
            category: "Reports Hub",
            items: [
                { name: "Reports Processing", permissions: {} },
                { name: "Approved Reports", permissions: {} },
                { name: "Rejected Reports", permissions: {} },
                { name: "Report Prints", permissions: {} },
                { name: "Approved Reports for Organisation", permissions: {} },
                { name: "Approved Reports for Referral", permissions: {} },
                { name: "Inventory Reports", permissions: {} },
                { name: "Product Consumption Report", permissions: {} },
                { name: "Purchase Order Report", permissions: {} },
                { name: "Expiry Report", permissions: {} },
            ],
        },
        {
            category: "Audit Trails",
            items: [
                { name: "Patient Audit Trail", permissions: {} },
                { name: "Billing Audit Trail", permissions: {} },
                { name: "Sample Audit Trail", permissions: {} },
                { name: "Calibration Audit Trail", permissions: {} },
            ],
        },
        {
            category: "MIS Reports",
            items: [
                { name: "Phlebotomists MIS Reports", permissions: {} },
                { name: "Collection Reports", permissions: {} },
                { name: "Organisation Wise Financial Report", permissions: {} },
                { name: "Referral Wise Financial Report", permissions: {} },
                { name: "Outsource Wise Financial Report", permissions: {} },
            ],
        },
        {
            category: "Analytics",
            items: [
                { name: "Operational Analytics", permissions: {} },
                { name: "Financial Analytics", permissions: {} },
                { name: "Test Analytics", permissions: {} },
                { name: "Expense Analytics", permissions: {} },
                { name: "Patient Insight Analytics", permissions: {} },
                { name: "Inventory Analytics", permissions: {} },
            ],
        },
        {
            category: "Support Tickets",
            items: [{ name: "Support Tickets", permissions: {} }],
        },
        {
            category: "Doctor Access",
            items: [
                { name: "Patient Shared Documents", permissions: {} },
                { name: "Interactive Chat System", permissions: {} },
            ],
        },
    ];


    const moduleEnumMap = {
        "Dashboard": "Dashboard",
        "Role Master": "RoleMaster",
        "Doctor Master": "DoctorMaster",
        "Department Master": "DepartmentMaster",
        "Sample Master": "SampleMaster",
        "Sample Test Mapping": "SampleTestMapping",
        "Test Configuration Master": "TestConfigurationMaster",
        "Profile Master": "ProfileMaster",
        "Coupon and Discount Master": "CouponAndDiscountMaster",
        "Signature Master": "SignatureMaster",
        "Referral Master": "ReferralMaster",
        "Branch Master": "BranchMaster",
        "Phlebotomist Master": "PhlebotomistMaster",
        "Discount Type Master": "DiscountTypeMaster",
        "Warehouse Master": "WarehouseMaster",
        "Center Master": "CenterMaster",
        "Storage Master": "StorageMaster",
        "Report Settings Master": "ReportSettingsMaster",
        "Expense Category Master": "ExpenseCategoryMaster",
        "Expense Master": "ExpenseMaster",
        "Outsource Center Master": "OutsourceCenterMaster",
        "Organisation Master": "OrganisationMaster",
        "Inventory Master": "InventoryMaster",
        "Supplier Master": "SupplierMaster",
        "Equipment Master": "EquipmentMaster",
        "Price List": "PriceList",
        "Staff Management": "StaffManagement",
        "Patient Registration": "PatientRegistration",
        "Appointment Management": "AppointmentManagement",
        "Home Collection Management": "HomeCollectionManagement",
        "Reports": "Reports",
        "Support Tickets": "SupportTickets",
        "Support Issue Type": "SupportIssueType",
        "Support Priority": "SupportPriority",
        "BarCode Management": "BarCodeManagement",
        "Sample Collection": "SampleCollection",
        "Sample Receive": "SampleReceive",
        "Acession": "Acession",
        "Organization Request Entries": "OrganizationRequestEntries",
        "Organization Request Log": "OrganizationRequestLog",
        "Billing": "Billing",
        "All Bills View": "AllBillsView",
        "Patient Billing Settlements": "PatientBillingSettlements",
        "Organization Invoice Settlements": "OrganizationInvoiceSettlements",
        "Invoice Management Organization": "InvoiceManagementOrganization",
        "Referral Commission Mapping": "RefferalCommissionMapping",
        "Organisation Commission Mapping": "OrganisationCommissionMapping",
        "Report Management": "ReportManagement",
        "Reports Processing": "ReportsProcessing",
        "Approved Reports": "ApprovedReports",
        "Rejected Reports": "RejectedReports",
        "Report Prints": "ReportPrints",
        "Approved Reports for Organisation": "ApprovedReportsForOrganisation",
        "Approved Reports for Referral": "ApprovedReportsForReferral",
        "Inventory Reports": "InventoryReports",
        "Product Consumption Report": "ProductConsumptionReport",
        "Purchase Order Report": "PurchaseOrderReport",
        "Expiry Report": "ExpiryReport",
        "Patient Audit Trail": "PatientAuditTrail",
        "Billing Audit Trail": "BillingAuditTrail",
        "Sample Audit Trail": "SampleAuditTrail",
        "Calibration Audit Trail": "CalibrationAuditTrail",
        "Phlebotomists MIS Reports": "PhlebotomistsMISReports",
        "Collection Reports": "CollectionReports",
        "Organisation Wise Financial Report": "OrganisationWiseFinancialReport",
        "Referral Wise Financial Report": "ReferralWiseFinancialReport",
        "Outsource Wise Financial Report": "OutsourceWiseFinancialReport",
        "Operational Analytics": "OperationalAnalytics",
        "Financial Analytics": "FinancialAnalytics",
        "Test Analytics": "TestAnalytics",
        "Expense Analytics": "ExpenseAnalytics",
        "Patient Insight Analytics": "PatientInsightAnalytics",
        "Inventory Analytics": "InventoryAnalytics",
        "Patient Shared Documents": "PatientSharedDocuments",
        "Interactive Chat System": "InteractiveChatSystem",
        "Payment Mode": "PaymentMode",
        "Relation": "Relation",
        "Designation": "Designation",
        "Location Master": "LocationMaster",
        "Lab Type": "LabType",
        "Lab Management": "LabManagement",
        "Packages": "Packages",
        "Notification": "Notification"
    };

    const createdBy = "3fa85f64-5717-4562-b3fc-2c963f66afa6";
    const handleSubmit = async () => {
        const finalPermissions = Object.entries(selectedPermissions).map(([moduleName, perms]) => ({
            active: true,
            moduleName: moduleEnumMap[moduleName] || moduleName,
            canCreate: perms["Add"] || false,
            canRead: perms["View"] || false,
            canUpdate: perms["Edit"] || false,
            canDelete: perms["Delete"] || false,
            createdBy,
            modifiedBy: createdBy,
        }));

        const payload = {
            active: role.active,
            createdBy,
            roleName: role.roleName,
            lab: "",
            permission: finalPermissions,
        };

        setRole((prev) => ({ ...prev, selectedPermissions: finalPermissions }));
        try {
            const response = await addRole(payload, createdBy);
            if (response.statusCode === "201 CREATED") {
                setPopupConfig({
                    icon: 'success',
                    title: 'Added Successfully',
                    text: '',
                    onClose: () => navigate('/lab-view/rolemaster'),
                });

            }
            else
                setPopupConfig({
                    icon: 'delete',
                    title: 'Failed to add role.',
                    text: 'Please try again.',
                    onClose: () => navigate('/lab-view/rolemaster'),
                })
        } catch (error) {
            console.error("Error while saving role", error);
        }
    };


    const isCategoryFullySelected = (category) => {
        const categoryItems = roleData.find(c => c.category === category)?.items || [];
        return categoryItems.every(item =>
            actions.every(action => selectedPermissions[item.name]?.[action])
        );
    };
    const togglePermission = (itemName, permission) => {
        setSelectedPermissions((prev) => {
            const newPermissions = { ...prev };

            if (!newPermissions[itemName]) {
                newPermissions[itemName] = {
                    Add: false,
                    View: false,
                    Edit: false,
                    Delete: false,
                    Manage: false,
                };
            }

            if (permission === "Manage") {
                const newManageState = !newPermissions[itemName].Manage;
                newPermissions[itemName] = {
                    Add: newManageState,
                    View: newManageState,
                    Edit: newManageState,
                    Delete: newManageState,
                    Manage: newManageState,
                };
            } else {

                newPermissions[itemName][permission] = !newPermissions[itemName][permission];
                newPermissions[itemName].Manage = ["Add", "View", "Edit", "Delete"].every(
                    (action) => newPermissions[itemName][action]
                );
            }
            return newPermissions;
        });
    };
    const toggleCategory = (category) => {
        setSelectedPermissions((prev) => {
            const newPermissions = { ...prev };
            const categoryItems = roleData.find(c => c.category === category)?.items || [];

            const allSelected = categoryItems.every(item =>
                actions.every(action => newPermissions[item.name]?.[action])
            );
            categoryItems.forEach(item => {
                if (allSelected) {
                    delete newPermissions[item.name];
                } else {
                    newPermissions[item.name] = actions.reduce((acc, act) => ({ ...acc, [act]: true }), {});
                }
            });

            return newPermissions;
        });
    };
    const handleBack = () => {
        navigate('/lab-view/rolemaster');
    }
    const [viewMode, setViewMode] = useState(false);
    const mode = location.state?.mode;
    const responseData = location.state?.roleData;
    useEffect(() => {
        if (mode === "view") {
            setViewMode(true);
        }
        if (mode === "view" || mode === "edit") {

            setRole({
                roleName: responseData.roleName || "",
                active: responseData.active,
            });
            console.log("role name", responseData.roleName);

            if (responseData.permission) {
                const formattedPermissions = responseData.permission.reduce((acc, perm) => {
                    const frontendModuleName = Object.keys(moduleEnumMap).find(
                        key => moduleEnumMap[key] === perm.moduleName
                    );

                    if (frontendModuleName) {
                        acc[frontendModuleName] = {
                            Add: perm.canCreate || false,
                            View: perm.canRead || false,
                            Edit: perm.canUpdate || false,
                            Delete: perm.canDelete || false,
                            Manage: perm.canCreate && perm.canRead && perm.canUpdate && perm.canDelete,
                        };
                    }
                    return acc;
                }, {});
                setSelectedPermissions(formattedPermissions);
            }
        }
    }, []);
    const [modifiedBy, setModifiedBy] = useState('3fa85f64-5717-4562-b3fc-2c963f66afa6');
    const handleUpdate = async () => {
        const finalPermissions = Object.entries(selectedPermissions).map(([moduleName, perms]) => ({
            active: role.active,
            moduleName: moduleEnumMap[moduleName] || moduleName,
            canCreate: perms["Add"] || false,
            canRead: perms["View"] || false,
            canUpdate: perms["Edit"] || false,
            canDelete: perms["Delete"] || false,
            createdBy,
            modifiedBy: createdBy,
        }));

        const payload = {
            active: role.active,
            modifiedBy,
            roleName: role.roleName,
            lab: "",
            permission: finalPermissions,
        };

        try {
            const response = await updateRole(location.state.roleData.id, payload, modifiedBy);
            if (response.statusCode === "200 OK") {
                setPopupConfig({
                    icon: 'success',
                    title: 'Updated Successfully',
                    text: '',
                    onClose: () => navigate('/lab-view/rolemaster'),
                });
            } else {
                setPopupConfig({
                    icon: 'delete',
                    title: 'Failed to update role.',
                    text: 'Please try again.',
                    onClose: () => navigate('/lab-view/rolemaster'),
                });
            }
        } catch (error) {
            console.error("Error while updating role", error);
        }
    };
    const handleDropDownChange = (selectedValue) => {
        if (mode === "view") return;
        setRole({ ...role, active: selectedValue.target.value.status });
    };

    return (
        <div className="add-role-l-container">
            <span className="role-head"> {viewMode ? "View Role" : location.state?.mode === "edit" ? "Edit Role" : "Add Role"}</span>
            <div className="role-fields-div">
                <div className="role-data-div">
                    <InputField label="Role Name" name="roleName" placeholder="Enter Here"
                        value={role.roleName}
                        onChange={(e) => setRole({ ...role, roleName: e.target.value })}
                        disabled={viewMode}
                        required />
                </div>
                <div className="role-data-div">
                    <DropDown
                        options={options}
                        label="Status"
                        name="active"
                        value={
                            responseData ? (role.active ? "Active" : "Inactive") : ""
                        }
                        onChange={(selectedOption) => handleDropDownChange(selectedOption)}
                        disabled={viewMode}
                        fieldName={"label"}
                        required
                    />
                </div>
            </div>
            <div className="table-role-div">
                <table className="role-table" rules="none">
                    <thead>
                        <tr className="lab-role-tr">
                            <div className="th-head">
                                <th className="category-wrapper-head">Module</th>
                                <div className="category-container-head">
                                    {actions.map((action, index) => (
                                        <span className={`span-style-${index}`}>{action}</span>
                                    ))}
                                </div>
                            </div>
                        </tr>
                    </thead>
                    <tbody>
                        {roleData.map((category, index) => (
                            <tr key={index}>
                                <td colSpan={actions.length + 1} className="category-wrapper">
                                    <div className="category-container">
                                        <div className="category-header">
                                            <span className="category-title">{category.category}</span>
                                            <Checkbox
                                                borderColor="#535353"
                                                checked={isCategoryFullySelected(category.category)}
                                                onChange={() => toggleCategory(category.category)}
                                                disabled={viewMode}
                                            />
                                        </div>
                                        <div className="category-items">
                                            {category.items.map((item, index2) => (
                                                <div key={index2} className="category-item">
                                                    <span>{item.name}</span>
                                                    <div className="checkbox-group">
                                                        {actions.map((action) => (
                                                            <Checkbox
                                                                key={action}
                                                                borderColor="#E1E1E1"
                                                                className="checkbox-item"
                                                                checked={selectedPermissions[item.name]?.[action] || false}
                                                                onChange={() => togglePermission(item.name, action)}
                                                            />
                                                        ))}
                                                    </div>
                                                </div>
                                            ))}
                                        </div>
                                    </div>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
            <div className="botton-div">
                {viewMode ? (<button className="btn-secondary" onClick={handleBack}>Back</button>) : location.state?.mode === "edit" ? (
                    <div><button className="btn-secondary" onClick={handleBack}>Back</button> <button className="btn-primary" onClick={handleUpdate}>update</button></div>
                ) : (<div><button className="btn-secondary" onClick={handleBack}>Back</button>     <button className="btn-primary" onClick={handleSubmit}>Save</button></div>)}
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

    );
};

export default AddRoleMaster;