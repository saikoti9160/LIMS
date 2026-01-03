import React, { useState, useEffect } from "react";
import "./SignUpPage.css";
import miniLogo from "../../assets/images/mini-logo.svg";
import SignInPageImg from "../../assets/images/sign-in-page-img.svg";
import { useNavigate } from "react-router-dom";
import { authenticationService } from "../../services/AuthenticationService";
import InputField from "../Homepage/InputField";
import { getAllCountries, locationMasterService } from "../../services/locationMasterService";
import DropDown from "../Re-usable-components/DropDown";
import { designationService } from "../../services/designationService";
import Error from "../Re-usable-components/Error";

const SignUpPage = () => {

  const [signUpForm, setSignUpForm] = useState({
    email: "",
    phone: "",
    password: "",
    confirmPassword: "",
    designation: "",
    labName: "",
    country: "",
    state: "",
    city: "",
    address: "",
    isSuperAdminCreated: false,
  });
  const [designationName, setDesignationName] = useState("");
  const [formErrors, setFormErrors] = useState({});
  const [designations, setDesignations] = useState([]);
  const [countries, setCountries] = useState([]);
  const [states, setStates] = useState([]);
  const [cities, setCities] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    fetchCountries();
    fetchDesignations();
  }, []);

  const fetchCountries = async () => {
    let response = await getAllCountries("", [], 0, 250, "countryName");
    setCountries(response.data);
  };

  const fetchDesignations = async () => {
    try {
      const data = await designationService.getAllDesignations();
      setDesignations(data.data);

    } catch (error) {
      console.error("Error fetching designations:", error);
    }
  };
  const handleChange = (e) => {
    if (e.target && e.target !== undefined) {
      const { name, value } = e.target;
      if (name === 'designation') {
        setSignUpForm((prev) => ({ ...prev, [name]: value.id }));
        setDesignationName(value.designationName);
        setFormErrors((prev) => ({ ...prev, [name]: "" }));
      } else {
        setSignUpForm((prev) => ({ ...prev, [name]: value }));
        setFormErrors((prev) => ({ ...prev, [name]: "" }));
      }
    }

  };
  const handlePhoneCode = (e) => {
    setSignUpForm((prev) => ({ ...prev, phone: e.phoneNumber }));
    setFormErrors((prev) => ({ ...prev, phone: "" })); 
  }

  const fetchStates = async (selectedCountry, dropdownName) => {
    setFormErrors((prev) => ({ ...prev, [dropdownName]: "" }));
    setSignUpForm({ ...signUpForm, country: selectedCountry, state: "", city: "" });
    setStates([]);
    setCities([]);
    try {
      const data = await locationMasterService.getAllStates("", [selectedCountry]);
      setStates(data.data);
    } catch (error) {
      console.error("Error fetching states:", error);
    }
  };

  const fetchCities = async (selectedState,dropdownName) => {
    setFormErrors((prev) => ({ ...prev, [dropdownName]: "" }));
    setSignUpForm({ ...signUpForm, state: selectedState, city: "" });
    try {
      const data = await locationMasterService.getAllCities("", [selectedState]);
      setCities(data.data);
    } catch (error) {
      console.error("Error fetching cities:", error);
    }
  };

  const validateForm = () => {
    let errors = {};
    if (!signUpForm.email) errors.email = "Email is required";
    if (!signUpForm.phone) errors.phone = "Phone number is required";
    if (!signUpForm.password) errors.password = "Password is required";
    if (!signUpForm.confirmPassword) errors.confirmPassword = "Confirm Password is required";
    if (signUpForm.password !== signUpForm.confirmPassword) errors.confirmPassword = "Passwords do not match";
    if (!signUpForm.designation) errors.designation = "Designation is required";
    if (!signUpForm.labName) errors.labName = "Lab Name is required";
    if (!signUpForm.country) errors.country = "Country is required";
    if (!signUpForm.state) errors.state = "State is required";
    if (!signUpForm.city) errors.city = "City is required";
    if (!signUpForm.address) errors.address = "Address is required";

    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSignup = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;
    // const isSuperAdminCreated = false;
    const accountType = "5e1f20d5-f5c8-42b9-8b8c-66758aa55911";

    try {
      const response = await authenticationService.signUp({ ...signUpForm, accountType });
      console.log(response.data);
      navigate("/login");
    } catch (error) {
      setFormErrors("Signup failed. Please try again.");
      console.error("Signup error:", error);
    }
  };

  return (
    <>
      <div className="signup-container">
        <div className="signup-inner-container">
          <div className="signup-image">
            <img src={SignInPageImg} alt="Sign up" />
          </div>
          <div className="signup-form-container">
            <div className="signup-form">
              <img src={miniLogo} alt="Logo" className="mini-logo" />
              <h2>Sign Up</h2>
              <form onSubmit={handleSignup} className="main-form">
                <div className="form-row">
                  <div className="form-group">
                    <InputField label="Email Address" type="email" name="email" placeholder="Enter Here" value={signUpForm.email} onChange={handleChange} />
                    {formErrors.email && <Error message={formErrors.email} type="error" />}

                  </div>
                  <div className="form-group">
                    <InputField label="Phone Number" type="phone" name="phone" placeholder="Enter Here" value={signUpForm?.phone} onChange={handlePhoneCode} />
                    {formErrors.phone && <Error message={formErrors.phone} type="error" />}
                  </div>
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <InputField autoComplete={true} label="Password" type="password" name="password" placeholder="Enter Here" value={signUpForm.password} onChange={handleChange} />
                    {formErrors.password && <Error message={formErrors.password} type="error" />}
                  </div>
                  <div className="form-group">
                    <InputField label="Confirm Password" type="password" name="confirmPassword" placeholder="Enter Here" value={signUpForm.confirmPassword} onChange={handleChange} />
                    {formErrors.confirmPassword && <Error message={formErrors.confirmPassword} type="error" />}
                  </div>
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label className="input-labels">Designation</label>

                    <DropDown className="signup-dropdown" value={designationName} name="designation" options={designations} placeholder="Select Designation" onChange={handleChange} fieldName="designationName" />
                    {formErrors.designation && <Error message={formErrors.designation} type="error" />}

                  </div>
                  <div className="form-group">
                    <InputField label="Lab Name" type="text" name="labName" placeholder="Enter Here" value={signUpForm.labName} onChange={handleChange} />
                    {formErrors.labName && <Error message={formErrors.labName} type="error" />}
                  </div>
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label className="input-labels">Country</label>

                    <DropDown className="signup-dropdown" options={countries} placeholder="Select Country" name="country" onChange={(e) => fetchStates(e.target.value.countryName, "country")} fieldName="countryName" />
                    {formErrors.country && <Error message={formErrors.country} type="error" />}
                  </div>
                  <div className="form-group">
                    <label className="input-labels">State</label>

                    <DropDown className="signup-dropdown" options={states} placeholder="Select State" name="state" onChange={(e) => fetchCities(e.target.value.stateName, "state")} disabled={!signUpForm.country} fieldName="stateName" />
                    {formErrors.state && <Error message={formErrors.state} type="error" />}
                  </div>
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label className="input-labels">City</label>

                    <DropDown className="signup-dropdown" options={cities} placeholder="Select City" onChange={(e) => handleChange({ target: { name: "city", value: e.target.value.cityName } })} disabled={!signUpForm.state} fieldName="cityName" />
                    {formErrors.city && <Error message={formErrors.city} type="error" />}
                  </div>
                  <div className="form-group">
                    <label className="input-labels"> Address</label>

                    <InputField
                      type="textarea"
                      name="address"
                      placeholder="Enter Here"
                      value={signUpForm.address}
                      onChange={handleChange}
                      className="input-field-textarea-address"
                    />
                    {formErrors.address && <Error message={formErrors.address} type="error" />}
                  </div>

                </div>

                {formErrors.general && <Error message={formErrors.general} type="error" />}


                <button type="submit" className="signup-button" >Sign Up</button>
                <button type="button" className="home-button-signup" onClick={() => navigate("/dashboard")}>Go to Home</button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default SignUpPage;
