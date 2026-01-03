import React, { useState, useEffect } from 'react';
import { addBranchType, updateBranchType } from '../../../services/branchType';
import { useNavigate, useParams, useLocation } from 'react-router-dom';
import InputField from '../../Homepage/InputField';
import Swal from '../../Re-usable-components/Swal';


const AddBranchType = () => {

  const [branchTypeName, setBranchTypeName] = useState('');
  const [popupConfig, setPopupConfig] = useState(null);
  const [viewMode, setViewMode] = useState(false);
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const location = useLocation();
  const createdBy = '3fa85f64-5717-4562-b3fc-2c963f66afa6';

  useEffect(() => {
    const mode = location.state?.mode;
    const branchDetails = location.state?.branchTypeDetails;
    if (branchDetails) {
      setBranchTypeName(branchDetails.data.branchTypeName);
    }
    if (mode === 'view') {
      setViewMode(true);
    } else if (mode === 'edit') {
      setViewMode(false);
    }
  }, [location.state]);

  const handleInputChange = (event) => {
    const { value } = event.target;
    const alphabetRegex = /^[A-Za-z\s]*$/;
    if (!alphabetRegex.test(value)) {
      return;
    }
    setError(null);
    setBranchTypeName(value);
  };

  const validateInput = () => {
    const trimmedName = branchTypeName.trim();
    if (trimmedName === '') {
      setError({ branchTypeName: 'Branch Type is required' });
      return false;
    }
    setError(null);
    return true;
  };

  const handleAdd = async () => {
    if (!validateInput()) return;
    try {
      const response = await addBranchType({ branchTypeName }, createdBy);
      if (response.statusCode === "201 CREATED") {
        setPopupConfig({
          icon: 'success',
          title: 'Added Successfully',
          onClose: () => navigate('/masters/branch-type'),
        });
      } else {
        setError({ branchTypeName: 'Branch Type already exists. Please try again.', type: 'error' });
      }
    } catch (error) {
      console.error('Error adding branch type:', error);
      setError({ branchTypeName: 'Failed to add Branch Type. Please try again.', type: 'error' });
    }
  };

  const handleUpdate = async () => {
    if (!validateInput()) return;
    try {
      const updatedBranchType = {
        branchTypeName,
        modifiedBy: '3fa85f64-5717-4562-b3fc-2c963f66afa6'
      };
      const response = await updateBranchType(location.state.branchTypeDetails.data.id, updatedBranchType);
      if (response.statusCode === "200 OK") {
        setPopupConfig({
          icon: 'success',
          title: 'Updated Successfully!',
          onClose: () => navigate('/masters/branch-type'),
        });
      } else {
        setError({ branchTypeName: 'Failed to update Branch Type. Please try again.', type: 'error' });
      }
    } catch (error) {
      console.error('Error updating branch type:', error);
      setError({ branchTypeName: 'Failed to update Branch Type. Please try again.', type: 'error' });
    }
  };

  const handleNavigate = () => {
    navigate('/masters/branch-type');
  };

  return (
    <div className="add-master-container">
      <div>
        <div className="title">
          {viewMode ? 'View Branch Type' : location.state?.mode === 'edit' ? 'Edit Branch Type' : 'Add Branch Type'}
        </div>

        <div className="input-inner-div">
          <div className="input-inner-div-child">
            <InputField
              label="Branch Type"
              type="text"
              name={branchTypeName}
              placeholder="Enter Here"
              required
              value={branchTypeName}
              onChange={handleInputChange}
              disabled={viewMode}
              error={error?.branchTypeName}
            />
          </div>
        </div>
      </div>

      <div className="button-div-container">
        {viewMode ? (
          <button className="btn-secondary" onClick={handleNavigate}>Back</button>
        ) : location.state?.mode === 'edit' ? (
          <>
            <button className="btn-secondary" onClick={handleNavigate}>Back</button>
            <button className="btn-primary" onClick={handleUpdate}>Update</button>
          </>
        ) : (
          <>
            <button className="btn-secondary" onClick={handleNavigate}>Back</button>
            <button className="btn-primary" onClick={handleAdd}>Save</button>
          </>
        )}
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

export default AddBranchType;