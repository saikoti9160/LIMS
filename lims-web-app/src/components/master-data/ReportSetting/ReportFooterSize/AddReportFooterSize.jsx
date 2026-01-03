
import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import InputField from '../../../Homepage/InputField';
import Swal from '../../../Re-usable-components/Swal';
import Error from '../../../Re-usable-components/Error';
import "../ReportSettings.css";
import { addReportFooterSize, updateReportFooterSize } from '../../../../services/MasterDataService/ReportSettingsMaster/ReportFooterSizeService';

const AddReportFooterSize = () => {
  const [footerSize, setFooterSize] = useState('');
  const [id, setId] = useState(null); 
  const [viewMode, setViewMode] = useState(false);
  const [popupConfig, setPopupConfig] = useState(null);
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const location = useLocation();
  const {previousSearch } = location.state || {};
  const [userId] = useState("3fa85f64-5717-4562-b3fc-2c963f66afa6");

  useEffect(() => {
    const footerSizeDetails = location.state?.footerSizeDetails;
    const mode = location.state?.mode;
    if (footerSizeDetails) {
      setFooterSize(footerSizeDetails.footerSize);
      setId(footerSizeDetails.id);
    }
    if (mode === 'view') {
      setViewMode(true);
    } else if (mode === 'edit') {
      setViewMode(false);
    }
  }, [location.state]);

  const handleInputChange = (event) => {
    if (!viewMode) {
      setFooterSize(event.target.value);
      setError(null);
    }
  };

  const validateInput = () => {
    if (!footerSize.trim()) {
      setError({ message: 'Footer Size is required.', type: 'error' });
      return false;
    }
    return true;
  };
  const navigateBack = () => {
    navigate('/masters/report-settings/footer-size', {
      state: { previousSearch }
    });
  };

  const handleSave = async () => {
    if (!validateInput()) return;
    try {
      let response;
      const reportFooterSizeData = { footerSize: footerSize.trim() };

      if (id) {

        response = await updateReportFooterSize(id, reportFooterSizeData, userId);
      } else {

        response = await addReportFooterSize(reportFooterSizeData, userId);
      }

      console.log('API Response:', response);
      setPopupConfig({
        icon: 'success',
        title: id ? 'Updated Successfully' : 'Added Successfully',
        onClose: () => navigate('/masters/report-settings/footer-size', {
          state: { previousSearch }
        }),
      });
    } catch (error) {
      setError({ message: 'Operation failed. Please try again.', type: 'error' });
    }
  };

  return (
    <div className="profile-container">
      <div>
          <h2 className="title">
            {viewMode ? 'View Footer Size' : id ? 'Edit Footer Size' : 'Add Footer Size'}
          </h2>
        <div className="report-setting-inner-container">
          <div className='report-setting-sub-container'>
            <InputField
              label="Footer Size"
              type="text"
              placeholder={viewMode ? 'Footer Size' : 'Enter Footer Size'}
              className="input-field-content"
              required
              value={footerSize}
              onChange={handleInputChange}
              readOnly={viewMode}
            />
            {error && <Error message={error.message} type={error.type} />}
          </div>
        </div>
      </div>
      <div className="report-setting-button-div-container">
        <button className="btn-secondary" onClick={navigateBack}>Back</button>
        {!viewMode && <button className="btn-primary" onClick={handleSave}>Save</button>}
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

export default AddReportFooterSize
