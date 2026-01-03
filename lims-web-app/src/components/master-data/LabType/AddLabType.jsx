import React, { useState, useEffect } from 'react';
import { addLabType, updateLabType } from '../../../services/labTypeService';
import { useLocation, useNavigate } from 'react-router-dom';
import InputField from '../../Homepage/InputField';
import Swal from '../../Re-usable-components/Swal';

const AddLabType = () => {
  const [labTypeName, setLabTypeName] = useState('');
  const [viewMode, setViewMode] = useState(false);
  const [popupConfig, setPopupConfig] = useState(null);
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const location = useLocation();
  const createdBy = '3fa85f64-5717-4562-b3fc-2c963f66afa6';

  useEffect(() => {
    const labTypeDetails = location.state?.labTypeDetails;
    const mode = location.state?.mode;
    if (labTypeDetails) {
      setLabTypeName(labTypeDetails.data.name);
    }
    if (mode === 'view') {
      setViewMode(true);
    } else if (mode === 'edit') {
      setViewMode(false);
    }
  }, [location.state]);

  const validateInput = () => {
    const trimmedName = labTypeName.trim();
    if (trimmedName === '') {
      setError({ labTypeName: 'Lab Type is required' });
      return false;
    }
    setError(null);
    return true;
  };

  const handleInputChange = (event) => {
    const { value } = event.target;
    if (!viewMode) {
      const alphabetRegex = /^[A-Za-z\s]*$/;
      if (!alphabetRegex.test(value)) {
        return;
      } else {
        setError(null);
      }
      setLabTypeName(value);
    }
  };

  const handleAdd = async () => {
    if (!validateInput()) return;
    try {
      const response = await addLabType({ name: labTypeName }, createdBy);
      if (response.statusCode === "200 OK") {
        setPopupConfig({
          icon: 'success',
          title: 'Added Successfully',
          onClose: () => navigate('/masters/lab-type'),
        });
      } else {
        setError({ labTypeName: 'Lab Type already exists. Please try again.', type: 'error' });
      }
    } catch (error) {
      setError({ labTypeName: 'Failed to add Lab Type. Please try again.', type: 'error' });
    }
  };

  const handleUpdate = async () => {
    if (!validateInput()) return;
    try {
      const updatedLabType = {
        name: labTypeName,
      };
      const response = await updateLabType(location.state.labTypeDetails.data.id, updatedLabType, createdBy);
      if (response.statusCode === "200 OK") {
        setPopupConfig({
          icon: 'success',
          title: 'Updated Successfully',
          onClose: () => navigate('/masters/lab-type'),
        });
      } else {
        setError({ labTypeName: 'Failed to update Lab Type. Please try again.', type: 'error' });
      }
    } catch (error) {
      console.error(error);
      setError({ labTypeName: 'Failed to update Lab Type. Please try again.', type: 'error' });
    }
  };

  const handleNavigate = () => {
    navigate('/masters/lab-type');
  };

  return (
    <div className="add-master-container">
      <div>
        <div className="title">
          {viewMode ? 'View Lab Type' : location.state?.mode === 'edit' ? 'Edit Lab Type' : 'Add Lab Type'}
        </div>

        <div className="input-inner-div">
          <div className='input-inner-div-child'>
            <InputField
              label="Lab Type Name"
              type="text"
              placeholder="Enter Here"
              required
              value={labTypeName}
              onChange={handleInputChange}
              disabled={viewMode}
              error={error?.labTypeName}
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

export default AddLabType;