import React, { useEffect, useState } from 'react'
import "./CenterMaster.css"
import DropDown from "../../Re-usable-components/DropDown"
import InputField from "../../Homepage/InputField"
import { useLocation, useNavigate } from 'react-router-dom'
import { getAllCities, getAllCountries, getAllStates } from '../../../services/locationMasterService'
import { uploadFile } from '../../../services/fileUploadService'
import dayjs, { utc } from 'dayjs'
import CustomTimePicker from '../../Re-usable-components/CustomTimePicker'
import closeIcon from '../../../assets/icons/add-close.svg';
import calenderIcon from "../../../assets/icons/Vector.svg";
import { findCenterMasterBylabId, saveCenterMaster, updateCenterMaster } from '../../../services/LabViewServices/CenterMasterService'
import Swal from '../../Re-usable-components/Swal'
import { getLabTypes } from '../../../services/labTypeService'

const CenterMaster = () => {
    const id = window?.localStorage?.getItem("userId") || "";
    const [data, setData] = useState({
        labName: "",
        labTypeId: "",
        phoneNumber: "",
        phoneCode: "",
        country: "",
        state: "",
        city: "",
        pinCode: "",
        address: "",
        availabilities: [],
        email: "",
        adminEmail: "",
        websiteUrl: "",
        reportHeader: "",
        reportFooter: "",
        billHeader: "",
        billFooter: "",
        labId: "2e2e1b97-a3aa-411b-b110-675c4dc6a5f0",
    })
    const [updateId,setUpdateId]=useState("")
    const [isOpen, setIsOpen] = useState(false)
    const [popup, setPopup] = useState(false)
    const [countries, setCountries] = useState([])
    const [states, setStates] = useState([])
    const [cities, setCities] = useState([])
    const [editMode, setEditMode] = useState(false)
    const [labTypeData, setLabTypeData] = useState([])
    const navigate = useNavigate()

    const handleInputChange = (e) => {
        setData({
            ...data,
            [e.target.name]: e.target.value
        })
    }

    const handlePhoneChange = (e) => {
        setData({
            ...data,
            phoneNumber: e.phoneNumber,
            phoneCode: e.countryCode
        })
    }

    const handleFileChange = (e, key) => {
        const uploadedFile = e.target.files[0];
        setData({ ...data, [key]: uploadedFile.name });
    };


    const handleDropDownChange = (e, fieldName) => {
        setData({
            ...data,
            [e.target.name]: e.target.value[fieldName]
        })
    }

    const handleSubmit = async () => {
        if (!validateForm()) {
            setPopup({
                icon: "delete",
                title: "Please Fill All The Required Fields",
                onClose: () => {
                    setPopup(null)
                },
            });
            return;
        }
        const formattedPayload = {
            ...data,
            availabilities: data.availabilities.map((availability) => ({
                ...availability,
                startTime: convertToTimestamp(availability.startTime),
                endTime: convertToTimestamp(availability.endTime),
            }))
        }
        const createdBy = window?.localStorage?.getItem("userId")
        const response = await saveCenterMaster(formattedPayload, createdBy)
        if (response.statusCode === "200 OK") {
            setPopup({
                icon: "success",
                title: "Saved Successfully",
                onClose: () => {
                    setPopup(null)
                }
            });
        } else {
            setPopup({
                icon: "delete",
                title: "error",
                onClose: () => { setPopup(null) }
            });
        }
    }

    const handleUpdate = async () => {
        
        if (!validateForm()) {
            setPopup({
                icon: "delete",
                title: "Please Fill All The Required Fields",
                onClose: () => {
                    setPopup(null)
                },
            });
            return;
        }
        const formattedAvailabilities = data.availabilities.map((availability) => ({
            ...availability,
            startTime: convertToTimestamp(availability.startTime),
            endTime: convertToTimestamp(availability.endTime),
        }));
        const formattedPayload = {
            ...data,
            availabilities: formattedAvailabilities,
        };
        try {
            const response = await updateCenterMaster(updateId, formattedPayload)
            if (response.data.statusCode === "200 OK") {
                setPopup({
                    icon: "success",
                    title: "Updated Successfully",
                    onClose: () => {
                        setPopup(null)
                    }
                });
            } else {
                setPopup({
                    icon: "delete",
                    title: "error",
                    onClose: () => { setPopup(null) }
                });
            }
        } catch (error) {
            console.log("error message is :", error)
        }
    }
    const fetchLabtypes = async () => {
        const createdBy = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        try {
            const response = await getLabTypes('', 0, 10, createdBy);
            setLabTypeData(response.data)
        } catch (error) {
            console.error('Error fetching lab types:', error);
        }
    }


    const fetchCenterMaster = async (id) => {
        try {
            const response = await findCenterMasterBylabId(id);
            if (response.data.statusCode === "200 OK") {
                setEditMode(true);
                setData(response.data.data);
                setUpdateId(response.data.data.id)
            }
        } catch (error) {
            console.error('Error fetching Center Master:', error);
        }
    };


    useEffect(() => {
        const fetchCountries = async () => {
            let response = await getAllCountries("", [], 0, 250, "countryName");
            setCountries(response.data);
            setStates([]);
            setCities([]);
        }
        const labId = "2e2e1b97-a3aa-411b-b110-675c4dc6a5f0";
        fetchCenterMaster(labId)
        fetchCountries();
        fetchLabtypes();
    }, [])

    useEffect(() => {
        const fetchStates = async () => {
            const response = await getAllStates("", [data?.country], 0, 250, "stateName")
            setStates(response.data)
            setCities([])
        }
        fetchStates()
    }, [data?.country])

    useEffect(() => {
        const fetchCities = async () => {
            const response = await getAllCities("", [data?.state], 0, 250, "cityName")
            setCities(response.data)
        }
        fetchCities()
    }, [data?.state])

    const [errors, setErrors] = useState({});
    const validateForm = () => {
        let newErrors = {};
        if (!data.labName.trim()) newErrors.labName = "Lab Name is required";
        if (!data.labTypeId.trim()) newErrors.labTypeId = "labTypeId is required";
        if (!data.phoneNumber.trim()) newErrors.phoneNumber = "Phone Number is required";
        if (!data.country.trim()) newErrors.country = "Country is required";
        if (!data.state.trim()) newErrors.state = "State is required";
        if (!data.city.trim()) newErrors.city = "City is required";
        if (!data.availabilities.length > 0) newErrors.availability = "Available Time is required";
        if (!data.email.trim()) newErrors.email = "Lab Email is required";
        if (!data.adminEmail.trim()) newErrors.adminEmail = "Admin Email is required";
        if (!data.websiteUrl.trim()) newErrors.websiteUrl = "Website Url is required";
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const weekDayEnum = {
        Sunday: "SUNDAY",
        Monday: "MONDAY",
        Tuesday: "TUESDAY",
        Wednesday: "WEDNESDAY",
        Thursday: "THURSDAY",
        Friday: "FRIDAY",
        Saturday: "SATURDAY"
    };
    const daysOfWeek = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
    const [schedule, setSchedule] = useState(
        daysOfWeek.map((day) => ({
            day,
            isEnabled: false,
            startTime: "",
            endTime: "",
        }))
    );

    const handleTimeChange = (index, type, value) => {
        if (!value) return;

        const formattedTime = value.format("HH:mm");
        const selectedDay = daysOfWeek[index];
        const weekDayEnumValue = weekDayEnum[selectedDay];

        setData((prev) => {
            const newAvailabilities = [...prev.availabilities];
            const dayIndex = newAvailabilities.findIndex((a) => a.weekDay === weekDayEnumValue);

            if (dayIndex !== -1) {
                newAvailabilities[dayIndex] = {
                    ...newAvailabilities[dayIndex],
                    [type]: formattedTime,
                };
            }

            return { ...prev, availabilities: newAvailabilities };
        });
        setSchedule((prevSchedule) =>
            prevSchedule.map((daySchedule, i) =>
                i === index ? { ...daySchedule, [type]: formattedTime } : daySchedule
            )
        );
    };

    dayjs.extend(utc);
    const convertToTimestamp = (time) => {
        if (!time) return "";
        return dayjs(`${dayjs().format('YYYY-MM-DD')} ${time}`).format("YYYY-MM-DDTHH:mm:ss");
    };


    const convertToLocalTime = (timestamp) => {
        if (!timestamp) return "";
        return dayjs(timestamp).utc().format("HH:mm");
    };

    const toggleDay = (index) => {
        const selectedDay = daysOfWeek[index];

        setSchedule((prevSchedule) =>
            prevSchedule.map((daySchedule, i) =>
                i === index ? { ...daySchedule, isEnabled: !daySchedule.isEnabled } : daySchedule
            )
        );

        setData((prev) => {
            const weekDayEnumValue = weekDayEnum[selectedDay];

            if (!prev.availabilities.some((a) => a.weekDay === weekDayEnumValue)) {
                return {
                    ...prev,
                    availabilities: [
                        ...prev.availabilities,
                        {
                            weekDay: weekDayEnumValue,
                            startTime: "",
                            endTime: ""
                        },
                    ],
                };
            } else {
                return {
                    ...prev,
                    availabilities: prev.availabilities.filter((a) => a.weekDay !== weekDayEnumValue),
                };
            }
        });
    };

    useEffect(() => {
        if (isOpen) {
            setSchedule((prevSchedule) =>
                prevSchedule.map((daySchedule) => ({
                    ...daySchedule,
                    startTime: daySchedule.startTime ? dayjs(daySchedule.startTime, "HH:mm") : null,
                    endTime: daySchedule.endTime ? dayjs(daySchedule.endTime, "HH:mm") : null,
                }))
            );
        }
    }, [isOpen]);


    useEffect(() => {
        if (!data) return;
        const formattedAvailabilities = data?.availabilities?.map((availability) => ({
            ...availability,
            startTime: convertToLocalTime(availability.startTime),
            endTime: convertToLocalTime(availability.endTime),
        })) || [];

        setData({
            labName: data?.labName || "",
            labTypeId: data?.labTypeId || "",
            phoneNumber: data?.phoneNumber || "",
            phoneCode: data?.phoneCode || "",
            country: data?.country || "",
            state: data?.state || "",
            city: data?.city || "",
            pinCode: data?.pinCode || "",
            address: data?.address || "",
            availabilities: formattedAvailabilities || [],
            email: data?.email || "",
            adminEmail: data?.adminEmail || "",
            websiteUrl: data?.websiteUrl || "",
            reportHeader: data?.reportHeader || "",
            reportFooter: data?.reportFooter || "",
            billHeader: data?.billHeader || "",
            billFooter: data?.billFooter || "",
            labId: data?.labId || "",
        });
    }, [editMode]);

    useEffect(() => {
        if (!data || !Array.isArray(data.availabilities)) return;

        setSchedule(
            daysOfWeek.map((day) => {
                const availability = data.availabilities.find(
                    (a) => a.weekDay === weekDayEnum[day]
                );
                return {
                    day,
                    isEnabled: !!availability,
                    startTime: availability ? availability.startTime : "",
                    endTime: availability ? availability.endTime : "",
                };
            })
        );
    }, [data]);

    useEffect(() => {
        if (isOpen) {
            document.body.style.overflow = "hidden";
        } else {
            document.body.style.overflow = "auto";
        }
        return () => {
            document.body.style.overflow = "auto";
        };
    }, [isOpen]);

    return (
        <div className='center-master '>
            <div className='title center-master-title'>Center Master</div>
            <div className='center-master-content'>
                <div className='cmc-child'>
                    <div className='cmc-field'>
                        <InputField
                            label="Lab Name"
                            type="text"
                            placeholder="Enter name"
                            name="labName"
                            onChange={handleInputChange}
                            value={data?.labName}
                            error={errors?.labName}
                            required
                        />
                    </div>
                    <div className='cmc-field'>
                        <DropDown
                            label="Lab Type"
                            options={labTypeData || []}
                            placeholder="Select"
                            name="labTypeId"
                            fieldName='name'
                            onChange={(option) => { handleDropDownChange(option, "id") }}
                            value={labTypeData.find(e => e.id === data.labTypeId)?.name || ""}
                            error={errors?.labTypeId}
                            required
                        />
                    </div>
                </div>
                <div className='cmc-child'>
                    <div className='cmc-field'>
                        <InputField 
                        type="phone" 
                        label="Phone Number" 
                        name="phoneNumber" 
                        onChange={handlePhoneChange} 
                        value={data.phoneNumber} 
                        error={errors?.phoneNumber} 
                        required 
                        />
                    </div>
                    <div className='cmc-field'>
                        <DropDown
                            label="Country"
                            options={countries || []}
                            placeholder="Select"
                            name="country"
                            fieldName="countryName"
                            value={data?.country}
                            onChange={(e) => { handleDropDownChange(e, "countryName") }}
                            error={errors?.country}
                            required
                        />
                    </div>
                </div>
                <div className='cmc-child'>
                    <div className='cmc-field'>
                        <DropDown
                            label="State"
                            options={states}
                            placeholder="Select"
                            name="state"
                            fieldName="stateName"
                            value={data?.state}
                            onChange={(e) => { handleDropDownChange(e, "stateName") }}
                            error={errors?.state}
                            disabled={!data?.country}
                            required
                        />
                    </div>
                    <div className='cmc-field'>
                        <DropDown
                            label="City"
                            options={cities}
                            placeholder="Select"
                            name="city"
                            fieldName="cityName"
                            value={data?.city}
                            onChange={(e) => { handleDropDownChange(e, "cityName") }}
                            error={errors?.city}
                            disabled={!data?.state}
                            required
                        />
                    </div>
                </div>
                <div className='cmc-child'>
                    <div className='cmc-field'>
                        <InputField
                            label="Pin Code"
                            type="text"
                            placeholder="Enter pin code"
                            name="pinCode"
                            value={data.pinCode}
                            onChange={handleInputChange}
                        />
                    </div>
                    <div className='cmc-field'>
                        <InputField
                            label="Adress"
                            type="textarea"
                            placeholder="Enter address"
                            name="address"
                            value={data.address}
                            onChange={handleInputChange}
                        />
                    </div>
                </div>
                <div className='cmc-child'>
                    <div className='cmc-field centerMaster-Time'>
                        <InputField
                            label={"Available Time"}
                            placeholder={"Select available time"}
                            value={
                                data.availabilities && data.availabilities.length > 0 && data.availabilities[0].startTime
                                    ? `${data.availabilities[0].weekDay}, ${dayjs(data.availabilities[0].startTime, "HH:mm").isValid()
                                        ? dayjs(data.availabilities[0].startTime, "HH:mm").format("hh:mm A")
                                        : "Invalid Time"}`
                                    : ""
                            }
                            error={errors?.availability}
                            required
                        />

                        <img src={calenderIcon} alt='calendar' className='cmc-icon' onClick={() => { setIsOpen(true) }} />
                    </div>
                    <div className='cmc-field'>
                        <InputField
                            label="Lab Email"
                            type="email"
                            placeholder="Enter Email"
                            name="email"
                            value={data.email}
                            onChange={handleInputChange}
                            error={errors?.email}
                            required
                        />
                    </div>
                </div>
                <div className='cmc-child'>
                    <div className='cmc-field'>
                        <InputField
                            label="Admin Email"
                            type="email"
                            placeholder="Enter Email"
                            name="adminEmail"
                            value={data.adminEmail}
                            onChange={handleInputChange}
                            error={errors?.adminEmail}
                            required
                        />
                    </div>
                    <div className='cmc-field'>
                        <InputField
                            label="Website URL"
                            type="text"
                            placeholder="Enter name"
                            name="websiteUrl"
                            value={data.websiteUrl}
                            onChange={handleInputChange}
                            error={errors?.websiteUrl}
                            required
                        />
                    </div>
                </div>
                <div className='cmc-child-file'>
                    <InputField type="file" label="Report Header" name="reportHeader" onChange={(e) => { handleFileChange(e, "reportHeader") }} value={data.reportHeader} existingFileName={data.reportHeader} />
                </div>
                <div className='cmc-child-file'>
                    <InputField type="file" label="Report Footer" name="reportFooter" onChange={(e) => { handleFileChange(e, "reportFooter") }} value={data.reportFooter} existingFileName={data.reportFooter} />
                </div>
                <div className='cmc-child-file'>
                    <InputField type="file" label="Bill Header" name="billHeader" onChange={(e) => { handleFileChange(e, "billHeader") }} value={data.billHeader} existingFileName={data.billHeader} />
                </div>
                <div className='cmc-child-file'>
                    <InputField type="file" label="Bill Footer" name="billFooter" onChange={(e) => { handleFileChange(e, "billFooter") }} value={data.billFooter} existingFileName={data.billFooter} />
                </div>
            </div>

            {isOpen && (
                <div className="model-overlay">

                    <div className="model-content">
                        <div className="img-close-pm">
                            <img
                                src={closeIcon}
                                alt="Close"
                                onClick={() => setIsOpen(false)}
                            />
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
                                            />
                                            <span className="to-text">To</span>
                                            <CustomTimePicker
                                                value={schedule[index].endTime ? dayjs(schedule[index].endTime, "HH:mm") : null}
                                                onChange={(value) => handleTimeChange(index, "endTime", value)}
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
            <div className='center-master-buttons'>
                <button className='btn-clear' onClick={() => navigate("/dashboard")}>Back</button>
                {editMode ? (
                    <button className='btn-primary' onClick={handleUpdate}>Update</button>
                ) : (
                    <button className='btn-primary' onClick={handleSubmit}>Save</button>
                )}
            </div>
            {popup && <Swal {...popup} />}
        </div>
    )
}

export default CenterMaster