import React, { useEffect, useState } from "react";
import InputField from "../Homepage/InputField";
import DropDown from "../Re-usable-components/DropDown";
import "./AddPackage.css";
import Swal from "../Re-usable-components/Swal";
import { useLocation, useNavigate } from "react-router-dom";
import close from "../../assets/icons/add-close.svg";

const AddPackage = () => {
  const [formData, setFormData] = useState({
    planName: "",
    monthlyPrice: "",
    annualPrice: "",
    features: [],
  });

  const [popupConfig, setPopupConfig] = useState(null);
  const [mode, setMode] = useState("");
  const [viewAll, setViewAll] = useState(false);
  const [viewData, setViewData] = useState([]);

  const location = useLocation();
  const navigate = useNavigate();
  const navigateUrl = "/packages";

  useEffect(() => {
    const packageDetails = location.state?.packageDetails;
    const modeFromState = location.state?.mode;
    const selectedFeatures = packageDetails?.features || [];

    if (modeFromState) {
      setMode(modeFromState);

      if (modeFromState === "edit" || modeFromState === "view") {
        const formattedFeatures = selectedFeatures.map((feature) => ({
          value: feature,
          label: feature,
        }));

        setFormData({
          planName: packageDetails?.planName || "",
          monthlyPrice: packageDetails?.monthlyPrice || "",
          annualPrice: packageDetails?.annualPrice || "",
          features: formattedFeatures,
        });
      }
    }
  }, []);

  const handleInputChange = (e) => {
    const { name, value } = e.target;

    const sanitizedValue =
      name === "monthlyPrice" || name === "annualPrice"
        ? value.replace(/[^0-9.]/g, "")
        : value;

    setFormData({ ...formData, [name]: sanitizedValue });
  };

  const handleSelectionChange = (name, value) => {
    setFormData((prevFormData) => ({
      ...prevFormData,
      [name]: value,
    }));
  };

  const handleSave = () => {
    const successTitle =
      mode === "edit" ? "Updated Successfully" : "Added Successfully";

    setPopupConfig({
      icon: "success",
      title: successTitle,
      onClose: () => {
        setPopupConfig(null);
        navigate(navigateUrl);
      },
    });
  };

  const handleClose = () => {
    setPopupConfig(null);
    navigate(navigateUrl);
  };

  const handleCancel = () => {
    setViewAll(false);
  };

  const handleBack = () => {
    navigate(navigateUrl);
  };

  const handleViewAll = () => {
    if (formData.features && formData.features.length > 0) {
      const featureLabels = formData.features.map((feature) => feature.label);
      setViewData(featureLabels);
      setViewAll(true);
    }
  };

  const handleRemoveFeature = (feature) => {
    const updatedFeatures = formData.features.filter(
      (f) => f.value !== feature.value
    );
    setFormData({ ...formData, features: updatedFeatures });
  };

  const dropdownOptions = [
    { value: "Notification", label: "Notification" },
    { value: "Dashboard", label: "Dashboard" },
    { value: "Enterprise", label: "Enterprise" },
  ];

  return (
    <div className="addPackage">
      <span className="tittleAdd">
        {mode === "view"
          ? "View Package"
          : mode === "edit"
          ? "Edit Package"
          : "Add Package"}
      </span>
      <div className="Package-form-group">
        <div className="input-fields-root">
          <InputField
            label="Plan Name"
            type="text"
            placeholder="Enter Plan Name"
            value={formData.planName}
            onChange={handleInputChange}
            name="planName"
            required
            disabled={mode === "view"}
          />
          <InputField
            label="Annual Price"
            type="text"
            placeholder="Enter Annual Price"
            value={formData.annualPrice}
            onChange={handleInputChange}
            name="annualPrice"
            disabled={mode === "view"}
          />
        </div>
        <div className="input-fields-root">
          <InputField
            label="Monthly Price"
            type="text"
            placeholder="Enter Monthly Price"
            value={formData.monthlyPrice}
            onChange={handleInputChange}
            name="monthlyPrice"
            disabled={mode === "view"}
          />
          <div className="forViewAll">
            <DropDown
              label="Features"
              placeholder="Select Features"
              options={dropdownOptions}
              value={dropdownOptions.value}
              onChange={(selectedOptions) =>
                handleSelectionChange("features", selectedOptions)
              }
              fieldName="label"
              multiple={true}
              required
              disabled={mode === "view"}
            />
            <span className="viewAll" onClick={handleViewAll}>
              View All
            </span>
          </div>
        </div>
      </div>

      <div className="form-btns">
        <button className="btn-secondary" type="button" onClick={handleBack}>
          Back
        </button>
        {mode !== "view" && (
          <button className="btn-primary" type="submit" onClick={handleSave}>
            {mode === "edit" ? "Update" : "Save"}
          </button>
        )}
      </div>

      {popupConfig && <Swal {...popupConfig} />}
      {viewAll && <div className="overlay"></div>}

      {viewAll && (
        <>
          <div className="packageViewAll">
            <div className="packageViewAll-header">
              <p className="packageViewAll-title">View All</p>
            </div>
            {viewData.map((item, index) => (
              <div className="chipIn" key={index}>
                {item}
                {mode === "edit" && (
                  <span>
                    <img
                      src={close}
                      alt="Close"
                      className="close-btn"
                      onClick={() => handleRemoveFeature({ value: item })}
                    />
                  </span>
                )}
              </div>
            ))}
            <img
              src={close}
              alt="Close"
              className="close-btn"
              onClick={handleCancel}
            />
          </div>
        </>
      )}
    </div>
  );
};

export default AddPackage;
