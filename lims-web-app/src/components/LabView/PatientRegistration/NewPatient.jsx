import React, { useEffect, useState } from 'react'
import LimsTable from '../../LimsTable/LimsTable'
import "./PatientRegistration.css";
import { Button, Input } from '@mui/material';
import InputField from '../../Homepage/InputField';
import DropDown from '../../Re-usable-components/DropDown';
import { getAllCities, getAllCountries, getAllStates } from '../../../services/locationMasterService';
import closeIcon from '../../../assets/icons/add-close.svg';
import calenderIcon from "../../../assets/icons/Vector.svg";
import { useLocation, useNavigate } from 'react-router-dom';
import Swal from '../../Re-usable-components/Swal';
import { Cancel } from '@mui/icons-material';
import { patientRegistrationSave, updatePatient } from '../../../services/LabViewServices/PatientRegistrationService';
import { addReferral, referralsGetAll } from '../../../services/LabViewServices/referralMasterService';
import { getAllRelations } from '../../../services/relationMasterService';
import { getAllOrganizationMaster } from '../../../services/LabViewServices/OrganizationMasterService';

const NewPatient = () => {
    const[countries, setCountries] = useState([]);
    const[states, setStates] = useState([]);
    const[cities, setCities] = useState([]);
    const [popup,setPopup]=useState(false)
    const  [editMode,setEditMode]=useState(false);
    const location = useLocation();
    const navigate=useNavigate();
    const [referral,setReferral]=useState([])
    const [relation,setRelation]=useState([])
    const [organisation,setOrganisation]=useState([])
    const [gender,setGender]=useState([])
      const [currentPage, setCurrentPage] = useState(0);
      const [pageSize, setPageSize] = useState(10);
      const [keyword, setKeyword] = useState('');
        const [flag, setFlag] = useState(true);
    const[tableData, setTableData] = useState({
        name: "",
        email: "",
        dateOfBirth: "",
        country: "",
        city: "",
        address: "",
        phoneNumber: "",
        gender: "",
        referralMaster: {},
        age: "",
        state: "",
        labId:'06228e13-e32b-4420-b980-0f6b8744e170',
        pinCode: "",
        relation: "",
        organisation: "",
        patientId: ""
});

  const [referralDetails, setReferralDetails] = useState({
        referralName: '',
        email: '',
        phoneNumber: '',
        dateOfBirth: '',
        roleId: '83d67f64-4525-4f67-8e75-5bd8c12ddd6c',
        labId:'06228e13-e32b-4420-b980-0f6b8744e170'
    });
    const [isRefferalModalOpen, setRefferalIsModalOpen] = useState(false);

useEffect(() => {
    const fetchCountry = async () => {
        const response = await getAllCountries("", [], 0, 250, "countryName");
        setCountries(response.data);
    }
    fetchCountry();
    fetchReferrals(currentPage, pageSize, keyword, flag);
    fetchRelations(currentPage, pageSize, keyword, flag);
    fetchOrganisation(currentPage, pageSize);
}, []);

useEffect(() => {
    const fetchStates = async () => {
        const response = await getAllStates("", [tableData.country], 0, 250, "stateName");
        setStates(response.data);
    }

    if (tableData.country) {
        fetchStates();
    } else {
        setStates([]);
    }
}, [tableData?.country]);

useEffect(() => {
    const fetchCities = async () => {
        const response = await getAllCities("", [tableData.state], 0, 250, "cityName");
        setCities(response.data);
    }

    if (tableData.state) {
        fetchCities();
    } else {
        setCities([]);
    }
}, [tableData?.state]);

  let fetchReferrals = async (currentPage, size, keyword = '', flag = true) => {
    try {
      const createdBy = '3fa85f64-5717-4562-b3fc-2c963f66afa6'; 
     let response = await referralsGetAll(createdBy, keyword, flag, currentPage, size);
        setReferral(response.data);
    } catch (error) {
      console.error('Error fetching referrals:', error);
    }
  };


  let fetchRelations= async (currentPage, size, keyword = '', sortedBy = 'relationName') => {
    try {
      const createdBy = '3fa85f64-5717-4562-b3fc-2c963f66afa9'; 

      let response = await getAllRelations(keyword, currentPage, size, sortedBy);
      setRelation(response.data);
  
    } catch (error) {
      console.error('Error fetching relation:', error);
    }
  };
  

  let fetchOrganisation = async (currentPage, size,flag) => {
    try {
        const createdBy = "ff96616a-dba1-4206-acaf-8541a3468111";
        const response = await getAllOrganizationMaster(currentPage, size, createdBy, flag);
      setOrganisation(response.data);
  
    } catch (error) {
      console.error('Error fetching organisation:', error);
    }
  };


const handlePhoneChange = (object) => {
    const{countryCode, phoneNumber} = object;
    setTableData((prevData) => ({
        ...prevData,
        phoneNumber: phoneNumber,
        phoneCode: countryCode
    }));
}
const handleInputChange = (event) => {
    const { name, value } = event.target;
    setTableData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  const handleDropdownChange = (fieldName, selectedOption) => {
  if(selectedOption.target.name!="referralMaster" || !editMode){
   
    setTableData({
        ...tableData,
        [selectedOption.target.name]: selectedOption.target.value[fieldName]
    })

       
  }else{
    setTableData({
        ...tableData,
    "referralMaster": selectedOption.target.value
       })
  }
  };

  const handleInputRefferalChange = (e) => {
    const { name, value } = e.target
    setReferralDetails(prev => ({
        ...prev,
        [name]: value
    }));
}
const handleRefferalPhoneChange = (e) => {
    setReferralDetails(prev => ({
        ...prev,
        phoneNumber: e.phoneNumber,
        phoneCode: e.countryCode
    }))

}

const handleSaveReferral = async () => {
    const createdBy = '6a4ba7df-baa9-4038-8b50-5f52b378a716'; 
    const payload = {
        ...referralDetails,
        roleId: referralDetails.roleId || null
    }
    try {
    const response= await addReferral(payload, createdBy);
    setRefferalIsModalOpen(false);
    setReferralDetails({})
        setPopup({
          icon: 'success',
          title: 'Added Successfully',
          onClose: () => {
            setPopup(null);
           
          },
        });
        fetchReferrals(currentPage, pageSize, keyword, flag);
    } catch (error) {
        console.error('Error saving referral:', error);
        setRefferalIsModalOpen(false);
        setReferralDetails({})
        setPopup({
          icon: 'error',
          title: 'Failed to save referral',
          onClose: () => setPopup(null),
        });
    }
}



const handleRegister = async (element) => {

    const patientData={
        ...tableData,
        referralMaster:referralDetails.referralName
    }
    try {
        const createdBy = '06228e13-e32b-4420-b980-0f6b8744e133';
        let response;
        if (tableData.id) {
          response = await updatePatient(tableData.id, tableData);
        } else {
          response = await patientRegistrationSave(patientData, createdBy);
        }
           if(element=="register"){
            setPopup({
                icon: "success",
                title: "Successfully Generated",
                discription:`Patient ID : ${response.data.patientSequenceId}`,
                onClose: () => {
                    navigate("/lab-view/patientRegistration");
                }
            })
           }else{
            if (tableData.id) {
                response = await updatePatient(tableData.id, tableData);
              } else {
                response = await patientRegistrationSave(tableData, createdBy);
              }
            setPopup({
                icon: "success",
                title: "Successfully Generated",
                discription:`Patient ID : ${response.data.patientSequenceId}`,
                isButton:true,
                buttonText:"Proceed with Billing",
                onButtonClick:()=>{
                    navigate("/lab-view/addBill");
                },
                onClose: () => {
                    navigate("/lab-view/patientRegistration");
                }
            })
           }
    } catch (error) {
      console.error("Error registering patient:", error);
      setPopup({
        icon: "error",
        title: "Registration Failed",
        message:"Something went wrong. Please try again.",
        onClose: () => {
            setPopup(null);
        }
      });
    }
  };

  useEffect(() => {
    const patientDetails = location.state?.patientDetails;
    const mode = location.state?.mode;

    if (mode === "edit") {
        setEditMode(true);
        setTableData(patientDetails.data); 
    }
}, [location.state]);
    return (
        <div className='patient-table-container'>
          <div className='title'> {editMode ? "Existing Patient" : "New Patient"}</div>
            <div className='new-parent-table'>
                <div className='new-patient-input-group'>
                    <div className='new-table-input'>
                        <InputField type="text" 
                        name="name" 
                        placeholder="Enter Here" 
                        onChange={handleInputChange}
                        label={"Name"} 
                        fieldName={"name"}
                        value={tableData.name}
                        required 
                        />
                    </div>
                    <div className='new-table-input'>
                        <InputField type="email" 
                          onChange={handleInputChange}
                        name="email" 
                        placeholder="Enter Here" 
                        label={"Email"}  
                        fieldName={"email"}
                        value={tableData.email}
                        />
                    </div>
                    <div className='new-table-input'>
                        <InputField type="Date" 
                        name="dateOfBirth" 
                        onChange={handleInputChange}
                        placeholder="Enter Here" 
                        label={"Date of Birth"} 
                        fieldName={"dateOfBirth"}
                        value={tableData.dateOfBirth}
                        required
                        />
                        {/* <img className='date-of-birth-icon' src={calenderIcon} alt=" " /> */}
                    </div>

                    <div className='new-table-input'>
                        <DropDown type="text" 
                        name="country" 
                        placeholder="Enter Here" 
                        label={"Country"} 
                        onChange={(selectedOption) => handleDropdownChange("countryName", selectedOption)}
                        options={countries || []} fieldName={"countryName" } 
                        value={tableData.country}
                        required
                        />
                    </div>

                    <div className='new-table-input'>
                        <DropDown type="text" 
                        name="city" 
                        placeholder="Enter Here" 
                        onChange={(selectedOption) => handleDropdownChange("cityName", selectedOption)}
                        label={"City"} 
                        options={cities || []} fieldName={"cityName"} 
                        value={tableData.city}
                        required 
                        />
                    </div>
                    <div className='new-table-input'>
                        <InputField type="text"
                         name="address"
                         onChange={handleInputChange}
                          placeholder="Enter Here" 
                          label={"Address"} 
                          fieldName={"address"}
                          value={tableData.address}
                          required
                          />
                    </div>
                    <div>
                              <DropDown
                                    label="Referral"
                                    name={"referralMaster"}
                                    options={referral||[]}
                                    onChange={(selectedOption) => handleDropdownChange("id", selectedOption)}
                                    fieldName={"referralName"}
                                    value={tableData?.referralMaster?.referralName}
                                    placeholder="Select" />
                                     <div className='patient-add-referral-tag-container'>
                                    <a href="#" className='patient-add-referral-tag' onClick={() => setRefferalIsModalOpen(true)}>Add referral</a></div>
                                {isRefferalModalOpen && (
                                    <div className="patient-refferal-overlay">
                                        <div className='patient-refferal-model'>
                                            <div className='patient-refferal-model-header'>
                                                <span className='patient-referral-header'>Add Referral</span>
                                                <img src={closeIcon} style={{ cursor: 'pointer' }} alt="close" onClick={() => setRefferalIsModalOpen(false)} />
                                            </div>
                                            <div className='patient-refferal-model-content'>
                                                <div className='patient-refferal-fields'>
                                                    <InputField label={"Referral Name"} name={"referralName"} value={referralDetails.referralName} type={"text"} placeholder={"Referral Name"} onChange={handleInputRefferalChange} />
                                                </div>

                                                <div className='patient-refferal-model-content'>
                                                    <InputField label={"Email"} name={"email"} type={"text"} value={referralDetails.email} placeholder={"Enter email"} onChange={handleInputRefferalChange} />
                                                </div>

                                                <div className='patient-refferal-model-content'>
                                                    <InputField label={"Phone Number"} name={"phoneNumber"} value={referralDetails.phoneNumber} type={"phone"} onChange={handleRefferalPhoneChange} />
                                                </div>

                                                <div className='patient-refferal-model-content'>
                                                    <InputField label="Date of Birth" type="date" name={"dateOfBirth"} value={referralDetails.dateOfBirth} placeholder="Enter Name" onChange={handleInputRefferalChange} />
                                                </div>
                                            </div>
                                            <div className='refferal-model-footer'><button className='btn-primary' onClick={handleSaveReferral}>Save</button></div>
                                        </div>
                                    </div>
                                )}
                            </div>
                </div>
                <div className='new-patient-input-group'>
                    <div className='new-table-input'>
                        <InputField type="phone" 
                        name="phoneNumber" 
                        placeholder="Enter Here" 
                        onChange={handlePhoneChange}
                        label={"Phone Number"} 
                        value={tableData.phoneNumber}
                        required
                     />
                    </div>
                    <div className='new-table-input'>
                        <DropDown type="text" 
                        name="gender" 
                        placeholder="Enter Here" 
                        label={"Gender"}
                        fieldName={"gender"}
                        options={[
                            {label: "Male", gender: "Male"},
                            {label: "Female", gender: "Female"},
                            {label: "Other", gender: "Other"}
                        ]}
                        value={tableData.gender}
                        onChange={(selectedOption) => handleDropdownChange("gender", selectedOption)}
                         required 
                         />
                    </div>
                    <div className='new-table-input'>
                        <InputField type="text"
                         name="age"
                          placeholder="Enter Here" 
                          onChange={handleInputChange}
                          label={"Age"}
                          value={tableData.age}
                           required 
                        />
                    </div>
                    <div className='new-table-input'>
                        <DropDown type="text" 
                        name="state" 
                        placeholder="Enter Here" 
                        onChange={(selectedOption) => handleDropdownChange("stateName", selectedOption)}
                        label={"State"} 
                        options={states || []} 
                        fieldName={"stateName"} 
                        value={tableData.state}
                        required
                        />
                    </div>
                    <div className='new-table-input'>
                        <InputField type="text" 
                        name="pinCode"
                         placeholder="Enter Here" 
                         onChange={handleInputChange}
                         label={"Pin Code"}
                         value={tableData.pinCode}
                          required
                          />
                    </div>
                    <div className='new-table-input'>
                        <DropDown type="text" 
                        name="relation" 
                        placeholder="Enter Here" 
                        onChange={(selectedOption) => handleDropdownChange("id", selectedOption)}
                        label={"Relation"} 
                        options={relation || []}
                        fieldName={"relationName"} 
                        value={relation.find(rel => rel.id === tableData.relation)?.relationName || ''}
                        required
                        />
                    </div>
                    <div className='new-table-input'>
                        <DropDown type="text" 
                        name="organisation"
                         placeholder="Enter Here" 
                         onChange={(selectedOption) => handleDropdownChange("id", selectedOption)}
                          label={"Organisation"} 
                          options={organisation || []}
                           fieldName={"name"} 
                           value={organisation.find(org => org.id === tableData.organisation)?.name || ''}
                          />
                    </div>
                </div>
            </div>
            <div className='new-patient-btn'>
                <div>
                <button className='clear'onClick={() => navigate("/lab-view/patientRegistration")}>Back</button>
                </div>
            <div className='new-patient-register-btn'>
            <button className='clear'onClick={()=>handleRegister("register")}> Register</button>
            <button className='btn-primary' onClick={()=>handleRegister("registerandbill")} >Register and Bill</button>
            </div>
            </div>
            {popup && <Swal {...popup} />}
        </div>
    )
}
export default NewPatient
