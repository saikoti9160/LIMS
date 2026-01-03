import React, { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import "./MyProfile.css";
import DefaultProfileImg from '../../assets/images/default-profile-img.svg';
import InputField from "./InputField";
import { useLocation, useNavigate} from "react-router-dom";
import { updatePassword, updateProfile } from "../../services/AuthenticationService";
import Swal from "../Re-usable-components/Swal";
import DropDown from "../Re-usable-components/DropDown";
import { getAllCities, getAllCountries, getAllStates } from "../../services/locationMasterService";
import { getLabByUser , labManagementUpdate } from "../../services/LabManagementService";

const UpdateMyProfile = () => { 

    const [labDto, setLabDto] = useState({});
    const [equipmentList,setEquipmentList] = useState([]);
    const [branches,setBranches]=useState([]);
    const [popupConfig, setPopupConfig] = useState(null);
    const navigate=useNavigate()

    const { mode } = useLocation().state;
    const [swalConfig, setSwalConfig] = useState(null);
    
    const userDetails = useSelector((state) => state?.user?.user);
    const [countries, setCountries] = useState([]);
    const [states, setStates] = useState([]);
    const [cities, setCities] = useState([]);

    const [labErrors,setLabErrors]=useState({})
    const validateLabInfo = () => {
      let errors = {};
      if (!labDto.country || labDto.country.trim() === '') errors.country = "Country is required";
      if (!labDto.state || labDto.state.trim() === '') errors.state = "State is required";
      if (!labDto.city || labDto.city.trim() === '') errors.city = "City is required";
      if (!labDto.zipCode || labDto.zipCode.trim() === '') errors.zipCode = "Zipcode is required";
      if (!labDto.address || labDto.address.trim() === '') errors.address = "Address is required";
      if (!labDto.phoneNumber || labDto.phoneNumber.trim() === '') errors.phoneNumber = "Phone number is required";
      setLabErrors(errors);
      return Object.keys(errors).length === 0;
    };

    const fetchCountries = async () => {
      let response = await getAllCountries("", [labDto.continent], 0, 250, "countryName");
      setCountries(response.data);
      setStates([]);
      setCities([]);
    };
    
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

    const [profileForm, setProfileForm] = useState({
        userName: userDetails?.userName,
        email: userDetails?.email
    });    

    const [passwordForm, setPasswordForm] = useState({
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
    });

    const handleSubmit = async(type) => {
        if(type === 'password') {
            const token = localStorage.getItem('accessToken');
            const payload = {
                email: userDetails?.email,
                password: passwordForm?.oldPassword,
                newPassword: passwordForm?.newPassword,
                token: token
            }
            const res = await updatePassword(payload, token);
        }
        else if(type === 'profile') {
            const payload = {
                userName: profileForm.userName,
                email: profileForm.email,
                userId: userDetails?.userId,
                profilePic: ""
            }
            const response = await updateProfile(payload);
            if(response.statusCode === '200 OK') {
                const userDetails = JSON.parse(localStorage.getItem('userDetails'));
                userDetails.userName = profileForm.userName;
                localStorage.setItem('userDetails', JSON.stringify(userDetails));
                setSwalConfig({
                    icon: "success",
                    title: "Success!",
                    text: "",
                    onClose: () => navigate('/my-profile')
                })
            }
            
        }

    };
    
    const handleEdit = (event) => {
        if(mode === 'profile') {
            const { name, value } = event.target;
            setProfileForm({ ...profileForm, [name]: value });
        }
        else {
            const { name, value } = event.target;
            setPasswordForm({ ...passwordForm, [name]: value });
        }
    };
    const handleLabDropdownChange=(fieldName,object)=>{
        const {name, value} = object.target;
        setLabDto(prevLabData => ({
            ...prevLabData,
            [name]: value[fieldName],
            ...(name === "country" ? { state: null, city: null } : {}), 
            ...(name === "state" ? { city: null } : {})
        }));
      }
  
    const fetchLabById = async (id) => {
        const response = await getLabByUser(id);
        setLabDto(response.data.labDto);
        setBranches(response.data.branches);
        setEquipmentList(response.data.equipmentList);
    };

    const handleLabChange=(e) => {
        setLabDto((prev) => ({
            ...prev,
            [e.target.name]: e.target.value,
          }));
    }
    const handlePhoneChange =(e) => {
        setLabDto((prev) => ({
            ...prev,
            phoneCode: e.countryCode,
            phoneNumber: e.phoneNumber
          }));
    }

    const handleLabUpdate = async () => {
        if(!validateLabInfo()){
          setPopupConfig({
            icon: "delete",
            title: "Please Fill All The Required Fields",
            onClose: () => {
              setPopupConfig(null)
            },
          });
          return;
        }
        const requestBody = {
            labDto,
            branches,
            equipmentList
          };
        try {
              const response = await labManagementUpdate(labDto.id, requestBody, {
                headers: {
                  "Content-Type": "application/json",
                  userId: "69323c18-6e5f-4d35-af42-fe5a898def41",
                },
              });
              if (response.statusCode === "200 OK") {
                console.log(response)
                setPopupConfig({
                  icon: "success",
                  title: "Updated Successfully",
                  onClose: () => {
                    navigate("/lab-view/lab-profile");
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
                onClose: ()=>{setPopupConfig(null)},
              });
            }
    }


    useEffect(() => {
        if(mode === 'address') {
            const id="69323c18-6e5f-4d35-af42-fe5a898def41"
            fetchLabById(id);
            fetchCountries();     
        }
    },[mode]);
    return (
        <div className="profile-container">
            <h2 className="title">
                {(mode === 'password')?
                ('Reset Password'):
                ("my Profile")}   
            </h2>

            {mode === 'profile' && (
                <div className="card">
                    <div className="profile">
                        <div className="image-container">
                            <img
                                src={userDetails?.profilePic || DefaultProfileImg } // Replace with actual image
                                alt="Profile"
                                className="profile-pic"
                            />
                            <span>Profile Picture</span>
                        </div>
                        <div className="profile-input-fields-container">
                            <InputField 
                                label="User Name"
                                type="text"
                                placeholder='Name'
                                name="userName"
                                width='470px'
                                value={profileForm?.userName}
                                onChange={handleEdit}
                                required
                            />
                            <InputField 
                                label="Email Address"
                                type="text"
                                placeholder='Email'
                                name="email"
                                width='470px'
                                value={profileForm?.email}
                                onChange={handleEdit}
                                required
                                disabled
                            />
                        </div>
                        <div className="edit-btn-container btn-bottom">
                            <button className="btn-primary" onClick={()=>handleSubmit('profile')}>
                            Save
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {mode === 'password' && (
                <div className="card">
                    <div className="profile">
                        <div className="profile-input-fields-container" style={{alignItems: "center"}} >
                            <InputField 
                                label="Old Password"
                                type="password"
                                placeholder='Old Password'
                                name="oldPassword"
                                width='470px'
                                autoComplete="true"
                                value={passwordForm.oldPassword}
                                onChange={handleEdit}
                                required
                            />
                            <InputField 
                                label="New Password"
                                type="password"
                                placeholder='New Password'
                                name="newPassword"
                                width='470px'
                                value={passwordForm.newPassword}
                                onChange={handleEdit}
                                required
                            />
                            <InputField 
                                label="Confirm New Password"
                                type="password"
                                placeholder='Confirm New Password'
                                name="confirmPassword"
                                width='470px'
                                value={passwordForm.confirmPassword}
                                onChange={handleEdit}
                                required
                            />
                        </div>
                        <div className="edit-btn-container btn-bottom">
                            <button className="btn-primary" onClick={()=>handleSubmit('password')}>
                                Save
                            </button>
                        </div>
                    </div>
                </div>
            )}
            {swalConfig && (
                <Swal {...swalConfig} />
            )}
            {mode === 'address' && (               
            <div className='lpc-address-update-profile'>
                <div className='lpc-address-child'>
                <div className="lpc-fields">
                    <DropDown
                      label="Country"
                      options={countries || []}
                      value={labDto.country}
                      placeholder="Country"
                      onChange={(option) => {handleLabDropdownChange("countryName",option)}}
                      name="country"
                      fieldName="countryName"
                      error={labErrors.country}
                      required
                    />
                </div>
                    <div className="lpc-fields">
                    <DropDown
                      value={labDto.state}
                      label="State"
                      options={states || []}
                      placeholder="Select"
                      onChange={(option) => {handleLabDropdownChange("stateName",option)}}
                      name="state"
                      fieldName="stateName"
                      error={labErrors.state}
                      required
                    />
                    </div>
                    <div className="lpc-fields"> 
                    <DropDown
                      label="City"
                      options={cities || []}
                      value={labDto.city}
                      placeholder="City"
                      onChange={(option) => {handleLabDropdownChange("cityName",option)}}
                      name="city"
                      fieldName="cityName"
                      error={labErrors.city}
                      required
                    />
                    </div>
                    </div>
                    <div className='lpc-address-child'>
                    <div className="lpc-fields">
                        <InputField label="Zip Code" type="text" placeholder="Enter Zip Code" name="zipCode" value={labDto.zipCode} onChange={handleLabChange} error={labErrors.zipCode} required />
                    </div>
                    <div className="lpc-fields">
                        <InputField label="Address" type="textarea" name="address" placeholder="Enter Address"  value={labDto.address} onChange={handleLabChange} error={labErrors.address} required />
                    </div>
                <div className="lpc-fields">
                    <InputField label="Phone Number" type="phone" name="phoneNumber" placeholder="Enter Phone Number" value={labDto.phoneNumber} onChange={(e) => handlePhoneChange(e)} error={labErrors.phoneNumber} required />
                </div>
                </div>
                <div className="lab-address-buttons">
                    <button className="btn-clear" onClick={()=>{navigate("/lab-view/lab-profile")}}>Back</button>
                    <button className="btn-primary" onClick={handleLabUpdate}>Update</button>
                </div>
            </div>                
            )}
           {popupConfig && <Swal {...popupConfig} />}
        </div>

    )
}

export default UpdateMyProfile;