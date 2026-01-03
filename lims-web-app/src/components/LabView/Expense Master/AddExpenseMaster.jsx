import React, { useEffect, useState } from 'react'
import ExpenseMaster from './ExpenseMaster'
import InputField from '../../Homepage/InputField';
import "./AddExpenseMaster.css"
import Button from '../../Re-usable-components/Button';
import DropDown from '../../Re-usable-components/DropDown';
import { useLocation, useNavigate } from 'react-router-dom';
import { saveExpense, updateExpenseById } from '../../../services/LabViewServices/ExpenseMasterService';
import Swal from '../../Re-usable-components/Swal';
import { uploadFile } from '../../../services/fileUploadService';
import { ExpenseCategoryGetAll } from '../../../services/LabViewServices/ExpenseCategoryService';
import Error from '../../Re-usable-components/Error';
const AddExpenseMaster = () => {
    const [expenseData, setExpenseData] = useState({
        expenseDate: "",
        expenseAmount: "",
        expenseCategory: {},
        description: "",
        uploadFile: ""
    });
    const [expenseCategory, setExpenseCategory] = useState([]);
    const [file, setFile] = useState(null);
    const [popupConfig, setPopupConfig] = useState(null);
    const [viewMode, setViewMode] = useState(false)
    const location = useLocation()
    const [errors, setErrors] = useState({});
    const handleChange = (e) => {
        const { name, value } = e.target;
        setExpenseData({ ...expenseData, [name]: value });
        // Clear the error if the user starts typing again
        // setErrors(prevErrors => ({
        //     ...prevErrors,
        //     [name]: value.trim() ? "" : prevErrors[name]
        // }));
    };
    const handleDescriptionChange = (e) => {
        const { value } = e.target;
        setExpenseData((prevState) => ({
            ...prevState,
            description: value
        }));
    };
    const validateForm = () => {
        let newErrors = {};

        if (!expenseData.expenseAmount) newErrors.expenseAmount = "Expense Amount is required";
        if (!expenseData.expenseCategory.expenseName) newErrors.expenseCategory = "Expense Category is required";
        if (!expenseData.expenseDate) newErrors.expenseDate = "Expense Date is required";
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;  // Returns true if no errors
    };
    const handleBlur = (e) => {
        const { name, value } = e.target;
    
        // Show error if the field is empty after losing focus
        if (!value.trim()) {
            setErrors((prevErrors) => ({
                ...prevErrors,
                [name]: `${name.replace(/([A-Z])/g, ' $1')} is required`
            }));
        }
    };
    const handleExpenseUpdate = async () => {
        if (!validateForm()) return;
        try {
            const id = location.state.expenseDetails.data.id;
            const updatedExpense = {
                ...location.state.expenseDetails.data, // Include existing fields
                ...expenseData, // Merge updated fields
            };
            const response = await updateExpenseById(id, updatedExpense);

            if (response.statusCode === "200 OK") {
                setPopupConfig({
                    icon: "success",
                    title: "Updated Successfully",
                    onClose: () => {
                        navigate("/expense-master");
                    },
                });
            } else {
                setPopupConfig({
                    icon: "error",
                    title: "Error updating",
                    onClose: () => { setPopupConfig(null); },
                });
                // console.log("update error", response.data.message);
            }
        } catch (error) {
            setPopupConfig({
                icon: "error",
                title: "Error updating",
                onClose: () => { setPopupConfig(null); },
            });
        }
    };

    const handleFileChange = (e) => {
        const uploadedFile = e.target.files[0];
        setFile(uploadedFile);
        setExpenseData((prevState) => ({
            ...prevState,
            uploadFile: uploadedFile.name,
        }));
        // console.log("File uploaded:", uploadedFile.name);
    };
    const navigate = useNavigate()
    const handleExpenseNavigate = () => {
        navigate('/expense-master')
    }
    const labId = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
    const fetchCategories = async () => {
        try {
            const response = await ExpenseCategoryGetAll(labId, 0, 10);
            // console.log("categories fetched", response.data);
            setExpenseCategory(response.data);
            // console.log("", expenseData)
        } catch (error) {
            console.error('Error fetching departments:', error);
        }
    }
    const handleExpenseAdd = async () => {
        if (!validateForm()) return;
        const payload = {
            expenseDate: expenseData.expenseDate,
            expenseAmount: expenseData.expenseAmount,
            expenseCategory: expenseData.expenseCategory,
            description: expenseData.description,
            uploadFile: expenseData.uploadFile,
            labId: "3fa85f64-5717-4562-b3fc-2c963f66afa6" // Replace with dynamic labId if needed
        };
        try {
            const response = await saveExpense(payload); // Ensure saveExpense is defined in your service
            console.log("Expense added:", response.data);
            setPopupConfig({
                icon: 'success',
                title: 'Expense Added Successfully',
                text: '',
                onClose: () => navigate('/expense-master'),
            });
        } catch (error) {
            console.error(error);
            setPopupConfig({
                icon: 'error',
                title: 'Failed to add expense.',
                text: 'Please try again.',
                onButtonClick: () => setPopupConfig(null),
                onClose: () => setPopupConfig(null),
            });
        }
    };
    useEffect(() => {
        if (location.state) {
            const { expenseDetails, mode } = location.state;
            if (mode === 'view') {
                setViewMode(true);
            }
            if (expenseDetails) {
                setExpenseData({
                    expenseDate: expenseDetails.data.expenseDate || "",
                    expenseAmount: expenseDetails.data.expenseAmount || "",
                    expenseCategory: expenseDetails.data.expenseCategory || {},
                    description: expenseDetails.data.description || "",
                    uploadFile: expenseDetails.data.uploadFile || "",
                });
            }
        }
    }, [location.state]);

    useEffect(() => {
        fetchCategories();
    }, []);
    return (
        <div className='parent-container'>
            <div className='expense-header'>
                <span className='expense-title'>
                    {viewMode ? 'View Expense Master' : location.state?.mode === 'edit' ? 'Edit Expense Master' : 'Add Expense Master'}
                </span>
            </div>
            <div className='expense-container'>
                <div className='expense-inner-container1'>
                    <div className='inner-group1'>
                        <div className='expense_master_field'>
                            <InputField
                                label="Expense Date"
                                type="date"
                                value={expenseData.expenseDate}
                                placeholder="Enter here"
                                name="expenseDate"
                                onChange={handleChange}
                                onBlur={handleBlur}
                                disabled={viewMode} required
                                error={errors.expenseDate}
                            />
                            {/* {errors.expenseDate && <Error message={errors.expenseDate} type="error" />} */}
                        </div>
                        <div className='expense_master_field'>
                            <DropDown
                                label="Expense Category"
                                options={expenseCategory}
                                value={expenseCategory.find((option) => option.expenseName === expenseData.expenseCategory.expenseName)}
                                name="expenseCategory"
                                fieldName={"expenseName"}
                                onChange={(option) => {
                                    setExpenseData({ ...expenseData, expenseCategory: option.target.value })
                                    // setErrors((prevErrors) => ({ ...prevErrors, expenseCategory: "" }));
                                }}
                                disabled={viewMode}
                                required
                                error={errors.expenseCategory}
                                onBlur={handleBlur}
                            />
                            {/* {errors.expenseCategory && <Error message={errors.expenseCategory} type="error" />} */}
                        </div>
                    </div>
                    <div className='inner-group2'>
                        <div className='expense_master_field'>
                            <InputField
                                label="Expense Amount"
                                type="number"
                                placeholder="Enter here"
                                value={expenseData.expenseAmount}
                                name="expenseAmount"
                                onChange={handleChange}
                                onBlur={handleBlur}
                                disabled={viewMode} required
                                error={errors.expenseAmount}
                            />
                            {/* {errors.expenseAmount && <Error message={errors.expenseAmount} type="error" />} */}
                        </div>
                    </div>
                </div>
                <div className='inner-group3'>
                    <div>
                        <InputField
                            label="Description"
                            type="textarea"
                            placeholder="Enter here"
                            value={expenseData.description}
                            name="description"
                            onChange={viewMode ? undefined : handleDescriptionChange} // Prevent edits when in view mode
                            disabled={viewMode}
                            
                        />
                    </div>
                </div>
                <div className='expense-inner-container2'>
                    <InputField
                        label="Upload Attachment"
                        type="file"
                        placeholder="Drag file here to upload (or) Select File"
                        onChange={viewMode ? undefined : handleFileChange} // Disable changes in view mode
                        existingFileName={expenseData.uploadFile}
                        name="uploadFile"
                        value={file}
                        disabled={viewMode}
                    />
                </div>
            </div>
            <div className="botton-divv">
                <div className='inner_btn'>
                    {viewMode ? (<button className="btn-secondary" onClick={handleExpenseNavigate}>Back</button>) : location.state?.mode === "edit" ? (
                        <><button className="btn-secondary" onClick={handleExpenseNavigate}>Back</button><button className="btn-primary" onClick={handleExpenseUpdate}>update</button></>
                    ) : (<><button className="btn-secondary" onClick={handleExpenseNavigate}>Back</button><button className="btn-primary" onClick={handleExpenseAdd}>Save</button></>)}
                </div>
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
    )
}

export default AddExpenseMaster
