import React, { useEffect, useState } from 'react'
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { designationService } from '../../../services/designationService';
import InputField from '../../Homepage/InputField';
import Button from '../../Re-usable-components/Button';
import Swal from '../../Re-usable-components/Swal';
import '../DesignationMaster/AddViewEditDesignation.css';

const AddViewEditDesignation = () => {
  const [designationName, setDesignationName] = useState('');
  const [viewMode, setViewMode] = useState(false);
  const [popupConfig, setPopupConfig] = useState(null);
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();


  useEffect(() => {
      const designationData = location.state?.designationData;
      const mode = location.state?.mode;
  
      if (designationData) {
        setDesignationName(designationData.designationName);
      }
  
      if (mode === 'view') {
        setViewMode(()=>true);
      } else if (mode === 'edit') {
        setViewMode(false);
      }
    }, [location.state]);

  const handleNavigate = () => {
    navigate('/masters/designation');
  }

  const handleAdd = async () => {
      if(!validateInput()) return;
       try {
        const createdBy = "3fa85f64-5717-4562-b3fc-2c963f66afa7"; // Replace with the actual createdBy value
            const response = await designationService.addDesignation({ designationName: designationName }, createdBy);
            setPopupConfig({
              icon: 'success',
              title: 'Added Successfully',
              text: '',
              onClose: () => navigate('/masters/designation'),
            });
          } catch (error) {
            console.error(error);
            setPopupConfig({
              icon: 'delete',
              title: 'Failed to add designation.',
              text: 'Please try again.',
              onButtonClick: () => setPopupConfig(null),
              onClose: () => setPopupConfig(null),
            });
          }
  }

  const handleUpdate = async () => {
      if(!validateInput()) return;
       try {
            const modifiedBy = "3fa85f64-5717-4562-b3fc-2c963f66afa7";
            const response = await designationService.updateDesignationById(id, { designationName: designationName }, modifiedBy);
            if (response.statusCode === "200 OK") {
              setPopupConfig({
                icon: 'success',
                title: 'Updated Successfully!',
                text: '',
                onClose: () => navigate('/masters/designation'),
              });
            } else {
              setPopupConfig({
                icon: 'delete',
                title: 'Failed to update designation.',
                text: 'Please try again.',
                onClose: () => navigate('/masters/designation'),
              });
            }
          } catch (error) {
      
  }}

  const validateInput = () => {
      const trimmedName = designationName.trim();
      if (trimmedName === '') {
      setPopupConfig({
          icon: 'delete',
          title: 'Validation Error',
          text: 'Designation name is required and cannot be empty or just spaces.',
          onClose: () => setPopupConfig(null),
      });
      return false;
      }
      return true;
  };
  const handleChange = (e) => {
    setDesignationName(e.target.value);
}

  return (
    <div className='designation'>
    <div className='designation-heading'>
        <span className='designation-title'>{viewMode
          ? 'View Designation'
          : location.state?.mode === 'edit'
          ? 'Edit Designation'
          : 'Add Designation'}</span>
    </div>

    <div className='designation-container'>
        <div className='designation-input'>
            <InputField
                label="Designation Name"
                type="text"
                name="designationName"
                className="input-field-designation"
                value={designationName}
                onChange={handleChange}
                required
                placeholder="Enter Designation name"
                disabled={viewMode}
            />
        </div>
    </div>

    <div className='button-container'>
    {viewMode ? (
    <div className="button-div">
      <Button text="Back" onClick={handleNavigate} />
    </div>
  ) : location.state?.mode === 'edit' ? (
    <div className="button-div">
      <Button text="Back" onClick={handleNavigate} />
      <Button text="Update" onClick={handleUpdate} />
    </div>
  ) : (
    <div className="button-div">
      <Button text="Back" onClick={handleNavigate} />
      <Button text="Save" onClick={handleAdd} />
    </div>
  )}
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
    );
}

export default AddViewEditDesignation