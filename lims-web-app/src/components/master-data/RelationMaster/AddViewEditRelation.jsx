import React, { useEffect, useState } from 'react'
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import Button from '../../Re-usable-components/Button';
import InputField from '../../Homepage/InputField';
import Swal from '../../Re-usable-components/Swal';
import { relationMasterService } from '../../../services/relationMasterService';
import '../RelationMaster/AddViewEditRelation.css'

const AddViewEditRelation = () => {
    const [relationName, setRelationName] = useState('');
    const [viewMode, setViewMode] = useState(false);
    const [popupConfig, setPopupConfig] = useState(null);
    const { id } = useParams();
    const navigate = useNavigate();
    const location = useLocation();


    useEffect(() => {
        const relationData = location.state?.relationData;
        const mode = location.state?.mode;
    
        if (relationData) {
            setRelationName(relationData.relationName);
        }
    
        if (mode === 'view') {
          setViewMode(true);
        } else if (mode === 'edit') {
          setViewMode(false);
        }
      }, [location.state]);

    const handleNavigate = () => {
      navigate('/masters/relation');
    }

    const handleAdd = async () => {
      const createdBy = "3fa85f64-5717-4562-b3fc-2c963f66afa7";
        if(!validateInput()) return;
         try {
              const response = await relationMasterService.addRelation({ relationName: relationName }, createdBy);
              setPopupConfig({
                icon: 'success',
                title: 'Added Successfully',
                text: '',
                onClose: () => navigate('/masters/relation'),
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
    }

    const handleUpdate = async () => {
        if(!validateInput()) return;
         try {
              const modifiedBy = "3fa85f64-5717-4562-b3fc-2c963f66afa7";
              const response = await relationMasterService.updateRelationById(id, { relationName: relationName }, modifiedBy);
              if (response.statusCode === "200 OK") {
                setPopupConfig({
                  icon: 'success',
                  title: 'Updated Successfully!',
                  text: '',
                  onClose: () => navigate('/masters/relation'),
                });
              } else {
                setPopupConfig({
                  icon: 'delete',
                  title: 'Failed to update relation.',
                  text: 'Please try again.',
                  onClose: () => navigate('/masters/relation'),
                });
              }
            } catch (error) {
        
    }}

    const validateInput = () => {
        const trimmedName = relationName.trim();
        if (trimmedName === '') {
        setPopupConfig({
            icon: 'delete',
            title: 'Validation Error',
            text: 'Relation name is required and cannot be empty or just spaces.',
            onClose: () => setPopupConfig(null),
        });
        return false;
        }
        return true;
    };

    const handleChange = (e) => {
      setRelationName(e.target.value);
  }

    return (
      <div className='relation'>
      <div className='relation-heading'>
          <span className='relation-title'>{viewMode
            ? 'View Relation Master'
            : location.state?.mode === 'edit'
            ? 'Edit Relation Master'
            : 'Add Relation Master'}</span>
      </div>
  
      <div className='relation-container'>
          <div className='relation-input'>
              <InputField
                  label="Relation Name"
                  type="text"
                  name="relationName"
                  className="input-field-relation"
                  value={relationName}
                  onChange={handleChange}
                  required
                  placeholder="Enter Relation name"
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

export default AddViewEditRelation