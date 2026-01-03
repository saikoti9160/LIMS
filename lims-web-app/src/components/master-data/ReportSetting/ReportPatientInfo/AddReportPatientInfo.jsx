import React, { useEffect, useState } from 'react';
import InputField from '../../../Homepage/InputField';
import Error from '../../../Re-usable-components/Error';
import Swal from '../../../Re-usable-components/Swal';
import { useLocation, useNavigate } from 'react-router-dom';
import { addReportPatientInfo, updateReportPatientInfo } from '../../../../services/ReportPatientInfoService';

function AddReportPatientInfo() {
  const [patientInfo, setPatientInfo] = useState([{ patientInfoName: '' }]);
  const [id, setId] = useState(null);
  const [viewMode, setViewMode] = useState(false);
  const [popupConfig, setPopupConfig] = useState(null);
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const location = useLocation();
  const createdBy = "3fa85f64-5717-4562-b3fc-2c963f66afa6";

  useEffect(() => {
    const patientInfoDetails = location.state?.patientInfo;
    const mode = location.state?.mode;

    if (patientInfoDetails) {
      setPatientInfo([{ patientInfoName: patientInfoDetails.patientInfoName }]);
      setId(patientInfoDetails.id);
    }

    setViewMode(mode === 'view');
  }, [location.state]);

  const handleInputChange = (event) => {
    setPatientInfo([{ ...patientInfo[0], patientInfoName: event.target.value }]);
    setError(null);
  };

  const validateInput = () => {
    if (!patientInfo[0].patientInfoName.trim()) {
      setError({ message: 'Patient Info Name is required.', type: 'error' });
      return false;
    }
    return true;
  };

  const handleSave = async () => {
    if (!validateInput()) return;

    try {
      let response;
      const reportPatientInfoData = { patientInfoName: patientInfo[0].patientInfoName.trim() };

      if (id) {
        response = await updateReportPatientInfo(id, reportPatientInfoData, createdBy);
      } else {
        response = await addReportPatientInfo(reportPatientInfoData, createdBy);
      }

      setPopupConfig({
        icon: 'success',
        title: id ? 'Updated Successfully' : 'Added Successfully',
        onClose: () => navigate('/masters/report-settings/patientInfo'),
      });
    } catch (error) {
      console.error('API Error:', error);
      setError({ message: 'Operation failed. Please try again.', type: 'error' });
    }
  };

  return (
    <div className="add-report-setting-container">
      <div>
        <div className="add-report-setting-header">
          <span className="add-report-setting-title">
            {viewMode ? 'View Patient Info' : id ? 'Edit Patient Info' : 'Add Patient Info'}
          </span>
        </div>
        <div className="report-setting-inner-container">
          <div className='report-setting-sub-container reportPatientInfoName'>
            <InputField
              label="Patient Info"
              type="text"
              placeholder={viewMode ? 'Patient Info Name' : 'Enter Patient Info Name'}
              className="input-field-content"
              required
              value={patientInfo[0].patientInfoName}
              onChange={handleInputChange}
              readOnly={viewMode}
            />
            {error && <Error message={error.message} type={error.type} />}
          </div>
        </div>
      </div>
      <div className="report-setting-button-div-container">
        <button className="btn-secondary" onClick={() => navigate('/masters/report-settings/patientInfo')}>Back</button>
        {!viewMode && <button className="btn-primary" onClick={handleSave}>{id ? 'Update' : 'Save'}</button>}
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
}

export default AddReportPatientInfo;
