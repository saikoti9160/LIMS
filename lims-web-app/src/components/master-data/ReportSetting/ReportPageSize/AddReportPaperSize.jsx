
import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import InputField from '../../../Homepage/InputField';
import Swal from '../../../Re-usable-components/Swal';
import Error from '../../../Re-usable-components/Error';
import "../ReportSettings.css";
import { addReportPaperSize,  updateReportPaperSize } from '../../../../services/MasterDataService/ReportSettingsMaster/ReportPaperSizeService';

const AddReportPaperSize = () => {
  const [paperSize, setPaperSize] = useState('');
  const [id, setId] = useState(null); 
  const [viewMode, setViewMode] = useState(false);
  const [popupConfig, setPopupConfig] = useState(null);
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const location = useLocation();
  const {previousSearch } = location.state || {};
  const [userId] = useState("3fa85f64-5717-4562-b3fc-2c963f66afa6");

  useEffect(() => {
    const paperSizeDetails = location.state?.paperSizeDetails;
    
    const mode = location.state?.mode;
    if (paperSizeDetails) {
      setPaperSize(paperSizeDetails.paperSize);
      setId(paperSizeDetails.id);
    }
    if (mode === 'view') {
      setViewMode(true);
    } else if (mode === 'edit') {
      setViewMode(false);
    }
  }, [location.state]);

  const handleInputChange = (event) => {
    if (!viewMode) {
      setPaperSize(event.target.value);
      setError(null);
    }
  };

  const validateInput = () => {
    if (!paperSize.trim()) {
      setError({ message: 'Paper Size is required.', type: 'error' });
      return false;
    }
    return true;
  };
  const navigateBack = () => {
    navigate('/masters/report-settings/paper-size', {
      state: { previousSearch }
    });
  };

  const handleSave = async () => {
    if (!validateInput()) return;
    try {
      let response;
      const reportPaperSizeData = { paperSize: paperSize.trim() };

      if (id) {

        response = await updateReportPaperSize(id, reportPaperSizeData, userId);
      } else {

        response = await addReportPaperSize(reportPaperSizeData, userId);
      }
      setPopupConfig({
        icon: 'success',
        title: id ? 'Updated Successfully' : 'Added Successfully',
        onClose: () => navigate('/masters/report-settings/paper-size'),  
      });
    } catch (error) {
      setError({ message: 'Operation failed. Please try again.', type: 'error' });
    }
  };

  return (
    <div className="profile-container">
      <div>
          <h2 className="title">
            {viewMode ? 'View Paper Size' : id ? 'Edit Paper Size' : 'Add Paper Size'}
          </h2>
        <div className="report-setting-inner-container">
          <div className='report-setting-sub-container'>
            <InputField
              label="Paper Size"
              type="text"
              placeholder={viewMode ? 'Paper size' : 'Enter Here'}
              className="input-field-content"
              required
              value={paperSize}
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

export default AddReportPaperSize
