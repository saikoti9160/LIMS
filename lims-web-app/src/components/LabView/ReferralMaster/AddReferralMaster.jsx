import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import './AddReferralMaster.css';
import InputField from '../../Homepage/InputField';
import DropDown from '../../Re-usable-components/DropDown';
import Swal from '../../Re-usable-components/Swal';
import { addReferral, updateReferral } from '../../../services/LabViewServices/referralMasterService';
import { getRoles } from '../../../services/RoleService';
import Error from '../../Re-usable-components/Error';

const AddReferralMaster = () => {
  const [formData, setFormData] = useState({
    referralName: '',
    phoneCode: '',
    phoneNumber: '',
    dateOfBirth: '',
    roleId: '',
    labId: '06228e13-e32b-4420-b980-0f6b8744e170',
    email: '',
    password: '',
    active: true
  });

  const [roles, setRoles] = useState([]);
  const [popupConfig, setPopupConfig] = useState(null);
  const [errors, setErrors] = useState({});
  const [mode, setMode] = useState('');
  const location = useLocation();
  const navigate = useNavigate();
  const createdBy = "3fa85f64-5717-4562-b3fc-2c963f66afa6";
  const today = new Date().toISOString().split('T')[0];

  useEffect(() => {
    fetchRoles();
    const referralDetails = location.state?.referralDetails;
    const modeFromState = location.state?.mode;
    if (modeFromState) {
      setMode(modeFromState);
      if (modeFromState === 'edit' || modeFromState === 'view') {
        setFormData({
          referralName: referralDetails.data?.referralName || '',
          phoneCode: referralDetails.data?.phoneCode || '',
          phoneNumber: referralDetails.data?.phoneNumber || '',
          dateOfBirth: referralDetails.data?.dateOfBirth || '',
          roleId: referralDetails.data?.roleId || '',
          email: referralDetails.data?.email || '',
          password: referralDetails.data?.password || '',
          active: referralDetails.data?.active ?? true
        });
      }
    }
  }, [location.state]);

  const fetchRoles = async () => {
    try {
      const response = await getRoles(0, 10, "", "", createdBy);
      const mappedRoles = response.data.map(role => ({
        id: role.id,
        name: role.roleName
      }));
      setRoles(mappedRoles);
    } catch (error) {
      console.error('Error fetching roles:', error);
    }
  };

  const handleInputChange = (e) => {
    if (mode === 'view') return;
    const { name, value } = e.target;
    if (name === 'referralName') {
      const alphabetRegex = /^[A-Za-z\s]*$/;
      if (!alphabetRegex.test(value)) {
        return;
      }
      else {
        setErrors(prevErrors => ({
          ...prevErrors,
          referralName: ''
        }));
      }
    }
    setFormData(prevState => ({
      ...prevState,
      [name]: value
    }));
    if (name === 'dateOfBirth') {
      validateDateOfBirth(value);
    } else {
      setErrors(prevErrors => ({
        ...prevErrors,
        [name]: ''
      }));
    }
  };

  const validateDateOfBirth = (date) => {
    let formErrors = { ...errors };

    if (date >= today) {
      formErrors.dateOfBirth = 'Date of Birth cannot be today or in the future';
    } else {
      formErrors.dateOfBirth = '';
    }
    setErrors(formErrors);
  };

  const handlePhoneChange = (object) => {
    if (mode === 'view') return;
    const { countryCode, phoneNumber } = object;
    setFormData((prevState) => ({
      ...prevState,
      phoneNumber: phoneNumber,
      phoneCode: countryCode,
    }));
    setErrors((prevErrors) => ({
      ...prevErrors,
      phoneNumber: '',
      phoneCode: ''
    }));
  };

  const validateForm = () => {
    let formErrors = {};

    if (!formData.referralName.trim()) formErrors.referralName = 'Referral Name is required';
    if (!formData.dateOfBirth) {
      formErrors.dateOfBirth = 'Date of Birth is required';
    } else if (formData.dateOfBirth >= today) {
      formErrors.dateOfBirth = 'Date of Birth cannot be today or in the future';
    }
    if (!formData.phoneNumber) formErrors.phoneNumber = 'Phone Number is required';
    if (!formData.roleId) formErrors.roleId = 'Role is required';
    if (!formData.email) formErrors.email = 'Email is required';
    if (!formData.password) {
      formErrors.password = 'Password is required';
    } else if (formData.password.length < 8) {
      formErrors.password = "Password must be at least 8 characters long";
    } else if (!/[A-Z]/.test(formData.password)) {
      formErrors.password = "Password must contain at least one uppercase letter";
    } else if (!/[a-z]/.test(formData.password)) {
      formErrors.password = "Password must contain at least one lowercase letter";
    } else if (!/[0-9]/.test(formData.password)) {
      formErrors.password = "Password must contain at least one number";
    } else if (!/[!@#$%^&*(),.?":{}|<>]/.test(formData.password)) {
      formErrors.password = "Password must contain at least one special character";
    }
    setErrors(formErrors);
    return Object.keys(formErrors).length === 0;
  };

  const handleSelectionChange = (name, value) => {
    setFormData((prevFormData) => ({
      ...prevFormData,
      [name]: value,
    }));
  };

  const handleSave = async () => {
    if (!validateForm()) return;
    try {
      const payload = {
        ...formData,
        roleId: formData.roleId || null
      };
      if (mode === 'edit') {
        handleUpdate(payload);
      } else {
        await addReferral(payload, createdBy);
        setPopupConfig({
          icon: 'success',
          title: 'Added Successfully',
          onClose: () => {
            setPopupConfig(null);
            navigate('/lab-view/referral');
          },
        });
      }
    } catch (error) {
      console.error('Error saving referral:', error);
      setPopupConfig({
        icon: 'error',
        title: 'Failed to save referral',
        onClose: () => setPopupConfig(null),
      });
    }
  };

  const handleUpdate = async (payload) => {
    if (!validateForm()) return;
    try {
      const referralId = location.state?.referralDetails.data?.id;
      if (!referralId) {
        throw new Error('Referral ID not found');
      }
      await updateReferral(referralId, payload);
      setPopupConfig({
        icon: 'success',
        title: 'Updated Successfully',
        onClose: () => {
          setPopupConfig(null);
          navigate('/lab-view/referral');
        },
      });
    } catch (error) {
      console.error('Error updating referral:', error);
      setPopupConfig({
        icon: 'error',
        title: 'Failed to update referral',
        onClose: () => setPopupConfig(null),
      });
    }
  };

  const handleDropDownChange = (selectedOption) => {
    setFormData((prevFormData) => ({
      ...prevFormData,
      roleId: selectedOption.target.value.id || "",
    }));
    setErrors((prevErrors) => ({
      ...prevErrors,
      roleId: '',
    }));
  }

  return (
    <div className="add-referral">
      <div className="title">
        {mode === 'view' ? 'View Referral Master' : mode === 'edit' ? 'Edit Referral Master' : 'Add Referral Master'}
      </div>

      <div className="referral-form">
        <div className="input-fields-root">
          <InputField
            label="Referral Name"
            type="text"
            name="referralName"
            placeholder="Enter Referral Name"
            value={formData.referralName}
            onChange={handleInputChange}
            required
            disabled={mode === 'view'}
            error={errors.referralName}
          />
          <InputField
            label="Date of Birth"
            type="date"
            name="dateOfBirth"
            placeholder="Enter Date of Birth"
            value={formData.dateOfBirth}
            onChange={handleInputChange}
            required
            max={new Date().toISOString().split('T')[0]}
            disabled={mode === 'view'}
            error={errors.dateOfBirth}
          />
        </div>

        <div className="input-fields-root">
          <InputField
            label="Phone Number"
            type="phone"
            name="phoneNumber"
            placeholder="Enter Phone Number"
            value={formData.phoneNumber || ""}
            onChange={handlePhoneChange}
            required
            disabled={mode === 'view'}
            error={errors.phoneNumber}
          />
          <DropDown
            label="Role"
            options={roles}
            placeholder="Select Role"
            value={roles.find(role => role.id === formData.roleId)?.name || ""}
            name="roleId"
            fieldName="name"
            onChange={(selectedOptiuon) => handleDropDownChange(selectedOptiuon)}
            disabled={mode === 'view'}
            required
            error={errors.roleId}
          />
        </div>
      </div>

      <div className="referral-login-container">
        <div className="referral-login">
          <span className="referral-login-header">Login Credentials</span>
        </div>

        <div className="referral-login-credentials">
          <div className='form-group1'>
            <InputField
              label="Email Address"
              type="email"
              name="email"
              placeholder="Enter Email"
              value={formData.email}
              onChange={handleInputChange}
              required
              disabled={mode === 'view'}
              error={errors.email}
            />
          </div>

          <div className='form-group2'>
            <InputField
              label="Set Password"
              type="password"
              name="password"
              placeholder="Enter Password"
              value={formData.password}
              onChange={handleInputChange}
              required
              disabled={mode === 'view'}
              error={errors.password}
            />
          </div>
        </div>
      </div>

      <div className="referral-form-btns">
        <button className="btn-secondary" type="button" onClick={() => navigate('/lab-view/referral')}>
          Back
        </button>
        {mode !== 'view' && (
          <button className="btn-primary" type="submit" onClick={handleSave}>
            {mode === 'edit' ? 'Update' : 'Save'}
          </button>
        )}
      </div>
      {popupConfig && <Swal {...popupConfig} />}
    </div>
  );
};

export default AddReferralMaster;