import React, { useState } from "react";
import "./AddBill.css";
import InputField from "../../Homepage/InputField";
import { useNavigate, useParams } from "react-router-dom";
import DropDown from "../../Re-usable-components/DropDown";

const AddBill = () => {
  const [patientName, setPatientName] = useState("");
  const [selectedPayment, setSelectedPayment] = useState("");
  const [selectedTests, setSelectedTests] = useState([]);
  const [selectedCoupon, setSelectedCoupon] = useState("");
  const [paymentDetails, setPaymentDetails] = useState([]);
  const [mode, viewMode] = useState("");
  const testOptions = [
    { label: "TSH", value: "TSH" },
    { label: "T3", value: "T3" },
    { label: "T4", value: "T4" },
    { label: "CBC", value: "CBC" },
    { label: "Lipid Profile", value: "Lipid Profile" },
  ];
  const paymentOptions = [
    { label: "Organisation Pay", value: "Organisation Pay" },
    { label: "Self Pay", value: "Self Pay" },
  ];
  const dropdownOptions = [
    { value: "Test1", label: "Test1" },
    { value: "Test2", label: "Test3" },
    { value: "Test3", label: "Test3" },
  ];
  const handleSelectionChange = (name, value) => {
    setFormData((prevFormData) => ({
      ...prevFormData,
      [name]: value,
    }));
  };

  const [viewData, setViewData] = useState([]);
  const [viewAll, setViewAll] = useState(false);
  const [formData, setFormData] = useState({});
  const handleViewAll = () => {
    if (formData.features && formData.features.length > 0) {
      const featureLabels = formData.features.map((feature) => feature.label);
      setViewData(featureLabels);
      setViewAll(true);
    }
  };

  const handlePaymentChange = (selected) => {
    setSelectedPayment(selected);
  };

  const handleTestChange = (selectedCoupon) => {
    setSelectedTests(selectedCoupon);
  };
  const handleCouponChange = (selected) => {
    setSelectedCoupon(selected);
  };

  const navigate = useNavigate();
  const handleBack = () => {
    navigate("/lab-view/bill");
  };

  return (
    <div className="add-bill-container">
      <div className="add-bill-div">
        <div className="add-bill-header">
          <span className="title">Bill</span>
        </div>

        <div className="patient-details">
          <div className="input-patient-div">
            <div className="input-patient">
              <InputField
                label="Name"
                type="text"
                placeholder="Enter Patient Name"
                className="input-field"
                value={patientName}
                readOnly={viewMode}
                onChange={(e) => setPatientName(e.target.value)}
              />
            </div>
            <div className="input-patient">
              <InputField
                label="Age"
                type="text"
                placeholder="Enter Patient Age"
                className="input-field"
                readOnly={viewMode}
              />
            </div>
          </div>

          <div className="input-patient-div">
            <div className="input-patient">
              <InputField
                label="Gender"
                type="text"
                placeholder="Enter Patient Gender"
                className="input-field"
                readOnly={viewMode}
              />
            </div>
            <div className="input-patient">
              <InputField
                label="Patient ID"
                type="text"
                placeholder="Enter Patient ID"
                className="input-field"
                readOnly={viewMode}
              />
            </div>
          </div>
        </div>

        <div className="select-test">
          <DropDown
            label="Select Payment Type"
            options={paymentOptions}
            value={paymentOptions.find(
              (option) => option.value === selectedPayment
            )}
            className="input-field"
            onChange={handlePaymentChange}
            fieldName={"label"}
          />
        </div>

        <div className="radio-btn">
          <InputField label="Test" type="radio" className="input-field" />
          <InputField label="Profile" type="radio" className="input-field" />
        </div>

        <div className="add-test-div">
          <div className="add-test-header">
            <span className="add-test">Test Details</span>
          </div>

          <div className="test-name-div">
            <DropDown
              label="Test Name"
              placeholder="Select Tests"
              options={dropdownOptions}
              value={dropdownOptions.value}
              onChange={(selectedOptions) =>
                handleSelectionChange("tests", selectedOptions)
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
        <div className="add-coupon-div">
          <div className="coupon-div">
            <div className="coupon-header">
              <span className="coupon">Coupon Details</span>
            </div>
            <div className="coupon-discount">
              <div className="coupon-name-outer">
                <div className="coupon-name">
                  <DropDown
                    label="Coupon"
                    placeholder="Select"
                    className="input-field"
                    options={["Coupon 1", "Coupon 2", "Coupon 3"]}
                    value={["Coupon 1", "Coupon 2", "Coupon 3"]}
                    onChange={handleCouponChange}
                  />
                </div>
                <div>
                  <InputField
                    label="Discount Type"
                    type="text"
                    placeholder="Enter Discount"
                    className="input-field"
                    readOnly={viewMode}
                  />
                </div>
              </div>
              <div className="discount-name-outer">
                <div className="discount-name">
                  <InputField
                    label="Discount Name"
                    type="text"
                    placeholder="Enter Coupon Name"
                    className="input-field"
                    readOnly={viewMode}
                  />
                </div>
                <div className="discount-amount">
                  <InputField
                    label="Discount Amount"
                    type="text"
                    placeholder="Enter Discount"
                    className="input-field"
                    readOnly={viewMode}
                  />
                </div>
              </div>
            </div>
          </div>
          <div></div>
          <div></div>

          <div className="org-div">
            <div className="org-header">
              <span className="organisation">Organisation Details</span>
            </div>
            <div className="organisation-details">
              <div className="org-name-outer">
                <div>
                  <InputField
                    label="Organisation"
                    type="text"
                    placeholder="Enter Organization"
                    className="input-field"
                    readOnly={viewMode}
                  />
                </div>
                <div>
                  <InputField
                    label="Organisation Balance"
                    type="text"
                    placeholder="Enter Organisation Balance"
                    className="input-field"
                    readOnly={viewMode}
                  />
                </div>
              </div>
              <div className="price-outer">
                <div>
                  <InputField
                    label="Price"
                    type="text"
                    placeholder="Enter Price"
                    className="input-field"
                    readOnly={viewMode}
                  />
                </div>
                <div>
                  <InputField
                    label="Organisation Payable Amount"
                    type="text"
                    placeholder="Enter Organisation Payable Amount"
                    className="input-field"
                    readOnly={viewMode}
                  />
                </div>
              </div>
            </div>
          </div>

          <div className="additionalDetails-div">
            <div className="additionalDetails-header">
              <span className="additionalDetails">Additional Details</span>
            </div>
            <div className="organisation-details">
              <div className="additionalDetails-outer">
                <div>
                  <InputField
                    label="Concession"
                    type="text"
                    placeholder="Enter Concession"
                    className="input-field"
                    readOnly={viewMode}
                  />
                </div>
                <div>
                  <InputField
                    label="Advance Paid"
                    type="text"
                    placeholder="Enter Advance Paid"
                    className="input-field"
                    readOnly={viewMode}
                  />
                </div>
              </div>
              <div className="additional-outer">
                <div>
                  <InputField
                    label="Additional Amount"
                    type="text"
                    placeholder="Enter Additional Amount"
                    className="input-field"
                    readOnly={viewMode}
                  />
                </div>
                <div>
                  <InputField
                    label="Taxes"
                    type="text"
                    placeholder="Enter Taxes"
                    className="input-field"
                    readOnly={viewMode}
                  />
                </div>
              </div>
            </div>
          </div>
          <div></div>
          <div></div>
        </div>

        <div className="paymentDetails">
          <div className="payment-header-div">
            <span className="payment-header">Payment Details</span>
          </div>
          <div className="paymentDetails-div">
            <div className="paymentMode">
              <DropDown
                label="Payment Mode"
                placeholder="Select"
                className="input-field"
                required
              />
            </div>
            <div className="amount-div">
              <InputField
                label="Amount"
                type="text"
                placeholder="Enter Amount"
                className="input-field"
                required
                readOnly={viewMode}
              />
            </div>
          </div>
          {paymentDetails.map((pd, index) => (
            <div key={index} className="paymentDetails-div">
              <div className="paymentMode">
                <DropDown
                  label="Payment Mode"
                  placeholder="Select"
                  className="input-field"
                  required
                  value={pd.paymentMode}
                  onChange={(e) =>
                    setPaymentDetails(
                      paymentDetails.map((p, i) =>
                        i === index ? { ...p, paymentMode: e.target.value } : p
                      )
                    )
                  }
                />
              </div>
              <div className="amount-div">
                <InputField
                  label="Amount"
                  type="text"
                  placeholder="Enter Amount"
                  className="input-field"
                  required
                  value={pd.amount}
                  onChange={(e) =>
                    setPaymentDetails(
                      paymentDetails.map((p, i) =>
                        i === index ? { ...p, amount: e.target.value } : p
                      )
                    )
                  }
                  readOnly={viewMode}
                />
              </div>
            </div>
          ))}
          <div className="add-btn-div">
            <button
              className="btn-primary"
              onClick={() =>
                setPaymentDetails([
                  ...paymentDetails,
                  { paymentMode: "", amount: "" },
                ])
              }
            >
              Add
            </button>
          </div>
        </div>

        <div className="billsummary-div">
          <div className="billsummary-header-div">
            <span className="billsummary-header">Bill Summary</span>
          </div>
          <div></div>
        </div>

        <div className="btn-container">
          <div className="back-btn-div">
            <button
              className="btn-secondary"
              type="button"
              onClick={handleBack}
            >
              Back
            </button>
          </div>
          <div className="confirm-print-div">
            <div className="confirm-btn">
              <button className="btn-secondary" type="button">
                Confirm and Bill
              </button>
            </div>
            <div className="print-btn">
              <button className="btn-primary" type="button">
                Print Receipt
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AddBill;
