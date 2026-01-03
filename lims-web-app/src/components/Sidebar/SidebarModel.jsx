import sidebarDashboardIcon from "../../assets/icons/sidebar-dashboard.svg";
import sidebarMastersIcon from "../../assets/icons/sidebar-masters.svg";
import departmentMasterIcon from "../../assets/icons/sidebar-masters-department-master.svg";
import roleMasterIcon from "../../assets/icons/sidebar-masters-role-master.svg";
import paymentModeIcon from "../../assets/icons/sidebar-masters-role-master.svg";
import relationIcon from "../../assets/icons/sidebar-masters-role-master.svg";
import userTypeIcon from "../../assets/icons/sidebar-masters-role-master.svg";
import locationMasterIcon from "../../assets/icons/sidebar-masters-location-master.svg";
import supportIssueTypeIcon from "../../assets/icons/sidebar-masters-role-master.svg";
import supportPriorityIcon from "../../assets/icons/sidebar-masters-role-master.svg";
import labTypeIcon from "../../assets/icons/sidebar-masters-role-master.svg";
import staffManagementIcon from "../../assets/icons/sidebar-staff-management.svg";
import labManagementIcon from "../../assets/icons/sidebar-lab-management.svg";
import packagesIcon from "../../assets/icons/sidebar-packages.svg";
import notificationIcon from "../../assets/icons/sidebar-notifications.svg";
import supportTicketsIcon from "../../assets/icons/sidebar-support-ticket.svg";
import downArrow from "../../assets/icons/down-arrow.svg";

export const items = {
  "Dashboard": {
    name: "Dashboard",
    link: "/dashboard",
    isPermitted: true,
    subItems: null,
    level: 0,
    isActive: false,
    icon: sidebarDashboardIcon
  },
  "Masters": {
    name: "Masters",
    link: "/masters/department",
    isPermitted: true,
    subItems: [
      "Department Master",
      "Role Master",
      "Payment Mode",
      "Relation",
      "Department",
      "Location Master",
      "Support Issue Type",
      "Support Priority",
      "Lab Type",
      "Branch Type"
    ],
    level: 0,
    isActive: false,
    icon: sidebarMastersIcon
  },
  "Department Master": {
    name: "Department Master",
    link: "/masters/department",
    isPermitted: true,
    subItems: null,
    level: 1,
    isActive: false,
    icon: departmentMasterIcon
  },
  "Role Master": {
    name: "Role Master",
    link: "/masters/role-master",
    isPermitted: true,
    subItems: null,
    level: 1,
    isActive: false,
    icon: roleMasterIcon
  },
  "Payment Mode": {
    name: "Payment Mode",
    link: "/masters/payment-mode",
    isPermitted: true,
    subItems: null,
    level: 1,
    isActive: false,
    icon: paymentModeIcon
  },
  "Relation": {
    name: "Relation",
    link: "/masters/relation",
    isPermitted: true,
    subItems: null,
    level: 1,
    isActive: false,
    icon: relationIcon
  },
  "Department": {
    name: "Department",
    link: "/masters/department",
    isPermitted: true,
    subItems: null,
    level: 1,
    isActive: false,
    icon: departmentMasterIcon
  },
  "Location Master": {
    name: "Location Master",
    link: "/masters/location/country",
    isPermitted: true,
    subItems: ["Country", "State", "City"],
    level: 1,
    isActive: false,
    icon: locationMasterIcon
  },
  "Country": {
    name: "Country",
    link: "/masters/location/country",
    isPermitted: true,
    subItems: null,
    level: 2,
    isActive: false,
    icon: null
  },
  "State": {
    name: "State",
    link: "/masters/location/state",
    isPermitted: true,
    subItems: null,
    level: 2,
    isActive: false,
    icon: null
  },
  "City": {
    name: "City",
    link: "/masters/location/city",
    isPermitted: true,
    subItems: null,
    level: 2,
    isActive: false,
    icon: null
  },
  "Support Issue Type": {
    name: "Support Issue Type",
    link: "/masters/support-issue-type",
    isPermitted: true,
    subItems: null,
    level: 1,
    isActive: false,
    icon: supportIssueTypeIcon
  },
  "Support Priority": {
    name: "Support Priority",
    link: "/masters/support-priority",
    isPermitted: true,
    subItems: null,
    level: 1,
    isActive: false,
    icon: supportPriorityIcon
  },
  "Lab Type": {
    name: "Lab Type",
    link: "/masters/lab-type",
    isPermitted: true,
    subItems: null,
    level: 1,
    isActive: false,
    icon: labTypeIcon
  },
  "Branch Type": {
    name: "Branch Type",
    link: "/masters/branch-type",
    isPermitted: true,
    subItems: null,
    level: 1,
    isActive: false,
    icon: labTypeIcon
  },
  "Staff Management": {
    name: "Staff Management",
    link: "/staff-management",
    isPermitted: true,
    subItems: null,
    level: 0,
    isActive: false,
    icon: staffManagementIcon
  },
  "Lab Management": {
    name: "Lab Management",
    link: "/lab-management",
    isPermitted: true,
    subItems: null,
    level: 0,
    isActive: false,
    icon: labManagementIcon
  },
  "Packages": {
    name: "Packages",
    link: "/packages",
    isPermitted: true,
    subItems: null,
    level: 0,
    isActive: false,
    icon: packagesIcon
  },
  "Notification": {
    name: "Notification",
    link: "/notification",
    isPermitted: true,
    subItems: null,
    level: 0,
    isActive: false,
    icon: notificationIcon
  },
  "Support Tickets": {
    name: "Support Tickets",
    link: "/support-tickets",
    isPermitted: true,
    subItems: null,
    level: 0,
    isActive: false,
    icon: supportTicketsIcon
  }
};

export default items;
