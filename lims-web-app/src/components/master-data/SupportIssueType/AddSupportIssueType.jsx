import React, { useState, useEffect } from "react";
import { addSupportIssueType, updateSupportIssueType } from "../../../services/supportedIssueTypeService";
import { useLocation, useNavigate } from "react-router-dom";
import InputField from "../../Homepage/InputField";
import Swal from "../../Re-usable-components/Swal";

const AddSupportIssueType = () => {
  const [supportIssueTypeName, setSupportIssueTypeName] = useState("");
  const [viewMode, setViewMode] = useState(false);
  const [popupConfig, setPopupConfig] = useState(null);
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    const supportIssueTypeDetails = location.state?.supportIssueTypeDetails;
    const mode = location.state?.mode;
    if (supportIssueTypeDetails) {
      setSupportIssueTypeName(supportIssueTypeDetails.data.name);
    }
    if (mode === "view") {
      setViewMode(true);
    } else if (mode === "edit") {
      setViewMode(false);
    }
  }, [location.state]);

  const validateInput = () => {
    const trimmedName = supportIssueTypeName.trim();
    if (trimmedName === "") {
      setError({ supportIssueTypeName: "Support Issue Type is required" });
      return false;
    }
    setError(null);
    return true;
  };

  const handleInputChange = (event) => {
    const { value } = event.target;
    if (!viewMode) {
      const alphabetRegex = /^[A-Za-z\s]*$/;
      if (!alphabetRegex.test(value)) {
        return;
      } else {
        setError(null);
      }
      setSupportIssueTypeName(value);
    }
  };

  const handleAdd = async () => {
    if (!validateInput()) return;
    try {
      const response = await addSupportIssueType({
        name: supportIssueTypeName,
      });
      if (response.statusCode === "200 OK") {
        setPopupConfig({
          icon: 'success',
          title: 'Added Successfully',
          onClose: () => navigate("/masters/support-issue"),
        });
      } else {
        setError({ supportIssueTypeName: 'Support Issue Type already exists. Please try again.', type: 'error' });
      }
    } catch (error) {
      setError({ supportIssueTypeName: "Failed to add Support Issue Type. Please try again.", type: "error" });
    }
  };

  const handleUpdate = async () => {
    if (!validateInput()) return;
    try {
      const response = await updateSupportIssueType(
        location.state.supportIssueTypeDetails.data.id,
        { name: supportIssueTypeName }
      );
      if (response?.status === 200 || response?.statusCode === "200 OK") {
        setPopupConfig({
          icon: "success",
          title: "Updated Successfully",
          onClose: () => navigate("/masters/support-issue"),
        });
      } else {
        setError({ supportIssueTypeName: 'Support Issue Type already exists. Please try again.', type: 'error' });
      }
    } catch (error) {
      console.error(error);
      setError({ supportIssueTypeName: "Failed to update Support Issue Type. Please try again.", type: "error" });
    }
  };

  const handleNavigate = () => {
    navigate("/masters/support-issue");
  };

  return (
    <div className="add-master-container">
      <div>
        <div className="title">
          {viewMode
            ? "View Support Issue Type"
            : location.state?.mode === "edit"
              ? "Edit Support Issue Type"
              : "Add Support Issue Type"}
        </div>

        <div className="input-inner-div">
          <div className="input-inner-div-child">
            <InputField
              label="Support Issue Type"
              type="text"
              placeholder="Enter Here"
              required
              value={supportIssueTypeName}
              onChange={handleInputChange}
              disabled={viewMode}
              error={error?.supportIssueTypeName}
            />
          </div>
        </div>
      </div>

      <div className="button-div-container">
        {viewMode ? (
          <button className="btn-secondary" onClick={handleNavigate}>
            Back
          </button>
        ) : location.state?.mode === "edit" ? (
          <>
            <button className="btn-secondary" onClick={handleNavigate}>
              Back
            </button>
            <button className="btn-primary" onClick={handleUpdate}>
              Update
            </button>
          </>
        ) : (
          <>
            <button className="btn-secondary" onClick={handleNavigate}>
              Back
            </button>
            <button className="btn-primary" onClick={handleAdd}>
              Save
            </button>
          </>
        )}
      </div>

      {popupConfig && (
        <Swal
          icon={popupConfig.icon}
          title={popupConfig.title}
          onButtonClick={popupConfig.onButtonClick}
          onClose={popupConfig.onClose}
        />
      )}
    </div>
  );
};

export default AddSupportIssueType;
