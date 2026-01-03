import logo from './assets/images/logo.svg';
import './App.css';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import FreeSamples from './components/Homepage/HomePage';
import PremiumPage from './components/Homepage/PremiumPage';
import Header from './components/Homepage/Header';
import LandingPage from './components/Homepage/LandingPage'; 
import LabManagement from './components/pages/platform-owners/LabManagement/LabManagement';

import Button from './components/Re-usable-components/Button';
import Home from './Home';

import MyProfile from './components/Homepage/MyProfile';
import LabProfile from "./components/LabView/Lab Profile/LabProfile"

import Sidebar from './components/Sidebar/Sidebar';
import AddDepartment from './components/master-data/Department/AddDepartment';
import DepartmentMaster from './components/master-data/Department/DepartmentMaster';
import LabTypeMaster from './components/master-data/LabType/LabTypeMaster';
import AddLabType from './components/master-data/LabType/AddLabType';
import SupportIssueTypeMaster from './components/master-data/SupportIssueType/SupportIssueTypeMaster';
import AddSupportIssueType from './components/master-data/SupportIssueType/AddSupportIssueType';
import AddPackage from './components/Packages/AddPackage';
import Package from './components/Packages/Package';
import AddSupportPriority from './components/master-data/SupportPriority/AddSupportPriority';
import SupportPriorityMaster from './components/master-data/SupportPriority/SupportPriorityMaster';

import AddRole from './components/master-data/Role/AddRole';
import Role from './components/master-data/Role/Role';

import Country from './components/master-data/LocationMaster/Country/Country';
import State from './components/master-data/LocationMaster/State/State';
import Relation from './components/master-data/RelationMaster/Relation';
import AddViewEditRelation from './components/master-data/RelationMaster/AddViewEditRelation';
import AddViewEditCountry from './components/master-data/LocationMaster/Country/AddViewEditCountry';
import AddViewEditState from './components/master-data/LocationMaster/State/AddViewEditState';
import AddViewEditCity from './components/master-data/LocationMaster/City/AddViewEditCity';
import City from './components/master-data/LocationMaster/City/City';
import Designation from './components/master-data/DesignationMaster/Designation';
import AddViewEditDesignation from './components/master-data/DesignationMaster/AddViewEditDesignation';
import UpdateMyProfile from './components/Homepage/UpdateMyProfile';

import AddLabManagement from './components/pages/platform-owners/LabManagement/AddLabManagement';
import SignInPage from './components/Authentication/SignInPage';
import ForgotPassword from './components/Authentication/ForgotPassword';
import SignupPage from './components/Authentication/SignUpPage';

import ExpenseMaster from './components/LabView/Expense Master/ExpenseMaster';
import AddExpenseMaster from './components/LabView/Expense Master/AddExpenseMaster';
import StaffManagement from './components/master-data/StaffManagement/StaffManagement';
import AddStaffManagement from './components/master-data/StaffManagement/AddStaffManagement';
import Lab from './components/Dashboard/Lab';
import PaymentMode from './components/master-data/PaymentMode/PaymentMode';
import ResetPassword from './components/Authentication/ResetPassword';
import SignatureMaster from './components/LabView/SignatureMaster/signatureMaster';
import DiscountMaster from './components/LabView/DiscountMaster/DiscountMaster';
import CouponAndDiscountMaster from './components/LabView/CouponAndDiscountMaster/couponDiscountMaster';
import AddEditCouponDiscountMaster from './components/LabView/CouponAndDiscountMaster/add-editCouponDiscount';
import TestConfiguration from './components/LabView/TestConfiguration/TestConfiguration';
import AddEditTestConfiguration from './components/LabView/TestConfiguration/AddEditTestConfiguration';
import AddDiscountMaster from './components/LabView/DiscountMaster/AddDiscountMaster';
import ReferralMaster from './components/LabView/ReferralMaster/ReferralMaster';
import AddReferralMaster from './components/LabView/ReferralMaster/AddReferralMaster';
import SampleMappingMaster from './components/LabView/SampleMappingMaster/SampleMappingMaster';
import ExpenseCategory from './components/LabView/ExpenseCategory/ExpenseCategory';
import AddExpenseCategory from './components/LabView/ExpenseCategory/AddExpenseCategory';
import OrganizationMaster from './components/LabView/OrganizationMaster/OrganizationMaster';
import AddOraganizationMaster from './components/LabView/OrganizationMaster/AddOraganizationMaster';
import ProfileConfiguration from './components/LabView/ProfileConfiguration/ProfileConfiguration';
import AddprofileConfiguration from './components/LabView/ProfileConfiguration/AddProfileConfiguration';
import AddPhlebotmistMaster from './components/LabView/PhlebotomistMaster/AddPhlebotomistMaster';
import PhlebotmistMaster from './components/LabView/PhlebotomistMaster/PhlebotomistMaster';
import SuperAdminDashboard from './components/Dashboard/super-admin/super-admin-dashboard/super-admin-dashboard';
import Doctor from './components/LabView/Doctor-master/Doctor';
import SampleMaster from './components/LabView/SampleMaster/SampleMaster';
import AddSampleMaster from './components/LabView/SampleMaster/AddSampleMaster';
import AddDoctor from './components/LabView/Doctor-master/AddDoctor';
import BranchType from './components/master-data/BranchType/BranchType';
import AddBranchType from './components/master-data/BranchType/AddBranchType';
import ReportFooterSize from './components/master-data/ReportSetting/ReportFooterSize/ReportFooterSize';
import AddReportFooterSize from './components/master-data/ReportSetting/ReportFooterSize/AddReportFooterSize';
import ReportFontSize from './components/master-data/ReportSetting/ReportFontSize/ReportFontSize';
import AddReportFontType from './components/master-data/ReportSetting/ReportFontType/AddReportFontType';
import ReportFontType from './components/master-data/ReportSetting/ReportFontType/ReportFontType';
import BranchMaster from './components/LabView/BranchMaster/BranchMaster';
import AddBranchMaster from './components/LabView/BranchMaster/AddBranchMaster';
import PatientRegistration from './components/LabView/PatientRegistration/PatientRegistration';
import ReportPatientInfo from './components/master-data/ReportSetting/ReportPatientInfo/ReportPatientInfo';
import AddReportPatientInfo from './components/master-data/ReportSetting/ReportPatientInfo/AddReportPatientInfo';
import AddEditViewReportFontSize from './components/master-data/ReportSetting/ReportFontSize/AddEditViewReportFontSize';
import NewPatient from './components/LabView/PatientRegistration/NewPatient';
import ExistingPatient from './components/LabView/PatientRegistration/ExistingPatient';
import ReportDateFormat from './components/master-data/ReportSetting/ReportDateFormat/ReportDateFormat';
import AddEditViewReportDateFormat from './components/master-data/ReportSetting/ReportDateFormat/AddEditViewReportDateFormat';
import BarCodeManagement from './components/LabView/BarCode-Management/BarCodeManagement';
import BarCodeManagementDetails from './components/LabView/BarCode-Management/BarCodeManagementDetails';
import StorageManagement from './components/LabView/BarCode-Management/StorageManagement';
import Bill from './components/LabView/Bill/Bill';
import AddBill from './components/LabView/Bill/AddBill';
import AppointmentManagement from './components/LabView/AppointmentManagement/AppointmentManagement';
import AddAppointment from './components/LabView/AppointmentManagement/AddAppointment';
import CenterMaster from './components/LabView/CenterMaster/CenterMaster';
import PriceList from './components/LabView/PriceList/PriceList';
import PriceListProfileService from './components/LabView/PriceList/PriceListProfileService';
import PriceListTestService from './components/LabView/PriceList/PriceListTestService';
import RoleMaster from './components/LabView/RoleMaster/RoleMaster';
import AddRoleMaster from './components/LabView/RoleMaster/AddRoleMaster';
import ReportPaperSize from './components/master-data/ReportSetting/ReportPageSize/ReportPaperSize';
import AddReportPaperSize from './components/master-data/ReportSetting/ReportPageSize/AddReportPaperSize';
import { ReportHeaderSize } from './components/master-data/ReportSetting/ReportHeaderSize/ReportHeaderSize';
import ReportSignPosition from './components/master-data/ReportSetting/ReportSignPosition/ReportSignPosition';
import AddReportSignPosition from './components/master-data/ReportSetting/ReportSignPosition/AddReportSignPosition';
import SampleReception from './components/LabView/Sample-reception/SampleReception';
import OrganisationCommision from './components/LabView/Commission Management/OrganisationCommission/OrganisationCommision';
import AddOrganisationCommission from './components/LabView/Commission Management/OrganisationCommission/AddOrganisationCommission';
import AddEditViewReportHeaderSize from './components/master-data/ReportSetting/ReportHeaderSize/AddEditViewreportHeaderSize';
import ReferralCommission from './components/LabView/Commission Management/ReferralCommission/ReferralCommission';
import AddReferralCommission from './components/LabView/Commission Management/ReferralCommission/AddReferralCommission';

const Layout = () => {
    
    return (
        <div className='main-content'> 
            <Routes>
                <Route path="/" element={<SuperAdminDashboard />} />
                <Route path="/dashboard" element={<SuperAdminDashboard/>} />
                <Route path="/free-samples" element={<FreeSamples />} />
                <Route path="/premium" element={<PremiumPage />} />
                <Route path="/lab-management" element={<LabManagement />} />
                <Route path="/lab-management/add" element={<AddLabManagement />} />
                <Route path="/forgot-password" element={<ForgotPassword />} />
                <Route path="/reset-password" element={<ResetPassword />}/>
                <Route path="/masters/department" element={<DepartmentMaster />} />
                <Route path="/masters/department/add" element={<AddDepartment />} />
                <Route path="/staff-management" element={<StaffManagement />} />
                <Route path="/add" element={<AddStaffManagement />} />
                <Route path="/my-profile" element={<MyProfile />} />
                <Route path="/my-profile/edit" element={<UpdateMyProfile />} />
                <Route path="/masters/lab-type" element={<LabTypeMaster />} />
                <Route path="/masters/lab-type/add" element={<AddLabType />} />
                <Route path='/masters/location' element={<Country/>}>
                <Route path='/masters/location/country' element={<Country/>} />
                
                </Route>
                <Route path='/masters/location/country/add' element={<AddViewEditCountry/>} />
                <Route path='/masters/location/country/view-edit/:id' element={<AddViewEditCountry/>} />
                <Route path='/masters/location/state' element={<State/>} />
                <Route path='/masters/location/state/add' element={<AddViewEditState/>} />
                <Route path='/masters/location/state/view-edit/:id' element={<AddViewEditState/>} />
                <Route path='/masters/location/city' element={<City/>} />
                <Route path='/masters/location/city/add' element={<AddViewEditCity/>} />
                <Route path='/masters/location/city/view-edit/:id' element={<AddViewEditCity/>} />
                <Route path='/masters/relation' element={<Relation/>} />
                <Route path='/masters/relation/add' element={<AddViewEditRelation/>} />
                <Route path='/masters/relation/view-edit/:id' element={<AddViewEditRelation/>} />
                <Route path='/masters/designation' element={<Designation/>} />
                <Route path='/masters/designation/add' element={<AddViewEditDesignation/>} />
                <Route path='/masters/designation/view-edit/:id' element={<AddViewEditDesignation/>} />
                <Route path="/masters/support-issue" element={<SupportIssueTypeMaster/>} />
                <Route path="/masters/support-issue/add" element={<AddSupportIssueType/>} />              
                <Route path="/packages" element={<Package />} />
                <Route path="/packages/add" element={<AddPackage />} />
                <Route path='/Addrole' element={<AddRole />} />
                <Route path='/masters/role' element={<Role />} />
                <Route path="/masters/support-priority" element={<SupportPriorityMaster />} />
                <Route path="/masters/support-priority/add" element={<AddSupportPriority />} />
                <Route path="/sign_up" element={<SignupPage />} />
                <Route path="/expense-master" element={<ExpenseMaster />} />
                <Route path="/add-expense-master" element={<AddExpenseMaster />} />
                <Route path="/reset-password" element={<ResetPassword />} />

                <Route path="/masters/payment-mode" element={<PaymentMode />} />
                <Route path="/lab-view/signature_master" element={<SignatureMaster />} />
                <Route path="/lab/dashboard"  element={<Lab/>}/>
                <Route path="/lab-view/coupon-discount_master" element={<CouponAndDiscountMaster />} />
                <Route path="/add-edit-coupon-discount" element={<AddEditCouponDiscountMaster />} />
                <Route path="/add-edit-coupon-discount/:id" element={<AddEditCouponDiscountMaster />} />
                <Route path="/test-configuration" element={<TestConfiguration />} />
                <Route path="/add-edit-test-configuration" element={<AddEditTestConfiguration />} />
                <Route path="/add-edit-test-configuration/:id" element={<AddEditTestConfiguration />} />
                <Route path="/lab-view/discount" element={<DiscountMaster />} />
                <Route path="/addDiscountMaster" element={<AddDiscountMaster />} />
                <Route path="/addDiscountMaster/:id" element={<AddDiscountMaster />} />
                <Route path="/lab-view/referral" element={<ReferralMaster />} />
                <Route path="/lab-view/referral/add" element={<AddReferralMaster />} />
                <Route path="/doctor/master" element={<Doctor />} />
                <Route path="/doctor/master/add" element={<AddDoctor />} />
                <Route path="/lab-view/sampleMaster" element={<SampleMaster />} />
                <Route path="/lab-view/addSample" element={<AddSampleMaster/>} />           
                <Route path="/sample-mapping-master" element={<SampleMappingMaster />} />
                <Route path='/masters/branch-type' element={<BranchType/>} />
                <Route path="/masters/branch-type/add" element={<AddBranchType />} />
                <Route path="/masters/report-settings/paper-size" element={<ReportPaperSize/>}/>
                <Route path="/masters/report-settings/paper-size/add" element={<AddReportPaperSize/>}/>
                <Route path="/masters/report-settings/footer-size" element={<ReportFooterSize/>}/>
                <Route path="/masters/report-settings/footer-size/add" element={<AddReportFooterSize/>}/>
                <Route path="/masters/report-settings/sign-position" element={<ReportSignPosition/>}/>
                <Route path="/masters/report-settings/sign-position/add" element={<AddReportSignPosition/>}/>
                <Route path='/addPhlebotmistMaster' element={<AddPhlebotmistMaster/>}/>
                <Route path='/lab-view/phlebotmistMaster' element={<PhlebotmistMaster/>}/>
                <Route path="/lab-view/expense-category" element={<ExpenseCategory />} />
                <Route path="/lab-view/expense-category/add" element={<AddExpenseCategory />} />
                <Route path="/lab-view/organization-master" element={<OrganizationMaster/>} />
                <Route path="/lab-view/organization-master/add" element={<AddOraganizationMaster/>}/>
                <Route path="/masters/report-settings/patientInfo" element={<ReportPatientInfo />} />
                <Route path="/masters/report-settings/addPatientInfo" element={<AddReportPatientInfo />} />
                <Route path="/lab-view/profile-configuration" element={<ProfileConfiguration />} />
                <Route path='/lab-view/profile-configuration/add' element={<AddprofileConfiguration />} />
                <Route path='/addPhlebotmistMaster' element={<AddPhlebotmistMaster/>}/>
                <Route path='/lab-view/phlebotmistMaster' element={<PhlebotmistMaster/>}/>   
                <Route path='/lab-view/branchMaster' element={<BranchMaster />} />    
                <Route path='/lab-view/addbranch' element={<AddBranchMaster />} />    
                <Route path='/lab-view/lab-profile' element={<LabProfile/>}/>            
                <Route path='/masters/report-settings/font-type/add' element={<AddReportFontType />} />
                <Route path='/masters/report-settings/font-type' element={<ReportFontType />} />                       
                <Route path='/masters/report-settings/font-size' element={<ReportFontSize />} />
                <Route path='/masters/report-settings/font-size/add' element={<AddEditViewReportFontSize />} />
                <Route path='/masters/report-settings/date-format' element={<ReportDateFormat />} />
                <Route path='/masters/report-settings/date-format/add' element={<AddEditViewReportDateFormat />} />
                <Route path='/lab-view/barcode-management' element={<BarCodeManagement />} />  
                <Route path='/lab-view/organisation-commission' element={<OrganisationCommision />} /> 
                <Route path='/lab-view/organisation-commission/add' element={<AddOrganisationCommission />} /> 
                <Route path='/lab-view/barcode-management-details' element={<BarCodeManagementDetails />} /> 
                <Route path='/lab-view/sample-reception' element={<SampleReception />} /> 
                <Route path='/lab-view/storage-management' element={<StorageManagement />} />
                <Route path='/lab-view/bill' element={<Bill />} />
                <Route path='/lab-view/bill/add' element={<AddBill />} />
                <Route path='/lab-view/appointment-management' element={<AppointmentManagement />} />
                <Route path='/lab-view/appointment-management/add' element={<AddAppointment />} />
                <Route path='/lab-view/patientRegistration' element={<PatientRegistration/>}/>   
                <Route path='/lab-view/newPatient' element={<NewPatient/>}/>    
                <Route path='/lab-view/existing-patient'  element={<ExistingPatient/>}/>
                <Route path='/lab-view/center-master' element={<CenterMaster/>}/>               
                <Route path='/lab-view/price-list' element={<PriceList/>}/>               
                <Route path='/lab-view/price-list/test-service' element={<PriceListTestService/>}/>               
                <Route path='/lab-view/price-list/profile-service' element={<PriceListProfileService/>}/>               
                <Route path='/lab-view/rolemaster' element={<RoleMaster />} />
                <Route path='/lab-view/addRoleMaster' element={<AddRoleMaster />} />
                <Route path='/masters/report-settings/header-size' element={<ReportHeaderSize/>} />
                <Route path='/masters/report-settings/header-size/add' element={<AddEditViewReportHeaderSize/>} />
                <Route path='/lab-view/referral-commission' element={<ReferralCommission/>}/>
                <Route path='/lab-view/referral-commission/add' element={<AddReferralCommission/>}/>
            </Routes>
        </div>
    )
}

export default Layout;