import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import InputField from '../../../Homepage/InputField';
import Swal from '../../../Re-usable-components/Swal';
import Error from '../../../Re-usable-components/Error';
import "../ReportSettings.css";
import { saveReportDateFormat, updateReportDateFormat } from '../../../../services/MasterDataService/ReportSettingsMaster/ReportDateFormatService';

const AddEditViewReportDateFormat = () => {
  const [dateFormat, setDateFormat] = useState('');
  const [id, setId] = useState(null);
  const [viewMode, setViewMode] = useState(false);
  const [popupConfig, setPopupConfig] = useState(null);
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const location = useLocation();
  const [userId] = useState("3fa85f64-5717-4562-b3fc-2c963f66afa6");

  useEffect(() => {
    const dateFormatDetails = location.state?.dateFormatDetails;
    const mode = location.state?.mode;
    if (dateFormatDetails) {
      setDateFormat(dateFormatDetails.dateFormat);
      setId(dateFormatDetails.id);
    }
    if (mode === 'view') {
      setViewMode(true);
    } else if (mode === 'edit') {
      setViewMode(false);
    }
  }, [location.state]);

  const handleInputChange = (event) => {
    if (!viewMode) {
      setDateFormat(event.target.value);
      setError(null);
    }
  };

 
  const handleBack = () => {
    navigate('/masters/report-settings/date-format', {
      state: { searchQuery: location.state?.searchQuery || '' }, // Preserve search
    });
  };
  
  const validateInput = () => {
    if (!dateFormat.trim()) {
      setError({ message: 'Date Format is required.', type: 'error' });
      return false;
    }
    return true;
  };

  const handleSave = async () => {
    if (!validateInput()) return;
    try {
      let response;
      const reportDateFormatData = { dateFormat : dateFormat.trim() };
        console.log("reportDateFormatData", reportDateFormatData);
        
      if (id) {
        response = await updateReportDateFormat(id, reportDateFormatData, userId);
      } else {
        response = await saveReportDateFormat(reportDateFormatData, userId);
      }

      console.log('API Response:', response);
      setPopupConfig({
        icon: 'success',
        title: id ? 'Updated Successfully' : 'Added Successfully',
        onClose: () =>  navigate('/masters/report-settings/date-format', {
          state: { searchQuery: location.state?.searchQuery || '' },
        }),
      });
    } catch (error) {
      console.error(error.response.data);
      setError({ message: error.response.data.message, type: 'error' });
    }
  };

  return (
    <div className="profile-container">
      <div>
        <h2 className="title">
          {viewMode ? 'View Date Format' : id ? 'Edit Date Format' : 'Add Date Format'}
        </h2>
        <div className="report-setting-inner-container">
          <div className='report-setting-sub-container'>
            <InputField
              label="Date Format"
              type="text"
              placeholder={viewMode ? 'Date format' : 'Enter date format'}
              className="input-field-content"
              required
              value={dateFormat}
              onChange={handleInputChange}
              readOnly={viewMode}
            />
            {error && <Error message={error.message} type={error.type} />}
          </div>
        </div>
      </div>
      <div className="report-setting-button-div-container">
        <button className="btn-secondary" onClick={handleBack} >Back</button>
        {!viewMode && <button className="btn-primary" onClick={handleSave}>{ id ? 'update' : 'save'}</button>}
      </div>
      {popupConfig && (
        <Swal
          icon={popupConfig.icon}
          title={popupConfig.title}
          text={popupConfig.text}
          onClose={popupConfig.onClose}
        />
      )}
    </div>
  );
};

export default AddEditViewReportDateFormat;

