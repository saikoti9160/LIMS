import React, { useEffect, useState } from 'react'
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import InputField from '../../../Homepage/InputField';
import { locationMasterService } from '../../../../services/locationMasterService';
import '../Country/AddViewEditCountry.css'
import Button from '../../../Re-usable-components/Button';
import Swal from '../../../Re-usable-components/Swal';
const AddViewEditCountry = () => {
    const [countryData, setCountryData] = useState({
      countryCode: '',
      countryName: '',
      continentName: '',
      continentCode: '',
      phoneCode:'',
      currencySymbol: '',
      currency: ''
  });
    const [viewMode, setViewMode] = useState(false);
    const [popupConfig, setPopupConfig] = useState(null);
    const { id } = useParams();
    const location = useLocation();
    const navigate = useNavigate();
 
    useEffect(() => {
        const countryDetails = location.state?.countryDetails;
        const mode = location.state?.mode;
 
        if (countryDetails) {
            console.log(countryDetails, "sdhafkjhasd ");
            const data = {
                countryCode: countryDetails.countryCode,
                countryName: countryDetails.countryName,
                continentName: countryDetails.continentName,
                continentCode: countryDetails.continentCode,
                phoneCode: countryDetails.phoneCode,
                currencySymbol: countryDetails.currencySymbol,
                currency: countryDetails.currency
            };
            setCountryData((prev) => {
                return { ...prev, ...data };
            });
        }
        
        if (mode === 'view') {
            setViewMode(true);
        } else if (mode === 'edit') {
            setViewMode(false);
        }
    }, [location.state]);

    const validateInput = () => {
        if (
            countryData.countryName.trim() === '' || 
            countryData.countryCode.trim() === '' || 
            countryData.continentName.trim() === '' || 
            countryData.continentCode.trim() === '' || 
            countryData.phoneCode.trim() === '' || 
            countryData.currencySymbol.trim() === '' || 
            countryData.currency.trim() === ''
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
      setCountryData((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    }
 
    const handleAdd = async () => {
        if (!validateInput()) return;
 
        try {
            const createdBy = "3fa85f64-5717-4562-b3fc-2c963f66afa7"; // Replace with the actual createdBy value
            // const labId = "5b680e6a-e0ea-4a0e-af86-1219694d3c60"; // Replace with the actual labId value
 
            const response = await locationMasterService.saveCountry(
                {
                    countryName: countryData.countryName,
                    countryCode: countryData.countryCode,
                    continentName: countryData.continentName,
                    continentCode: countryData.continentCode,
                    phoneCode: countryData.phoneCode,
                    currencySymbol: countryData.currencySymbol,
                    currency: countryData.currency,
                },
                createdBy
            );
 
            setPopupConfig({
                icon: 'success',
                title: 'Added Successfully',
                text: '',
                onClose: () => navigate('/masters/location/country'),
              });
            } catch (error) {
              console.error(error);
              setPopupConfig({
                icon: 'delete',
                title: 'Failed to add relation.',
                text: 'Please try again.',
                onButtonClick: () => setPopupConfig(null),
                onClose: () => setPopupConfig(null),
              });
            }
    };

    const handleNavigate = () => {
        navigate('/masters/location/country');
      }

      const handleUpdate = async () => {
        if(!validateInput()) return;
         try {
            // const id = "3fa85f64-5717-4562-b3fc-2c963f66afa7"; // Replace with the actual createdBy value
              const modifiedBy = "5b680e6a-e0ea-4a0e-af86-1219694d3c60";
              const response = await locationMasterService.updateCountryById(id, countryData, modifiedBy);
              if (response.statusCode === "200 OK") {
                setPopupConfig({
                  icon: 'success',
                  title: 'Updated Successfully!',
                  text: '',
                  onClose: () => navigate('/masters/location/country'),
                });
              } else {
                setPopupConfig({
                  icon: 'delete',
                  title: 'Failed to update relation.',
                  text: 'Please try again.',
                  onClose: () => navigate('/masters/location/country'),
                });
              }
            } catch (error) {
        
    }}
 
    return (
        <div className='country'>
            <div className='country-heading'>
                <span className='country-title'>{viewMode
                  ? 'View Country'
                  : location.state?.mode === 'edit'
                  ? 'Edit Country'
                  : 'Add Country'}</span>
            </div>
 
            <div className='country-container'>
                <div className='country-input'>
                    <InputField
                        label="Country Name"
                        type="text"
                        name="countryName"
                        className="input-field-country"
                        value={countryData.countryName}
                        onChange={handleChange}
                        required
                        placeholder="Enter Country name"
                        disabled={viewMode}
                        readonly={viewMode}
                    />
                </div>
 
                <div className='country-input'>
                    <InputField
                        label="Continent Code"
                        type="text"
                        name="continentCode"
                        className="input-field-country"
                        value={countryData.continentCode}
                        onChange={handleChange}
                        required
                        placeholder="Enter Continent Code"
                        disabled={viewMode}
                        readonly={viewMode}
                    />
                </div>

                <div className='country-input'>
                    <InputField
                        label="Continent Name"
                        type="text"
                        name="continentName"
                        className="input-field-country"
                        value={countryData.continentName}
                        onChange={handleChange}
                        required
                        placeholder="Enter Continent Name"
                        disabled={viewMode}
                        readonly={viewMode}
                    />
                </div>

                <div className='country-input'>
                    <InputField
                        label="Country Code"
                        type="text"
                        name="countryCode"
                        className="input-field-country"
                        value={countryData.countryCode}
                        onChange={handleChange}
                        required
                        placeholder="Enter Country Code"
                        disabled={viewMode}
                        readonly={viewMode}
                    />
                </div>

                <div className='country-input'>
                    <InputField
                        label="Phone Code"
                        type="text"
                        name="phoneCode"
                        className="input-field-country"
                        value={countryData.phoneCode}
                        onChange={handleChange}
                        required
                        placeholder="Enter Phone Code"
                        disabled={viewMode}
                        readonly={viewMode}
                    />
                </div>

                <div className='country-input'>
                    <InputField
                        label="Currency Symbol"
                        type="text"
                        name="currencySymbol"
                        className="input-field-country"
                        value={countryData.currencySymbol}
                        onChange={handleChange}
                        required
                        placeholder="Enter Currency Symbol"
                        disabled={viewMode}
                        readonly={viewMode}
                    />
                </div>

                <div className='country-input'>
                    <InputField
                        label="Currency"
                        type="text"
                        name="currency"
                        className="input-field-country"
                        value={countryData.currency}
                        onChange={handleChange}
                        required
                        placeholder="Enter Currency"
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

export default AddViewEditCountry