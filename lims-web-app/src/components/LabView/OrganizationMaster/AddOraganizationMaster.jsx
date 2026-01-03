import React, { useEffect, useState } from "react";
import InputField from "../../Homepage/InputField";
import DropDown from "../../Re-usable-components/DropDown";
import { useLocation, useNavigate } from "react-router-dom";
import Swal from "../../Re-usable-components/Swal";
import {  getAllCities,  getAllContinents,  getAllCountries,  getAllStates } from "../../../services/locationMasterService";
import { organizationMasterSave, organizationMasterUpdate } from "../../../services/LabViewServices/OrganizationMasterService";
const AddOraganizationMaster = () => {
  const [selectedOption, setSelectedOption] = useState("");
  const [popup, setPopup] = useState(null);
  const navigate = useNavigate();
  const location = useLocation();
  const [viewMode, setViewMode] = useState(false);
  const [editMode, setEditMode] = useState(false);
  const [formData, setFormData] = useState({
    name: "",
    phoneCode: "",
    phoneNumber: "",
    email: "",
    password: "",
    country: "",
    state: "",
    city: "",
    pinCode: "",
    address: "",
    labId: "",
    roleId: "",
    comments: "",
    invoiceGenerationFrequency: "",
    customFrequency: "",
    paymentDetails: {
      prepaid: [{ prepaidAdvance: "", paymentMode: "" }],
      postPaid: [{ postPaidCreditLimit: "", paymentMode: "" }],
    },
    patientConfiguration: {
      sendReportsAndBills: false,
      sendReportsOnly: false,
    },
    reportAccessConfiguration: {
      showHeader: false,
      showFooter: false,
      headerText: "",
      footerText: "",
    },
    billAccessConfiguration: {
      showHeader: false,
      showFooter: false,
      headerText: "",
      footerText: "",
    },
    labId: "0e681d43-a8d2-47fd-9298-31c7872c8a59",
    roleId: "ea8ff0df-2832-4ffe-aaa1-d34d4f3b74a8",
  });

  const paymentMode = [
    { enum: "CASH", name: "Cash" },
    { enum: "CREDIT_CARD", name: "Credit card" },
    { enum: "DEBIT_CARD", name: "Debit card" },
    { enum: "NET_BANKING", name: "Net banking" },
    { enum: "UPI", name: "UPI" },
    { enum: "CHEQUE", name: "Cheque" },
    { enum: "DEMAND_DRAFT", name: "Demand draft" },
  ];

  const [countries, setCountries] = useState([]);
  const [states, setStates] = useState([]);
  const [cities, setCities] = useState([]);

  useEffect(() => {
    const fetchCountries = async () => {
      let response = await getAllCountries("", [], 0, 250, "countryName");
      setCountries(response.data);
      setStates([]);
      setCities([]);
    };
    fetchCountries();
  }, []);

  useEffect(() => {
    const fetchStates = async () => {
      if (formData.country) {
        let response = await getAllStates("", [formData.country], 0, 250, "stateName");
        setStates(response.data);
        setCities([]);
      } else {
        setStates([]);
      }
    };
    fetchStates();
  }, [formData.country]);

  useEffect(() => {
    const fetchCities = async () => {
      if (formData.state) {
        let response = await getAllCities( "", [formData.state], 0, 250, "cityName");
        setCities(response.data);
      } else {
        setCities([]);
      }
    };
    fetchCities();
  }, [formData.state]);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevFormData) => ({
      ...prevFormData,
      [name]: value,
    }));
  };
  const handlePhoneChange = (object) => {
    const { countryCode, phoneNumber } = object;
    setFormData((prevFormData) => ({
      ...prevFormData,
      phoneCode: countryCode,
      phoneNumber: phoneNumber,
    }));
  };

  const handleDropdownChange = (fieldName, object) => {
    const { name, value } = object.target;
    setFormData((prevFormData) => ({
      ...prevFormData,
      [name]: value[fieldName],
      ...(name === "country" ? { state: null, city: null } : {}),
      ...(name === "state" ? { city: null } : {}),
    }));
  };

  const handlePatientConfigChange = (option) => {
    setFormData((prev) => {
      const isAlreadySelected =
        (option === "Send reports and bills" &&
          prev.patientConfiguration.sendReportsAndBills) ||
        (option === "Send reports only" &&
          prev.patientConfiguration.sendReportsOnly);

      return {
        ...prev,
        patientConfiguration: isAlreadySelected
          ? { sendReportsAndBills: false, sendReportsOnly: false }
          : {
              sendReportsAndBills: option === "Send reports and bills",
              sendReportsOnly: option === "Send reports only",
            },
      };
    });
  };

  const handleAccessChange = (section, option) => {
    setFormData((prev) => ({
      ...prev,
      [section]: {
        ...prev[section],
        showHeader:
          option.toLowerCase() === "show header"
            ? !prev[section].showHeader
            : prev[section].showHeader,
        showFooter:
          option.toLowerCase() === "show footer"
            ? !prev[section].showFooter
            : prev[section].showFooter,
      },
    }));
  };

  const handlePaymentChange = (index, type, eventOrValue) => {
    setFormData((prev) => {
      const updatedPayments = [...prev.paymentDetails[type]];
      if (typeof eventOrValue.target.value === "string") {
        updatedPayments[index] = {
          ...updatedPayments[index],
          [eventOrValue.target.name]: eventOrValue.target.value,
        };
      } else {
        updatedPayments[index] = {
          ...updatedPayments[index],
          paymentMode: eventOrValue.target.value.enum,
        };
      }
      return {
        ...prev,
        paymentDetails: { ...prev.paymentDetails, [type]: updatedPayments },
      };
    });
  };

  const [isCustomSelected, setIsCustomSelected] = useState(false);
  const handleInvoiceChange = (event) => {
    const { value } = event.target;
    setFormData((prev) => ({
      ...prev,
      invoiceGenerationFrequency:
        prev.invoiceGenerationFrequency === value ? "" : value,
      customFrequency: "",
    }));
    setIsCustomSelected(false);
  };

  const handleCustomCheckboxChange = () => {
    setIsCustomSelected((prev) => !prev);
    setFormData((prev) => ({
      ...prev,
      invoiceGenerationFrequency: "",
      customFrequency: isCustomSelected ? "" : prev.customFrequency,
    }));
  };

  const addPaymentField = (type) => {
    setFormData((prev) => ({
      ...prev,
      paymentDetails: {
        ...prev.paymentDetails,
        [type]: [
          ...prev.paymentDetails[type],
          {
            [type === "prepaid" ? "prepaidAdvance" : "postPaidCreditLimit"]: "",
            paymentMode: "",
          },
        ],
      },
    }));
  };

  const removePaymentField = (index, type) => {
    const response = handleDeleteConfirm();
    if (response) {
      setFormData((prev) => ({
        ...prev,
        paymentDetails: {
          ...prev.paymentDetails,
          [type]: prev.paymentDetails[type].filter((_, i) => i !== index),
        },
      }));
    }
  };

  const handleDeleteConfirm = () => {
    return true;
  };

  const handleFileChange = (e, key, objectKey) => {
    const uploadedFile = e.target.files[0];

    setFormData((prevFormData) => ({
      ...prevFormData,
      [objectKey]: {
        ...prevFormData[objectKey],
        [key]: uploadedFile.name,
      },
    }));
  };

  const handleSave = async () => {
    if (!validateForm()) {
      setPopup({
        icon: "delete",
        title: "Please Fill All The Required Fields",
        onClose: () => {
          setPopup(null);
        },
      });
      return;
    }

    const response = await organizationMasterSave(formData, {
      headers: {
        "Content-Type": "application/json",
        createdBy: "3fa85f64-5717-4562-b3fc-2c963f66af77",
      },
    });
    if (response.statusCode === "200 OK") {
      setPopup({
        icon: "success",
        title: "Saved Successfully",
        onClose: () => {
          navigate("/lab-view/organization-master");
        },
      });
    } else {
      setPopup({
        icon: "delete",
        title: "error",
        onClose: () => {
          setPopup(null);
        },
      });
    }
  };

  const handleUpdate = async () => {
    if (!validateForm()) {
      setPopup({
        icon: "delete",
        title: "Please Fill All The Required Fields",
        onClose: () => {
          setPopup(null);
        },
      });
      return;
    }
    const response = await organizationMasterUpdate(formData.id, formData);
    if (response.statusCode === "200 OK") {
      setPopup({
        icon: "success",
        title: "Updated Successfully",
        onClose: () => {
          navigate("/lab-view/organization-master");
        },
      });
    } else {
      setPopup({
        icon: "delete",
        title: "error",
        onClose: () => {
          setPopup(null);
        },
      });
    }
  };

  const [errors, setErrors] = useState({});
  const validateForm = () => {
    let newErrors = {};
    if (!formData.name) newErrors.name = "Name is required";
    if (!formData.phoneNumber)
      newErrors.phoneNumber = "Phone Number is required";
    if (!formData.roleId) newErrors.roleId = "Role is required";
    if (!formData.country) newErrors.country = "Country is required";
    if (!formData.state) newErrors.state = "State is required";
    if (!formData.city) newErrors.city = "City is required";
    if (!formData.pinCode) newErrors.pinCode = "Pin Code is required";
    if (!formData.address) newErrors.address = "Address is required";
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  useEffect(() => {
    const organizationDetails = location.state?.organizationDetails;
    const mode = location.state?.mode;
    if (mode === "view") {
      setViewMode(true);
      setFormData(organizationDetails.data);
    } else if (mode === "edit") {
      setEditMode(true);
      setFormData(organizationDetails.data);
    }
  }, []);

  return (
    <div className="organization-master">
      <div className="title organization-master-title">Organization Master</div>
      <div className="organization-master-content">
        <div className="omc-intro">
          <div className="omci-one">
            <div className="organization-master-fields">
              <InputField
                label="Name"
                type="text"
                placeholder="Enter name"
                onChange={handleInputChange}
                name="name"
                required
                value={formData.name}
                error={errors?.name}
                disabled={viewMode}
              />
            </div>
            <div className="organization-master-fields">
              <DropDown
                value={formData.roleId}
                label="Role"
                options={[
                  { id: "1", role: "role 1" },
                  { id: "2", role: "role 2" },
                ]}
                onChange={(selectedOption) =>
                  handleDropdownChange("role", selectedOption)
                }
                placeholder="Select"
                name="roleId"
                fieldName="role"
                error={errors?.roleId}
                disabled={viewMode}
                required
              />
            </div>
            <div className="organization-master-fields">
              <DropDown
                value={formData.state}
                label="State"
                options={states || []}
                onChange={(selectedOption) =>
                  handleDropdownChange("stateName", selectedOption)
                }
                placeholder="Select"
                name="state"
                fieldName={"stateName"}
                error={errors?.state}
                required
                disabled={!formData.country || viewMode}
              />
            </div>
            <div className="organization-master-fields">
              <InputField
                label="Pin Code"
                type="text"
                placeholder="Enter pincode"
                onChange={handleInputChange}
                name="pinCode"
                value={formData.pinCode}
                error={errors?.pinCode}
                disabled={viewMode}
                required
              />
            </div>
          </div>
          <div className="omci-two">
            <div className="organization-master-fields">
              <InputField
                label="Phone Number"
                type="phone"
                placeholder="Enter number"
                onChange={handlePhoneChange}
                name="phoneNumber"
                value={formData.phoneNumber}
                error={errors?.phoneNumber}
                disabled={viewMode}
                required
              />
            </div>
            <div className="organization-master-fields">
              <DropDown
                value={formData.country}
                label="Country"
                options={countries || []}
                onChange={(selectedOption) =>
                  handleDropdownChange("countryName", selectedOption)
                }
                placeholder="Select"
                name="country"
                fieldName={"countryName"}
                error={errors?.country}
                disabled={viewMode}
                required
              />
            </div>
            <div className="organization-master-fields">
              <DropDown
                value={formData.city}
                label="City"
                options={cities || []}
                onChange={(selectedOption) =>
                  handleDropdownChange("cityName", selectedOption)
                }
                placeholder="Select"
                name="city"
                fieldName={"cityName"}
                error={errors?.city}
                disabled={!formData.state || viewMode}
                required
              />
            </div>
            <div className="organization-master-fields">
              <InputField
                label="Adress"
                type="textarea"
                placeholder="Enter address"
                onChange={handleInputChange}
                name="address"
                value={formData.address}
                error={errors?.address}
                disabled={viewMode}
                required
              />
            </div>
          </div>
        </div>
        <div className="omc-payment-details">
          <div className="om-subtitles"> Payment Details</div>
          {formData.paymentDetails?.prepaid?.map((item, index) => (
            <div className="omcp-one" key={index}>
              <div className="omcpo-content">
                <div className="omcpoc-child">
                  <InputField
                    label="Prepaid-Advance"
                    type="text"
                    placeholder="Enter pre-paid advance"
                    onChange={(e) => handlePaymentChange(index, "prepaid", e)}
                    name="prepaidAdvance"
                    value={item.prepaidAdvance}
                    disabled={viewMode}
                  />
                </div>
                <div className="omcpoc-child">
                  <DropDown
                    value={item.paymentMode}
                    label="Payment mode"
                    options={paymentMode}
                    onChange={(selectedOption) =>
                      handlePaymentChange(index, "prepaid", selectedOption)
                    }
                    placeholder="Select"
                    name="paymentMode"
                    fieldName="name"
                    disabled={viewMode}
                  />
                </div>
              </div>
              {!viewMode && (
                <div className="omcpo-button">
                  {formData.paymentDetails.prepaid.length > 1 && (
                    <button className="btn-clear" onClick={() => removePaymentField(index, "prepaid")}> Delete </button>
                  )}
                  <button className="btn-primary" onClick={() => addPaymentField("prepaid")}> Add More </button>
                </div>
              )}
            </div>
          ))}
          {formData.paymentDetails?.postPaid?.map((payment, index) => (
            <div className="omcp-two">
              <div className="omcpt-content">
                <div className="omcpoc-child">
                  <InputField
                    label="Post paid-Credit Limit"
                    type="text"
                    placeholder="Enter post paid credit limit"
                    onChange={(e) => handlePaymentChange(index, "postPaid", e)}
                    name="postPaidCreditLimit"
                    value={payment.postPaidCreditLimit}
                    disabled={viewMode}
                  />
                </div>
                <div className="omcpoc-child">
                  <DropDown
                    value={payment.paymentMode}
                    label="Payment mode"
                    options={paymentMode}
                    onChange={(selectedOption) =>
                      handlePaymentChange(index, "postPaid", selectedOption)
                    }
                    placeholder="Select"
                    name="paymentMode"
                    fieldName="name"
                    disabled={viewMode}
                  />
                </div>
              </div>
              {!viewMode && (
                <div className="omcpt-button">
                  {formData.paymentDetails.postPaid.length > 1 && (
                    <button className="btn-clear" onClick={() => removePaymentField(index, "postPaid")}> Delete </button>
                  )}
                  <button className="btn-primary" onClick={() => addPaymentField("postPaid")}> Add More </button>
                </div>
              )}
            </div>
          ))}
          <div className="omcp-three">
            <div className="ompct-child">
              <InputField
                label="Any Comments"
                type="textarea"
                placeholder="Enter your comments"
                onChange={handleInputChange}
                value={formData.comments}
                name="comments"
                disabled={viewMode}
              />
            </div>
          </div>
        </div>
        <div className="omc-invoice-generation">
          <div className="om-subtitles">Invoice Generation Frequency</div>
          <div className="omcig-checkbox">
            <input
              type="checkbox"
              name="generationFrequency"
              value="Every 15 days"
              checked={formData.invoiceGenerationFrequency === "Every 15 days"}
              onChange={handleInvoiceChange}
              disabled={viewMode}
            />{" "}
            Every 15 days
          </div>
          <div className="omcig-checkbox">
            <input
              type="checkbox"
              name="generationFrequency"
              value="Once Every Month"
              checked={
                formData.invoiceGenerationFrequency === "Once Every Month"
              }
              onChange={handleInvoiceChange}
              disabled={viewMode}
            />{" "}
            Once Every Month
          </div>
          <div className="omcig-custom">
            <div className="omcig-checkbox">
              <input
                type="checkbox"
                checked={isCustomSelected}
                onChange={handleCustomCheckboxChange}
                disabled={viewMode}
              />{" "}
              Custom
            </div>
            {isCustomSelected && (
              <div className="omcig-custom-input">
                <InputField
                  type="text"
                  placeholder="Enter custom period"
                  name="customFrequency"
                  value={formData.customFrequency || ""}
                  onChange={handleInputChange}
                  disabled={viewMode}
                />
              </div>
            )}
          </div>
        </div>
        <div className="omc-patient-configuration">
          <div className="om-subtitles">Patient Configuration</div>
          <div className="omcig-checkbox">
            <input
              type="checkbox"
              checked={formData?.patientConfiguration?.sendReportsAndBills}
              onChange={() =>
                handlePatientConfigChange("Send reports and bills")
              }
              disabled={viewMode}
            />{" "}
            Send reports and bills
          </div>
          <div className="omcig-checkbox">
            <input
              type="checkbox"
              checked={formData?.patientConfiguration?.sendReportsOnly}
              onChange={() => handlePatientConfigChange("Send reports only")}
              disabled={viewMode}
            />{" "}
            Send reports only
          </div>
        </div>
        <div className="omc-report-access">
          <div className="om-subtitles">Report Access</div>
          <div className="omcig-checkbox">
            <input
              type="checkbox"
              checked={formData?.reportAccessConfiguration?.showHeader}
              onChange={() =>
                handleAccessChange("reportAccessConfiguration", "Show Header")
              }
              disabled={viewMode}
            />{" "}
            Show header
          </div>
          <div className="omcig-checkbox">
            <input
              type="checkbox"
              checked={formData?.reportAccessConfiguration?.showFooter}
              onChange={() =>
                handleAccessChange("reportAccessConfiguration", "Show Footer")
              }
              disabled={viewMode}
            />{" "}
            Show footer
          </div>
          <div className="omcra-header">
            <InputField
              type="file"
              label="Report Header"
              name="headerText"
              onChange={(e) =>
                handleFileChange(e, "headerText", "reportAccessConfiguration")
              }
              value={formData.reportAccessConfiguration.headerText}
              existingFileName={formData.reportAccessConfiguration.headerText}
              disabled={viewMode}
            />
          </div>
          <div className="omcra-footer">
            <InputField
              type="file"
              label="Report Footer"
              name="footerText"
              onChange={(e) =>
                handleFileChange(e, "footerText", "reportAccessConfiguration")
              }
              value={formData.reportAccessConfiguration.footerText}
              existingFileName={formData.reportAccessConfiguration.footerText}
              disabled={viewMode}
            />
          </div>
        </div>
        <div className="omc-bill-access">
          <div className="om-subtitles">Bill Access</div>
          <div className="omcig-checkbox">
            <input
              type="checkbox"
              checked={formData?.billAccessConfiguration?.showHeader}
              onChange={() =>
                handleAccessChange("billAccessConfiguration", "Show Header")
              }
              disabled={viewMode}
            />{" "}
            Show header
          </div>
          <div className="omcig-checkbox">
            <input
              type="checkbox"
              checked={formData?.billAccessConfiguration?.showFooter}
              onChange={() =>
                handleAccessChange("billAccessConfiguration", "Show Footer")
              }
              disabled={viewMode}
            />{" "}
            Show footer
          </div>
          <div className="omcra-header">
            <InputField
              type="file"
              label="Report Header"
              name="headerText"
              onChange={(e) =>
                handleFileChange(e, "headerText", "billAccessConfiguration")
              }
              value={formData.billAccessConfiguration.headerText}
              existingFileName={formData.billAccessConfiguration.headerText}
              disabled={viewMode}
            />
          </div>
          <div className="omcra-footer">
            <InputField
              type="file"
              label="Report Footer"
              name="footerText"
              onChange={(e) =>
                handleFileChange(e, "footerText", "billAccessConfiguration")
              }
              value={formData.billAccessConfiguration.footerText}
              existingFileName={formData.billAccessConfiguration.footerText}
              disabled={viewMode}
            />
          </div>
        </div>
        <div className="omc-login-credentials">
          <div className="om-subtitles">Login Creddentials</div>
          <div className="omclc-content">
            <div className="omclc-child">
              <InputField
                label="Email Address"
                type="text"
                placeholder="Enter email address"
                onChange={handleInputChange}
                name="email"
                value={formData.email}
                disabled={viewMode}
                required
              />
            </div>
            <div className="omclc-child">
              <InputField
                label="Set Password"
                type="password"
                placeholder="Enter password"
                onChange={handleInputChange}
                name="password"
                value={formData.password}
                disabled={viewMode}
                required
              />
            </div>
          </div>
        </div>
        <div className="omc-buttons">
          <button className="btn-clear" onClick={() => { navigate("/lab-view/organization-master") }}> Back </button>
          {editMode ? (
          <button className="btn-primary" onClick={handleUpdate}> Update </button>
          ) : (
          <button className="btn-primary" onClick={handleSave}> Save </button>
          )}
        </div>
      </div>
      {popup && <Swal {...popup} />}
    </div>
  );
};

export default AddOraganizationMaster;
