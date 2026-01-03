import React, { useEffect, useState } from "react";
import "./signatureMaster.css";
import LimsTable from "../../LimsTable/LimsTable";
import { signatureMasterService } from "../../../services/LabViewServices/signatureMasterService";
import InputField from "../../Homepage/InputField";
import DropDown from "../../Re-usable-components/DropDown";
import { getAllDoctors } from "../../../services/LabViewServices/DoctorService";
import Swal from "../../Re-usable-components/Swal";

const SignatureMaster = () => {
    const [signers, setSigners] = useState([]);
    const [pageNumber, setPageNumber] = useState(0);
    const [keyword, setKeyword] = useState("");
    const [addSigner, setAddSigner] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [isViewing, setIsViewing] = useState(false);
    const [formData, setFormData] = useState({ 
        signerName: "", 
        id: null,
        uploadSignature: null 
    });
    const [popupConfig, setPopupConfig] = useState(null);
    const [pageSize, setPageSize] = useState(10);
    const [totalCount, setTotalCount] = useState(0);
    const [fileUploaded, setFileUploaded] = useState(null);
    // const [createdBy, setCreatedBy] = useState(null);
    const [flag, setFlag] = useState(true);
     const [signerOptions, setSignerOptions] = useState({names: []});
    const createdBy = "3fa85f64-5717-4562-b3fc-2c963f66af11"; //taken as reference will remove later

    const columns = [
        { key: 'slNo', label: 'Sl. No.', width: '80px', align: 'center' },
        { key: 'signerName', label: 'Signer Name', width: '1fr', align: 'center' },
        { key: 'action', label: 'Action', width: '160px', align: 'center' }
    ];

    useEffect(() => {
        fetchAllSignerNames();
        fetchSignerOptions();
    }, [createdBy, keyword, flag, pageNumber, pageSize]);


    const fetchAllSignerNames = async () => {
        try {
            const {data,totalCount} = await signatureMasterService.getAllSignerNames(
                createdBy,
                keyword,
                flag, 
                pageNumber,
                pageSize
            );

            setSigners(data);
            setTotalCount(totalCount);
        } catch (error) {
            setSigners([]);
            setTotalCount(0);
        }
    };
    const fetchSignerOptions = async () => {
        try {
            const response = await getAllDoctors(createdBy, "", true, 0, 100);
            const doctors = response?.data || [];
            const approverDoctors = doctors.filter(doctor => doctor.isReportApprover === true);
            const options = approverDoctors.map(doctor => ({
                value: doctor.id,
                label: doctor.doctorName,
                signerName: doctor.doctorName
            }));
            
            setSignerOptions({ names: options });
            
        } catch (error) {
            console.error('Error fetching doctor options:', error);
            setSignerOptions({ names: [] });
        }
    };

    const handleEdit = async (row) => {
        try {
            const response = await signatureMasterService.getSignatureById(row.id);
            
            setFormData({
                signerName: row.signerName,
                uploadSignature: response.data.uploadSignature,
                id: row.id
            });
    
            setIsEditing(true);
            setAddSigner(true);
        } catch (error) {
            setPopupConfig({
                icon: 'error',
                title: 'Error',
                text: 'Failed to fetch signature details. Please try again.',
                onClose: () => setPopupConfig(null)
            });
        }
        
    };
    const handleFileUploaded = ( url )=>{
        const parts = url.split('/');
        const fileName = parts[parts.length - 1];
        const fileUrl = fileName.toString();
        setFileUploaded(()=> fileUrl);
    }
    const handleSave = async () => {
        const trimmedName = (formData.signerName || '').trim();
        console.log("trimmedName",trimmedName);
    
        if (!trimmedName) {
            setPopupConfig({
                icon: 'error',
                title: 'Validation Error',
                text: 'Signer Name is Required',
                onClose: () => setPopupConfig(null),
            });
            return;
        }

        if (!formData.uploadSignature && !isEditing) {
            setPopupConfig({
                icon: 'delete',
                title: 'Validation Error',
                text: 'Please upload a signature file.',
                onClose: () => setPopupConfig(null),
            });
            return;
        }

        try {
            const data = {
                labId:"20cd288a-d7ed-4dde-9b1a-a520dd11e9c8",
                signerName: trimmedName,
                uploadSignature: fileUploaded,
            };

            let response;

            if (isEditing) {
                response = await signatureMasterService.updateSignature(formData.id, {
                    ...data,
                    ...(formData.uploadSignature ? { uploadSignature: fileUploaded } : {})
                });
                
                if (response?.status === 200 || response?.statusCode === '200 OK') {
                    setPopupConfig({
                        icon: 'success',
                        title: 'Updated Successfully',
                        text: '',
                        onClose: () => {
                            setPopupConfig(null);
                            resetForm();
                            fetchAllSignerNames();
                        }
                    });
                } else {
                    throw new Error('Update failed');
                }
            } else {
                response = await signatureMasterService.saveSignature(data,createdBy);
                
                if (response?.status === 200 || response?.statusCode === '200 OK') {
                    setPopupConfig({
                        icon: 'success',
                        title: 'Added Successfully',
                        text: '',
                        onClose: () => {
                            setPopupConfig(null);
                            resetForm();
                            fetchAllSignerNames();
                        }
                    });
                } else {
                    throw new Error('Save failed');
                }
            }
        } catch (error) {
            
        }
    };

    const resetForm = () => {
        setFormData({ signerName: "", id: null, uploadSignature: null });
        setFileUploaded(null);
        setIsEditing(false);
        setAddSigner(false);
    };
    const handleDelete = (row) => {
        setPopupConfig({
            icon: 'delete',
            title: 'Are you sure?',
            text: 'Do you want to delete this Signer?',
            onButtonClick: async () => {
                try {
                    await signatureMasterService.deleteSignature(row.id);
                    setPopupConfig({
                        icon: 'success',
                        title: 'Deleted Successfully',
                        text: '',
                        onClose: () => {
                            setPopupConfig(null);
                            fetchAllSignerNames();
                        }
                    });
                } catch (error) {
                    console.error("Error deleting signer:", error);
                    setPopupConfig({
                        icon: 'delete',
                        title: 'Failed to delete',
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
            const response = await signatureMasterService.getSignatureById(row.id);
            setFormData({
                signerName: row.signerName,
                uploadSignature: response.data.uploadSignature,
                id: row.id
            });
            setIsViewing(true);
            setAddSigner(true);
        } catch (error) {
            console.error("Error fetching signer details:", error);
            setPopupConfig({
                icon: 'delete',
                title: 'Failed to Fetch Details',
                text: 'Something went wrong. Please try again.',
                onButtonClick: () => setPopupConfig(null),
                onClose: () => setPopupConfig(null),
            });
        }
    };

    const handleBack = () => {
        setAddSigner(false);
        setIsEditing(false);
        setIsViewing(false);
        setFormData({ signerName: "", id: null });
    };

    const handleSearch = (query) => {
        setPageNumber(0);
        setKeyword(() => query);
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
   
    const handleSignerNameChange = (event) => {
        const selectedOption = event.target.value;
        const signerName = selectedOption?.signerName || "";
        setFormData(prev => ({
            ...prev,
            signerName: signerName
        }));
    };


    return (
        <div className="master-container">
        {addSigner ? (
            <div className="profile-container">
                <h2 className="title-text">
                    {isViewing ? "View Signature Master" : isEditing ? "Edit Signature Master" : "Add Signature Master"}
                </h2>
                <div className="add-field">
                    <label>Signer Name <span className="text-danger">*</span></label>

                    <DropDown
                                options={signerOptions.names}
                                placeholder="Select Signer Name"
                                onChange={handleSignerNameChange}
                                name="signerName"
                                value={formData.signerName}
                                disabled={isViewing}
                                fieldName="signerName"
                                required
                                width={"50%"}
                            />
                    <label className="signature-file-label">Upload Signature <span className="text-danger">*</span></label>
                        <InputField
                            type="file"
                            value={formData.uploadSignature}
                            name="uploadSignature"
                            onChange={(e) => setFormData({ ...formData, [e.target.name]: e.target.files[0]})}
                            readOnly={isViewing}
                            handleFileUploaded={handleFileUploaded}
                            existingFileName={formData.uploadSignature}
                            />
                </div>
                <div className="button-container">
                    <button 
                        className="button-back" 
                        style={isViewing ? { marginRight: '10  px' } : {}}
                        onClick={handleBack}
                    >
                        Back
                    </button>
                    {!isViewing && (
                        <button 
                            className="button-save" 
                            onClick={handleSave}
                             disabled={!(formData.signerName || '').trim()}
                        >
                            {isEditing ? "Update" : "Save"}
                        </button>
                    )}
                </div>
            </div>
        ) : (
            <LimsTable
                title="Signature Master"
                totalCount={totalCount}
                showSearch={true}
                showAddButton={true}
                showPagination={true}
                columns={columns}
                data={signers}
                onAdd={() => setAddSigner(true)}
                onView={handleView}
                onEdit={handleEdit}
                onHandleSearch={handleSearch}
                onDelete={handleDelete}
                currentPage={pageNumber}
                onPageChange={onPageChange}
                onPageSizeChange={onPageSizeChange}
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

export default SignatureMaster;
