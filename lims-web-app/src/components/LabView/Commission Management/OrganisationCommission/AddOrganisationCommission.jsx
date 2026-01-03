import React, { useEffect, useState } from "react";
import InputField from "../../../Homepage/InputField";
import DropDown from "../../../Re-usable-components/DropDown";
import { useNavigate } from "react-router-dom";
import LimsTable from "../../../LimsTable/LimsTable";
import { getAllOrganizationMaster } from "../../../../services/LabViewServices/OrganizationMasterService";
import { getAllTestConfigurations } from "../../../../services/LabViewServices/testConfigurationService";
import { getAllprofiles } from "../../../../services/LabViewServices/ProfileConfigurationService";
import { set } from "date-fns";

const AddOrganisationCommission = () => {
  const navigate = useNavigate();
  const [organizationCommissionData, setOrganizationCommissionData] = useState({
    tests: [],
    profile: [],
  });
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [organizationDetails, setOrganizationDetails] = useState([]);
  const [isTest, setIsTest] = useState(true);
  const [isConstant, setIsConstant] = useState(true);
  const [testData, setTestData] = useState([]);
  const [profileData, setProfileData] = useState([]);
  const labId = "0e681d43-a8d2-47fd-9298-31c7872c8a59";
  const columns = [
    { key: "slNo", label: "Sl. No." },
    { key: "testName", label: "Test or Profile Name" },
    { key: "commision", label: "Commision %", editable: true },
  ];
  const data = [{ names: "ABC" }, { names: "XYZ" }];
  const handleSubmit = () => {
    console.log("Submitted Successfully...");
  };
  const tableData = [...profileData, ...testData];
  const handleDropDownChange = (event) => {
    const { name, value } = event?.target;
    setTimeout(() => {
      setOrganizationCommissionData((prev) => ({
        ...prev,
        [name]: value,
      }));
    }, 0);
  };

  const fetchAllOrganizations = async () => {
    const createdBy = "69323c18-6e5f-4d35-af42-fe5a898def41";
    const response = await getAllOrganizationMaster(
      currentPage,
      pageSize,
      createdBy
    );
    setOrganizationDetails(response.data);
  };

  const fetchAllTestConfiguration = async () => {
    const response = await getAllTestConfigurations(labId, null, null);
    if (response?.data) {
      const transformedData = response.data.map((item) => ({
        testName: item.sampleMapping?.testName || "N/A",
        testPrice: item.testPrice || 0,
      }));
      setTestData(transformedData);
    }
  };

  const fetchAllProfileConfiguration = async () => {
    const createdBy = "3fa85f64-5717-4562-b3fc-2c963f66af77";
    const response = await getAllprofiles(createdBy, "");
    if (response?.data) {
      const transformedData = response?.data.map((item) => ({
        id: item.id,
        profileName: item.profileName,
        testName: item.tests?.[0]?.testName || "",
        totalAmount: item.totalAmount,
      }));
      setProfileData(transformedData);
    }
  };
  useEffect(() => {
    fetchAllOrganizations();
    fetchAllTestConfiguration();
    fetchAllProfileConfiguration();
  }, []);

  return (
    <div className="OrganisationCommisionMapping">
      <div className="title">Organisation Commission Mapping</div>
      <div className="ocm-child">
        <div className="ocm-title">Organisation Details</div>
        <div className="ocm-dropdown">
          <DropDown
            label="Organisation"
            name="Organisation"
            options={organizationDetails}
            fieldName="name"
            required
          />
        </div>
      </div>
      <div className="ocm-child">
        <div className="ocm-title">Service Details</div>
        <div className="ocm-radio">
          <InputField
            type="radio"
            label="Test"
            name="serviceDetails"
            value="Test"
            onChange={() => setIsTest(true)}
          />
          <InputField
            type="radio"
            label="Profile"
            name={"serviceDetails"}
            value="Profile"
            onChange={() => setIsTest(false)}
          />
        </div>
        <div className="ocm-dropdown">
          {isTest ? (
            <DropDown
              label="Test Name"
              name="tests"
              options={testData}
              fieldName="testName"
              multiple={true}
              onChange={handleDropDownChange}
              value={organizationCommissionData.tests}
              required
            />
          ) : (
            <DropDown
              label="Profile Name"
              name="profile"
              options={profileData}
              fieldName="profileName"
              multiple={true}
              // onChange={handleDropDownChange}
              value={organizationCommissionData.profile}
              required
            />
          )}
        </div>
        <div className="ocm-dropdown-content">
          {isTest
            ? organizationCommissionData.tests.map((item, index) => (
                <div className="ocm-content" key={index}>
                  <span>{item.testName}</span>
                  <span>{item.testPrice}</span>
                </div>
              ))
            : organizationCommissionData.profile.map((item, index) => (
                <div className="ocm-content" key={index}>
                  <span>{item.profileName}</span>
                  <span>{item.testPrice}</span>
                </div>
              ))}
        </div>
      </div>
      <div className="ocm-child">
        <div className="ocm-title">Commission Details</div>
        <div className="ocm-radio">
          <InputField
            type="radio"
            label="Constant"
            name={"commissionDetails"}
            value="Constant"
            onChange={() => setIsConstant(true)}
          />
          <InputField
            type="radio"
            label="Variable"
            name={"commissionDetails"}
            value="Variable"
            onChange={() => setIsConstant(false)}
          />
        </div>
        <div className="ocm-commission-inputs">
          {isConstant ? (
            <>
              <InputField
                label="Commission %"
                name="commission"
                type="text"
                placeholder={"Enter Constant Percentage"}
                required
              />
              <InputField
                label="Total Commission Amount"
                name="commission"
                type="text"
                placeholder={"Enter Total Commission"}
              />
            </>
          ) : (
            <>
              <LimsTable columns={columns} data={data} />
            </>
          )}
        </div>
      </div>
      <div className="ocm-buttons">
        <button
          className="btn-clear"
          onClick={() => navigate("/lab-view/organisation-commission")}
        >
          Back
        </button>
        <button className="btn-primary" onClick={handleSubmit}>
          Save
        </button>
      </div>
    </div>
  );
};

export default AddOrganisationCommission;
