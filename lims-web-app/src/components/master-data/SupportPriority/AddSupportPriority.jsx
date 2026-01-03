import React, { useState, useEffect } from "react";
import { addSupportPriority, updateSupportPriority } from "../../../services/supportPriorityService";
import { useLocation, useNavigate } from "react-router-dom";
import InputField from "../../Homepage/InputField";
import Swal from "../../Re-usable-components/Swal";

const AddSupportPriority = () => {
  const [priorityName, setPriorityName] = useState("");
  const [viewMode, setViewMode] = useState(false);
  const [popupConfig, setPopupConfig] = useState(null);
  const [error, setError] = useState({});
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    const priorityDetails = location.state?.supportPriorityDetails;
    const mode = location.state?.mode;
    if (priorityDetails) {
      setPriorityName(priorityDetails.data.name);
    }
    if (mode === "view") {
      setViewMode(true);
    } else if (mode === "edit") {
      setViewMode(false);
    }
  }, [location.state]);

  const validateInput = () => {
    const trimmedName = priorityName.trim();
    if (trimmedName === "") {
      setError({ priorityName: "Support Priority is required" });
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
      setPriorityName(value);
    }
  };

  const handleAdd = async () => {
    if (!validateInput()) return;
    try {
      const response = await addSupportPriority({ name: priorityName });

      if (response.statusCode === "200 OK") {
        setPopupConfig({
          icon: 'success',
          title: 'Added Successfully',
          onClose: () => navigate("/masters/support-priority"),
        });
      } else {
        setError({ priorityName: 'Support Priority already exists. Please try again.', type: 'error' });
      }
    } catch (error) {
      setError({ priorityName: "Failed to add Support Priority. Please try again.", type: "error" });
    }
  };

  const handleUpdate = async () => {
    if (!validateInput()) return;

    try {
      const response = await updateSupportPriority(
        location.state.supportPriorityDetails.data.id,
        { name: priorityName }
      );
      if (response.statusCode === "200 OK") {
        setPopupConfig({
          icon: "success",
          title: "Updated Successfully!",
          onClose: () => navigate("/masters/support-priority"),
        });
      } else {
        setError({ priorityName: "Support Priority already exists. Please try again.", type: "error" });
      }
    } catch (error) {
      console.error(error);
      setError({ priorityName: "Failed to update Support Priority. Please try again.", type: "error" });
    }
  };

  const handleNavigate = () => {
    navigate("/masters/support-priority");
  };

  return (
    <div className="add-master-container">
      <div>
        <div className="title">
          {viewMode ? "View Support Priority" : location.state?.mode === "edit" ? "Edit Support Priority" : "Add Support Priority"}
        </div>

        <div className="input-inner-div">
          <div className="input-inner-div-child">
            <InputField
              label="Support Priority"
              type="text"
              placeholder="Enter Here"
              required
              value={priorityName}
              onChange={handleInputChange}
              disabled={viewMode}
              error={error?.priorityName}
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
          text={popupConfig.text}
          onButtonClick={popupConfig.onButtonClick}
          onClose={popupConfig.onClose}
        />
      )}
    </div>
  );
};

export default AddSupportPriority;
