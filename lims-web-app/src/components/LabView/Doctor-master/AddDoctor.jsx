
import React, { useState, useEffect, useRef } from 'react';
import './Doctor.css';
import { useLocation, useNavigate } from 'react-router-dom';
import InputField from '../../Homepage/InputField';
import DropDown from '../../Re-usable-components/DropDown';
import calenderIcon from '../../../assets/icons/Vector.svg';
import { getLabDepartments } from '../../../services/LabViewServices/labDepartmentService';
import { getRoles } from "../../../services/RoleService";
import CustomTimePicker from '../../Re-usable-components/CustomTimePicker';
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import closeIcon from '../../../assets/icons/add-close.svg';
import Swal from '../../Re-usable-components/Swal';
import { addDoctor, updateDoctorById } from '../../../services/LabViewServices/DoctorService';
dayjs.extend(utc);

function AddDoctor() {
    const [popupConfig, setPopupConfig] = useState(null);
    const location = useLocation();
    const navigate = useNavigate();
    const navigateUrl = '/doctor/master';
    const [isOpen, setIsOpen] = useState(false);
    const [viewMode, setViewMode] = useState(false);
    const [roles, setRoles] = useState([]);
    const [labDepartments, setLabDepartments] = useState([]);
    const [errors, setErrors] = useState({});

    const validateForm = () => {
        let newErrors = {};
        if (!doctor.doctorName) {
            newErrors.doctorName = "Doctor Name is required";
        }
        if (!doctor.phoneNumber) {
            newErrors.phoneNumber = "Phone Number is required";
        }
        if (!doctor.dateOfBirth) {
            newErrors.dateOfBirth = "Date of Birth is required";
        }
        if (!doctor.departmentId) {
            newErrors.departmentId = "Department is required";
        }
        if (!doctor.roleId) {
            newErrors.roleId = "Role is required";
        }
        if (!doctor.showOnAppointment) {
            newErrors.showOnAppointment = "Appointment status is required";
        }
        if (doctor.availabilities.length === 0) {
            newErrors.availabilities = "Availability is required";
        }

        if (!doctor.isReportApprover) {
            newErrors.isReportApprover = "Report Approver is required";
        }

        if (!doctor.doctorPasskey) {
            newErrors.doctorPasskey = "Passkey is required";
        }
        if (!doctor.email) {
            newErrors.email = "Email is required";
        } else if (!/^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i.test(doctor.email)) {
            newErrors.email = "Invalid email format";
        }
        if (!doctor.setPassword) {
            newErrors.setPassword = "Password is required";
        } else if (doctor.setPassword.length < 8) {
            newErrors.password = "Password must be at least 8 characters long";
        } else if (!/[A-Z]/.test(doctor.setPassword)) {
            newErrors.password = "Password must contain at least one uppercase letter";
        } else if (!/[a-z]/.test(doctor.setPassword)) {
            newErrors.password = "Password must contain at least one lowercase letter";
        } else if (!/[0-9]/.test(doctor.setPassword)) {
            newErrors.password = "Password must contain at least one number";
        } else if (!/[!@#$%^&*(),.?":{}|<>]/.test(doctor.setPassword)) {
            newErrors.password = "Password must contain at least one special character";
        }
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };
    const [doctor, setDoctor] = useState({
        doctorName: '',
        phoneNumber: '',
        phoneCode: '',
        dateOfBirth: '',
        departmentId: '',
        roleId: '',
        showOnAppointment: '',
        availabilities: [],
        doctorPasskey: '',
        email: '',
        setPassword: '',
        isReportApprover: '',
        labId: '20cd288a-d7ed-4dde-9b1a-a520dd11e9c8'
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
    const fetchLabDepartments = async () => {
        const createdBy = "3fa85f64-5717-4562-b3fc-2c963f66afa6";
        try {
            const response = await getLabDepartments(0, 10, '', createdBy);
            setLabDepartments(response.data.content);
        } catch (error) {
            console.error("Error fetching lab departments:", error);
        }
    };
    useEffect(() => {
        fetchRoles();
        fetchLabDepartments();
    }, []);
    const handlePhoneChange = (e) => {
        setDoctor(prev => ({
            ...prev,
            phoneNumber: e.phoneNumber,
            phoneCode: e.countryCode
        }));
    };
    const handleInputChange = (e) => {
        setDoctor({ ...doctor, [e.target.name]: e.target.value });
    };
    const handleTextFieldChange = (e) => {
        const value = e.target.value;
        if (value === "" || /^[A-Za-z][a-zA-Z\s]*$/g.test(value)) {
            setDoctor({ ...doctor, [e.target.name]: e.target.value });
        }
    }
    const handleDropDownChange = (selectedOption, fieldName) => {
        setDoctor(prev => ({
            ...prev,
            [fieldName]: selectedOption.target.value.id
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
        setDoctor((prev) => {
            const updatedAvailabilities = prev.availabilities.slice();
            const dayIndex = updatedAvailabilities.findIndex((a) => a.weekDay === weekDayEnumValue);

            if (dayIndex === -1) {
                updatedAvailabilities.push({
                    weekDay: weekDayEnumValue,
                    startTime: "",
                    endTime: "",
                    available: true,
                });
            } else {
                updatedAvailabilities.splice(dayIndex, 1);
            }

            return {
                ...prev,
                availabilities: updatedAvailabilities,
            };
        });
    };
    const handleTimeChange = (index, type, value) => {
        if (!value) return;
        const formattedTime = value.format("HH:mm");
        const selectedDay = daysOfWeek[index];
        const weekDayEnumValue = weekDayEnum[selectedDay];
        setDoctor((prev) => {
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
    const handleSave = async () => {
        const formattedAvailabilities = doctor.availabilities.map((availability) => {
            let formattedStartTime = availability.startTime;
            let formattedEndTime = availability.endTime;

            if (formattedStartTime && !formattedStartTime.includes("T")) {
                formattedStartTime = dayjs(availability.startTime, "HH:mm").format("YYYY-MM-DDTHH:mm:ss");
            }
            if (formattedEndTime && !formattedEndTime.includes("T")) {
                formattedEndTime = dayjs(availability.endTime, "HH:mm").format("YYYY-MM-DDTHH:mm:ss");
            }

            return {
                ...availability,
                startTime: formattedStartTime,
                endTime: formattedEndTime,
            };
        });

        const formattedPayload = {
            ...doctor,
            availabilities: formattedAvailabilities,
        };
        if (validateForm()) { }
        try {
            const response = await addDoctor(formattedPayload, "519c5fba-c91a-40b2-9541-44aa60dd0293")
            if (response.statusCode === "200 OK") {
                setPopupConfig({
                    icon: 'success',
                    title: 'Saved Successfully!',
                    onClose: () => navigate(navigateUrl)
                });
            }
        } catch (error) {
            console.error("Error saving doctor:", error);
        }

    };

    const handleUpdate = async () => {
        const formattedAvailabilities = doctor.availabilities.map((availability) => {
            let formattedStartTime = availability.startTime;
            let formattedEndTime = availability.endTime;

            if (formattedStartTime && !formattedStartTime.includes("T")) {
                formattedStartTime = dayjs(availability.startTime, "HH:mm").format("YYYY-MM-DDTHH:mm:ss");
            }
            if (formattedEndTime && !formattedEndTime.includes("T")) {
                formattedEndTime = dayjs(availability.endTime, "HH:mm").format("YYYY-MM-DDTHH:mm:ss");
            }

            return {
                ...availability,
                startTime: formattedStartTime,
                endTime: formattedEndTime,
            };
        });

        const formattedPayload = {
            ...doctor,
            availabilities: formattedAvailabilities,
        };
        try {
            const response = await updateDoctorById(doctor.id, formattedPayload);
            if (response.statusCode === "200 OK") {
                setPopupConfig({
                    icon: 'success',
                    title: 'Updated Successfully!',
                    onClose: () => navigate(navigateUrl)
                });
            } else {
                console.error("Error updating doctor:", response.data.message);
            }
        } catch (error) {
            console.error("Error updating doctor:", error);
        }
    };

    const doctorData = location.state?.doctorData;

    useEffect(() => {

        const mode = location.state?.mode;


        setViewMode(mode === "view");

        if (doctorData) {
            const formattedAvailabilities = doctorData?.availabilities?.map((availability) => {
                const startTime = availability.startTime ? dayjs(availability.startTime).utc().format("HH:mm") : "";
                const endTime = availability.endTime ? dayjs(availability.endTime).utc().format("HH:mm") : "";

                return {
                    ...availability,
                    startTime: startTime,
                    endTime: endTime,
                };
            });

            setDoctor({
                id: doctorData?.id,
                doctorName: doctorData?.doctorName || "",
                phoneNumber: doctorData?.phoneNumber || "",
                phoneCode: doctorData?.phoneCode || "",
                dateOfBirth: doctorData?.dateOfBirth || "",
                departmentId: doctorData?.departmentId || "",
                roleId: doctorData?.roleId || "",
                showOnAppointment: doctorData?.showOnAppointment || "",
                availabilities: formattedAvailabilities || [],
                doctorPasskey: doctorData?.doctorPasskey || "",
                email: doctorData?.email || "",
                setPassword: "",
                isReportApprover: doctorData?.isReportApprover || ""
            });
        }
    }, [location.state]);

    useEffect(() => {
        if (!doctor || !Array.isArray(doctor.availabilities)) return;

        setSchedule(
            daysOfWeek.map((day) => {
                const availability = doctor.availabilities.find(
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
    }, [doctor]);

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
        if (isOpen) {
            document.body.classList.add("no-scroll");
        } else {
            document.body.classList.remove("scroll-auto");
        }
        return () => {
            document.body.classList.remove("scroll-auto");
        };
    }, [isOpen]);

    const AppointmentOptions = [
        {
            label: "Yes",
            id: "true"
        },
        {
            label: "No",
            id: "false"
        }
    ]
    return (
        <div className='add-doctor-container'>
            <div className='add-doctorTitle'>{viewMode ? "View Doctor Master" : location.state?.mode === "edit" ? "Edit Doctor Master" : "Add Doctor Master"}</div>
            <div className='add-doctorForm'>
                <div className='add-doctorFormSection1'>
                    <span className='add-doctorFormSection1Left'>
                        <div className='available-time-container'>
                            <InputField
                                type='text'
                                label='Doctor Name'
                                placeholder='Enter Doctor Name'
                                name="doctorName"
                                value={doctor.doctorName}
                                onChange={handleTextFieldChange}
                                disabled={viewMode}
                                required
                                error={errors.doctorName}
                            />
                        </div>
                        <div className='available-time-container'>
                            <InputField
                                type='date'
                                label='Date of Birth'
                                name="dateOfBirth"
                                value={doctor.dateOfBirth}
                                onChange={handleInputChange}
                                disabled={viewMode}
                                required
                                error={errors.dateOfBirth}
                            /></div>
                        <div className='available-time-container'>
                            <DropDown
                                label='Role'
                                placeholder='Select Role'
                                options={roles}
                                name="roleId"
                                value={roles.find(role => role.id === doctor.roleId)?.roleName}
                                fieldName="roleName"
                                onChange={(e) => handleDropDownChange(e, "roleId")}
                                disabled={viewMode}
                                required
                                error={errors.roleId}
                            />
                        </div>
                    </span>

                    <span className='add-doctorFormSection1Right'>
                        <div className='available-time-container'>
                            <InputField
                                type='phone'
                                label='Phone Number'
                                placeholder='Enter Phone Number'
                                onChange={handlePhoneChange}
                                value={doctor.phoneNumber}
                                disabled={viewMode}
                                required
                                error={errors.phoneNumber}
                            />
                        </div>
                        <div className='available-time-container'>
                            <DropDown
                                label='Department'
                                placeholder='Select Department'
                                options={labDepartments}
                                name="departmentId"
                                value={labDepartments.find(dept => dept.id === doctor.departmentId)?.departmentName}
                                fieldName="departmentName"
                                onChange={(e) => handleDropDownChange(e, "departmentId")}
                                disabled={viewMode}
                                required
                                error={errors.departmentId}
                            />
                        </div>
                    </span>

                </div>
                <div className='add-doctorFormSection2'>
                    <div className='available-time-container'>
                        <DropDown
                            label='Show doctor on Appointment'
                            placeholder='Select Status'
                            options={AppointmentOptions}
                            name="showOnAppointment"
                            value={doctorData ? (doctor.showOnAppointment ? "Yes" : "No") : ""}
                            fieldName="label"
                            onChange={(e) => handleDropDownChange(e, "showOnAppointment")}
                            disabled={viewMode}
                            required
                            error={errors.showOnAppointment}
                        />
                    </div>
                    <div className='available-time-container'>
                        <InputField
                            type='text'
                            label='Available Time'
                            placeholder='Enter Available Time'
                            value={doctor.availabilities.length > 0 && doctor.availabilities[0].startTime
                                ? `${doctor.availabilities[0].weekDay}, ${dayjs(`${dayjs().format('YYYY-MM-DD')} ${doctor.availabilities[0].startTime}`).format("hh:mm A")}`
                                : ''
                            }
                            disabled={viewMode}
                            required
                            error={errors.availabilities}
                        />
                        <img src={calenderIcon} alt='calendar' className='DoctorCalenderIcon' onClick={handleModel} />
                    </div>
                </div>
                <div className='add-doctorFormSection2'>
                    <div className='available-time-container'>
                        <DropDown
                            label='Doctor responsible for report approval'
                            placeholder='Select approval'
                            options={AppointmentOptions}
                            name="isReportApprover"
                            value={doctorData ? (doctor.isReportApprover ? "Yes" : "No") : ""}
                            fieldName="label"
                            onChange={(e) => handleDropDownChange(e, "isReportApprover")}
                            disabled={viewMode}
                            required
                            error={errors.isReportApprover}
                        />
                    </div>
                    <div className='available-time-container'>
                        <InputField
                            type='text'
                            label='Doctor passkey'
                            placeholder='Enter Passkey'
                            name="doctorPasskey"
                            value={doctor.doctorPasskey}
                            onChange={handleInputChange}
                            disabled={viewMode}
                            required
                            error={errors.doctorPasskey}
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

            <div className='add-doctorFormLogin'>
                <span className="loginTitleText">Login Credentials</span>
                <div className="loginContainer">
                    <span className='available-time-container'>
                        <InputField
                            label={"Email Address"}
                            placeholder={"Enter Email Address"}
                            name={'email'}
                            value={doctor.email}
                            onChange={handleInputChange}
                            disabled={viewMode}
                            autoComplete="new-email"
                            required
                            error={errors.email}
                        />
                    </span>
                    <span className='available-time-container'>
                        <InputField
                            label={"Password"}
                            placeholder={"Enter password"}
                            name={'setPassword'}
                            value={doctor.setPassword}
                            onChange={handleInputChange}
                            type="password"
                            disabled={viewMode}
                            autoComplete="new-password"
                            required
                            error={errors.setPassword}
                        />
                    </span>
                </div>
            </div>
            <div className="btn-div">
                <button className="btn-secondary" onClick={() => navigate(navigateUrl)}>Back</button>
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

export default AddDoctor;