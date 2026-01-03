import React, { useEffect, useState } from "react";
import Swal from "../../../Re-usable-components/Swal";
import InputField from "../../../Homepage/InputField";
import { useLocation, useNavigate } from "react-router-dom";
import Error from "../../../Re-usable-components/Error";
import { addReportSignPosition, updateReportSignPosition} from "../../../../services/MasterDataService/ReportSettingsMaster/ReportSignPositionService";

const AddReportSignPosition = () => {
  const [signPosition, setSignPosition] = useState("");
  const [id, setId] = useState(null);
  const [viewMode, setViewMode] = useState(false);
  const [editMode, setEditMode] = useState(false);
  const [popupConfig, setPopupConfig] = useState(null);
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const location = useLocation();
  const { previousSearch } = location.state || {};
  const userId = "07131f7b-50c0-4889-bfae-777579d19f31";

  useEffect(() => {
    const signPositionDetails = location.state?.signPositionDetails;
    const mode = location.state?.mode;
    if (signPositionDetails) {
      setSignPosition(signPositionDetails.signPosition);
      setId(signPositionDetails.id);
    }
    if (mode === "view") {
      setViewMode(true);
    } else if (mode === "edit") {
      setViewMode(false);
      setEditMode(true);
    }
  }, [location.state]);

  const handleInputChange = (event) => {
    if (!viewMode) {
      setSignPosition(event.target.value);
      setError(null);
    }
  };

  const validateInput = () => {
    if (!signPosition.trim()) {
      setError({ message: "Sign Position is required.", type: "error" });
      return false;
    }
    return true;
  };
  const navigateBack = () => {
    navigate("/masters/report-settings/sign-position", {
      state: { previousSearch },
    });
  };

  const handleSave = async () => {
    if (!validateInput()) return;
    try {
      let response;
      const reportSignPositionData = { signPosition: signPosition.trim() };

      if (id) {
        response = await updateReportSignPosition(
          id,
          reportSignPositionData,
          userId
        );
      } else {
        response = await addReportSignPosition(reportSignPositionData, userId);
      }
      setPopupConfig({
        icon: "success",
        title: id ? "Updated Successfully" : "Added Successfully",
        onClose: () => navigate("/masters/report-settings/sign-position"),
      });
    } catch (error) {
      setError({
        message: "Operation failed. Please try again.",
        type: "error",
      });
    }
  };
  return (
    <div className="profile-container">
      <div>
        <h2 className="title">
          {viewMode
            ? "View Sign Position"
            : id
              ? "Edit Sign Position"
              : "Add Sign Position"}
        </h2>
        <div className="report-setting-inner-container">
          <div className="report-setting-sub-container">
            <InputField
              label="Sign Position"
              name="signPosition"
              type="text"
              placeholder="Enter Sign Position"
              className="input-field-content"
              required
              value={signPosition}
              onChange={handleInputChange}
              readOnly={viewMode}
            />
            {error && <Error message={error.message} type={error.type} />}
          </div>
        </div>
      </div>
      <div className="report-setting-button-div-container">
        <button className="btn-secondary" onClick={navigateBack}>
          Back
        </button>
        {!viewMode && !editMode && (
          <button className="btn-primary" onClick={handleSave}>
            Save
          </button>
        )}
        {!viewMode && editMode && (
          <button className="btn-primary" onClick={handleSave}>
            Update
          </button>
        )}
      </div>
      {popupConfig && (
        <Swal
          icon={popupConfig.icon}
          title={popupConfig.title}
          text={popupConfig.text}
          onClose={popupConfig.onClose}
        />
      )}
    </div>
  );
};

export default AddReportSignPosition;
