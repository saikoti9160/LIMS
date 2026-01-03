import React, { useState, useEffect } from 'react';
import { addDepartment, updateDepartment } from '../../../services/departmentService';
import './AddDepartment.css';
import { useLocation, useNavigate } from 'react-router-dom';
import InputField from '../../Homepage/InputField';
import Swal from '../../Re-usable-components/Swal';

const AddDepartment = () => {
  const [departmentName, setDepartmentName] = useState('');
  const [viewMode, setViewMode] = useState(false);
  const [popupConfig, setPopupConfig] = useState(null);
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const location = useLocation();
  const createdBy = '3fa85f64-5717-4562-b3fc-2c963f66afa6';

  useEffect(() => {
    const departmentDetails = location.state?.departmentDetails;
    const mode = location.state?.mode;
    if (departmentDetails) {
      setDepartmentName(departmentDetails.data.name);
    }
    if (mode === 'view') {
      setViewMode(true);
    } else if (mode === 'edit') {
      setViewMode(false);
    }
  }, [location.state]);

  const handleInputChange = (event) => {
    const { value } = event.target;
    if (!viewMode) {
      const alphabetRegex = /^[A-Za-z\s]*$/;
      if (!alphabetRegex.test(value)) {
        return;
      } else {
        setError(null);
      }
      setDepartmentName(value);
    }
  };

  const validateInput = () => {
    const trimmedName = departmentName.trim();
    if (trimmedName === '') {
      setError({ departmentName: 'Department is required' });
      return false;
    }
    setError(null);
    return true;
  };

  const handleAdd = async () => {
    if (!validateInput()) return;
    try {
      const response = await addDepartment({ name: departmentName }, createdBy);
      if (response.statusCode === "200 OK") {
        setPopupConfig({
          icon: 'success',
          title: 'Added Successfully',
          onClose: () => navigate('/masters/department'),
        });
      } else {
        setError({ departmentName: 'Department already exists. Please try again.', type: 'error' });
      }
    } catch (error) {
      setError({ departmentName: 'Failed to add Department. Please try again.', type: 'error' });
    }
  };

  const handleUpdate = async () => {
    if (!validateInput()) return;
    try {
      const updatedDepartment = {
        name: departmentName
      };
      const response = await updateDepartment(location.state.departmentDetails.data.id, updatedDepartment, createdBy);
      if (response.statusCode === "200 OK") {
        setPopupConfig({
          icon: 'success',
          title: 'Updated Successfully',
          onClose: () => navigate('/masters/department'),
        });
      }
    } catch (error) {
      console.error(error);
      setError({ departmentName: 'Failed to update Department. Please try again.', type: 'error' });
    }
  };

  const handleNavigate = () => {
    navigate('/masters/department');
  };

  return (
    <div className="add-master-container">
      <div>
        <div className="title">
          {viewMode ? 'View Department' : location.state?.mode === 'edit' ? 'Edit Department' : 'Add Department'}
        </div>
        <div className="input-inner-div">
          <div className='input-inner-div-child'>
            <InputField
              label="Department Name"
              type="text"
              placeholder="Enter Here"
              required
              value={departmentName}
              onChange={handleInputChange}
              disabled={viewMode}
              error={error?.departmentName}
            />
          </div>
        </div>
      </div>

      <div className="button-div-container">
        {viewMode ? (<button className="btn-secondary" onClick={handleNavigate}>Back</button>) : location.state?.mode === "edit" ? (
          <><button className="btn-secondary" onClick={handleNavigate}>Back</button>
            <button className="btn-primary" onClick={handleUpdate}>Update</button></>
        ) : (
          <><button className="btn-secondary" onClick={handleNavigate}>Back</button>
            <button className="btn-primary" onClick={handleAdd}>Save</button></>)}
      </div>

      {popupConfig && (
        <Swal
          icon={popupConfig.icon}
          title={popupConfig.title}
          onButtonClick={popupConfig.onButtonClick}
          onClose={popupConfig.onClose}
        />
      )}
    </div>
  );
};

export default AddDepartment;