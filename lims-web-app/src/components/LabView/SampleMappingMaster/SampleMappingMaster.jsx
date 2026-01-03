
import React, { useEffect, useState } from "react";
import "./SampleMappingMaster.css";
import LimsTable from "../../LimsTable/LimsTable";
import Swal from "../../Re-usable-components/Swal";
import { sampleMappingService } from "../../../services/LabViewServices/sampleMappingService";
import DropDown from "../../Re-usable-components/DropDown";
import InputField from "../../Homepage/InputField";
import SampleMaster from "../SampleMaster/SampleMaster";

const SampleMappingMaster = () => {
    const [sampleMappings, setSampleMappings] = useState([]);
    const [pageNumber, setPageNumber] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [totalCount, setTotalCount] = useState(0);
    const [searchText, setSearchText] = useState("");
    const [showSampleForm, setShowSampleForm] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [isViewing, setIsViewing] = useState(false);
    const [popupConfig, setPopupConfig] = useState(null);

    const [testOptions, setTestOptions] = useState([]);
    const [sampleOptions, setSampleOptions] = useState([]);

    const labId = "4934a96c-84c1-455b-adb6-f84ae7f9ce39"; 
    const userId = "cc1f7267-195b-4c8e-a42a-8ce440f71cc3"; 

    const initialFormData = {
        testName: "",
        sampleName: "",
        sampleType: "",
        id: null
    };

    const [formData, setFormData] = useState(initialFormData);

    useEffect(() => {
        fetchSampleMappings();
        fetchDropdownData();
    }, []);

    const fetchSampleMappings = async () => {
        try {
            const response = await sampleMappingService.getAllSampleMappings(labId, pageNumber, pageSize, searchText);
            setSampleMappings(response.data);
            setTotalCount(response.totalCount);
        } catch (error) {
            setSampleMappings([]);
            setTotalCount(0);
        }
    };

    const fetchDropdownData = async () => {
        try {
            const testData = await sampleMappingService.getTestNames(labId);
            const sampleData = await sampleMappingService.getSampleNames(labId);
            setTestOptions(testData);
            setSampleOptions(sampleData);
        } catch (error) {
            setTestOptions([]);
            setSampleOptions([]);
        }
    };

    const handleDropdownChange = (field, value) => {
        const updatedFormData = { ...formData, [field]: value };

        if (field === "testName" || field === "sampleName") {
            updatedFormData.sampleType = `${updatedFormData.testName || ""} - ${updatedFormData.sampleName || ""}`.trim();
        }

        setFormData(updatedFormData);
    };

    const handleViewAll = () => {
        fetchSampleMappings(); 
    };

    const handleSave = async () => {
        if (!formData.testName.trim() || !formData.sampleName.trim() || !formData.sampleType.trim()) {
            setPopupConfig({
                icon: 'error',
                title: 'Validation Error',
                text: 'All fields are required.',
                onClose: () => setPopupConfig(null),
            });
            return;
        }

        try {
            if (isEditing) {
                await sampleMappingService.editSampleMapping(formData.id, formData);
            } else {
                await sampleMappingService.addSampleMapping({ ...formData, createdBy: userId }, userId);
            }
            fetchSampleMappings();
            resetForm();
        } catch (error) {
            setPopupConfig({
                icon: 'error',
                title: 'Error',
                text: 'Failed to save Sample Mapping.',
                onClose: () => setPopupConfig(null),
            });
        }
    };

    const resetForm = () => {
        setFormData(initialFormData);
        setIsEditing(false);
        setIsViewing(false);
        setShowSampleForm(false);
    };

    return (
        <div className="sample-mapping-container">
            {showSampleForm ? (
                <div className="profile-container">
                    <h2 className="title-text">
                        {isViewing ? "View Sample Mapping" : isEditing ? "Edit Sample Mapping" : "Add Sample Mapping"}
                    </h2>
                    <div className="form-group-container">
                       <div className="form-test-align">
                        <div className="form-group" >
                            <label>Test Name</label>
                            <InputField
                                placeholder={"Test Name"}
                                value={formData.testName}
                                onChange={(e) => handleDropdownChange("testName", e.target.value)}
                                readOnly={isViewing}
                            />
                        </div>
                        <div className="form-group">
                            <label>Sample Name</label>
                            <DropDown
                                options={sampleOptions}
                                value={formData.sampleName}
                                onChange={(value) => handleDropdownChange("sampleName", value)}
                                readOnly={isViewing}
                            />
                        </div>
                        </div>
                            <div className="form-group" id="form-sample-type">
                            <label>Sample Type</label>
                            <div className="input-container">
                                <InputField
                                    placeholder="Sample Type"
                                    value={formData.sampleType}
                                    readOnly
                                />
                                <span className="view-all-link" onClick={handleViewAll}>View all</span>
                            </div>
                        </div>

                    </div>
                    <div className="button-container">
                        <button className="button-back" onClick={resetForm}>Back</button>
                        {!isViewing && (
                            <button className="button-save" onClick={handleSave}>
                                {isEditing ? "Update" : "Save"}
                            </button>
                        )}
                    </div>
                </div>
            ) : (
                <LimsTable
                    title="Sample Master"
                    totalCount={totalCount}
                    showSearch={true}
                    showAddButton={true}
                    showPagination={true}
                    columns={[
                        { key: 'slNo', label: 'Sl. No.', width: '50px', align: 'center' },
                        { key: 'testName', label: 'Test Name', width: '1fr', align: 'center' },
                        { key: 'sampleName', label: 'Sample Name', width: '1fr', align: 'center' },
                        { key: 'sampleType', label: 'Sample Type', width: '1fr', align: 'center' },
                        { key: 'action', label: 'Action', width: '116px', align: 'center' }
                    ]}
                    data={sampleMappings}
                    onAdd={() => {
                        resetForm();
                        setShowSampleForm(true);
                    }}
                    onView={(row) => {
                        setFormData(row);
                        setIsViewing(true);
                        setShowSampleForm(true);
                    }}
                    onEdit={(row) => {
                        setFormData(row);
                        setIsEditing(true);
                        setShowSampleForm(true);
                    }}
                    onDelete={(row) => {
                        setPopupConfig({
                            icon: 'warning',
                            title: 'Are you sure?',
                            text: 'Do you want to delete this Sample Mapping?',
                            onButtonClick: async () => {
                                await sampleMappingService.deleteSampleMapping(row.id);
                                fetchSampleMappings();
                                setPopupConfig(null);
                            },
                            onClose: () => setPopupConfig(null),
                        });
                    }}
                />
            )}
            {popupConfig && <Swal {...popupConfig} />}
        </div>
    );
};

export default SampleMappingMaster;
