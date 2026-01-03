import React, { useEffect, useState } from "react";
import InputField from "../../Homepage/InputField";
import "./PhlebotomistMaster.css";
import DropDown from "../../Re-usable-components/DropDown";
import { useLocation, useNavigate } from "react-router-dom";
import { getRoles } from "../../../services/RoleService";
import calenderIcon from "../../../assets/icons/Vector.svg";
import CustomTimePicker from "../../Re-usable-components/CustomTimePicker";
import closeIcon from '../../../assets/icons/add-close.svg';
import dayjs from "dayjs";
import { addPhlebotomist, UpdatePhlebotomist } from "../../../services/LabViewServices/PhlebotomistMasterService";
import Swal from "../../Re-usable-components/Swal";
import utc from 'dayjs/plugin/utc';

const AddPhlebotmistMaster = () => {
    const navigate = useNavigate();

    const [phlebotmist, setPhlebotmist] = useState({
        name: "",
        phoneNumber: "",
        phoneCode: "",
        dateOfBirth: "",
        roleId: "",
        email: "",
        employeeId: "",
        setPassword: "",
        labId: "3fa85f64-5717-4562-b3fc-2c963f66afa6",
        availabilities: []
    });
    const [roles, setRoles] = useState([]);
    const fetchRoles = async () => {
        try {
            const response = await getRoles();
            if (response.statusCode === "200 OK") {
                setRoles(response.data);
            }
        } catch (error) {
            console.error("Error fetching roles:", error);
        }
    };

    useEffect(() => {
        fetchRoles();
    }, []);


    const handlePhoneChange = (e) => {
        setPhlebotmist(prev => ({
            ...prev,
            phoneNumber: e.phoneNumber,
            phoneCode: e.countryCode
        }));
    };


    const handleDropDownChange = (selectedOption) => {
        setPhlebotmist(prev => ({
            ...prev,
            roleId: selectedOption.target.value.id
        }));
    };
    const handleAdd = async () => {
        const formattedPayload={
            ...phlebotmist,
            availabilities: phlebotmist.availabilities.map((availability) => ({
                ...availability,
                startTime: convertToTimestamp(availability.startTime),
                endTime: convertToTimestamp(availability.endTime),
            }))
        }
        try {
            const response = await addPhlebotomist(formattedPayload, "6a4ba7df-baa9-4038-8b50-5f52b378a716");
            console.log("response", response);
            if (response.statusCode === "200 OK") {
                setPopupConfig({
                    icon: 'success',
                    title: 'Saved Successfully!',
                    text: '',
                    onClose: () => navigate('/lab-view/phlebotmistMaster')
                })
            }
        } catch (error) {

        }
    };
    const [isOpen, setIsOpen] = useState(false);
    const handelModel = () => {
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

    const toggleDay = (index) => {
        const selectedDay = daysOfWeek[index];

        setSchedule((prevSchedule) =>
            prevSchedule.map((daySchedule, i) =>
                i === index ? { ...daySchedule, isEnabled: !daySchedule.isEnabled } : daySchedule
            )
        );

        setPhlebotmist((prev) => {
            const weekDayEnumValue = weekDayEnum[selectedDay];

            if (!prev.availabilities.some((a) => a.weekDay === weekDayEnumValue)) {
                return {
                    ...prev,
                    availabilities: [
                        ...prev.availabilities,
                        {

                            weekDay: weekDayEnumValue,
                            startTime: "",
                            endTime: "",
                            available: true,
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

    const handleTimeChange = (index, type, value) => {
        if (!value) return;

        const formattedTime = value.format("HH:mm"); 
        const selectedDay = daysOfWeek[index];
        const weekDayEnumValue = weekDayEnum[selectedDay];

        setPhlebotmist((prev) => {
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

    const [popupConfig, setPopupConfig] = useState(null);

    const handleInputChange = (e) => {
        setPhlebotmist(prev => ({ ...prev, [e.target.name]: e.target.value }))
    }
    const handleUpdate = async () => {
        const formattedAvailabilities = phlebotmist.availabilities.map((availability) => ({
            ...availability,
            startTime: convertToTimestamp(availability.startTime),
            endTime: convertToTimestamp(availability.endTime),
        }));

        const formattedPayload = {
            ...phlebotmist,
            availabilities: formattedAvailabilities,
        };
        console.log("formated phlebotomist", formattedPayload);

        try {
            const response = await UpdatePhlebotomist(formattedPayload, location.state.phlebotmistData.id);
            if (response.statusCode === "200 OK") {
                setPopupConfig({
                    icon: "success",
                    title: "Updated Successfully!",
                    onClose: () => navigate("/lab-view/phlebotmistMaster")
                });
            }
        } catch (error) {
            console.error("Error updating phlebotomist:", error);
        }
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
    
    const location = useLocation();
    const [viewMode, setViewMode] = useState(false);
    useEffect(() => {
        const mode = location.state?.mode;
        const phlebotmistData = location.state?.phlebotmistData;
        if (!phlebotmistData) return;
        setViewMode(mode === "view");
        const formattedAvailabilities = phlebotmistData?.availabilities?.map((availability) => ({
            ...availability,
            startTime: convertToLocalTime(availability.startTime),
            endTime: convertToLocalTime(availability.endTime),
        })) || [];

        setPhlebotmist({
            name: phlebotmistData?.name || "",
            phoneNumber: phlebotmistData?.phoneNumber || "",
            phoneCode: phlebotmistData?.phoneCode || "",
            dateOfBirth: phlebotmistData?.dateOfBirth || "",
            roleId: phlebotmistData?.roleId || "",
            email: phlebotmistData?.email || "",
            employeeId: phlebotmistData?.employeeId || "",
            setPassword: "",
            labId: phlebotmistData?.labId || "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            availabilities: formattedAvailabilities,
        });
    }, [location.state]);

    useEffect(() => {
        if (!phlebotmist || !Array.isArray(phlebotmist.availabilities)) return;

        setSchedule(
            daysOfWeek.map((day) => {
                const availability = phlebotmist.availabilities.find(
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
    }, [phlebotmist]);

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
        <div className="phlebotmist-master-container">

            {viewMode ? <span className="title">View Phlebotmist Master</span> : location.state?.mode === "edit" ? <span className="title">Edit Phlebotmist Master</span> : <span className="title">Add Phlebotmist Master</span>}
            <div className="phlebotmist-master-form">
                <div className="phlebotmist-form-1">
                    <InputField
                        label="Phlebotmist Name"
                        name={"name"}
                        placeholder="Enter Phlebotmist Name"
                        value={phlebotmist.name}
                        onChange={handleInputChange}
                        disabled={viewMode}

                    />
                    <InputField label="Phone Number" type="phone" onChange={handlePhoneChange}
                        value={phlebotmist.phoneNumber}
                        disabled={viewMode} />
                    <DropDown
                        label="Role"
                        options={roles}
                        name={"roleId"}
                        value={roles.find(role => role.id === phlebotmist.roleId)}
                        fieldName="roleName"
                        placeholder="Select Role"
                        onChange={handleDropDownChange}
                        disabled={viewMode}
                    />

                </div>

                <div className="phlebotmist-form-2">
                    <InputField
                        label="Employee ID"
                        name={"employeeId"}
                        type="text"
                        value={phlebotmist.employeeId}
                        placeholder="Enter Employee ID"
                        onChange={handleInputChange}
                        disabled={viewMode}
                    />
                    <InputField
                        type="date"
                        label="Date of Birth"
                        name={"dateOfBirth"}
                        value={phlebotmist.dateOfBirth}
                        onChange={handleInputChange}
                        disabled={viewMode}
                    />
                    <div className="phlebotmist-picker">
                        <InputField
                            label={"Available Time"}
                            placeholder={"Select available time"}
                            value={
                                (viewMode && location.state?.mode === "edit") // Edit mode
                                    ? (phlebotmist.availabilities && phlebotmist.availabilities.length > 0
                                        ? `${phlebotmist.availabilities[0].weekDay}, ${dayjs(phlebotmist.availabilities[0].startTime).utc().format("hh:mm A")}`
                                        : ""
                                    )
                                    : !(viewMode && location.state?.mode === "add") // Add mode
                                        ? (phlebotmist.availabilities.length > 0 && phlebotmist.availabilities[0].startTime
                                            ? `${phlebotmist.availabilities[0].weekDay}, ${dayjs(`${dayjs().format('YYYY-MM-DD')} ${phlebotmist.availabilities[0].startTime}`).format("hh:mm A")}`
                                            : ""
                                        )
                                        : ""
                            }

                            disabled={viewMode}
                        />
                        <img src={calenderIcon} alt='calendar' className='phlebotmist-cal-icon' onClick={handelModel} />
                    </div>

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


            <div className="phlebotmist-master-form-2">
                <span className="login-text">Login Credentials</span>
                <div className="login-div">
                    <div className="login-div1">
                        <InputField label="Email" placeholder="Enter Email"
                            name={"email"}
                            value={phlebotmist.email}
                            onChange={handleInputChange}
                            disabled={viewMode} />

                    </div>

                    <div className="login-div2">
                        <InputField label="Password"
                            name={"setPassword"}
                            value={phlebotmist.setPassword || ""}
                            onChange={handleInputChange}
                            placeholder="Enter Password"
                            type="password"
                            disabled={viewMode}
                            autoComplete="new-password" />
                    </div>
                </div>
            </div>

            <div className="phlebotmist-button-div">
                <button className="btn-secondary" onClick={() => navigate("/lab-view/phlebotmistMaster")}>Back</button>
                {!viewMode && location.state?.mode === "edit" ? <button className="btn-primary" onClick={handleUpdate}>Update</button> :
                    <button className="btn-primary" onClick={handleAdd} >Save</button>}
            </div>

            {popupConfig && (
                <Swal
                    icon={popupConfig.icon}
                    title={popupConfig.title}
                    text={popupConfig.text}
                    onButtonClick={popupConfig.onButtonClick}
                    onClose={popupConfig.onClose}
                />
            )}
        </div>
    );
};

export default AddPhlebotmistMaster; 