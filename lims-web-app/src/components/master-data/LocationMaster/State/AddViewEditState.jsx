import React, { useEffect, useState } from 'react'
import Swal from '../../../Re-usable-components/Swal';
import Button from '../../../Re-usable-components/Button';
import InputField from '../../../Homepage/InputField';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { locationMasterService } from '../../../../services/locationMasterService';
import '../State/AddViewEditState.css'

const AddViewEditState = () => {
  const [stateData, setStateData] = useState({
    countryCode: '',
    countryName: '',
    stateName: '',
    stateCode: ''
});
const [viewMode, setViewMode] = useState(false);
const [popupConfig, setPopupConfig] = useState(null);
const { id } = useParams();
const location = useLocation();
const navigate = useNavigate();

useEffect(() => {
    const stateDetails = location.state?.stateDetails;
    const mode = location.state?.mode;

    if (stateDetails) {
        const data = {
            stateName: stateDetails.stateName,
            stateCode: stateDetails.stateCode,
            countryCode: stateDetails.countryCode,
            countryName: stateDetails.countryName,
        };

        setStateData((prev) => {
            return { ...prev, ...data };
        });
    }
    
    if (mode === 'view') {
        setViewMode(true);
    }
}, [location.state]);

const validateInput = () => {
    if (
      stateData.stateName.trim() === '' || 
      stateData.stateCode.trim() === '' ||
      stateData.countryName.trim() === '' || 
      stateData.countryCode.trim() === ''
    )  {
    setPopupConfig({
        icon: 'delete',
        title: 'Validation Error',
        text: 'All fields are required and cannot be empty or just spaces.',
        onClose: () => setPopupConfig(null),
    });
    return false;
    }
    return true;
};


const handleChange = (e) => {
    setStateData((prev) => ({ ...prev, [e.target.name]: e.target.value }));
}

const handleAdd = async () => {
    if (!validateInput()) return;

    try {
        const createdBy = "3fa85f64-5717-4562-b3fc-2c963f66afa7"; // Replace with the actual createdBy value
        // const labId = "5b680e6a-e0ea-4a0e-af86-1219694d3c60"; // Replace with the actual labId value

        const response = await locationMasterService.saveState(
            {
              stateName: stateData.stateName,
              stateCode: stateData.stateCode,
              countryCode: stateData.countryCode,
              countryName: stateData.countryName,
            },
            createdBy
        );

        setPopupConfig({
            icon: 'success',
            title: 'Added Successfully',
            text: '',
            onClose: () => navigate('/masters/location/state'),
          });
        } catch (error) {
          console.error(error);
          setPopupConfig({
            icon: 'delete',
            title: 'Failed to add state.',
            text: 'Please try again.',
            onButtonClick: () => setPopupConfig(null),
            onClose: () => setPopupConfig(null),
          });
        }
};

const handleNavigate = () => {
    navigate('/masters/location/state');
  }

  const handleUpdate = async () => {
    if(!validateInput()) return;
     try {
        // const id = "3fa85f64-5717-4562-b3fc-2c963f66afa7"; // Replace with the actual createdBy value
          const modifiedBy = "5b680e6a-e0ea-4a0e-af86-1219694d3c60"; // Just for the referene will remove later
          const response = await locationMasterService.updateStateById(id, stateData, modifiedBy);
          if (response.statusCode === "200 OK") {
            setPopupConfig({
              icon: 'success',
              title: 'Updated Successfully!',
              text: '',
              onClose: () => navigate('/masters/location/state'),
            });
          } else {
            setPopupConfig({
              icon: 'delete',
              title: 'Failed to update state.',
              text: 'Please try again.',
              onClose: () => navigate('/masters/location/state'),
            });
          }
        } catch (error) {
    
}}

return (
    <div className='state'>
        <div className='state-heading'>
            <span className='state-title'>{viewMode
              ? 'View State'
              : location.state?.mode === 'edit'
              ? 'Edit State'
              : 'Add State'}</span>
        </div>

        <div className='state-container'>
            <div className='state-input'>
                <InputField
                    label="State Name"
                    type="text"
                    name="stateName"
                    className="input-field-state"
                    value={stateData.stateName}
                    onChange={handleChange}
                    required
                    placeholder="Enter State name"
                    disabled={viewMode}
                    readonly={viewMode}
                />
            </div>

            <div className='state-input'>
                <InputField
                    label="State Code"
                    type="text"
                    name="stateCode"
                    className="input-field-state"
                    value={stateData.stateCode}
                    onChange={handleChange}
                    required
                    placeholder="Enter State Code"
                    disabled={viewMode}
                    readonly={viewMode}
                />
            </div>

            <div className='state-input'>
                <InputField
                    label="Country Name"
                    type="text"
                    name="countryName"
                    className="input-field-state"
                    value={stateData.countryName}
                    onChange={handleChange}
                    required
                    placeholder="Enter Country Name"
                    disabled={viewMode}
                    readonly={viewMode}
                />
            </div>

            <div className='state-input'>
                <InputField
                    label="Country Code"
                    type="text"
                    name="countryCode"
                    className="input-field-state"
                    value={stateData.countryCode}
                    onChange={handleChange}
                    required
                    placeholder="Enter Country Code"
                    disabled={viewMode}
                    readonly={viewMode}
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

export default AddViewEditState