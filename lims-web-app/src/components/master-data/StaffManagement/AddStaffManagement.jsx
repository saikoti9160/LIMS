import React, { useEffect, useState } from 'react'
import InputField from '../../Homepage/InputField'
import DropDown from '../../Re-usable-components/DropDown';
import "./AddStaffManagement.css"
import Button from '../../Re-usable-components/Button';
import { useLocation, useNavigate } from 'react-router-dom';
import { departmentsGetAll } from '../../../services/departmentService';
import Swal from '../../Re-usable-components/Swal';
import { saveStaff, updateStaffById } from '../../../services/staffManagementService';
import { getRoles } from '../../../services/RoleService';
import Error from '../../Re-usable-components/Error';

const AddStaffManagement = () => {
    const [usermaster, setUsermaster] = useState({
        firstName: "",
        dateOfBirth: "",
        phoneCode: "",
        phone: "",
        department: {
            id: "",
            name: ""
        },
        role: {
            id: "",
            roleName: "",
            preferences: []
        },
        position: "",
        profilePic: "",
        status: "active",
        email: "",
        password: "",
    });
    // const [preview, setPreview] = useState(null);
    const [roles, setRoles] = useState([]); // State for roles
    const [departments, setDepartments] = useState([]); // State for departments
    const [viewMode, setViewMode] = useState(false);
    const location = useLocation();
    const [file, setFile] = useState(null);
    const [popupConfig, setPopupConfig] = useState(null);
    const [errors, setErrors] = useState({});  // State to store validation errors

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        if (name === 'dateOfBirth') {
            const today = new Date();
            today.setHours(0, 0, 0, 0); // Reset time to midnight for accurate comparison
            const selectedDate = new Date(value);
            if (selectedDate >= today) {
                setErrors(prevErrors => ({
                    ...prevErrors,
                    [name]: "Date of birth should be in the past"
                }));
                return;
            }
        }
        if (!viewMode) {
            console.log("usermaster",usermaster)
            setUsermaster(prevState => ({
                ...prevState,
                [name]: value
            }))
        };
    };
    const handlePhoneChange = (event) => {
        if (viewMode) return;
        let phone = event?.phoneNumber.replace(/\D+/g, ''); // accept only numerics
        let countryCode = event?.countryCode;
        setUsermaster((prevState) => {
            const updatedState = {
                ...prevState,
                phone: phone,
                phoneCode: countryCode,
            };
            return updatedState; // Ensure both phone and phoneCode are updated together
        });
        setErrors((prevErrors) => ({ ...prevErrors, phone: "" }));
    };
    
    const handleBlur = (e) => {
        const { name, value } = e.target;
        // If the field is empty, set a required error message
        setErrors((prevErrors) => ({
            ...prevErrors,
            [name]: value.trim() ? '' : `${name.charAt(0).toUpperCase() + name.slice(1)} is required`
        }));
    };


    const navigate = useNavigate();
    const handleUserNavigate = () => {
        console.log("navigate")
        navigate("/staff-management")
    }
    const validateForm = () => {
        let newErrors = {};

        if (!usermaster.firstName) newErrors.firstName = "User Name is required";
        if (!usermaster.phone) newErrors.phone = "Phone Number is required";
        if (!usermaster.role.id) newErrors.role = "Role is required";
        if (!usermaster.dateOfBirth) newErrors.dateOfBirth = "Date of Birth is required";
        if (!usermaster.department.id) newErrors.department = "Department is required";
        if (!usermaster.position) newErrors.position = "Position is required";
        if (!usermaster.status || !["Active", "InActive"].includes(usermaster.status)) {
            newErrors.status = "Status is required";
        }
        if (!usermaster.email) newErrors.email = "Email Address is required";
        // Password Validation
        if (!usermaster.password) {
            newErrors.password = "Set Password is required";
        } else if (usermaster.password.length < 8) {
            newErrors.password = "Password must be at least 8 characters long";
        } else if (!/[A-Z]/.test(usermaster.password)) {
            newErrors.password = "Password must contain at least one uppercase letter";
        } else if (!/[a-z]/.test(usermaster.password)) {
            newErrors.password = "Password must contain at least one lowercase letter";
        } else if (!/[0-9]/.test(usermaster.password)) {
            newErrors.password = "Password must contain at least one number";
        } else if (!/[!@#$%^&*(),.?":{}|<>]/.test(usermaster.password)) {
            newErrors.password = "Password must contain at least one special character";
        }
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;  // Returns true if no errors
    };
    const handleUserAdd = async () => {
        if (!validateForm()) return;

        const payload = {
            firstName: usermaster.firstName,
            dateOfBirth: usermaster.dateOfBirth,
            phoneCode: usermaster.phoneCode,
            phone: usermaster.phone,
            department: usermaster.department.id,
            role: usermaster.role.id,
            position: usermaster.position,
            profilePic: usermaster.profilePic,
            status: usermaster.status,
            email: usermaster.email,
            password: usermaster.password,
            accountType: "75f2bd80-8f41-415b-bbe4-354b25ea449d"
        };
        console.log("Payload:", payload);

        try {
            const response = await saveStaff(payload);
            console.log(response.data);
            setPopupConfig({
                icon: 'success',
                title: 'Added Successfully',
                text: '',
                onClose: () => navigate('/staff-management'),
            });
        } catch (error) {
            console.error(error);
            setPopupConfig({
                icon: 'delete',
                title: 'Failed to add staff.',
                text: 'Please try again.',
                onButtonClick: () => setPopupConfig(null),
                onClose: () => setPopupConfig(null),
            });
        }
    }

    useEffect(() => {
        fetchDepartments(); // Fetch departments
        fetchroles(); // Fetch roles

        const userMasterDetails = location.state?.userMasterDetails?.data;
        const mode = location.state?.mode;

        if (userMasterDetails) {
            setUsermaster((prevState) => {
                const updatedState = {
                    ...userMasterDetails,
                    department: userMasterDetails.department, // Keep department as an object
                    role: userMasterDetails.role, // Keep role as an object
                    profilePic: userMasterDetails.profilePic
                };

                console.log("Updated userMaster state in mode:", mode, updatedState);
                return updatedState;
            });

            setViewMode(mode === "view");
        }
    }, [location.state]);

    // This effect will run when 'usermaster' changes
    useEffect(() => {
    }, [usermaster]);

    const handleUpdate = async () => {
        if (!validateForm()) return;
        const ide = location.state?.userMasterDetails.data?.id;
        console.log("user master updated", ide)
        try {
            const response = await updateStaffById(ide, usermaster);

            if (response.statusCode === "200 OK") {
                setPopupConfig({
                    icon: 'success',
                    title: 'Updated Successfully!',
                    text: '',
                    onClose: () => navigate('/staff-management'),
                });
            } else {
                setPopupConfig({
                    icon: 'delete',
                    title: 'Failed to update department.',
                    text: 'Please try again.',
                    onClose: () => navigate('/staff-management'),
                });
            }
        } catch (error) {
            console.error(error);
            setPopupConfig({
                icon: 'delete',
                title: 'Failed to update department.',
                text: 'Please try again.',
                onButtonClick: () => setPopupConfig(null),
                onClose: () => setPopupConfig(null),
            });
        }
    }
    const fetchDepartments = async () => {
        try {
            const response = await departmentsGetAll(0, 10, '', createdBy);
            setDepartments(response.data);
        } catch (error) {
            console.error('Error fetching departments:', error);
        }
    };

    const createdBy = "3fa85f64-5717-4562-b3fc-2c963f66afa6";
    const fetchroles = async () => {
        try {
            const response = await getRoles(0, 10, "", "", createdBy); 
            setRoles(response.data);
        } catch (error) {
            console.error('Error fetching roles:', error);
        }
    };

    const handleFileChange = (e) => {
        const uploadedFile = e.target.files[0];
        setFile(uploadedFile);
        setUsermaster((prevState) => ({
            ...prevState,
            profilePic: uploadedFile.name,
        }));
        console.log("File uploaded:", uploadedFile.name);
    };



    return (
        <div class="userMaster">
            <div className='add-user-parent'>
                <span className='add-user'>
                    {viewMode
                        ? 'View Staff'
                        : location.state?.mode === 'edit'
                            ? 'Edit Staff'
                            : 'Add Staff'}</span>
            </div>
            <div class="container">
                <div class="user_container1">
                    <div class="inner_container1">
                        <div class="group1">
                            <div className="formgroup">
                                <InputField
                                    label="User Name"
                                    type="text"
                                    placeholder="Enter Username"
                                    value={usermaster.firstName||""}
                                    name={"firstName"}
                                    onChange={handleInputChange}
                                    onBlur={handleBlur}
                                    disabled={viewMode} required
                                    error={errors.firstName}
                                />
                            </div>
                            <div className="formgroup">
                                <InputField
                                    label="Phone Number"
                                    type="phone"
                                    placeholder="Enter Phone Number"
                                    value={usermaster.phone || ""} // Ensure value is never undefined
                                    name="phone"
                                    phoneCode={usermaster.phoneCode||''}
                                    onChange={(e) => {
                                        handlePhoneChange({ phoneNumber: e.phoneNumber, countryCode: e.countryCode })
                                    }}
                                    onBlur={handleBlur}
                                    disabled={viewMode} required
                                    error={errors.phone}
                                />
                            </div>
                            <div className="formgroup">
                                <DropDown
                                    label="Role"
                                    options={roles}
                                    placeholder="Select role"
                                    value={roles.find(role => role.id === usermaster.role?.id)}
                                    name={"role"}
                                    fieldName="roleName"
                                    onChange={(option) => {
                                        console.log("option", option.target.value);
                                        setUsermaster({ ...usermaster, role: option.target.value });
                                        setErrors((prevErrors) => ({ ...prevErrors, role: "" }));
                                    }}
                                    disabled={viewMode}
                                    required
                                    error={errors.role}
                                    onBlur={handleBlur}

                                />
                            </div>
                        </div>
                        <div class="group2">
                            <div className="formgroup">
                                <InputField
                                    label="Date of Birth"
                                    type="date"
                                    value={usermaster.dateOfBirth}
                                    name={"dateOfBirth"}
                                    onChange={handleInputChange}
                                    disabled={viewMode} required
                                    error={errors.dateOfBirth}
                                />
                            </div>
                            <div className="formgroup">
                                <DropDown
                                    label="Department"
                                    options={departments}
                                    placeholder="Select Department"
                                    value={departments.find(department => department.id === usermaster.department?.id)}
                                    name={"department"}
                                    fieldName="name"
                                    onChange={(option) => {
                                        console.log("option", option.target.value);
                                        setUsermaster({ ...usermaster, department: option.target.value });
                                        // Clear error when user selects a value
                                        // setErrors((prevErrors) => ({ ...prevErrors, department: "" }));
                                    }}
                                    disabled={viewMode}
                                    required
                                    error={errors.department}
                                    onBlur={handleBlur}
                                />
                            </div>
                            <div className="formgroup">
                                <InputField
                                    label="Position"
                                    type="text"
                                    placeholder="Enter Position"
                                    value={usermaster.position}
                                    name={"position"}
                                    onChange={handleInputChange}
                                    disabled={viewMode} required
                                    error={errors.position}
                                />
                            </div>
                        </div>
                    </div>
                    <div class="inner_container2">
                        <div class="group3">
                            <InputField
                                label="Upload Profile"
                                type="file"
                                placeholder="Drag file here to upload (or) Select File"
                                onChange={handleFileChange}
                                existingFileName={usermaster.profilePic}
                                name={"profilePic"}
                                value={file}
                                disabled={viewMode}
                            />
                            <div class="inner2">
                                <label className='status-label'><b>Status</b><span className="star">*</span></label>
                                <div className='radio'>
                                    <InputField
                                        type="radio"
                                        name="status"
                                        value="Active"
                                        label={"Active"}
                                        checked={usermaster.status === "Active"}
                                        onChange={viewMode ? undefined : () => {
                                            setUsermaster({ ...usermaster, status: "Active" });
                                            setErrors((prev) => ({ ...prev, status: undefined }));
                                        }}
                                        disabled={viewMode}
                                    />
                                    <InputField
                                        type="radio"
                                        name="status"
                                        label={"InActive"}
                                        value="InActive"
                                        checked={usermaster.status === "InActive"}
                                        onChange={viewMode ? undefined : () => {
                                            setUsermaster({ ...usermaster, status: "InActive" });
                                            setErrors((prev) => ({ ...prev, status: undefined }));
                                        }}
                                        disabled={viewMode}
                                    />
                                    {errors.status && <Error message={errors.status} type="error" />}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="user_container2">
                    <div class="user_login">
                        <span class="user_login_header">Login credentials</span>
                    </div>
                    <div className="login_credentials">
                        <div className="formgroup">
                            <InputField
                                label="Email Address"
                                type="email"
                                placeholder="Enter Email"
                                value={usermaster.email}
                                name={"email"}
                                onChange={handleInputChange}
                                disabled={viewMode} required
                                error={errors.email}
                            />
                        </div>
                        <div className="formgroup">
                            <InputField
                                label="Set Password"
                                type="password"
                                placeholder="Enter Password"
                                value={usermaster.password}
                                name={"password"}
                                onChange={handleInputChange}
                                disabled={viewMode} required
                                autoComplete={"newPassword"}
                                error={errors.password}
                            />
                        </div>
                    </div>
                </div>
                <div className="botton-divv">
                    <div className='inner_btn'>
                        {viewMode ? (<button className="btn-secondary" onClick={handleUserNavigate}>Back</button>) : location.state?.mode === "edit" ? (
                            <><button className="btn-secondary" onClick={handleUserNavigate}>Back</button><button className="btn-primary" onClick={handleUpdate}>Update</button></>
                        ) : (<><button className="btn-secondary" onClick={handleUserNavigate}>Back</button><button className="btn-primary" onClick={handleUserAdd}>Save</button></>)}
                    </div>
                </div>
            </div>
            {/* Show popup if popupConfig is set */}
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
    )
}

export default AddStaffManagement
