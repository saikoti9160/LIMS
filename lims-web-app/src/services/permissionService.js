import sidebarDashboardIcon from "../../assets/icons/sidebar-dashboard.svg";
import sidebarMastersIcon from "../../assets/icons/sidebar-masters.svg"
import departmentMasterIcon from  "../../assets/icons/sidebar-masters-department-master.svg"
import roleMasterIcon from  "../../assets/icons/sidebar-masters-role-master.svg"
import paymentModeIcon from  "../../assets/icons/sidebar-masters-role-master.svg"
import relationIcon from  "../../assets/icons/sidebar-masters-role-master.svg"
import userTypeIcon from  "../../assets/icons/sidebar-masters-role-master.svg"
import locationMasterIcon from  "../../assets/icons/sidebar-masters-location-master.svg"
import supportIssueTypeIcon from  "../../assets/icons/sidebar-masters-role-master.svg"
import supportPriorityIcon from  "../../assets/icons/sidebar-masters-role-master.svg"
import labTypeIcon from  "../../assets/icons/sidebar-masters-role-master.svg"
import staffManagementIcon from "../../assets/icons/sidebar-staff-management.svg"
import labManagementIcon from "../../assets/icons/sidebar-lab-management.svg"
import packagesIcon from "../../assets/icons/sidebar-packages.svg"
import notificationIcon from "../../assets/icons/sidebar-notifications.svg"
import supportTicketsIcon from "../../assets/icons/sidebar-support-ticket.svg"

  const getSuperAdminMenu = () => {
    return [
        {name: 'Dashboard', icon: sidebarDashboardIcon, link: '/dashboard', permissions: ["DASHBOARD_VIEW", "DASHBOARD_CREATE", "DASHBOARD_EDIT", "DASHBOARD_DELETE"]},
        {name: 'Masters', icon: sidebarMastersIcon,link: "/masters", active: true,
            subItems:[
                {name: 'Department Master', icon: departmentMasterIcon, link:"/masters/department", permissions: ["DEPARTMENT_VIEW", "DEPARTMENT_CREATE", "DEPARTMENT_EDIT", "DEPARTMENT_DELETE"]},
                {name: 'Role Master', icon: roleMasterIcon, link:"/masters/role", permissions: ["ROLE_VIEW", "ROLE_CREATE", "ROLE_EDIT", "ROLE_DELETE"]},
                {name: 'Payment Mode Master', icon: paymentModeIcon, link:"/masters/payment-mode", permissions: ["PAYMENT_MODE_VIEW", "PAYMENT_MODE_CREATE", "PAYMENT_MODE_EDIT", "PAYMENT_MODE_DELETE"]},
                {name: 'Relation', icon: relationIcon, link:"/masters/relation", permissions: ["RELATION_VIEW", "RELATION_CREATE", "RELATION_EDIT", "RELATION_DELETE"]},
                {name: 'Department', icon: userTypeIcon, link:"/masters/user-type", permissions: ["USER_TYPE_VIEW", "USER_TYPE_CREATE", "USER_TYPE_EDIT", "USER_TYPE_DELETE"]},
                {name: 'Location Master', icon: locationMasterIcon, link:"/masters/location",
                    subItems: [
                        {name: 'Country', link:"/masters/location/country", permissions: ["COUNTRY_VIEW", "COUNTRY_CREATE", "COUNTRY_EDIT", "COUNTRY_DELETE"]},
                        {name: 'State', link:"/masters/location/state", permissions: ["STATE_VIEW", "STATE_CREATE", "STATE_EDIT", "STATE_DELETE"]},
                        {name: 'City', link:"/masters/location/city", permissions: ["CITY_VIEW", "CITY_CREATE", "CITY_EDIT", "CITY_DELETE"]},
                    ]
                },
                {name: 'Support Issue Type', icon: supportIssueTypeIcon, link:"/masters/support-issue", permissions: ["SUPPORT_ISSUE_TYPE_VIEW", "SUPPORT_ISSUE_TYPE_CREATE", "SUPPORT_ISSUE_TYPE_EDIT", "SUPPORT_ISSUE_TYPE_DELETE"]},
                {name: 'Support Priority', icon: supportPriorityIcon, link:"/masters/support-priority", permissions: ["SUPPORT_PRIORITY_VIEW", "SUPPORT_PRIORITY_CREATE", "SUPPORT_PRIORITY_EDIT", "SUPPORT_PRIORITY_DELETE"]},
                {name: "Lab Type", icon: labTypeIcon, link: "/masters/lab-type", permissions: ["LAB_TYPE_VIEW", "LAB_TYPE_CREATE", "LAB_TYPE_EDIT", "LAB_TYPE_DELETE"]},
            ]
        },
        {name: "Staff Management", icon: staffManagementIcon, link: "/staff-management", permissions: ["STAFF_MANAGEMENT_VIEW", "STAFF_MANAGEMENT_CREATE", "STAFF_MANAGEMENT_EDIT", "STAFF_MANAGEMENT_DELETE"]},
        {name: "Lab Management", icon: labManagementIcon, link: "/lab-management", permissions: ["LAB_MANAGEMENT_VIEW", "LAB_MANAGEMENT_CREATE", "LAB_MANAGEMENT_EDIT", "LAB_MANAGEMENT_DELETE"]},
        {name: "Packages", icon: packagesIcon, link: "/packages", permissions: ["PACKAGES_VIEW", "PACKAGES_CREATE", "PACKAGES_EDIT", "PACKAGES_DELETE"]},
        {name: "Notification", icon: notificationIcon, link: "/notification", permissions: ["NOTIFICATION_VIEW", "NOTIFICATION_CREATE", "NOTIFICATION_EDIT", "NOTIFICATION_DELETE"]},
        {name: "Support Tickets", icon: supportTicketsIcon, link: "/support-tickets", permissions: ["SUPPORT_TICKETS_VIEW", "SUPPORT_TICKETS_CREATE", "SUPPORT_TICKETS_EDIT", "SUPPORT_TICKETS_DELETE"]},
    ]
}

const permissionService = {
    getSuperAdminMenu: getSuperAdminMenu
}
export default permissionService;