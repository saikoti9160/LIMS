import React, { useEffect, useState } from 'react'
import MyProfile from '../../Homepage/MyProfile'
import "./LabProfile.css"
import EditBtnPencil from "../../../assets/icons/edit-btn-pencil.svg"
import { getAllCities, getAllContinents, getAllCountries, getAllStates } from '../../../services/locationMasterService'
import DropDown from '../../Re-usable-components/DropDown'
import InputField from '../../Homepage/InputField'
import Swal from '../../Re-usable-components/Swal'
import { deleteBranchOrEquipment, deletePOC, getLabByUser, labManagementUpdate } from '../../../services/LabManagementService'
import { useNavigate } from 'react-router-dom'

const LabProfile = () => {
  const navigate = useNavigate();
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
  const [tab, setTab] = useState(0);
  const [continents, setContinents] = useState([])
  const [countries, setCountries] = useState([]);
  const [popupConfig, setPopupConfig] = useState(false)
  const [branchDropdownOptions, setBranchDropdownOptions] = useState([]);
  const [POCDetails, setPOCDetails] = useState([
    {
      employeeName: "",
      employeePosition: "",
      email: "",
      phoneNumber: "",
      phoneCode: ""
    }
  ])
  const addPOC = () => {
    setPOCDetails([...POCDetails, {
      employeeName: "",
      employeePosition: "",
      email: "",
      phoneNumber: "",
      phoneCode: ""
    }]);
  };
  const handlePOCChange = (e, index) => {
    const { name, value } = e.target;
    const updatedPOC = POCDetails.map((e, i) =>
      i === index ? { ...e, [name]: value } : e
    );
    setPOCDetails(updatedPOC);

    if (POCErrors[index] && POCErrors[index][name]) {
      let newErrors = { ...POCErrors };
      delete newErrors[index][name];

      if (Object.keys(newErrors[index]).length === 0) {
        delete newErrors[index];
      }
      setPOCErrors(newErrors);
    }
  };

  const handlePOCPhoneChange = (e, index) => {
    const updatedPOC = POCDetails.map((poc, i) =>
      i === index ? { ...poc, phoneNumber: e.phoneNumber, phoneCode: e.countryCode } : poc);
    setPOCDetails(updatedPOC)
  }

  const [POCErrors, setPOCErrors] = useState({})
  const validateLabInfo = () => {
    let errors = {};
    POCDetails.forEach((poc, index) => {
      let pocErrors = {};
      if (!poc.employeeName?.trim()) pocErrors.employeeName = "Employee Name is required";
      if (!poc.employeePosition?.trim()) pocErrors.employeePosition = "Employee Position is required";
      if (!poc.email?.trim()) pocErrors.email = "Email is required";
      if (!poc.phoneNumber?.trim()) pocErrors.phoneNumber = "Phone number is required";

      if (Object.keys(pocErrors).length > 0) errors[index] = pocErrors;
    });
    setPOCErrors(errors);
    return Object.keys(errors).length === 0;
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

  const addBranch = () => {
    setBranches([
      ...branches,
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
      }
    ])
  }
  const handleBranchChange = (e, index) => {
    const { name, value } = e.target;
    const updatedBranches = branches.map((branch, i) =>
      i === index ? { ...branch, [name]: value } : branch
    );
    setBranches(updatedBranches);
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
  const handleDelete = (index) => {
    setPopupConfig(tab === 1 ? {
      icon: "delete",
      title: "Are you sure?",
      text: "This will permanently delete the POC detail.",
      onButtonClick: () => handleDeletePOC(index),
      onClose: handleDeleteClose,
    } : {
      icon: "delete",
      title: "Are you sure?",
      text: "This will permanently delete the item.",
      onButtonClick: () => handleDeleteConfirm(index),
      onClose: handleDeleteClose,
    });
  };
  const handleDeleteClose = () => {
    setPopupConfig(null);
  };

  const handleDeletePOC = async (index) => {
    const itemId = POCDetails[index]?.id;
    if (!itemId) {
      removeItemFromUI(index);
      return;
    }
    try {
      const response = await deletePOC(itemId);

      if (response.statusCode === "200 OK") {
        setPOCDetails((prevPOC) => prevPOC.filter((_, i) => i !== index));
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
  }

  const handleDeleteConfirm = async (index) => {
    const isBranch = tab === 3;
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
  const handleUpdate = async (e) => {
    labDto.hasBranches = branches.some((branch) => branch.branchName !== '');
    e.preventDefault()
    const requestBody = {
      labDto,
      branches: branches.map((branch) => ({
        ...branch,
      })),
      equipmentList: equipmentList.map((equipment) => ({
        ...equipment,
      })),
      pointOfContact: POCDetails.map((poc) => ({
        ...poc,
      })),
    };
    try {
      const response = await labManagementUpdate(labDto.id, requestBody, {
        headers: {
          "Content-Type": "application/json",
          userId: "29c8b17d-a3e1-41dc-8f72-3e7b40540ad7",
        },
      });
      if (response.statusCode === "200 OK") {
        setPopupConfig({
          icon: "success",
          title: "Updated Successfully",
          onClose: () => {
            window.location.reload()
          },
        });
      } else {
        setPopupConfig({
          icon: "delete",
          title: "error updating",
          onClose: () => { setPopupConfig(null) }
        });
        console.log("update error", response.data.message)
      }
    } catch (error) {
      setPopupConfig({
        icon: "delete",
        title: "Error updating",
        onClose: handleDeleteClose,
      });
    }
  };

  const removeItemFromUI = (index) => {
    if (tab === 3) {
      setBranches((prevBranches) => prevBranches.filter((_, i) => i !== index));
    } else if (tab === 2) {
      setEquipmentList((prevEquipments) =>
        prevEquipments.filter((_, i) => i !== index)
      )
    } else if (tab === 1) {
      setPOCDetails((prevPOC) =>
        prevPOC.filter((_, i) => i !== index)
      )
    }
    setPopupConfig({
      icon: "success",
      title: "Deleted Successfully",
      onClose: handleDeleteClose,
    });
  };

  const handleTabPrevious = () => {
    if (tab > 0) {
      setTab(tab - 1);
    }
  }
  const handleTabNext = () => {
    if (tab == 1 && !validateLabInfo()) {
      setPopupConfig({
        icon: "delete",
        title: "Please Fill All The Required Fields",
        onClose: () => {
          setPopupConfig(null)

        }
      });
      return;
    }
    if (tab < 3) {
      setTab(tab + 1);
    }
  }

  useEffect(() => {
    const fetchContinents = async () => {
      let response = await getAllContinents();
      setContinents(response.data);
    };
    fetchContinents();
    fetchLabById("69323c18-6e5f-4d35-af42-fe5a898def41");
  }, []);

  const fetchLabById = async (id) => {
    const response = await getLabByUser(id);
    setLabDto(response.data.labDto);
    if (response.data.labDto.hasBranches) {
      setBranches(response.data.branches);
    }
    setEquipmentList(response.data.equipmentList);
    if (response.data.pointOfContact.length > 0) {
      setPOCDetails(response.data.pointOfContact);
    }
  };

  const handleTabSwitch = (option) => {
    if (tab === 1 && !validateLabInfo()) {
      return;
    } else {
      setTab(option);
    }
  }

  return (
    <div className='lab-profile'>
      <div className="lab-profile-title-container">
        <div className="lab-profile-title">My Profile</div>
        <div className="tabs-parent-lab-profile">
          <span className={`${tab === 0 ? "active-tab" : "inactive-tab"}`} onClick={() => { handleTabSwitch(0) }}>Lab info</span>
          <span className={`${tab === 1 ? "active-tab" : "inactive-tab"}`} onClick={() => { handleTabSwitch(1) }}>POC Details</span>
          <span className={`${tab === 2 ? "active-tab" : "inactive-tab"}`} onClick={() => { handleTabSwitch(2) }}>Equipment Details</span>
          <span className={`${tab === 3 ? "active-tab" : "inactive-tab"}`} onClick={() => { handleTabSwitch(3) }}>Branch Details</span>
        </div>
      </div>
      {tab == 0 &&
        <div className='lab-profile-content'>
          <div className='lpc-myprofile'><MyProfile showTitle={false} /></div>

          <div className='lpc-address'>
            <div className="lpc-edit-button">
              <div className="lpc-title">Profile Details</div>
              <button className="edit-btn" onClick={() => { navigate('/my-profile/edit', { state: { mode: 'address' } }) }}>
                Edit <span className=""> <img src={EditBtnPencil} alt="Edit-icon" /></span>
              </button>
            </div>
            <div className='lpc-address-child'>
              <div className="lpc-fields"><DropDown label="Country" value={labDto?.country} placeholder="Country" name="country" fieldName="countryName" disabled /></div>
              <div className="lpc-fields"><DropDown label="State" value={labDto?.state} placeholder="State" name="state" fieldName="stateName" disabled /></div>
              <div className="lpc-fields"><DropDown label="City" value={labDto?.city} placeholder="City" name="city" fieldName="cityName" disabled /></div>
            </div>
            <div className='lpc-address-child'>
              <div className="lpc-fields"><InputField label="Zip Code" value={labDto?.zipCode} type="text" placeholder="Enter Zip Code" name="zipCode" disabled /></div>
              <div className="lpc-fields"><InputField label="Address" value={labDto?.address} type="textarea" name="address" placeholder="Enter Address" disabled /></div>
              <div className="lpc-fields"><InputField label="Phone Number" value={labDto?.phoneNumber} type="phone" name="phoneNumber" placeholder="Enter Phone Number" disabled /></div>
            </div>
          </div>
        </div>}
      {tab == 1 &&
        POCDetails.map((e, index) =>
          <div className='lpc-poc-details'>
            <div className='lpc-address-child'>
              <div className="lpc-fields"><InputField label="Employee Name" type="text" placeholder="Enter Employee Name" name="employeeName" value={e.employeeName} required onChange={(e) => { handlePOCChange(e, index) }} error={POCErrors[index]?.employeeName} /></div>
              <div className="lpc-fields"><InputField label="Employee Position" type="text" name="employeePosition" placeholder="Enter Employee Position" value={e.employeePosition} required onChange={(e) => { handlePOCChange(e, index) }} error={POCErrors[index]?.employeePosition} /></div>
              <div className="lpc-fields"><InputField label="Email" type="email" name="email" placeholder="Enter Email" value={e.email} required onChange={(e) => { handlePOCChange(e, index) }} error={POCErrors[index]?.email} /></div>
            </div>
            <div className='lpc-poc-child'>
              <div className="lpc-fields"><InputField label="Phone Number" type="phone" name="phoneNumber" placeholder="Enter Phone Number" value={e.phoneNumber} required onChange={(e) => { handlePOCPhoneChange(e, index) }} error={POCErrors[index]?.phoneNumber} /></div>
            </div>
            <div className='lpc-poc-buttons'>
              <button className='btn-primary' onClick={addPOC}>Add</button>
              {POCDetails.length > 1 && <button className='delete' onClick={() => handleDelete(index)}>Delete</button>}
            </div>
          </div>
        )
      }
      {tab == 2 &&
        equipmentList.map((equipment, index) => (
          <div className='lpc-equipment'>
            <div className='lpc-address-child'>
              <div className="lpc-fields"><InputField label="Equipment Name" type="text" placeholder="Enter Equipment Name" name="equipmentName" value={equipment.equipmentName} onChange={(e) => handleEquipmentChange(e, index)} /></div>
              <div className="lpc-fields"><InputField label="Equipment Model" type="text" name="model" value={equipment.model} onChange={(e) => handleEquipmentChange(e, index)} placeholder="Enter Equipment model" /></div>
            </div>
            <div className='lpc-equipment-buttons'>
              <button className='btn-primary' onClick={addEquipment}>Add</button>
              {equipmentList.length > 1 && <button className='delete' onClick={() => handleDelete(index)}>Delete</button>}
            </div>
          </div>
        ))
      }
      {tab == 3 &&
        branches?.map((e, index) => (
          <div className='lab-profile-branch' key={index}>
            <div className='lpc-address-child'>
              <div className="lpc-fields"><InputField label="Branch Name" type="text" placeholder="Enter Branch Name" name="branchName" value={e.branchName} onChange={(e) => { handleBranchChange(e, index) }} /></div>
              <div className="lpc-fields"><DropDown label="Branch Type" value={e.branchType} options={[{ id: 1, branchType: "Dygnostic" }, { id: 2, branchType: "Research" }]} placeholder="Branch Type" onChange={(option) => { }} name="branchType" fieldName="branchType" /></div>
              <div className="lpc-fields"><InputField label="Contact Person Name" type="text" placeholder="Enter Contact Person Name" name="contactPerson" value={e.contactPerson} onChange={(e) => { handleBranchChange(e, index) }} /></div>
            </div>
            <div className='lpc-address-child'>
              <div className="lpc-fields"><InputField label="Phone Number" type="phone" placeholder="Enter Phone Number" name="phoneNumber" value={e.phoneNumber} onChange={(e) => { handleBranchPhoneNumberChange(e, index) }} /></div>
              <div className="lpc-fields"><InputField label="Email Id" type="text" placeholder="Enter Email" name="email" value={e.email} onChange={(e) => { handleBranchChange(e, index) }} /></div>
              <div className="lpc-fields"><DropDown label="Continent" value={e.continent} options={continents || []} placeholder="Continent" onChange={(option) => { handleBranchDropDownChange(index, option, "continentName") }} name="continent" fieldName="continentName" /></div>
            </div>
            <div className='lpc-address-child'>
              <div className="lpc-fields"><DropDown label="Country" value={e.country} options={branchDropdownOptions[index]?.countries || []} placeholder="Country" onChange={(option) => { handleBranchDropDownChange(index, option, "countryName") }} name="country" fieldName="countryName" /></div>
              <div className="lpc-fields"><DropDown label="State" placeholder="State" options={branchDropdownOptions[index]?.states || []} value={e.state} onChange={(option) => { handleBranchDropDownChange(index, option, "stateName") }} name="state" fieldName="stateName" /></div>
              <div className="lpc-fields"><DropDown label="City" placeholder="City" options={branchDropdownOptions[index]?.cities || []} value={e.city} onChange={(option) => { handleBranchDropDownChange(index, option, "cityName") }} name="city" fieldName="cityName" /></div>
            </div>
            <div className='lpc-address-child lpc-branch-address'>
              <div className="lpc-fields"><InputField label="ZipCode" type="text" placeholder="Enter ZipCode" name="zipCode" value={e.zipCode} onChange={(e) => { handleBranchChange(e, index) }} /></div>
              <div className="lpc-fields"><InputField label="Address" type="textarea" placeholder="Enter Address" name="address" value={e.address} onChange={(e) => { handleBranchChange(e, index) }} /></div>
            </div>
            <div className='lpc-equipment-buttons'>
              <button className='btn-primary' onClick={addBranch}>Add</button>
              {branches.length > 1 && <button className='delete' onClick={() => handleDelete(index)}>Delete</button>}
            </div>
          </div>
        ))
      }
      <div className='lab-profile-buttons'>
        <button className='clear' onClick={handleTabPrevious}>Back</button>
        {tab !== 3 && <button className='btn-primary' onClick={handleTabNext}>Next</button>}
        {tab === 3 && <button className='btn-primary' onClick={handleUpdate}>Update</button>}
      </div>
      <div>{popupConfig && <Swal {...popupConfig} />}</div>
    </div>
  )
}

export default LabProfile