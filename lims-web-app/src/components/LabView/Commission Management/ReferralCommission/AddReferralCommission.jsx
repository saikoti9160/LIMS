import React, { useEffect, useState } from "react";
import InputField from "../../../Homepage/InputField";
import DropDown from "../../../Re-usable-components/DropDown";
import { useLocation, useNavigate } from "react-router-dom";
import { referralsGetAll } from "../../../../services/LabViewServices/referralMasterService";
import { getAllTestConfigurations } from "../../../../services/LabViewServices/testConfigurationService";
import LimsTable from "../../../LimsTable/LimsTable";
import { saveReferralCommission } from "../../../../services/LabViewServices/ReferralCommissionService";
import { getAllprofiles } from "../../../../services/LabViewServices/ProfileConfigurationService";

function AddReferralCommission() {
  const navigate = useNavigate();
  const navigateUrl = "/lab-view/referral-commission";
  const location = useLocation();
  const [referralList, setReferralList] = useState([]);
  const [selectedReferral, setSelectedReferral] = useState("");
  const [testNameList, setTestNameList] = useState([]);
  const [profileNameList, setProfileNameList] = useState([]);
  const [selectedTestName, setSelectedTestName] = useState("");
  const [selectedProfileName, setSelectedProfileName] = useState("");
  const [viewMode, setViewMode] = useState(false);
  const [commissionType, setCommissionType] = useState("");
  const [serviceRadioType, setServiceRadioType] = useState("");
  const createdBy = "3fa85f64-5717-4562-b3fc-2c963f66afa6";

  const columns = [
    { key: "slNo", label: "Sl. No." },
    { key: "names", label: "Test or Profile Name" },
    { key: "commision", label: "Commision %", editable: true },
  ];
  const data = [{ names: "ABC" }, { names: "XYZ" }];
  const handleReferralChange = (event) => {
    setSelectedReferral(event.target.value);
  };
  const handleTestChange = (event) => {
    setSelectedTestName(event.target.value);
  };

  const handleProfileChange = (event) => {
    setSelectedProfileName(event.target.value);
  };

  const fetchProfileNameList = async () => {
    try {
      const response = await getAllprofiles(createdBy);
      const profileNameOptions = response.data.map((profileName) => ({
        value: profileName.id,
        label: profileName.profileName,
      }));
      setProfileNameList(profileNameOptions);
    } catch (error) {
      console.error("Error fetching test name list:", error);
    }
  };
  const fetchTestNameList = async () => {
    try {
      const response = await getAllTestConfigurations(
        "06228e13-e32b-4420-b980-0f6b8744e170"
      );
      const testNameOptions = response.data.map((testName) => ({
        label: testName.sampleMapping.testName,
        value: testName.sampleMapping.id,
      }));
      setTestNameList(testNameOptions);
    } catch (error) {
      console.error("Error fetching test name list:", error);
    }
  };

  const fetchReferralList = async () => {
    try {
      const response = await referralsGetAll(createdBy);
      const referralOptions = response.data.map((referral) => ({
        label: referral.referralName,
        value: referral.id,
      }));
      setReferralList(referralOptions);
    } catch (error) {
      console.error("Error fetching referral list:", error);
    }
  };

  const handleBack = () => {
    navigate(navigateUrl);
  };

  const handleSubmit = async () => {
    try {
      const response = await saveReferralCommission(data, createdBy);
    } catch (error) {
      console.error("Error in handleSubmit:", error);
    }
  };

  useEffect(() => {
    fetchReferralList();
  }, []);

  useEffect(() => {
    if (serviceRadioType === "Test") {
      fetchTestNameList();
    } else if (serviceRadioType === "Profile") {
      fetchProfileNameList();
    }
  }, [serviceRadioType]);

  useEffect(() => {
    const mode = location.state?.mode;
    if (mode === "view") {
      setViewMode(true);
    }
  }, [location.state]);

  return (
    <div className="OrganisationCommisionMapping">
      <div className="title">
        {viewMode
          ? "View Referral Commission Mapping"
          : location.state?.mode === "edit"
          ? "Edit Referral Commission Mapping"
          : "Referral Commission Mapping"}
      </div>
      <div className="ocm-child">
        <div className="ocm-title">Referral Details</div>
        <div className="ocm-dropdown">
          <DropDown
            label="Referral"
            name="Referral"
            options={referralList}
            fieldName="label"
            onChange={handleReferralChange}
            disabled={viewMode}
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
            name={"serviceDetails"}
            value="Test"
            checked={serviceRadioType === "Test"}
            onChange={() => setServiceRadioType("Test")}
            disabled={viewMode}
          />
          <InputField
            type="radio"
            label="Profile"
            name={"serviceDetails"}
            value="Profile"
            checked={serviceRadioType === "Profile"}
            onChange={() => setServiceRadioType("Profile")}
            disabled={viewMode}
          />
        </div>
        <div className="ocm-dropdown">
          {serviceRadioType === "Test" ? (
            <DropDown
              label="Test Name"
              name="testName"
              value={selectedTestName}
              multiple={true}
              options={testNameList}
              fieldName="label"
              onChange={handleTestChange}
              disabled={viewMode}
              required
            />
          ) : (
            <DropDown
              label="Profile Name"
              name="profieName"
              value={selectedProfileName}
              multiple={true}
              options={profileNameList}
              fieldName="label"
              onChange={handleProfileChange}
              disabled={viewMode}
              required
            />
          )}
        </div>
        <div className="ocm-dropdown-content">
          <div className="ocm-content"></div>
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
            checked={commissionType === "Constant"}
            onChange={() => setCommissionType("Constant")}
            disabled={viewMode}
          />
          <InputField
            type="radio"
            label="Variable"
            name={"commissionDetails"}
            value="Variable"
            checked={commissionType === "Variable"}
            onChange={() => setCommissionType("Variable")}
            disabled={viewMode}
          />
        </div>
        {commissionType === "Variable" ? (
          <div className="ocm-commission-inputs-table">
            <LimsTable columns={columns} data={data} />
            <div className="total-commission-amount">
              <InputField
                label="Total Commission Amount"
                name="commission"
                type="text"
                placeholder={"Enter Total Commission"}
                disabled={viewMode}
              />
            </div>
          </div>
        ) : (
          <div className="ocm-commission-inputs">
            <InputField
              label="Commission %"
              name="commission"
              type="text"
              placeholder={"Enter Constant Percentage"}
              disabled={viewMode}
              required
            />
            <InputField
              label="Total Commission Amount"
              name="commission"
              type="text"
              placeholder={"Enter Total Commission"}
              disabled={viewMode}
            />
          </div>
        )}
      </div>
      <div className="ocm-buttons">
        <button className="btn-clear" onClick={handleBack}>
          Back
        </button>
        {!viewMode && (
          <button className="btn-primary" onSubmit={handleSubmit}>
            {location.state?.mode === "edit" ? "Update" : "Save"}
          </button>
        )}
      </div>
    </div>
  );
}

export default AddReferralCommission;
