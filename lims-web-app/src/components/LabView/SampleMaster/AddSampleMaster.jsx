import React, { useEffect, useState } from 'react';
import InputField from '../../Homepage/InputField';
import { useLocation, useNavigate } from 'react-router-dom';
import "./SampleMaster.css";
import { saveSampleMaster, updateSample, getSamplesNames } from '../../../services/LabViewServices/SampleMasterService';
import Swal from '../../Re-usable-components/Swal';
import Button from '../../Re-usable-components/Button';
import Error from '../../Re-usable-components/Error';

const AddSampleMaster = () => {
    const [sampleName, setSampleName] = useState('');
    const [sampleTypes, setSampleTypes] = useState(['']);
    const [viewMode, setViewMode] = useState(false);
    const [isEditMode, setIsEditMode] = useState(false);
    const [popupConfig, setPopupConfig] = useState(null);
    const [errors, setErrors] = useState({ sampleName: '', sampleTypes: [] });

    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        const sampleData = location.state?.sampleData;
        const mode = location.state?.mode;

        if (sampleData) {
            setSampleName(sampleData.data.sampleName || '');
            setSampleTypes(sampleData.data.sampleType.length > 0 ? sampleData.data.sampleType : ['']);
        }

        if (mode === 'view') {
            setViewMode(true);
        } else if (mode === 'edit') {
            setIsEditMode(true);
        }
    }, [location.state]);

    const handleSampleTypeChange = (index, value) => {
        const updatedSampleTypes = [...sampleTypes];
        updatedSampleTypes[index] = value;
        setSampleTypes(updatedSampleTypes);

        const updatedErrors = { ...errors };

        if (index === 0) {
            updatedErrors.sampleTypes[0] = value.trim() === '' ? 'Sample Type cannot be empty' : '';
        } else {
            updatedErrors.sampleTypes[index] = '';
        }

        setErrors(updatedErrors);
    };

    const addSampleTypeField = () => {
        setSampleTypes([...sampleTypes, '']);
        setErrors({ ...errors, sampleTypes: [...errors.sampleTypes, ''] });
    };

    const validateInput = () => {
        let newErrors = { sampleName: '', sampleTypes: [] };
        let isValid = true;

        if (sampleName.trim() === '') {
            newErrors.sampleName = 'Sample Name is required';
            isValid = false;
        }

        if (sampleTypes[0].trim() === '') {
            newErrors.sampleTypes[0] = 'Sample Type is required';
            isValid = false;
        }

        setErrors(newErrors);
        return isValid;
    };

    const handleNavigate = () => {
        navigate('/lab-view/sampleMaster');
    };

    const checkSampleNameExists = async () => {
        try {
            const existingSamples = await getSamplesNames(sampleName);

            if (isEditMode) {
                return false;
            } else {
                return existingSamples && existingSamples.length > 0;
            }
        } catch (error) {
            console.error('Error checking sample name:', error);
            return false;
        }
    };

    const handleSaveOrUpdate = async () => {
        if (!validateInput()) return;

        const sampleNameExists = await checkSampleNameExists();

        if (sampleNameExists) {
            setErrors((prevErrors) => ({
                ...prevErrors,
                sampleName: 'Sample Name already exists. Please choose a different name.',
            }));
            return;
        }

        try {
            const labId = "b090a206-aa2b-4b6f-8581-123de3a75996";
            const sampleData = {
                sampleName,
                sampleType: sampleTypes.filter(type => type.trim() !== ''),
                labId,
            };

            if (isEditMode) {
                await updateSample(location.state?.sampleData?.data?.id, sampleData);
                setPopupConfig({
                    icon: 'success',
                    title: 'Updated Successfully',
                    onClose: () => navigate('/lab-view/sampleMaster'),
                });
            } else {
                const userId = "8f8bd09f-084e-4f73-b878-bbc1cf1a37b2";
                await saveSampleMaster(sampleData, userId);
                setPopupConfig({
                    icon: 'success',
                    title: 'Added Successfully',
                    onClose: () => navigate('/lab-view/sampleMaster'),
                });
            }
        } catch (error) {
            console.error("Error adding/updating sample:", error);
            setPopupConfig({
                icon: 'error',
                title: 'Error',
                text: 'There was an error while saving/updating the sample.',
            });
        }
    };

    return (
        <div className='sample'>
            <div className='add-sample-parent'>
                <span className='add-sample'>
                    {viewMode
                        ? 'View Sample Master'
                        : location.state?.mode === 'edit'
                            ? 'Edit Sample Master'
                            : 'Add Sample Master'}
                </span>
            </div>
            <div className='parent-containerr'>
                <div className='sample-container'>
                    <div className='sample-input'>
                        <InputField
                            label="Sample Name"
                            type="text"
                            placeholder="Sample Name"
                            value={sampleName}
                            onChange={(e) => !viewMode && setSampleName(e.target.value)}
                            readOnly={viewMode}
                            required
                        />
                        {errors.sampleName && <div className="error-message">{errors.sampleName}</div>}
                    </div>

                    {sampleTypes.map((type, index) => (
                        <div className='sample-input' key={index}>
                            <InputField
                                label="Sample Type"
                                type="text"
                                placeholder="Sample Type"
                                value={type}
                                onChange={(e) => !viewMode && handleSampleTypeChange(index, e.target.value)}
                                readOnly={viewMode}
                                required
                            />
                            {errors.sampleTypes[index] && <div className="error-message">{errors.sampleTypes[index]}</div>}
                        </div>
                    ))}
                </div>
                {!viewMode && (
                    <div className="add-btn-container">
                        <button className="add-btn" onClick={addSampleTypeField}>Add Another Sample Type</button>
                    </div>
                )}
            </div>

            <div className="button-div">
                <Button text="Back" onClick={handleNavigate} />
                {!viewMode && <Button text={isEditMode ? "Update" : "Save"} onClick={handleSaveOrUpdate} />}
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

export default AddSampleMaster;