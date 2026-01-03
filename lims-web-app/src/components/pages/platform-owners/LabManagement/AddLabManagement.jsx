import React, { useEffect, useState } from "react";
import "./AddLabManagement.css";
import DropDown from "../../../Re-usable-components/DropDown";
import InputField from "../../../Homepage/InputField";
import Swal from "../../../Re-usable-components/Swal";
import { useLocation, useNavigate } from "react-router-dom";
import { getAllCities, getAllContinents, getAllCountries, getAllStates } from "../../../../services/locationMasterService";
import { labManagementSave, labManagementDelete, labManagementUpdate, deleteBranchOrEquipment } from "../../../../services/LabManagementService";
import { getLabTypes } from "../../../../services/labTypeService"
import Error from "../../../Re-usable-components/Error";

const AddLabManagement = () => {
  //labdto,branches,equipmentList objects start
  const [labDto, setLabDto] = useState({
    labManagerName: "",
    labName: "",
    labTypeId: "",
    email: "",
    password: "",
    continent: "",
    country: "",
    state: "",
    city: "",
    address: "",
    zipCode: "",
    phoneNumber: "",
    phoneCode: "",
    packageId: "",
    hasBranches: "",
    logo: "",
    active: ""
  });
  const [branches, setBranches] = useState([
    {
      branchName: "",
      branchType: "",
      contactPerson: "",
      email: "",
      phoneNumber: "",
      phoneCode: "",
      continent: "",
      country: "",
      state: "",
      city: "",
      address: "",
      zipCode: ""
    },
  ]);
  const [equipmentList, setEquipmentList] = useState([
    { equipmentName: "", model: "" },
  ]);
  //labdto,branches,equipmentList objects end

  //validation start
  const [labErrors, setLabErrors] = useState({});
  const [branchErrors, setBranchErrors] = useState({});
  const [loginErrors, setLoginErrors] = useState({});
  const validateLabInfo = () => {
    let errors = {};
    if (!labDto.labName || labDto.labName.trim() === '') errors.labName = "Lab name is required";
    if (!labDto.labManagerName || labDto.labManagerName.trim() === '') errors.labManagerName = "Lab manager name is required";
    if (!labDto.labTypeId || labDto.labTypeId.trim() === '') errors.labTypeId = "Lab Type is required";
    if (!labDto.continent || labDto.continent.trim() === '') errors.continent = "Continent is required";
    if (!labDto.country || labDto.country.trim() === '') errors.country = "Country is required";
    if (!labDto.state || labDto.state.trim() === '') errors.state = "State is required";
    if (!labDto.city || labDto.city.trim() === '') errors.city = "City is required";
    if (!labDto.address || labDto.address.trim() === '') errors.address = "Address is required";
    if (!labDto.zipCode || labDto.zipCode.trim() === '') errors.zipCode = "Zipcode is required";
    if (!labDto.phoneNumber || labDto.phoneNumber.trim() === '') errors.phoneNumber = "Phone number is required";
    if (!labDto.packageId || labDto.packageId.trim() === '') errors.packageId = "Package is required";
    if (labDto.active === null || labDto.active === undefined || labDto.hasBranches === "") {
      errors.active = "Status is required";
    }
    if (labDto.hasBranches === null || labDto.hasBranches === undefined || labDto.hasBranches === "") {
      errors.hasBranches = "Branches is required";
    }
    if (!labDto.logo) errors.logo = "Logo is required";
    setLabErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const validateBranchInfo = () => {
    if (!labDto.hasBranches) return true;
    let errors = {};
    branches.forEach((branch, index) => {
      let branchErrors = {};
      if (!branch.branchType || branch.branchType.trim() === '') branchErrors.branchType = "Branch Type is required";
      if (!branch.address || branch.address.trim() === '') branchErrors.address = "Address is required";
      if (Object.keys(branchErrors).length > 0) errors[index] = branchErrors;
    });

    setBranchErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const validateLoginInfo = () => {
    let errors = {};
    if (!labDto.email) {
      errors.email = "Email is required";
    } else if (!/\S+@\S+\.\S+/.test(labDto.email)) {
      errors.email = "Invalid email format";
    }

    setLoginErrors(errors);
    return Object.keys(errors).length === 0;
  };
  //validation end

  const navigate = useNavigate();
  const handleLabChange = (e) => {
    e.preventDefault()
    const { name, value, type, checked } = e.target;
    {
      setLabDto({
        ...labDto,
        [name]: type === "checkbox" ? checked : value,
      });
    }
  };
  const handleLabDropdownChange = (fieldName, object) => {
    const { name, value } = object.target;
    setLabDto(prevLabData => ({
      ...prevLabData,
      [name]: value[fieldName],
      ...(name === "continent" ? { country: "", state: "", city: "" } : {}),
      ...(name === "country" ? { state: null, city: null } : {}),
      ...(name === "state" ? { city: null } : {})
    }));
  }

  // Location DropDown Management for lab and branches start
  const [continents, setContinents] = useState([]);
  const [countries, setCountries] = useState([]);
  const [states, setStates] = useState([]);
  const [cities, setCities] = useState([]);

  const [branchDropdownOptions, setBranchDropdownOptions] = useState([]);
  useEffect(() => {
    const fetchContinents = async () => {
      let response = await getAllContinents();
      setContinents(response.data);
    };
    fetchContinents();
  }, []);

  useEffect(() => {
    const fetchCountries = async () => {
      if (labDto.continent) {
        let response = await getAllCountries("", [labDto.continent], 0, 250, "countryName");
        setCountries(response.data);
        setStates([]);
        setCities([]);
      } else {
        setCountries([]);
      }
    };
    fetchCountries();
  }, [labDto.continent]);

  useEffect(() => {
    const fetchStates = async () => {
      if (labDto.country) {
        let response = await getAllStates("", [labDto.country], 0, 250, "stateName");
        setStates(response.data);
        setCities([]);
      } else {
        setStates([]);
      }
    };
    fetchStates();
  }, [labDto.country]);

  useEffect(() => {
    const fetchCities = async () => {
      if (labDto.state) {
        let response = await getAllCities("", [labDto.state], 0, 250, "cityName");
        setCities(response.data);
      } else {
        setCities([]);
      }
    };
    fetchCities();
  }, [labDto.state]);

  const handleBranchChange = (e, index) => {
    const { name, value } = e.target;
    const updatedBranches = branches.map((branch, i) =>
      i === index ? { ...branch, [name]: value } : branch
    );
    setBranches(updatedBranches);
    if (branchErrors[index] && branchErrors[index][name]) {
      let newErrors = { ...branchErrors };
      delete newErrors[index][name];
      if (Object.keys(newErrors[index]).length === 0) {
        delete newErrors[index];
      }
      setBranchErrors(newErrors);
    }
  };

  const handleBranchPhoneNumberChange = (e, index) => {
    const updatedBranches = branches.map((branch, i) =>
      i === index ? { ...branch, phoneNumber: e.phoneNumber, phoneCode: e.countryCode } : branch
    );
    setBranches(updatedBranches);
  }

  const handleBranchDropDownChange = async (index, option, fieldName) => {
    const { name, value } = option.target;
    const updatedBranches = branches.map((branch, i) => {
      if (i === index) {
        return {
          ...branch,
          [name]: value[fieldName],
          ...(name === "continent" ? { country: "", state: "", city: "" } : {}),
          ...(name === "country" ? { state: "", city: "" } : {}),
          ...(name === "state" ? { city: "" } : {}),
        };
      }
      return branch;
    });

    setBranches(updatedBranches);

    if (name === "continent") {
      const response = await getAllCountries("", [value[fieldName]], 0, 250, "countryName");
      updateBranchDropdownOptions(index, "countries", response.data);
    } else if (name === "country") {
      const response = await getAllStates("", [value[fieldName]], 0, 250, "stateName");
      updateBranchDropdownOptions(index, "states", response.data);
    } else if (name === "state") {
      const response = await getAllCities("", [value[fieldName]], 0, 250, "cityName");
      updateBranchDropdownOptions(index, "cities", response.data);
    }
  };
  // Location DropDown Management for lab and branches end

  const updateBranchDropdownOptions = (index, field, data) => {
    setBranchDropdownOptions((prevOptions) => {
      const updatedOptions = [...prevOptions];
      updatedOptions[index] = {
        ...updatedOptions[index],
        [field]: data,
      };
      return updatedOptions;
    });
  };

  const addBranch = () => {
    setBranches([
      ...branches,
      {
        branchName: "",
        branchType: "",
        contactPerson: "",
        email: "",
        phoneNumber: "",
        continent: "",
        country: "",
        state: "",
        city: "",
        address: "",
        zipCode: "",
      },
    ]);
  };

  const addEquipment = () => {
    setEquipmentList([...equipmentList, { equipmentName: "", model: "" }]);
  };

  const handleEquipmentChange = (e, index) => {
    const { name, value } = e.target;
    const updatedEquipments = equipmentList.map((equipment, i) =>
      i === index ? { ...equipment, [name]: value } : equipment
    );
    setEquipmentList(updatedEquipments);
  };

  const [tab, setTab] = useState(0);

  const handleTabClick = (tabIndex) => {
    if (tab === 0 && !validateLabInfo()) return;
    if (tab === 1 && !validateBranchInfo()) return;
    if (tab === 3 && !validateLoginInfo()) return;
    setTab(tabIndex);
  };

  const handleTabPrevious = () => {
    if (tab === 2 && labDto.hasBranches) {
      setTab(1);
    } else if (tab === 2 && !labDto.hasBranches) {
      setTab(0);
    } else if (tab > 0) {
      setTab(tab - 1);
    } else {
      navigate("/lab-management");
    }
  };

  const handleSave = () => {
    if (mode === "edit" || mode === "add") {
      if (tab === 0) {
        if (!validateLabInfo()) return;
        setTab(labDto.hasBranches ? 1 : 2);
      } else if (tab === 1) {
        if (!validateBranchInfo()) return;
        setTab(tab + 1);
      } else if (tab === 3) {
        if (!validateLoginInfo()) return;
        setTab(tab + 1);
      } else {
        setTab(tab + 1);
      }
    } else if (mode === "view") {
      if (tab === 0) {
        setTab(labDto.hasBranches ? 1 : 2);
      } else {
        setTab(tab + 1);
      }
    }
  }

  const [popupConfig, setPopupConfig] = useState(null);

  const handleDeleteClose = () => {
    setPopupConfig(null);
  };

  const handleDelete = (index) => {
    if (mode === "edit") {
      setPopupConfig({
        icon: "delete",
        title: "Are you sure?",
        isButton: true,
        buttonText: "Delete",
        onButtonClick: () => handleDeleteConfirm(index),
        onClose: handleDeleteClose,
      });
    } else {
      setPopupConfig({
        icon: "delete",
        title: "Are you sure?",
        isButton: true,
        buttonText: "Delete",
        onButtonClick: () => removeItemFromUI(index),
        onClose: handleDeleteClose,
      });
    }
  };

  const handleDeleteConfirm = async (index) => {
    const isBranch = tab === 1;
    const itemId = isBranch ? branches[index]?.id : equipmentList[index]?.id;
    if (!itemId) {
      removeItemFromUI(index);
      return;
    }
    try {
      const response = await deleteBranchOrEquipment(isBranch, itemId);
      if (response.statusCode === "200 OK") {
        if (isBranch) {
          setBranches((prevBranches) => prevBranches.filter((_, i) => i !== index));
        } else {
          setEquipmentList((prevEquipmentList) => prevEquipmentList.filter((_, i) => i !== index));
        }
        setPopupConfig({
          icon: "success",
          title: "Deleted Successfully",
          onClose: handleDeleteClose,
        });
      } else {
        setPopupConfig({
          icon: "error",
          title: "Error deleting",
          onClose: handleDeleteClose,
        });
      }
    } catch (error) {
      setPopupConfig({
        icon: "error",
        title: "Error deleting",
        onClose: handleDeleteClose,
      });
    }
  };



  const removeItemFromUI = (index) => {
    if (tab === 1) {
      setBranches((prevBranches) => prevBranches.filter((_, i) => i !== index));
    } else if (tab === 2) {
      setEquipmentList((prevEquipments) =>
        prevEquipments.filter((_, i) => i !== index)
      );
    }
    setPopupConfig({
      icon: "success",
      title: "Deleted Successfully",
      onClose: handleDeleteClose,
    });
  };

  const handleFileChange = (e) => {
    const uploadedFile = e.target.files[0];
    setLabDto({ ...labDto, logo: uploadedFile.name });
  };

  const handlePhoneNumberChange = (data) => {
    setLabDto({ ...labDto, phoneNumber: data.phoneNumber, phoneCode: data.countryCode })
  }

  const handleSubmit = async (e) => {
    e.preventDefault();
    const isLabValid = validateLabInfo();
    const isBranchValid = validateBranchInfo();
    const isLoginValid = validateLoginInfo();
    if (!isLabValid || !isBranchValid || !isLoginValid) {
      return;
    }
    const requestBody = {
      labDto,
      branches: branches.map((branch) => ({
        ...branch,
      })),
      equipmentList: equipmentList.map((equipment) => ({
        ...equipment,
      })),
    };
    try {
      const response = await labManagementSave(requestBody,
        {
          headers: {
            "Content-Type": "application/json",
            userId: "3fa85f64-5717-4562-b3fc-2c963f66af77",
          },
        })

      if (response.statusCode === "200 OK") {
        setPopupConfig({
          icon: "success",
          title: "Saved Successfully",
          onClose: () => {
            navigate("/lab-management");
          },
        });
      } else {
        setPopupConfig({
          icon: "delete",
          title: "error",
          onClose: () => { setPopupConfig(null) }
        });
      }
    } catch (error) {
      console.error("Error saving lab:", error.response.data.message);
    }
  };

  const handleUpdate = async (e) => {
    e.preventDefault()
    const isLabValid = validateLabInfo();
    const isBranchValid = validateBranchInfo();
    const isLoginValid = validateLoginInfo();

    if (!isLabValid || !isBranchValid || !isLoginValid) {
      return;
    }

    const requestBody = {
      labDto,
      branches: branches.map((branch) => ({
        ...branch,
      })),
      equipmentList: equipmentList.map((equipment) => ({
        ...equipment,
      })),
    };
    try {
      const response = await labManagementUpdate(labDto.id, requestBody, {
        headers: {
          "Content-Type": "application/json",
          userId: "3fa85f64-5717-4562-b3fc-2c963f66af77",
        },
      });
      if (response.statusCode === "200 OK") {
        setPopupConfig({
          icon: "success",
          title: "Updated Successfully",
          onClose: () => {
            navigate("/lab-management");
          },
        });
      } else {
        setPopupConfig({
          icon: "delete",
          title: "error updating",
          onClose: () => { setPopupConfig(null) }
        });
      }
    } catch (error) {
      setPopupConfig({
        icon: "delete",
        title: "Error updating",
        onClose: handleDeleteClose,
      });
    }
  };

  const location = useLocation();
  const [viewMode, setViewMode] = useState(false);
  const [editMode, setEditMode] = useState(false);
  const [addMode, setAddMode] = useState(false);
  const mode = location.state?.mode;
  const [labTypeData, setLabTypeData] = useState([])

  const fetchLabtypes = async () => {
    const createdBy = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
    try {
      const response = await getLabTypes('', 0, 10, createdBy);
      setLabTypeData(response.data)
    } catch (error) {
      console.error('Error fetching lab types:', error);
    }
  }

  useEffect(() => {
    if (mode === "view") {
      setViewMode(true);
      const data = location.state?.response;
      setLabDto(data.labDto);
      setBranches(data.branches);
      setEquipmentList(data.equipmentList);
    } else if (mode === "edit") {
      setEditMode(true);
      const data = location.state?.response;
      setLabDto(data.labDto);
      if (data.branches?.length > 0) {
        setBranches(data.branches);
      }
      setEquipmentList(data.equipmentList);
    } else {
      setAddMode(true);
    }
    fetchLabtypes();
  }, []);

  useEffect(() => {
    if (mode !== "edit" && mode !== "add") return;

    if (tab === 1 && labDto?.hasBranches && (branches?.length ?? 0) === 0) {
      addBranch();
      setLabDto((prev) => ({ ...prev, hasBranches: false }));
      setTab(0);
    } else if (tab === 2 && (equipmentList?.length ?? 0) === 0) {
      addEquipment();
    }
  }, [mode, tab, labDto?.hasBranches, branches, equipmentList]);

  return (
    <div className="LabManagement">
      <div className="add-lab-container">
        <div className="add-lab-parent"><span className="add-lab"> {viewMode ? "View Lab" : location.state?.mode === "edit" ? "Edit Lab" : "Add Lab"} </span></div>
        <div className="infos-parent"><span className={`${tab === 0 ? "active-tab" : "inactive-tab"}`} onClick={() => handleTabClick(0)}> Lab info</span>
          {labDto.hasBranches && (
            <span className={`${tab === 1 ? "active-tab" : "inactive-tab"}`} onClick={() => handleTabClick(1)}>Branch info</span>)}
          <span className={`${tab === 2 ? "active-tab" : "inactive-tab"}`} onClick={() => handleTabClick(2)}>Equipment info</span>
          <span className={`${tab === 3 ? "active-tab" : "inactive-tab"}`} onClick={() => handleTabClick(3)}>Login info</span>
        </div>
      </div>
      <form className="add-lab-form">
        {tab === 0 && (
          <>
            <div className="all-sections">
              <div className="first-two-sections">
                <div className="form-section-one">
                  <div className="form-group-labmanagement">
                    <DropDown
                      value={labTypeData.find(e => e.id === labDto.labTypeId)?.name || ""}
                      label="Lab Type"
                      options={labTypeData || []}
                      placeholder="Select"
                      onChange={(option) => { handleLabDropdownChange("id", option) }}
                      name="labTypeId"
                      fieldName='name'
                      required
                      disabled={viewMode}
                      error={labErrors.labTypeId}
                    />
                  </div>
                  <div className="form-group-labmanagement">
                    <InputField label="Lab Name" type="text" name="labName" placeholder="Enter Lab Name" value={labDto.labName} onChange={handleLabChange} disabled={viewMode} required error={labErrors.labName} />
                  </div>
                  <div className="form-group-labmanagement">
                    <DropDown
                      label="Country"
                      options={countries || []}
                      value={labDto.country}
                      placeholder="Country"
                      onChange={(option) => { handleLabDropdownChange("countryName", option) }}
                      name="country"
                      fieldName="countryName"
                      required
                      disabled={viewMode}
                      error={labErrors.country}
                    />
                  </div>
                  <div className="form-group-labmanagement">
                    <DropDown
                      label="City"
                      options={cities || []}
                      value={labDto.city}
                      placeholder="City"
                      onChange={(option) => { handleLabDropdownChange("cityName", option) }}
                      name="city"
                      fieldName="cityName"
                      required
                      disabled={viewMode}
                      error={labErrors.city}
                    />
                  </div>
                  <div className="form-group-labmanagement">
                    <InputField label="Address" type="textarea" name="address" placeholder="Enter Address" value={labDto.address} onChange={handleLabChange} required disabled={viewMode} error={labErrors.address} />
                  </div>
                  <div className="form-group-labmanagement">
                    <DropDown
                      value={labDto.packageId}
                      label="Package"
                      options={[
                        { id: "1", packageId: "Basic" },
                        { id: "2", packageId: "Premium" }
                      ]}
                      placeholder="Select"
                      onChange={() => setLabDto({ ...labDto, packageId: "3fa85f64-5717-4562-b3fc-2c963f66afa6" })}
                      name="packageId"
                      fieldName={"packageId"}
                      required
                      disabled={viewMode}
                      error={labErrors.packageId}
                    />
                  </div>
                  <div className="form-group-labmanagement alm-radio-buttons status-radio-buttons">
                    <label className="alm-label">
                      Status<span className="star">*</span>{" "}
                    </label>
                    <div className="lm-input-radio-buttons">
                      <InputField
                        label={"Active"}
                        type="radio"
                        name={"status"}
                        value={true}
                        checked={labDto.active === true}
                        onChange={() => setLabDto({ ...labDto, active: true })}
                        disabled={viewMode}
                      />
                      <InputField
                        label={"Inactive"}
                        type="radio"
                        name={"status"}
                        value={false}
                        checked={labDto.active === false}
                        onChange={() => setLabDto({ ...labDto, active: false })}
                        disabled={viewMode}
                      />
                      {labErrors.active && <Error message={labErrors.active} type="error" />}
                    </div>
                  </div>
                </div>
                <div className="form-section-two">
                  <div className="form-group-labmanagement">
                    <InputField label="Lab manager's Name" type="text" name="labManagerName" placeholder="Enter Lab manager's Name" value={labDto.labManagerName} onChange={handleLabChange} required disabled={viewMode} error={labErrors.labManagerName} />
                  </div>
                  <div className="form-group-labmanagement">
                    <DropDown
                      value={labDto.continent}
                      label="Continent"
                      options={continents || []}
                      placeholder="Select"
                      onChange={(option) => { handleLabDropdownChange("continentName", option) }}
                      name="continent"
                      fieldName={"continentName"}
                      required
                      disabled={viewMode}
                      error={labErrors.continent}
                    />
                  </div>
                  <div className="form-group-labmanagement">
                    <DropDown
                      value={labDto.state}
                      label="State"
                      options={states || []}
                      placeholder="Select"
                      onChange={(option) => { handleLabDropdownChange("stateName", option) }}
                      name="state"
                      fieldName="stateName"
                      required
                      disabled={viewMode}
                      error={labErrors.state}
                    />
                  </div>
                  <div className="form-group-labmanagement">
                    <InputField label="Zip Code" type="text" placeholder="Enter Zip Code" value={labDto.zipCode} onChange={handleLabChange} name="zipCode" disabled={viewMode} required error={labErrors.zipCode} />
                  </div>
                  <div className="form-group-labmanagement lm-phone-number">
                    <InputField label="Phone Number" type="phone" name="phoneNumber" placeholder="Enter Phone Number" value={labDto.phoneNumber} onChange={handlePhoneNumberChange} required disabled={viewMode} error={labErrors.phoneNumber} />
                  </div>
                  <div className="form-group-labmanagement alm-radio-buttons">
                    <label className="alm-label alm-label-branches">
                      Has Branches?<span className="star">*</span>{" "}
                    </label>
                    <div className="lm-input-radio-buttons-hb">
                      <InputField
                        type="radio"
                        name={"hasBranches"}
                        value="true"
                        checked={labDto.hasBranches === true}
                        onChange={() => setLabDto({ ...labDto, hasBranches: true })}
                        disabled={viewMode}
                        label={"Yes"}
                      />
                      <InputField
                        type="radio"
                        name={"hasBranches"}
                        value="false"
                        checked={labDto.hasBranches === false}
                        onChange={() => setLabDto({ ...labDto, hasBranches: false })}
                        disabled={viewMode}
                        label={"No"}
                      />
                      {labErrors.hasBranches && <Error message={labErrors.hasBranches} type="error" />}
                    </div>
                  </div>
                </div>
              </div>
              <div className="form-section-three">
                <div className="lm-upload-logo">
                  <InputField label="Upload Lab Logo" type="file" name="logo" value={labDto.logo} onChange={handleFileChange} required disabled={viewMode} existingFileName={labDto.logo} error={labErrors.logo} />
                </div>
              </div>
            </div>
          </>
        )}
        {tab === 1 && (
          <>
            <div className="branchesParent">
              {branches.map((e, index) => (
                <div key={index} className="branchesInfo">
                  <div className="first-two-sections ">
                    <div className="form-section-one ">
                      <div className="form-group-labmanagement">
                        <DropDown
                          value={e.branchType}
                          label="Branch Type"
                          options={[
                            { id: "1", branchType: "Diagnostic" },
                            { id: "2", branchType: "Research" }
                          ]}
                          onChange={(option) => {
                            handleBranchDropDownChange(index, option, "branchType")
                          }}
                          placeholder="Select"
                          name="branchType"
                          fieldName={"branchType"}
                          required
                          disabled={viewMode}
                          error={branchErrors[index]?.branchType}
                        />
                      </div>

                      <div className="form-group-labmanagement">
                        <InputField label="Contact Person" type="text" placeholder="Enter Contact Person" value={e.contactPerson} onChange={(e) => handleBranchChange(e, index)} name="contactPerson" disabled={viewMode} />
                      </div>
                      <div className="form-group-labmanagement">
                        <DropDown
                          value={e.continent}
                          label="Continent"
                          options={continents || []}
                          onChange={(option) => handleBranchDropDownChange(index, option, "continentName")}
                          placeholder="Select"
                          fieldName="continentName"
                          name="continent"
                          disabled={viewMode}
                        />
                      </div>

                      <div className="form-group-labmanagement">
                        <DropDown
                          label="State"
                          value={e.state}
                          options={branchDropdownOptions[index]?.states || []}
                          placeholder="Select"
                          name="state"
                          onChange={(option) => {
                            handleBranchDropDownChange(index, option, "stateName")
                          }}
                          disabled={viewMode}
                          fieldName="stateName"
                        />
                      </div>
                      <div className="form-group-labmanagement">
                        <InputField label="Zip Code" type="text" name="zipCode" placeholder="Enter Zip Code" value={e.zipCode} onChange={(e) => handleBranchChange(e, index)} disabled={viewMode} />
                      </div>
                      <div className="form-group-labmanagement">
                        <InputField label="Email" type="email" name="email" placeholder="Enter Email" value={e.email} onChange={(e) => handleBranchChange(e, index)} disabled={viewMode} />
                      </div>
                    </div>
                    <div className="form-section-two">
                      <div className="form-group-labmanagement">
                        <InputField label="Branch Name" type="text" name="branchName" placeholder="Enter Branch Name" value={e.branchName} onChange={(e) => handleBranchChange(e, index)} disabled={viewMode} />
                      </div>
                      <div className="form-group-labmanagement lm-phone-number">
                        <InputField label="Phone Number" type="phone" name="phoneNumber" placeholder="Enter Phone Number" value={e.phoneNumber} onChange={(option) => handleBranchPhoneNumberChange(option, index)} disabled={viewMode} />
                      </div>
                      <div className="form-group-labmanagement">
                        <DropDown
                          label="Country"
                          value={e.country}
                          options={branchDropdownOptions[index]?.countries || []}
                          placeholder="Select"
                          name="country"
                          fieldName="countryName"
                          disabled={viewMode}
                          onChange={(option) => {
                            handleBranchDropDownChange(index, option, "countryName")
                          }}
                        />
                      </div>
                      <div className="form-group-labmanagement">
                        <DropDown
                          label="City"
                          value={e.city}
                          options={branchDropdownOptions[index]?.cities || []}
                          placeholder="Select"
                          name="city"
                          onChange={(option) => {
                            handleBranchDropDownChange(index, option, "cityName")
                          }}
                          disabled={viewMode}
                          fieldName="cityName"
                        />
                      </div>
                      <div className="form-group-labmanagement">
                        <InputField label="Address" type="textarea" name="address" placeholder="Enter Address" value={e.address} onChange={(e) => handleBranchChange(e, index)} disabled={viewMode} required error={branchErrors[index]?.address} />
                      </div>
                    </div>
                  </div>
                  <br />
                  {!viewMode && (
                    <div className="equipmentButtons">
                      <button className="equipmentButton" type="button" onClick={addBranch}> Add </button>
                      <button className="equipmentButton" type="button" onClick={() => { handleDelete(index); }}> Delete </button>
                    </div>
                  )}
                </div>
              ))}
            </div>
          </>
        )}
        {tab === 2 && (
          <>
            <div className="equipmentsParent">
              {equipmentList.map((e, index) => (
                <div key={index} className="equipmentsInfo">
                  <div className="equipments">
                    <div className="form-group-labmanagement">
                      <InputField label="Equipment Name" type="text" placeholder="Enter Equipment Name" value={e.equipmentName} onChange={(e) => handleEquipmentChange(e, index)} name="equipmentName" disabled={viewMode} />
                    </div>
                    <div className="form-group-labmanagement">
                      <InputField label="Equipment Model Name" type="text" placeholder="Enter Equipment Model Name" value={e.model} onChange={(e) => handleEquipmentChange(e, index)} name="model" disabled={viewMode} />
                    </div>
                  </div>
                  <br />
                  {!viewMode && (
                    <div className="equipmentButtons">
                      <button className="equipmentButton" type="button" onClick={addEquipment}>Add</button>
                      <button className="equipmentButton" type="button" onClick={() => handleDelete(index)}>Delete</button>
                    </div>
                  )}
                </div>
              ))}
            </div>
          </>
        )}
        {tab === 3 && (
          <>
            <div className="loginParent">
              <div className="form-group-labmanagement">
                <div className="login-child">
                  <InputField label="Login Email ID" type="email" placeholder="Enter Email Address" value={labDto.email} onChange={handleLabChange} name="email" required disabled={viewMode} error={loginErrors.email} />
                </div>
              </div>
              <div className="form-group-labmanagement">
                <div className="login-child">
                  <InputField label="Set Password" type="password" name="password" placeholder="Enter Set Password" value={labDto.password} onChange={handleLabChange} disabled={viewMode} />
                </div>
              </div>
            </div>
            {tab === 3 && (
              <div className="form-buttons">
                <button className="backButton" type="button" onClick={handleTabPrevious}>Back</button>
                {!viewMode && !editMode && (
                  <button className="Button" type="submit" onClick={handleSubmit}>Save</button>
                )}
                {editMode && (
                  <button className="Button" type="submit" onClick={handleUpdate}>Update</button>
                )}
              </div>
            )}
          </>
        )}
      </form>
      {tab !== 3 && (
        <>
          <div className="form-buttons">
            <button className="backButton" type="button" onClick={handleTabPrevious}>Back</button>
            <button className="Button" type="submit" onClick={handleSave}>
              {viewMode ? "Next" : editMode ? "Update & Next" : "Save & Next"}
            </button>
          </div>
        </>
      )}
      <div>{popupConfig && <Swal {...popupConfig} />}</div>
    </div>
  );
};

export default AddLabManagement;