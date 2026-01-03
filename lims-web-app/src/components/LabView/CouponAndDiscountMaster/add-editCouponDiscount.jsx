import React, { useState, useEffect } from "react";
import { useNavigate, useParams, useLocation } from "react-router-dom";
import "./couponDiscountMaster.css";
import InputField from "../../Homepage/InputField";
import DropDown from "../../Re-usable-components/DropDown";
import { couponDiscountService } from "../../../services/LabViewServices/couponDiscountService";
import { getAllDiscounts } from "../../../services/LabViewServices/DiscountMasterService";
import Swal from "../../Re-usable-components/Swal";

const AddEditCouponDiscountMaster = () => {
    const [discountOptions, setDiscountOptions] = useState({
        names: [],
        types: []
      });
    const navigate = useNavigate();
    const { id } = useParams();
    const location = useLocation();
    const mode = location.state?.mode || 'add';
    const [popupConfig, setPopupConfig] = useState(null);
    const [formData, setFormData] = useState({
        startDate: "",
        endDate: "",
        couponName: "",
        couponDescription: "",
        discountName: "",
        discountType: "",
        discountId:"",
        discountAmount: "",
        discountPercentage: "",
        minAmountRequired: "",
        maxAmountRequired: "",
        gender: "",
        // ageRange: "",
        fromAge: "",
        toAge: "",
        visitFrequency: [],
        specificNumber: "",
        others: ""
    });
    const createdBy = "20cd288a-d7ed-4dde-9b1a-a520dd11e111";  // taken as dummy reference need to revive it later



    const isViewing = mode === 'view';
    const isEditing = mode === 'edit';
    const [isLoading, setIsLoading] = useState(false);
    const [rawDiscountData, setRawDiscountData] = useState([]); 

     useEffect(() => {
        const initializeData = async () => {
            await fetchDiscountOptions();
            if (id) {
                await fetchCouponDetails();
            }
        };
        initializeData();
    }, [id]);

    const fetchCouponDetails = async () => {
        
        try {
            const response = await couponDiscountService.getCouponDiscountById(id);
            const couponData = response.data;
            
          
            const discountResponse = await getAllDiscounts(createdBy,'', true, 0, 250,);
            
            const matchingDiscount = discountResponse.data.find(
                discount => discount.id === couponData.discountId
            );

            const visitFrequencies = Array.isArray(response.data.visitFrequencies) 
                ? response.data.visitFrequencies 
                : response.data.visitFrequencies?.split(',') || [];
             const mappedData = {
                startDate: response.data.startDate ? new Date(response.data.startDate).toISOString().split('T')[0] : '',
                endDate: response.data.endDate ? new Date(response.data.endDate).toISOString().split('T')[0] : '',
                couponName: response.data.couponName || '',
                couponDescription: response.data.couponDescription || '',
                discountName: matchingDiscount?.discountName || '',
                discountType: matchingDiscount?.discountType || '',
                discountId:response.data.discountId || '',
                discountAmount: response.data.discountAmount || '',
                discountPercentage: response.data.discountPercentage || '',
                minAmountRequired: response.data.minAmountRequired || '',
                maxAmountRequired: response.data.maxAmountRequired || '',
                gender: response.data.gender || 'Male',
                // ageRange: response.data.ageRange || '',
                fromAge: response.data.fromAge || '25',
                toAge: response.data.toAge || '45',
                visitFrequency: Array.isArray(response.data.visitFrequency) 
                ? response.data.visitFrequency 
                : response.data.visitFrequency?.split(',') || [],
                specificNumber: response.data.specificNumber || '',
                others: response.data.others || '',
                couponSequenceId: response.data.couponSequenceId || ''
            };
            setFormData(mappedData);
        } catch (error) {
            setPopupConfig({
                icon: 'delete',
                title: 'Error',
                text: 'Failed to fetch coupon details',
                onClose: () => {
                    setPopupConfig(null);
                    // navigate('lab-view/coupon-discount_master');
                }
            });
        }
    };
    const fetchDiscountOptions = async () => {
        try {
            setIsLoading(true);
             const response = await getAllDiscounts(createdBy,'', true, 0, 250,);
            if (response && response.data) {
                setRawDiscountData(response.data);
                const uniqueDiscounts = Array.from(new Set(response.data.map(item => item.discountName)))
                    .map(name => ({
                        value: name,
                        discountName: name
                    }));

                setDiscountOptions(prev => ({
                    ...prev,
                    names: uniqueDiscounts,
                    types: []
                }));
            }
        } catch (error) {
            console.error('Error fetching discount options:', error);
        } finally {
            setIsLoading(false);
        }
    };
    const updateDiscountTypes = (selectedDiscountName) => {
        if (!selectedDiscountName) {
            setDiscountOptions(prev => ({
                ...prev,
                types: []
            }));
            return;
        }
    
        const discountNameValue = selectedDiscountName.value || selectedDiscountName;
    
        const filteredTypes = rawDiscountData
            .filter(item => item.discountName === discountNameValue)
            .map(item => ({
                value: item.discountType,
                discountType: item.discountType,
                discountId: item.id
            }));
    
        const uniqueTypes = Array.from(new Set(filteredTypes.map(type => type.value)))
            .map(type => {
                const matchingType = filteredTypes.find(t => t.value === type);
                return {
                value: type,
                discountType: type,
                discountId: matchingType.discountId
                };
            });
    
        setDiscountOptions(prev => ({
            ...prev,
            types: uniqueTypes
        }));
    };
    const handleDiscountNameChange = (e) => {
        const selectedName = e.target.value.discountName;
        setFormData(prev => ({
            ...prev,
            discountName: selectedName,
            discountType: '',
            discountId: '' 
        }));
        updateDiscountTypes(selectedName);
    };

    const handleDiscountTypeChange = (e) => {
        const selectedType = e.target.value;
        const matchingDiscount = rawDiscountData.find(
            item => item.discountName === formData.discountName && 
                   item.discountType === selectedType.discountType
        );
        
        setFormData(prev => ({
            ...prev,
            discountType: selectedType.discountType,
            discountId: matchingDiscount?.id || '' 
        }));
    };
    
    const handleSave = async () => {
         if (!validateForm()) return;

        try {
            const {discountName, discountType, ...payloadData} = formData;
            
            const data = {
                labId: "20cd288a-d7ed-4dde-9b1a-a520dd11e9c8",  // need to replace it with actual labId
                ...payloadData
            };

            const response = isEditing
                ? await couponDiscountService.updateCouponDiscount(id, data)
                : await couponDiscountService.saveCouponDiscount(data, createdBy);

            if (response?.status === 200 || response?.statusCode === '200 OK') {
                setPopupConfig({
                    icon: 'success',
                    title: `${isEditing ? 'Updated' : 'Added'} Successfully`,
                    text: '',
                    onClose: () => {
                        setPopupConfig(null);
                        navigate(-1);
                    }
                });
            }
        } catch (error) {
            setPopupConfig({
                icon: 'error',
                title: 'Error',
                text: `Failed to ${isEditing ? 'update' : 'save'} coupon`,
                onClose: () => setPopupConfig(null)
            });
        }
    };

    const validateForm = () => {
        const requiredFields = [
            { field: 'startDate', label: 'Valid From' },
            { field: 'endDate', label: 'Valid To' },
            { field: 'couponName', label: 'Coupon Name' },
            { field: 'discountName', label: 'Discount Name' },
            { field: 'discountType', label: 'Discount Type' }
        ];
    
        for (const { field, label } of requiredFields) {
            if (!formData[field]?.trim()) {
                setPopupConfig({
                    icon: 'error',
                    title: 'Validation Error',
                    text: `${label} is required`,
                    onClose: () => setPopupConfig(null)
                });
                return false;
            }
        }

        if (new Date(formData.startDate) > new Date(formData.endDate)) {
            setPopupConfig({
                icon: 'error',
                title: 'Validation Error',
                text: 'Valid To date must be after Valid From date',
                onClose: () => setPopupConfig(null)
            });
            return false;
        }
    
        return true;
    }; 
    
    const handleVisitFrequencyChange = (value) => {
        setFormData(prevData => {
            const currentFrequencies = [...prevData.visitFrequency];
            
            if (currentFrequencies.includes(value)) {
                return {
                    ...prevData,
                    visitFrequency: currentFrequencies.filter(freq => freq !== value),
                    ...(value === 'specificNumber' && { specificNumber: '' }),
                    ...(value === 'other' && { others: '' })
                };
            } else {
                return {
                    ...prevData,
                    visitFrequency: [...currentFrequencies, value]
                };
            }
        });
    };
    const handleBack = () => {
        navigate("/lab-view/coupon-discount_master");
    };
    

    return (
        <div className="master-container">
            <div className="container-div">
           
                <h2 className="title-text">
                    {isViewing ? "View Coupon Discount Master" : isEditing ? "Edit Coupon Discount Master" : "Add Coupon Discount Master"}
                </h2>
                <div className="coupon-form-container">
                <div className="form-section">
                    <div className="form-row">
                        <div className="form-group">
                            <label>Valid From <span className="text-danger">*</span></label>
                            <InputField
                                type="date"
                                value={formData.startDate}
                                onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
                                readOnly={isViewing}
                            />
                        </div>
                        <div className="form-group">
                            <label>Valid To <span className="text-danger">*</span></label>
                            <InputField
                                type="date"
                                value={formData.endDate}
                                onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
                                readOnly={isViewing}
                            />
                        </div>
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label>Coupon Name <span className="text-danger">*</span></label>
                            <InputField
                                type="text"
                                placeholder="Enter Here"
                                value={formData.couponName}
                                onChange={(e) => setFormData({ ...formData, couponName: e.target.value })}
                                readOnly={isViewing}
                               
                            />
                        </div>
                        <div className="form-group coupon-textarea">
                            <label>Coupon Description</label>
                            <InputField
                                type="textarea"
                                placeholder="Enter Here"
                                value={formData.couponDescription}
                                onChange={(e) => setFormData({ ...formData, couponDescription: e.target.value })}
                                disabled={isViewing}
                            />
                        </div>
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label>Discount Name <span className="text-danger">*</span></label>
                            <DropDown
                                options={discountOptions.names}
                                placeholder="Select Discount Name"
                                onChange={handleDiscountNameChange}
                                name="discountName"
                                value={formData.discountName}
                                disabled={isViewing}
                                fieldName="discountName"
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label>Discount Type <span className="text-danger">*</span></label>
                            <DropDown
                                options={discountOptions.types}
                                placeholder="Select Discount Type"
                                onChange={handleDiscountTypeChange}
                                name="discountType"
                                value={formData.discountType}
                                disabled={isViewing || !formData.discountName}
                                fieldName="discountType"
                                required
                            />
                        </div>
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label>Discount Amount</label>
                            <InputField
                                type="number"
                                placeholder="Enter Here"
                                value={formData.discountAmount}
                                onChange={(e) => setFormData({ ...formData, discountAmount: e.target.value })}
                                readOnly={isViewing || formData.discountType !== 'INR'}
                            />
                        </div>
                        <div className="form-group">
                            <label>Discount Percentage</label>
                            <InputField
                                type="number"
                                placeholder="Enter Here"
                                value={formData.discountPercentage}
                                onChange={(e) => setFormData({ ...formData, discountPercentage: e.target.value })}
                                readOnly={isViewing || formData.discountType !== 'Percentage'}
                            />
                        </div>
                    </div>

                    <div className="form-row">
                        <div className="form-group">
                            <label>Minimum Amount Required</label>
                            <InputField
                                type="number"
                                placeholder="Enter Here"
                                value={formData.minAmountRequired}
                                onChange={(e) => setFormData({ ...formData, minAmountRequired: e.target.value })}
                                readOnly={isViewing}
                            />
                        </div>
                        <div className="form-group">
                            <label>Maximum Amount Required</label>
                            <InputField
                                type="number"
                                placeholder="Enter Here"
                                value={formData.maxAmountRequired}
                                onChange={(e) => setFormData({ ...formData, maxAmountRequired: e.target.value })}
                                readOnly={isViewing}
                            />
                        </div>
                    </div>
                    </div>
                </div>
                <div className="coupon-form-container">
                    <h3 className="section-title">Conditions</h3>

                    <div className="form-row">
                        <div className="form-group div-conditions">
                        <div className="age-range-div">
                            <label>Age Range</label>
                            <div className="range-div">
                        
                            <InputField
                                type="number"
                                value={formData.fromAge}
                                placeholder={"From Age"}
                                onChange={(e) => setFormData({ ...formData, fromAge: e.target.value })}
                                readOnly={isViewing}
                                />
                       
                            <InputField
                                type="number"
                                value={formData.toAge}
                                placeholder={"To Age"}
                                onChange={(e) => setFormData({ ...formData, toAge: e.target.value })}
                                readOnly={isViewing}
                                />
                           </div> 
                        </div>
                    <div className="visit-frequency">
                        <label>Visit Frequency</label>
                        <div className="checkbox-group">
                          <div className="visit-frequency-checkbox">
                                <InputField
                            
                                    type="checkbox"
                                    checked={formData.visitFrequency.includes('firstVisit')}
                                    onChange={() => handleVisitFrequencyChange('firstVisit')}
                                    disabled={isViewing}
                                />
                                <label>First Visit Only</label>
                        </div>
                        <div className="visit-frequency-checkbox">
                                <InputField
                                    type="checkbox"
                                    checked={formData.visitFrequency.includes('repeatVisit')}
                                    onChange={() => handleVisitFrequencyChange('repeatVisit')}
                                    disabled={isViewing}
                                />
                               <label> Repeat Visits Only </label>
                            </div>
                            <div className="visit-frequency-checkbox">
                                <InputField
                                    type="checkbox"
                                    checked={formData.visitFrequency.includes('specificNumber')}
                                    onChange={() => handleVisitFrequencyChange('specificNumber')}
                                    disabled={isViewing}
                                />
                              <label>  Specific Number </label>
                           </div>
                           <div className="visit-frequency-checkbox">
                                <InputField
                                    type="checkbox"
                                    checked={formData.visitFrequency.includes('other')}
                                    onChange={() => handleVisitFrequencyChange('other')}
                                    disabled={isViewing}
                                />
                               <label> Other </label>
                            </div>
                        </div>
                    </div>
                </div></div>
                <div className="form-row">
        {formData.visitFrequency.includes('specificNumber') && (
            
                <div className="form-group">
                    <label>Specific Number</label>
                    <InputField
                        type="number"
                        placeholder="Enter Here"
                        value={formData.specificNumber}
                        onChange={(e) => setFormData({ ...formData, specificNumber: e.target.value })}
                        readOnly={isViewing}
                        width={formData.visitFrequency.includes('other') ? 'auto' : '66%'}
                    />
                </div>
            
        )}

        {formData.visitFrequency.includes('other') && (
           
                <div className="form-group">
                    <label>Others</label>
                    <InputField
                        type="text"
                        placeholder="Enter Here"
                        value={formData.others}
                        onChange={(e) => setFormData({ ...formData, others: e.target.value })}
                        readOnly={isViewing}
                        width={formData.visitFrequency.includes('specificNumber') ? 'auto' : '66%'}

                    />
                </div>
            
        )}
          </div>      </div>

                <div className="button-container">
                    <button 
                        className="button-back" 
                        onClick={handleBack}
                    >
                        Back
                    </button>
                    {!isViewing && (
                        <button 
                            className="button-save" 
                            onClick={handleSave}
                        >
                            {isEditing ? "Update" : "Save"}
                        </button>
                    )}
                </div>
            
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

export default AddEditCouponDiscountMaster;