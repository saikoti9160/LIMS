import React, { useEffect, useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom';
import InputField from '../../Homepage/InputField';
import DropDown from '../../Re-usable-components/DropDown';
import closeIcon from '../../../assets/icons/add-close.svg';
import calenderIcon from "../../../assets/icons/Vector.svg";

import dayjs from 'dayjs';
import CustomTimePicker from '../../Re-usable-components/CustomTimePicker';
import Swal from '../../Re-usable-components/Swal';
import { getAllCities, getAllCountries, getAllStates } from '../../../services/locationMasterService';

const AddBranchMaster = () => {
    const [popupConfig, setPopupConfig] = useState(null);
    const location = useLocation();
    const navigate = useNavigate();
    const [isOpen, setIsOpen] = useState(false);
    const [viewMode, setViewMode] = useState(false);
    const [roles, setRoles] = useState([]);
    const [labDepartments, setLabDepartments] = useState([]);
    const [file, setFile] = useState(null);
    const [countries, setCountries] = useState([]);
    const [states, setStates] = useState([]);
    const [cities, setCities] = useState([]);
    const [errors, setErrors] = useState({});

    const [branch, setBranch] = useState({
        branchName: '',
        branchType: '',
        phoneNumber: '',
        roleId: "",
        address: '',
        city: '',
        state: '',
        country: '',
        pinCode: '',
        branchTime: [],
        reportHeader: null,
        reportFooter: null,
        billHeader: null,
        billFooter: null,
        email: ' ',
        setPassword: ''
    });


    const handleModel = () => {
        setIsOpen(true);
    }
    const [isEnabled, setIsEnabled] = useState(true);

    const daysOfWeek = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];

    const [schedule, setSchedule] = useState(
        daysOfWeek.map((day) => ({
            day,
            isEnabled: false,
            startTime: "",
            endTime: "",
        }))
    );

    const weekDayEnum = {
        Sunday: "SUNDAY",
        Monday: "MONDAY",
        Tuesday: "TUESDAY",
        Wednesday: "WEDNESDAY",
        Thursday: "THURSDAY",
        Friday: "FRIDAY",
        Saturday: "SATURDAY"
    };

    useEffect(() => {
        const mode = location.state?.mode;
        setViewMode(mode === "view");
    }, [location]);

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
            if (branch.country) {
                let response = await getAllStates("", [branch.country], 0, 250, "stateName");
                setStates(response.data);
                setCities([]);
            } else {
                setStates([]);
            }
        };
        fetchStates();
    }, [branch.country]);

    useEffect(() => {
        const fetchCities = async () => {
            if (branch.state) {
                let response = await getAllCities("", [branch.state], 0, 250, "cityName");
                setCities(response.data);
            } else {
                setCities([]);
            }
        };
        fetchCities();
    }, [branch.state]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setBranch(prev => ({
            ...prev,
            [name]: value
        }));
    };


    const handlePhoneChange = (e) => {
        setBranch(prev => ({
            ...prev,
            phoneNumber: e.phoneNumber,
            phoneCode: e.countryCode
        }));
    };

    const handleDropDownChange = (selectedOption, fieldName) => {
        setBranch(prev => ({
            ...prev,
            [fieldName]: selectedOption.target.value.id
        }));
    };

    const handleSave = async () => {
        if(!validateBranch()) return;
    };

    const handleUpdate = async () => {
        if(!validateBranch()) return;
    }

    const handleFileChange = (e, fieldName) => {
        console.log("Field name:", fieldName);
        const uploadedFile = e.target.files[0];
        console.log("File uploaded:", uploadedFile.name);
        if (!uploadedFile) {
            return;
        }
        setFile(uploadedFile);
        setBranch((prevState) => ({
            ...prevState,
            [fieldName]: uploadedFile.name,
        }));
    };


    const toggleDay = (index) => {
        const selectedDay = daysOfWeek[index];
        const weekDayEnumValue = weekDayEnum[selectedDay];

        setSchedule((prevSchedule) =>
            prevSchedule.map((daySchedule, i) =>
                i === index ? { ...daySchedule, isEnabled: !daySchedule.isEnabled } : daySchedule
            )
        );

        setBranch((prev) => {
            const updatedBranchTime = prev.branchTime.slice();
            const dayIndex = updatedBranchTime.findIndex((a) => a.weekDay === weekDayEnumValue);

            if (dayIndex === -1) {
                updatedBranchTime.push({
                    weekDay: weekDayEnumValue,
                    startTime: "",
                    endTime: "",
                    available: true,
                });
            } else {
                updatedBranchTime.splice(dayIndex, 1);
            }

            return {
                ...prev,
                branchTime: updatedBranchTime,
            };
        });
    };

    const handleTimeChange = (index, type, value) => {
        if (!value) return;

        const formattedTime = value.format("HH:mm");
        const selectedDay = daysOfWeek[index];
        const weekDayEnumValue = weekDayEnum[selectedDay];

        setBranch((prev) => {
            const newBranchTime = [...prev.branchTime];
            const dayIndex = newBranchTime.findIndex((a) => a.weekDay === weekDayEnumValue);

            if (dayIndex !== -1) {
                newBranchTime[dayIndex] = {
                    ...newBranchTime[dayIndex],
                    [type]: formattedTime,
                };
            }

            return { ...prev, branchTime: newBranchTime };
        });
        setSchedule((prevSchedule) =>
            prevSchedule.map((daySchedule, i) =>
                i === index ? { ...daySchedule, [type]: formattedTime } : daySchedule
            )
        );
    };

    const validateBranch = () => {
        let newErrors = {};
    
        if (!branch.branchName.trim()) newErrors.branchName = "Branch Name is required";
        if (!branch.phoneNumber.trim()) {
            newErrors.phoneNumber = "Phone Number is required";
        } else if (!/^\d{10}$/.test(branch.phoneNumber)) {
            newErrors.phoneNumber = "Phone Number must be 10 digits";
        }
        if (!branch.email.trim()) {
            newErrors.email = "Email is required";
        } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(branch.email)) {
            newErrors.email = "Invalid email format";
        }
        if (branch.pinCode.trim() && !/^\d{6}$/.test(branch.pinCode)) {
            newErrors.pinCode = "Pincode must be exactly 6 digits";
        }
    }


    return (
        <div className='parent-branch-container'>
            <div className='add-branchTitle'>{viewMode ? "View Branch Master" : location.state?.mode === "edit" ? "Edit Branch Master" : "Add Branch Master"}</div>
            <div className='branch-container'>
                <div className='branch-header'>
                    <div className='add-branchSection1'>
                        <div className='add-branchSection-branchTime'>
                            <InputField
                                label="Branch Name"
                                type='text'
                                placeholder="Enter Branch Name"
                                name="branchName"
                                value={branch.branchName}
                                onChange={handleInputChange}
                                disabled={viewMode}
                                required
                            />
                        </div>
                        <div className='add-branchSection-branchTime'>
                            <InputField label="Phone Number" type="phone"
                                onChange={handlePhoneChange}
                                value={branch.phoneNumber}
                                disabled={viewMode} 
                                required/>
                        </div>
                        <div className='add-branchSection-branchTime'>
                            <DropDown
                                value={branch.state}
                                label="State"
                                options={states || []}
                                placeholder="Select"
                                onChange={(option) => {
                                    setBranch({ ...branch, state: option.target.value.stateName })
                                }}
                                name="state"
                                fieldName="stateName"
                                required
                                disabled={viewMode}
                            /></div>

                        <div className='add-branchSection-branchTime'>
                            <InputField
                                label="Pincode"
                                type='text'
                                name="pinCode"
                                placeholder="Enter pincode"
                                value={branch.pinCode}
                                onChange={handleInputChange}
                                disabled={viewMode}

                            />
                        </div>

                        <div className='add-branchSection-branchTime'>
                            <DropDown
                                label="Role"
                                options={roles}
                                name={"roleId"}
                                value={branch.roleId}
                                fieldName="roleName"
                                placeholder="Select Role"
                                onChange={handleDropDownChange}
                                disabled={viewMode}
                            />
                        </div>
                    </div>
                    <div className='add-branchSection2'>
                        <div className='add-branchSection-branchTime'>
                            <DropDown
                                label="Branch Type"
                                name="branchType"
                                options={branch.branchType}
                                placeholder="Select Branch Type"
                                value={branch.branchType}
                                onChange={handleDropDownChange}
                                disabled={viewMode}
                                required
                            />
                        </div>
                        <div className='add-branchSection-branchTime'>
                            <DropDown
                                label="Country"
                                options={countries || []}
                                value={branch.country}
                                placeholder="Country"
                                onChange={(option) => {
                                    console.log(option)
                                    setBranch({ ...branch, country: option.target.value.countryName })
                                }}
                                name="country"
                                fieldName="countryName"
                                required
                                disabled={viewMode}
                            />
                        </div>
                        <div className='add-branchSection-branchTime'>
                            <DropDown
                                label="City"
                                options={cities || []}
                                value={branch.city}
                                placeholder="City"
                                onChange={(option) => {
                                    setBranch({ ...branch, city: option.target.value.cityName })
                                }}
                                name="city"
                                fieldName="cityName"
                                required
                                disabled={viewMode}
                            />
                        </div>

                        <div className='add-branchSection-branchTime'>
                            <InputField
                                label="Address"
                                type='text'
                                placeholder="Enter Address"
                                name="address"
                                value={branch.address}
                                onChange={handleInputChange}
                                disabled={viewMode}
                            />
                        </div>
                        <div className='add-branchSection-branchTime'>
                            <InputField
                                label="Branch Time"
                                // type='text'
                                placeholder="Select Time"
                                value={branch.branchTime.length > 0 && branch.branchTime[0].startTime
                                    ? `${branch.branchTime[0].weekDay}, ${dayjs(`${dayjs().format('YYYY-MM-DD')} ${branch.branchTime[0].startTime}`).format("hh:mm")}`
                                    : ''
                                }
                                disabled={viewMode}
                                name="branchTime"
                                required
                            />
                            <img src={calenderIcon} alt='calendar' className='BranchCalenderIcon' onClick={handleModel} />
                        </div>
                    </div>
                </div>
                <div className='branch-file'>
                    <div>
                        <InputField
                            label="Report Header"
                            type="file"
                            placeholder="Drag file here to upload (or) Select File"
                            onChange={(e) => handleFileChange(e, "reportHeader")}
                            existingFileName={branch.reportHeader}
                            disabled={viewMode}
                        />
                    </div>
                    <div>
                        <InputField
                            label="Report Footer"
                            type="file"
                            placeholder="Drag file here to upload (or) Select File"
                            onChange={(e) => handleFileChange(e, "reportFooter")}
                            existingFileName={branch.reportFooter}
                            disabled={viewMode}
                        />
                    </div>
                    <div>
                        <InputField
                            label="Bill Header"
                            type="file"
                            placeholder="Drag file here to upload (or) Select File"
                            onChange={(e) => handleFileChange(e, "billHeader")}
                            existingFileName={branch.billHeader}
                            disabled={viewMode}
                        />
                    </div>
                    <div>
                        <InputField
                            label="Bill Footer"
                            type="file"
                            placeholder="Drag file here to upload (or) Select File"
                            onChange={(e) => handleFileChange(e, "billFooter")}
                            existingFileName={branch.billFooter}
                            disabled={viewMode}
                        />
                    </div>
                </div>
            </div>

            {isOpen && (
                <div className="model-overlay">
                    <div className="model-content">
                        <div className="img-close-pm">
                            <img src={closeIcon} alt="Close" onClick={() => setIsOpen(false)} />
                        </div>

                        <div className="model-text-area">
                            <span className="text-head">Set Standard Hours</span>
                            <span className="text-child">Configure the Standard Working Hours</span>
                        </div>

                        <div className="model-inputs">
                            {schedule.map((daySchedule, index) => (
                                <div key={index} className={`working-hours ${daySchedule.isEnabled ? "active" : ""}`}>
                                    <div className="label-data">
                                        <span className="day-label">{daySchedule.day}</span>
                                    </div>
                                    <div className="togle-data">
                                        <label className="switch">
                                            <input
                                                type="checkbox"
                                                checked={daySchedule.isEnabled}
                                                onChange={() => toggleDay(index)}
                                            />
                                            <span className="slider"></span>
                                        </label>
                                    </div>
                                    {daySchedule.isEnabled && (
                                        <div className="time-data">
                                            <CustomTimePicker
                                                value={schedule[index].startTime ? dayjs(schedule[index].startTime, "HH:mm") : null}
                                                onChange={(value) => handleTimeChange(index, "startTime", value)}
                                                disabled={viewMode}
                                            />
                                            <span className="to-text">To</span>
                                            <CustomTimePicker
                                                value={schedule[index].endTime ? dayjs(schedule[index].endTime, "HH:mm") : null}
                                                onChange={(value) => handleTimeChange(index, "endTime", value)}
                                                disabled={viewMode}
                                            />
                                        </div>
                                    )}
                                </div>
                            ))}

                        </div>
                        <div className="model-buttons">
                            <button className="btn-secondary" onClick={() => setIsOpen(false)}>Back</button>
                            <button className="btn-primary" onClick={() => setIsOpen(false)}>Save</button>
                        </div>
                    </div>
                </div>
            )}

            <div className='branch-login-container'>
                <div className="branch-login">
                    <span className="branch-login-header">Login Credentials</span>
                </div>
                <div className='branch-login-credentials'>
                    <div className='branch-login-form1'>
                        <InputField
                            label="Email"
                            type='email'
                            placeholder={"Enter Email"}
                            name={"email"}
                            value={branch.email}
                            onChange={handleInputChange}
                            disabled={viewMode}
                            required
                        />
                    </div>
                    <div className='branch-login-form2'>
                        <InputField
                            label="Set Password"
                            type='password'
                            placeholder={"Enter Password"}
                            name={"setPassword"}
                            value={branch.setPassword}
                            onChange={handleInputChange}
                            disabled={viewMode}
                            required
                        />
                    </div>

                </div>
            </div>
            <div className="buttons">
                <button className="btn-secondary" onClick={() => navigate('/lab-view/branchMaster')}>Back</button>
                {!viewMode && (
                    <button className="btn-primary" onClick={location.state?.mode === 'edit' ? handleUpdate : handleSave}>
                        {location.state?.mode === 'edit' ? 'Update' : 'Save'}
                    </button>
                )}
            </div>
            {popupConfig && (
                <Swal icon={popupConfig.icon} title={popupConfig.title} onClose={popupConfig.onClose} />
            )}
        </div>
    )
}

export default AddBranchMaster
