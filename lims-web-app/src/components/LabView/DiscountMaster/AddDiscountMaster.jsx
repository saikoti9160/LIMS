import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import InputField from '../../Homepage/InputField';
import Swal from '../../Re-usable-components/Swal';
import { addDiscount, updateDiscount } from '../../../services/LabViewServices/DiscountMasterService';
import './DiscountMaster.css';
const AddDiscountMaster = () => {
    const [discountName, setDiscountName] = useState('');
    const [discountType, setDiscountType] = useState('');
    const [viewMode, setViewMode] = useState(false);
    const [isEditMode, setIsEditMode] = useState(false);
    const [popupConfig, setPopupConfig] = useState(null);

    const{id} = useParams();
    const location = useLocation();
    const navigate = useNavigate();

    useEffect(() => {
        const discountDetails = location.state?.discountDetails;
        const mode = location.state?.mode;

        if (discountDetails) {
            setDiscountName(discountDetails.discountName);
            setDiscountType(discountDetails.discountType);
        }

        if (mode === 'view') {
            setViewMode(true);
            console.log(discountDetails)
            setDiscountName(discountDetails.data.discountName);
            setDiscountType(discountDetails.data.discountType);
        } else if (mode === 'edit') {
            setIsEditMode(true);
            console.log('edit', discountDetails)
            setDiscountName(discountDetails.data.discountName);
            setDiscountType(discountDetails.data.discountType);
            
        }
    }, [location.state]);

    const validateInput = () => {
        if (discountName.trim() === '' || discountType.trim() === '') {
            setPopupConfig({
                icon: 'delete',
                title: 'Validation Error',
                text: 'All fields are required.',
                onClose: () => setPopupConfig(null),
            });
            return false;
        }
        return true;
    };

    const handleAdd = async () => {
        if (!validateInput()) return;
    
        try {
            const createdBy = "3fa85f64-5717-4562-b3fc-2c963f66af11"; 
            const labId = "20cd288a-d7ed-4dde-9b1a-a520dd11e9c8";
    
            const discountData = {
                discountName,
                discountType,
                labId,
                active: true
            };
    
            const headers = {
                'Content-Type': 'application/json',
                'createdBy': createdBy, 
            };
    
            if (isEditMode) {
                const discountId = location.state?.discountDetails?.id;
                await updateDiscount(discountData, id);
                setPopupConfig({
                    icon: 'success',
                    title: 'Updated Successfully',
                    onClose: () => navigate('/lab-view/discount'),
                });
            } else {
                await addDiscount(discountData, headers);
                setPopupConfig({
                    icon: 'success',
                    title: 'Added Successfully',
                    onClose: () => navigate('/lab-view/discount'),
                });
            }
        } catch (error) {
            console.error(error);
            setPopupConfig({
                icon: 'delete',
                title: 'Failed to save discount.',
                text: 'Please try again.',
                onClose: () => setPopupConfig(null),
            });
        }
    };
    

    return (
        <div className='discount'>
            <div className='discount-heading'>
                <span className='discount-title'>
                    {isEditMode ? 'Edit Discount Master' : viewMode ? 'View Discount Master' : 'Add Discount Master'}
                </span>
            </div>

            <div className='discount-container'>
                <div className='discount-input'>
                    <InputField
                        label="Discount Name"
                        type="text"
                        className="input-field-dis"
                        value={discountName}
                        onChange={(e) => setDiscountName(e.target.value)}
                        required
                        placeholder="Enter name"
                        disabled={viewMode}
                    />
                </div>

                <div className='discount-input'>
                    <InputField
                        label="Discount Type"
                        type="text"
                        className="input-field-dis"
                        value={discountType}
                        onChange={(e) => setDiscountType(e.target.value)}
                        required
                        placeholder="Enter type"
                        disabled={viewMode}
                    />
                </div>
            </div>

            <div className='button-container-discount'>
                <button className='btn-cancel' onClick={() => navigate(-1)}>Back</button>
                {!viewMode && (
                    <button className='btn-add' onClick={handleAdd}>
                        {isEditMode ? 'Update' : 'Save'}
                    </button>
                )}
            </div>

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
};

export default AddDiscountMaster;