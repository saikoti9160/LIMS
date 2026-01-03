import React, { useEffect, useState } from "react";
import "./paymentMode.css";
import LimsTable from "../../LimsTable/LimsTable";
import { paymentModeService } from "../../../services/paymentModeService";
import InputField from "../../Homepage/InputField";
import Swal from "../../Re-usable-components/Swal";

const PaymentMode = () => {
    const [paymentModes, setPaymentModes] = useState([]);
    const [pageNumber, setPageNumber] = useState(0);
    const [sortedBy, setSortedBy] = useState("paymentModeName");
    const [addPaymentMode, setAddPaymentMode] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [isViewing, setIsViewing] = useState(false);
    const [formData, setFormData] = useState({ paymentModeName: "", id: null });
    const [popupConfig, setPopupConfig] = useState(null);
    const [pageSize, setPageSize] = useState(10);
    const [totalCount, setTotalCount] = useState(0);
    const [startsWith, setStartsWith] = useState('');
    // const [createdBy, setCreatedBy] = useState(null);
    const createdBy = "3fa85f64-5717-4562-b3fc-2c963f66af10"; //taken as reference will remove later
   
    const columns = [
        { key: 'slNo', label: 'Sl. No.', width: '80px', align: 'center' },
        { key: 'paymentModeName', label: 'Payment Mode', width: '1fr', align: 'center' },
        { key: 'action', label: 'Action', width: '160px', align: 'center' }
    ];


    useEffect(() => {
        fetchPaymentModes();
    }, [startsWith, pageNumber, pageSize, sortedBy ]);
      
    const fetchPaymentModes = async () => {
        try {
            const { data, totalCount } = await paymentModeService.getAllPaymentModes(
                startsWith,
                pageNumber,
                pageSize,
                sortedBy
            );
            
            setPaymentModes(data);
            setTotalCount(totalCount);
        } catch (error) {
            setPaymentModes([]);
            setTotalCount(0);
        }
    }; 
    const handleEdit = (row) => {
        setFormData({
            paymentModeName: row.paymentModeName,
            id: row.id  
        });
        setIsEditing(true);
        setAddPaymentMode(true);
    };
    
    const handleSave = async () => {
        const trimmedName = formData.paymentModeName === null ? '' : formData.paymentModeName.trim();
        if (trimmedName === '') {
            setPopupConfig({
                icon: 'delete',
                title: 'Validation Error',
                text: 'Payment Mode name is required and cannot be empty or just spaces.',
                onClose: () => setPopupConfig(null),
            });
            return;
        }
    
        try {
            if (isEditing) {
                const modifiedBy = createdBy
                await paymentModeService.editPaymentModes(formData.id, {
                    paymentModeName: trimmedName,
                }, modifiedBy);
                setPopupConfig({
                    icon: 'success',
                    title: 'Updated Successfully',
                    text: '',
                    onClose: () => {
                        setPopupConfig(null);
                        setFormData({ paymentModeName: "", id: null });
                        setIsEditing(false);
                        setAddPaymentMode(false);
                        fetchPaymentModes();
                    }
                });
            } else {
                await paymentModeService.addPaymentModes({
                    paymentModeName: trimmedName,
                    createdBy: createdBy
                }, createdBy);
                setPopupConfig({
                    icon: 'success',
                    title: 'Added Successfully',
                    text: '',
                    onClose: () => {
                        setPopupConfig(null);
                        setFormData({ paymentModeName: "", id: null });
                        setAddPaymentMode(false);
                        fetchPaymentModes();
                    }
                });
            }
        } catch (error) {
            console.error("Error saving payment mode:", error);
            setPopupConfig({
                icon: 'delete',
                title: 'Failed to save Payment Mode',
                text: 'Please try again.',
                onButtonClick: () => setPopupConfig(null),
                onClose: () => setPopupConfig(null),
            });
        }
    };
    const handleDelete = (row) => {
        setPopupConfig({
            icon: 'delete',
            title: 'Are you sure?',
            text: 'Do you want to delete this Payment Mode?',
            onButtonClick: async () => {
                try {
                    await paymentModeService.deletePaymentModes(row.id);
                    setPopupConfig({
                        icon: 'success',
                        title: 'Deleted Successfully',
                        text: '',
                        onClose: () => {
                            setPopupConfig(null);
                            fetchPaymentModes();
                        }
                    });
                } catch (error) {
                    console.error("Error deleting payment mode:", error);
                    setPopupConfig({
                        icon: 'delete',
                        title: 'Failed to delete Payment Mode',
                        text: 'Please try again.',
                        onButtonClick: () => setPopupConfig(null),
                        onClose: () => setPopupConfig(null),
                    });
                }
            },
            onClose: () => setPopupConfig(null)
        });
    };

    const handleView = async (row) => {
        try {
            const response = await paymentModeService.viewPaymentMode(row.id);
            setFormData({
                paymentModeName: response.data.paymentModeName,
                id: response.id
            });
            setIsViewing(true);
            setAddPaymentMode(true);
        } catch (error) {
            console.error("Error fetching payment mode details:", error);
            setPopupConfig({
                icon: 'error',
                title: 'Failed to Fetch Details',
                text: 'Something went wrong. Please try again.',
                onButtonClick: () => setPopupConfig(null),
                onClose: () => setPopupConfig(null),
            });
        }
    };
    const handleBack = () => {
        setAddPaymentMode(false);
        setIsEditing(false);
        setIsViewing(false);
        setFormData({ paymentModeName: "", id: null });
    };
    const handleSearch = (query) => {
        setPageNumber(0);
        setStartsWith(() => query);
    };
    
    const onPageChange = (value) => {
        if (value >= 0 && value < Math.ceil(totalCount / pageSize)) {
            setPageNumber(value);
        }
    };
    
    const onPageSizeChange = (value) => {
        setPageNumber(0);
        setPageSize(value);
    };

    return (
        
        <div className="master-container">

            {addPaymentMode ? (
                <div className="profile-container">
                    <h2 className="title-text">
                        {isViewing ? "View Payment Mode" : isEditing ? "Edit Payment Mode" : "Add Payment Mode"}
                    </h2>
                    <div className="mode-field">
                        <label>Payment Mode <span className="text-danger">*</span></label>
                      <InputField
                            type="text"
                            placeholder="Enter Payment Mode"
                            value={formData.paymentModeName}
                            onChange={(e) => setFormData({ ...formData, paymentModeName: e.target.value })}
                            width={"75%"}
                            readOnly={isViewing}
                        />
                    </div>
                    <div className="button-container">
                        <button 
                            className="button-back" 
                            style={isViewing ? { marginRight: "35px" } : {}}
                            onClick={handleBack}
                        >
                            Back
                        </button>
                        {!isViewing && (
                            <button 
                                className="button-save" 
                                onClick={handleSave}
                                disabled={!formData.paymentModeName.trim()}
                            >
                                {isEditing ? "Update" : "Save"}
                            </button>
                        )}
                    </div>
                </div>
            ) : (
                <LimsTable
                    title="Payment Mode"
                    totalCount={totalCount}
                    showSearch={true}
                    showAddButton={true}
                    showPagination={true}
                    columns={columns}
                    data={paymentModes}
                    onAdd={() => setAddPaymentMode(true)}
                    onView={handleView}
                    onEdit={handleEdit}
                    onHandleSearch={handleSearch}
                    onDelete={handleDelete}
                    currentPage={pageNumber} 
                    onPageChange={onPageChange}
                    onPageSizeChange={onPageSizeChange}
                    onSortChange={(sortKey) => setSortedBy(sortKey)}
                />
            )}
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

export default PaymentMode;