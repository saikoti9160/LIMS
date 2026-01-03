import React, { useCallback, useEffect, useState } from "react";
import LimsTable from "../../LimsTable/LimsTable";
import "./Profileconfiguration.css";
import { useLocation, useNavigate } from "react-router-dom";
import InputField from "../../Homepage/InputField";
import calenderIcon from "../../../assets/icons/Vector.svg";
import DropDown from "../../Re-usable-components/DropDown";
import timeIcon from "../../../assets/icons/timeIcon.svg";
import Swal from "../../Re-usable-components/Swal";
import Checkbox from "../../Re-usable-components/Checkbox";
import { addProfileconfiguration } from "../../../services/LabViewServices/ProfileConfigurationService";
import { getAllTestConfigurations } from "../../../services/LabViewServices/testConfigurationService";

function AddprofileConfiguration() {
    const [viewMode, setViewMode] = useState(false);
    const [tab, setTab] = useState(0);
    const location = useLocation();
    const [mode, setMode] = useState('');
    const [popupConfig, setPopupConfig] = useState(null);
    const navigate = useNavigate();
    const navigateUrl = '/lab-view/profile-configuration';
    const [selectedResultType, setSelectedResultType] = useState("");
    const [selectResultNumeric, setSelectedResultNumeric] = useState('');
    const [errors, setErrors] = useState({});
    const createdBy = localStorage.getItem('createdBy');
    const [tableData, setTableData] = useState([]);
    const [pageSize, setPageSize] = useState(10);
    const [searchText, setSearchText] = useState("");
    const [tests, setTests] = useState([]);
    const [totalCount, setTotalCount] = useState(0);
    const [pageNumber, setPageNumber] = useState(0);
    const labId = "20cd288a-d7ed-4dde-9b1a-a520dd11e9c8";

    const validateForm = () => {
        let newErrors = {};

        if (tab === 0) {
            if (!reportParaData.profileName) {
                newErrors.profileName = "Profile Name is required";
            }
            if (!reportParaData.timeToShareDays && !reportParaData.timeToShareHours && !reportParaData.timeToShareMinutes) {
                newErrors.timeToShare = "Time to Share Report is required";
            }
            if (!reportParaData.turnaroundTime) {
                newErrors.turnaroundTime = "Turnaround Time is required";
            }
            if (!reportParaData.tests) {
                newErrors.tests = "Tests are required";
            }
            if (!reportParaData.source) {
                newErrors.source = "Source is required";
            }
            if (!reportParaData.turnaroundTimeDays) {
                newErrors.turnaroundTimeDays = "Turnaround Time (Days) is required";
            }
            if (!reportParaData.turnaroundTimeMinutes) {
                newErrors.turnaroundTimeMinutes = "Turnaround Time (Minutes) is required";
            }
        } else if (tab === 1) {
            if (!reportParaData.profileParameter) {
                newErrors.profileParameter = "Profile Parameter is required";
            }
            if (!reportParaData.resultType) {
                newErrors.resultType = "Result Type is required";
            }
            if (reportParaData.resultType === "Text" && !reportParaData.referenceResult) {
                newErrors.referenceResult = "Reference Result is required";
            } else if (reportParaData.resultType === "Numeric") {
                if (reportParaData.numericConfig === "Gender") {
                    genderOptions.forEach(gender => {
                        if (!reportParaData[`lowValue${gender}`] || !reportParaData[`highValue${gender}`]) {
                            newErrors[`range${gender}`] = `Range for ${gender} is required`;
                        }
                    });
                } else if (reportParaData.numericConfig === "Age") {
                    genderOptions.forEach(gender => {
                        if (!reportParaData[`lowValue${gender}`] || !reportParaData[`highValue${gender}`] || !reportParaData[`ageRangeFrom${gender}`] || !reportParaData[`ageRangeTo${gender}`]) {
                            newErrors[`range${gender}`] = `Range for ${gender} is required`;
                        }
                    });
                } else if (reportParaData.numericConfig === "Range") {
                    if (!reportParaData.referenceValueLow || !reportParaData.referenceValueHigh) {
                        newErrors.referenceValueRange = "Reference Value Range is required";
                    }
                }
                if (!reportParaData.units) {
                    newErrors.units = "Units are required";
                }
            }
        } else if (tab === 2) {
            if (!reportParaData.settingName) {
                newErrors.settingName = "Setting Name is required";
            }
            if (!reportParaData.patientInfo) {
                newErrors.patientInfo = "Patient Info is required";
            }
            if (!reportParaData.paperSize) {
                newErrors.paperSize = "Paper Size is required";
            }
            if (!reportParaData.signPosition) {
                newErrors.signPosition = "Sign Position is required";
            }
            if (!reportParaData.dateFormat) {
                newErrors.dateFormat = "Date Format is required";
            }
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };


    const genderOptions = ["Male", "Female", "Child"];
    const columns = [
        { key: 'slNo', label: 'Sl. No.', width: '100px', align: 'center' },

        ...(tab === 0
            ? [
                { key: 'testName', label: 'Test Name', width: '1fr', align: 'center' },
                { key: 'price', label: 'Price', width: '1fr', align: 'center' }
            ]
            : [
                ...(selectResultNumeric === 'Gender' || selectResultNumeric === 'Age'
                    ? [
                        { key: 'testParameter', label: 'Test Parameter', width: '1fr', align: 'center' },
                        { key: 'testResult', label: 'Test Result', width: '20%', editable: true, placeholder: 'Enter Here' },
                        { key: 'unit', label: 'Unit', width: '1fr', align: 'center' },
                        { key: 'male', label: 'Male', width: '1fr', align: 'center' },
                        { key: 'female', label: 'Female', width: '1fr', align: 'center' },
                        { key: 'child', label: 'Child', width: '1fr', align: 'center' }
                    ]
                    : selectResultNumeric === 'Range' ?
                        [
                            { key: 'testParameter', label: 'Test Parameter', width: '1fr', align: 'center' },
                            { key: 'testResult', label: 'Test Result', width: '20%', editable: true, placeholder: 'Enter Here' },
                            { key: 'unit', label: 'Unit', width: '1fr', align: 'center' },
                            { key: 'referenceValue', label: 'Reference Value', width: '180px', align: 'center' }
                        ]
                        : [
                            { key: 'testParameter', label: 'Test Parameter', width: '1fr', align: 'center' },
                            { key: 'testResult', label: 'Test Result', width: '20%', editable: true, placeholder: 'Enter Here' },
                            { key: 'referenceResult', label: 'Reference Result', width: '250px', align: 'center' }
                        ])
            ]
        ),

        { key: 'action', label: 'Actions', width: '116px', align: 'center' }

    ];

    const [reportParaData, setReportParaData] = useState({
        profileName: "",          
        outSource: false,        
        days: 0,                 
        hours: 0,                
        minutes: 0,              
        turnaroundTimeDays: 0,   
        turnaroundTimeHours: 0,  
        turnaroundTimeMinutes: 0,
        tests: [],               
        profileDescription: "",  
        profileInstructions: "", 
        totalAmount: 0.0,        
        labId: "",                 
        reportParameter: {
            testParameter: "",     
            resultType: "",                  
            textReference: {
                referenceResult: ""  
            },
            numericConfiguration: {
                numericType: "",    
                genderBasedConfigs: [
                    {
                        gender: "",   
                        highValue: 0, 
                        lowValue: 0   
                    }
                ],
                ageBasedConfigs: [
                    {
                        ageGroup: "",      
                        fromYear: "",    
                        toYear: "",        
                        highValue: 0,  
                        lowValue: 0        
                    }
                ],
                rangeBasedConfig: {
                    referenceLowValue: 0,  
                    referenceHighValue: 0  
                }
            },
            unit: "",  
            labId: "", 
            remarks: "" 
        }
    });

    const fetchTests = useCallback(async () => {
        try {
            const response = await getAllTestConfigurations(labId, searchText, pageNumber, pageSize);
            setTests(response.data || []);
            setTotalCount(response.totalCount || 0);
        } catch (error) {
            console.error('Error fetching tests:', error);
        }
    }, [labId, searchText, pageNumber, pageSize]);

    useEffect(() => {
        fetchTests();
    }, [fetchTests]);

    const ageGroupOptions = [
        { label: "Male", value: "male" },
        { label: "Female", value: "female" },
        { label: "Child", value: "child" }
    ]
    const ageRangeOptions = [
        { label: 'Days', value: 'days' },
        { label: 'Months', value: 'months' },
        { label: 'Years', value: 'years' }
    ]
    const data = [
        { id: 1, slNo: 1, testName: "Test 1", price: 100, testParameter: "Parameter 1", testResult: "", referenceResult: "Normal", unit: "mg/dL", male: "80-120", female: "75-115", child: "70-110", referenceValue: '10-20' },
        { id: 2, slNo: 2, testName: "Test 2", price: 200, testParameter: "Parameter 2", testResult: "", referenceResult: "Normal", unit: "mmol/L", male: "4.0-6.0", female: "3.8-5.8", child: "3.5-5.5", referenceValue: '10-20' },
        { id: 3, slNo: 3, testName: "Test 3", price: 300, testParameter: "Parameter 3", testResult: "", referenceResult: "Abnormal", unit: "g/L", male: "13-17", female: "12-16", child: "11-15", referenceValue: '10-20' }
    ];
    const handleSave = () => {
        if (validateForm()) {
            try {
                const createdBy = createdBy;
                const response = addProfileconfiguration(reportParaData, createdBy);
                setPopupConfig({
                    icon: "success",
                    title: "Saved Successfully",
                    onClose: () => {
                        setPopupConfig(null);
                        if (tab > 2) {
                            setTab(tab + 1);
                        } else {
                            navigate(navigateUrl);
                        }
                    }
                })

            } catch (error) {
                setPopupConfig({
                    icon: "error",
                    title: "Something went wrong",
                    text: error.response?.data?.message || "Failed to save profile configuration",
                    onClose: () => setPopupConfig(null)
                })
            }
        }
    }
    const handleInputChange = (field, value) => {
        setReportParaData((prev => ({
            ...prev,
            profileConfiguration: {
                ...prev.profileConfiguration,
                [field]: value
            }
        })));
    }
    const handleParameterAdd = () => {
        const newParameter = {
            testParameter: reportParaData.testParameter,
            resultType: reportParaData.resultType,
            unit: reportParaData.unit,
            referenceResult: reportParaData.textReference.referenceResult
        };

        if (selectResultNumeric) {
            newParameter.numericConfiguration = {
                type: selectResultNumeric,
                ...reportParaData.numericConfiguration
            };
        }
        setTableData(prev => [...prev, { ...newParameter, id: Date.now() }]);
        setReportParaData(prev => ({
            ...prev,
            testParameter: "",
            resultType: "",
            unit: "",
            textReference: { referenceResult: "" },
            numericConfiguration: {
                numericType: "",
                genderBasedConfigs: [{ gender: "", highValue: 0, lowValue: 0 }],
                ageBasedConfigs: [{ ageGroup: "", fromYear: "", toYear: "", highValue: 0, lowValue: 0 }],
                rangeBasedConfig: { referenceLowValue: 0, referenceHighValue: 0 }
            }
        }));
    };
    const handleDelete = (id) => {
        setTableData(prev => prev.filter(item => item.id !== id));
    };
    const resultTypeOptions = [
        { label: 'Text', value: 'text' },
        { label: 'Numeric', value: 'numeric' }
    ]
    const numericOptions = [
        { label: 'Gender', value: 'gender' },
        { label: 'Age', value: 'age' },
        { label: 'Range', value: 'range' },
    ]

    const handleEdit = () => {
        if (validateForm()) {
            setPopupConfig({
                icon: "success",
                title: "Updated Successfully",
                onClose: () => {
                    setPopupConfig(null);
                }
            })
        }

    }
    const [includePDFHeaderFooter, setIncludePDFHeaderFooter] = useState(false);

    useEffect(() => {
        const mode = location.state?.mode;
        setMode(mode);

        if (mode === "view") {
            setViewMode(true);
            const profileData = location.state?.profileData || {};
            setReportParaData(prevState => ({
                ...prevState,
                profileName: profileData.profileName || "",
                outSource: profileData.outSource || false,
                days: profileData.days || 0,
                hours: profileData.hours || 0,
                minutes: profileData.minutes || 0,
                turnaroundTimeDays: profileData.turnaroundTimeDays || 0,
                turnaroundTimeHours: profileData.turnaroundTimeHours || 0,
                turnaroundTimeMinutes: profileData.turnaroundTimeMinutes || 0,
                tests: profileData.tests || [],
                profileDescription: profileData.profileDescription || "",
                profileInstructions: profileData.profileInstructions || "",
                totalAmount: profileData.totalAmount || 0.0,
                labId: profileData.labId || "",
                reportParameter: {
                    testParameter: profileData.reportParameter?.testParameter || "",
                    resultType: profileData.reportParameter?.resultType || "",
                    textReference: {
                        referenceResult: profileData.reportParameter?.textReference?.referenceResult || ""
                    },
                    numericConfiguration: {
                        numericType: profileData.reportParameter?.numericConfiguration?.numericType || "",
                        genderBasedConfigs: profileData.reportParameter?.numericConfiguration?.genderBasedConfigs || [
                            { gender: "", highValue: 0, lowValue: 0 }
                        ],
                        ageBasedConfigs: profileData.reportParameter?.numericConfiguration?.ageBasedConfigs || [
                            { ageGroup: "", fromYear: "", toYear: "", highValue: 0, lowValue: 0 }
                        ],
                        rangeBasedConfig: {
                            referenceLowValue: profileData.reportParameter?.numericConfiguration?.rangeBasedConfig?.referenceLowValue || 0,
                            referenceHighValue: profileData.reportParameter?.numericConfiguration?.rangeBasedConfig?.referenceHighValue || 0
                        }
                    },
                    unit: profileData.reportParameter?.unit || "",
                    labId: profileData.reportParameter?.labId || "",
                    remarks: profileData.reportParameter?.remarks || ""
                }
            }));
        }
    }, [location.state]);

    return (
        <div className="ProfileConfigContainer">
            <div className="title-container">
                <div className="title-text">{viewMode ? "View Profile Configuration" : location.state?.mode === 'edit' ? "Edit Profile Configuration" : "Add Profile Configuration"}</div>
                <div className="infos-parent">
                    <span className={`${tab === 0 ? "active-tab" : "inactive-tab"}`} onClick={() => { setTab(0); }} > Profile Details </span>
                    <span className={`${tab === 1 ? "active-tab" : "inactive-tab"}`} onClick={() => { setTab(1) }}> Report Parameters </span>
                    <span className={`${tab === 2 ? "active-tab" : "inactive-tab"}`} onClick={() => { setTab(2); }}> Report Settings </span>
                </div>
            </div>
            <div className="ProfileContainer">
                {tab === 0 && (
                    <>
                        <div className="ProfileForm">
                            <div className="profileSectionDetails">
                                <InputField
                                    label="Profile Name"
                                    type="text"
                                    placeholder="Enter Profile Name"
                                    value={reportParaData.profileName}
                                    onChange={(e) => {
                                        handleInputChange("profileName", e.target.value)
                                    }}
                                    name="value"
                                    fieldName="profileName"
                                    required
                                    error={errors?.profileName}
                                />
                                <label className="ReportLabel">Time Takes To Share Report<span className="required">*</span></label>
                                <div className="timeContainer">
                                    <input
                                        type="text"
                                        placeholder="Days"
                                        value={reportParaData.days}
                                        className="dayscontainer"
                                        onChange={(e) => { handleInputChange("days", e.target.value) }}
                                    />
                                    <img src={calenderIcon} alt='calendar' className='ProfileCalender' onClick={() => document.querySelector('.custum-datepicker').focus()} />
                                    <input
                                        type="text"
                                        placeholder="Hours"
                                        value={reportParaData.hours}
                                        className="dayscontainer"
                                    />
                                    <img src={timeIcon} alt='calendar' className='ProfileCalender' onClick={() => document.querySelector('.custum-datepicker').focus()} />
                                    <input
                                        type="text"
                                        placeholder="Minutes"
                                        value={reportParaData.minutes}
                                        className="dayscontainer"
                                    />
                                    <img src={timeIcon} alt='calendar' className='ProfileCalender' onClick={() => document.querySelector('.custum-datepicker').focus()} />
                                </div>
                                <InputField
                                    label="Turnaround Time(Hours)"
                                    type="text"
                                    value={reportParaData.turnaroundTimeHours}
                                    placeholder="Enter Turnaround Time"
                                    name="turnaroundTime"
                                />
                                <DropDown
                                    label="Add Tests To Profile"
                                    placeholder="Select Tests"
                                    multiple={true}
                                    value={reportParaData.tests}
                                    onChange={(selected) => handleInputChange('tests', selected)}
                                    required
                                    error={errors?.tests}
                                />
                            </div>
                            <div className="sectionSource">
                                <div className="form-group-labmanagement alm-radio-buttons status-radio-buttons">
                                    <label className="alm-label">
                                        Out Source<span className="required">*</span>{" "}
                                    </label>
                                    <div className="lm-input-radio-buttons">
                                        <InputField
                                            label={"Active"}
                                            type="radio"
                                            name={"status"}
                                            value={reportParaData.status}

                                        />
                                        <InputField
                                            label={"Inactive"}
                                            type="radio"
                                            name={"status"}
                                            value={false}
                                        />
                                    </div>
                                </div>
                                <InputField
                                    type='text'
                                    label='Turnaround Time(Days)'
                                    value={reportParaData.turnaroundTimeDays}
                                    placeholder='Enter Days'
                                />
                                <InputField
                                    type='text'
                                    label='Turnaround Time(Minutes)'
                                    value={reportParaData.turnaroundTimeMinutes}
                                    placeholder='Enter Minutes'
                                />
                            </div>
                        </div>
                        <div className="ReportText">
                            <InputField
                                type="textarea"
                                label="Profile Description"
                                value={reportParaData.profileDescription}
                                placeholder="Enter Profile Description"
                            />
                            <InputField
                                type="textarea"
                                label="Profile Instructions"
                                value={reportParaData.profileInstructions}
                                placeholder="Enter Profile Instructions"
                            />
                        </div>
                    </>
                )}
                {tab === 1 && (
                    <>
                        <div className="ReportParamConatiner">
                            <span className="reportParam available-time-container">
                                <InputField
                                    type='text'
                                    label='Profile Parameter'
                                    value={reportParaData.testParameter}
                                    placeholder='Enter Profile Parameter'
                                    required
                                    error={errors?.profileParameter}
                                />
                            </span>
                            <span className="reportParam available-time-container">
                                <DropDown
                                    type='text'
                                    label='Result Type'
                                    placeholder="Select Result Type"
                                    value={selectedResultType}
                                    options={resultTypeOptions}
                                    onChange={(option) => {
                                        setSelectedResultType(option.target.value.label);
                                    }}
                                    fieldName={"label"}
                                    required
                                    error={errors?.resultType}
                                />
                            </span>

                        </div>
                        {selectedResultType === "Text" ? (
                            <div className="refrenceResult">
                                <InputField
                                    type='text'
                                    label='Reference Result'
                                    placeholder='Enter Reference Range'
                                    value={reportParaData.referenceRange}
                                    required
                                    error={errors?.referenceRange}
                                />
                                <div className="referenceBtns">
                                    <button className="btn btn-primary">
                                        {mode === 'edit' ? "Update" : "Add"}
                                    </button>
                                    <button className="btn btn-primary">Clear</button>
                                </div>
                            </div>

                        ) : selectedResultType === "Numeric" ? (
                            <span className="refrenceResult">
                                <div className="numericDropdown available-time-container">
                                    <DropDown
                                        label='Numeric Configuration'
                                        options={numericOptions}
                                        placeholder="Select Configuration"
                                        value={selectResultNumeric}
                                        onChange={(option) => {
                                            setSelectedResultNumeric(option.target.value.label);
                                        }}
                                        fieldName={"label"}
                                        required
                                    />
                                </div>
                                {
                                    selectResultNumeric === "Gender" ? (
                                        <div className="genderRange">
                                            {genderOptions.map((gender) => (
                                                <div key={gender} className="genderField">
                                                    <div className="lowField">
                                                        <label className="ReportLabel">By Gender {gender}</label>
                                                        <input
                                                            className="genderRangeInput"
                                                            type="text"
                                                            placeholder={'Enter Low Value '}
                                                        />
                                                    </div>
                                                    <div className="highField">
                                                        <input
                                                            className="genderRangeField"
                                                            type="text"
                                                            placeholder={'Enter High Value '}
                                                        />
                                                    </div>
                                                </div>
                                            ))}
                                            <div className="unitField available-time-container">
                                                <InputField
                                                    type='text'
                                                    label='Units'
                                                    placeholder='Enter Units'
                                                    required
                                                    error={errors?.units}
                                                />
                                            </div>
                                        </div>

                                    ) : selectResultNumeric === "Age" ? (
                                        <div className="ageRangeGroup">
                                            {genderOptions.map((gender) => (
                                                <div key={gender} className="ageRangeField">
                                                    <div className="groupFields">
                                                        <DropDown
                                                            label="Age Group"
                                                            options={ageGroupOptions}
                                                            placeholder={`Select ${gender}`}
                                                            fieldName="label"
                                                            required
                                                            error={errors?.ageGroup}
                                                        />
                                                        <InputField
                                                            type="text"
                                                            placeholder={`Enter Low Value for ${gender}`}
                                                        />
                                                    </div>
                                                    <div className="groupFields">
                                                        <div className="groupAgeFields ">
                                                            <DropDown
                                                                label="Age Range"
                                                                options={ageRangeOptions}
                                                                placeholder="Year"
                                                                fieldName="label"
                                                                required
                                                                error={errors?.ageRange}
                                                            />
                                                            <InputField
                                                                type="text"
                                                                placeholder={`From `}
                                                            />
                                                            <DropDown
                                                                options={ageRangeOptions}
                                                                placeholder="Year"
                                                                fieldName="label"
                                                            />
                                                            <InputField
                                                                type="text"
                                                                placeholder={`To`}
                                                            />
                                                        </div>
                                                        <InputField
                                                            type="text"
                                                            placeholder={`Enter High Value `}
                                                        />
                                                    </div>
                                                </div>
                                            ))}
                                            <div className="unitField available-time-container" >
                                                <InputField
                                                    type='text'
                                                    label='Units'
                                                    placeholder='Enter Units'
                                                    required
                                                    error={errors?.units}
                                                />
                                            </div>
                                        </div>
                                    ) : selectResultNumeric === "Range" ? (
                                        <div className="ageRangeGroup">
                                            <div className="groupAgeFields">
                                                <div className="available-time-container">
                                                    <InputField
                                                        type='text'
                                                        label='Reference Value-Low'
                                                        placeholder={'Enter Low Value '}
                                                        required
                                                        error={errors?.lowValue}
                                                    />
                                                </div>
                                                <div className="available-time-container">
                                                    <InputField
                                                        type='text'
                                                        placeholder={'Enter High Value '}
                                                        label='Reference Value-High'
                                                        required
                                                        error={errors?.highValue}
                                                    />
                                                </div>
                                            </div>
                                            <div className="unitField available-time-container">
                                                <InputField
                                                    type='text'
                                                    label='Units'
                                                    placeholder='Enter Units'
                                                    required
                                                    error={errors?.units}
                                                />
                                            </div>
                                        </div>
                                    ) : null
                                }
                                <div className="referenceBtns">
                                    <button className="btn btn-primary">
                                        {mode === 'edit' ? "Update" : "Add"}
                                    </button>
                                    <button className="btn btn-primary">Clear</button>
                                </div>
                            </span>
                        ) : null}
                    </>
                )}
                {tab === 2 && (
                    <>
                        <div className="ReportSettingsConatiner">
                            <div className="groupFields">
                                <div className="available-time-container">
                                    <InputField type='text' label='Setting Name' placeholder='Enter Setting' required error={errors?.settingName} />
                                </div>
                                <div className="available-time-container">
                                    <DropDown label='Patient Info' placeholder="Select Patient Info" multiple={true} required error={errors?.patientInfo} />
                                </div>
                                <div className="available-time-container">
                                    <DropDown label='Font Size' placeholder="Select Patient Info" />
                                </div>
                                <div className="available-time-container">
                                    <DropDown label='Footer Size' placeholder="Select Patient Info" />
                                </div>
                                <div className="available-time-container">
                                    <DropDown label='Sign Size' placeholder="Select Patient Info" />
                                </div>
                            </div>
                            <div className="groupFields">
                                <div className="available-time-container">
                                    <DropDown label='Paper Size' placeholder="Select Paper Size" required error={errors?.paperSize} />
                                </div>
                                <div className="available-time-container">
                                    <DropDown label='Font Type' placeholder="Select Patient Info" />
                                </div>
                                <div className="available-time-container">
                                    <DropDown label='Header Size' placeholder="Select Patient Info" />
                                </div>
                                <div className="available-time-container">
                                    <DropDown label='Sign Position' placeholder="Select Patient Info" required error={errors?.signPosition} />
                                </div>
                                <div className="available-time-container dateFormatContainer">
                                    <InputField type='date' label='Date Format' placeholder='dd-mm-yyyy' required error={errors?.dateFormat} />
                                </div>
                            </div>
                        </div>
                        <div className="ReportText">
                            <InputField type='textarea' label='End Of Report Text' placeholder='Enter Report Text' />
                            <InputField type='file' label='Water Mark' />
                        </div>
                        <div className="profileCheckBox">
                            <label className="ReportLabel">Including PDF Header/Footer</label>
                            <Checkbox
                                borderColor="#fb8500"
                                checked={includePDFHeaderFooter}
                                onChange={(value) => setIncludePDFHeaderFooter(value)}
                            />
                        </div>
                    </>
                )}
            </div>
            {(tab === 0 || (tab !== 2 && (selectedResultType === 'Text' || selectResultNumeric === 'Range' || selectResultNumeric === 'Age' || selectResultNumeric === 'Gender'))) && (
                <div className="ProfileTable">
                    <LimsTable
                        columns={columns}
                        data={data}
                        actionIcons={["edit", "delete"]}
                    />
                </div>
            )}
            {tab === 1 && (
                <>
                    <div className="ReportText">
                        <InputField
                            type='textarea'
                            label='Remarks'
                            placeholder='Enter Remarks'
                        />
                    </div>
                </>
            )}

            <div className="form-btns">
                <button className="btn btn-secondary" onClick={() => { if (tab > 0) setTab(tab - 1); else navigate(navigateUrl); }}>Back</button>
                {mode !== 'view' && (
                    <button className="btn-primary" type="submit" onClick={() => { handleSave(); if (tab < 2) setTab(tab + 1); }} >
                        {mode === 'edit' ? (tab === 2 ? 'Update' : 'Update & Next') : (tab === 2 ? 'Save' : 'Continue')}
                    </button>
                )}
            </div>
            {popupConfig && <Swal {...popupConfig} />}
        </div>
    );
}
export default AddprofileConfiguration;

