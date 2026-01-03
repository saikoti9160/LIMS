import React, { useEffect, useState } from "react";
import LimsTable from "../../LimsTable/LimsTable";
import "./TestConfiguration.css";
import { useLocation, useNavigate } from "react-router-dom";
import InputField from "../../Homepage/InputField";
import calenderIcon from "../../../assets/icons/Vector.svg";
import DropDown from "../../Re-usable-components/DropDown";
import timeIcon from "../../../assets/icons/timeIcon.svg";
import Swal from "../../Re-usable-components/Swal";
import DatePicker from "react-datepicker";
import Checkbox from "../../Re-usable-components/Checkbox";

function AddEditTestConfiguration() {
    const [viewMode, setViewMode] = useState(false);
    const [tab, setTab] = useState(0);
    const location = useLocation();
    const [mode, setMode] = useState('');
    const [popupConfig, setPopupConfig] = useState(null);
    const navigate = useNavigate();
    const [selectedResultType, setSelectedResultType] = useState("");
    const [selectResultNumeric, setSelectedResultNumeric] = useState('');

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
                        { 
                            key: 'testResult', 
                            label: 'Test Result', 
                            width: '20%',
                            editable: true, 
                            placeholder: 'Enter Here'
                          },
                        { key: 'unit', label: 'Unit', width: '1fr', align: 'center' },
                        { key: 'male', label: 'Male', width: '1fr', align: 'center' },
                        { key: 'female', label: 'Female', width: '1fr', align: 'center' },
                        { key: 'child', label: 'Child', width: '1fr', align: 'center' }
                    ]
                    : selectResultNumeric === 'Range' ?
                        [
                            { key: 'testParameter', label: 'Test Parameter', width: '1fr', align: 'center' },
                            { 
                                key: 'testResult', 
                                label: 'Test Result', 
                                width: '20%',
                                editable: true, 
                                placeholder: 'Enter Here'
                              },
                            { key: 'unit', label: 'Unit', width: '1fr', align: 'center' },
                            { key: 'referenceValue', label: 'Reference Value', width: '180px', align: 'center' }
                        ]
                        : [
                            { key: 'testParameter', label: 'Test Parameter', width: '1fr', align: 'center' },
                            { 
                                key: 'testResult', 
                                label: 'Test Result', 
                                width: '20%',
                                editable: true, 
                                placeholder: 'Enter Here'
                              },
                            { key: 'referenceResult', label: 'Reference Result', width: '250px', align: 'center' }
                        ])
            ]
        ),

        { key: 'action', label: 'Action', width: '160px', align: 'center' }
    ];

    const [reportParaData, setReportParaData] = useState({
        testName: "",
        price: "",
        testParameter: "",
        testResult: "",
        referenceResult: ""
    }
    );
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


    const handleBack = () => {
        navigate("/test-configuration");
    }
    const handleSave = () => {
        setPopupConfig({
            icon: "success",
            title: "Added Successfully",
            onClose: () => {
                setPopupConfig(null);
                // navigate("/lab-view/profile-configuration");
            }
        })
    }

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

    }
    const handleDelete = () => {

    }
    const [includePDFHeaderFooter, setIncludePDFHeaderFooter] = useState(false);

    useEffect(() => {
        const mode = location.state?.mode;
        setMode(mode);
        if (mode === "view") {
            setViewMode(true);
        }
    })
    return (
        <div className="testconfig-container">
            <div className="title-container">
                <div className="title-text">{viewMode ? "View Test Configuration" : location.state?.mode === 'edit' ? "Edit Test Configuration" : "Add Test Configuration"}</div>
                <div className="infos-parent">
                    <span className={`${tab === 0 ? "active-tab" : "inactive-tab"}`} onClick={() => { setTab(0); }} > Test Details </span>
                    <span className={`${tab === 1 ? "active-tab" : "inactive-tab"}`} onClick={() => { setTab(1) }}> Report Parameters </span>
                    <span className={`${tab === 2 ? "active-tab" : "inactive-tab"}`} onClick={() => { setTab(2); }}> Report Settings </span>
                </div>
            </div>
            <div className="ProfileContainer">
                {tab === 0 && (
                    <div>
                        <div className="test-form">
                            <div className="test-column">
                                <DropDown
                                    label="Test Name"
                                    type="text"
                                    placeholder="Select"
                                    name="testName"
                                    required
                                />

                                <InputField
                                    label="Sample Types"
                                    type="text"
                                    placeholder="Auto flow"
                                    name="sampleTypes"
                                    required
                                    // width={"76.5%"}
                                    
                                />

                                <InputField
                                    label="Test Price"
                                    type="text"
                                    placeholder="Enter Here"
                                    name="testPrice"
                                    required
                                    // width={"76.5%"}
                                />
                                <div className="test-outsource alm-radio-buttons status-radio-buttons">
                                    <label className="alm-label">
                                        Out Source<span className="required">*</span>{" "}
                                    </label>
                                    <div className="test-input-radio-buttons">
                                        <InputField
                                            label={"Yes"}
                                            type="radio"
                                            name={"status"}
                                            value={true}

                                        />
                                        <InputField
                                            label={"No"}
                                            type="radio"
                                            name={"status"}
                                            value={false}
                                        />
                                    </div>
                                    </div>
                                
                                    <InputField
                                    type='text'
                                    label='Turnaround Time(Hours)'
                                    placeholder='Enter Here'
                                    name="turnaroundHours"
                                />
                            </div>
                            <div className="test-column">
                                <DropDown
                                    label="Sample Name"
                                    type="text"
                                    placeholder="Select"
                                    name="sampleName"
                                    required
                                />
                                <div className="time-div">
                                 <label className="ReportLabel">Time Takes To Share Report<span className="required">*</span></label>
                                <div className="timeContainer-div">
                                    <input
                                        type="text"
                                        placeholder="Days"
                                        className="dayscontainer"
                                    />
                                    <img src={calenderIcon} alt='calendar' className='icons-size' onClick={() => document.querySelector('.custum-datepicker').focus()} />
                                    <input
                                        type="text"
                                        placeholder="Hours"
                                        className="dayscontainer"
                                    />
                                    <img src={timeIcon} alt='calendar' className='icons-size' onClick={() => document.querySelector('.custum-datepicker').focus()} />
                                    <input
                                        type="text"
                                        placeholder="Minutes"
                                        className="dayscontainer"
                                    />
                                    <img src={timeIcon} alt='calendar' className='icons-size' onClick={() => document.querySelector('.custum-datepicker').focus()} />
                                    </div>
                                </div> 

                                <DropDown
                                    label="Department"
                                    type="text"
                                    placeholder="Select"
                                    name="department"
                                    required
                                    // width={"46.5%"}
                                />

                                <InputField
                                    label="Turnaround Time(Days)"
                                    type="text"
                                    placeholder="Enter Here"
                                    name="turnaroundDays"
                                />

                                <InputField
                                    type='text'
                                    label='Turnaround Time(Minutes)'
                                    placeholder='Enter Here'
                                    name="turnaroundMinutes"
                                />
                            </div>
                        </div>
                        
                        <div className="test-remarks">
                            <InputField
                                type="textarea"
                                label="Test Description"
                                placeholder="Enter Here"
                                name="testDescription"
                            />
                            <InputField
                                type="textarea"
                                label="Test Instructions"
                                placeholder="Enter Here"
                                name="testInstruction"
                            />
                        </div>
                    </div>
                    
                )}
                {tab === 1 && (
                    <>
                        <div className="ReportParamConatiner">
                            <span className="reportParam">
                                <InputField
                                    type='text'
                                    label='Test Parameter'
                                    placeholder='Enter Here'
                                    required
                                />
                            </span>
                            <span className="reportParam">
                                <DropDown
                                    type='text'
                                    label='Result Type'
                                    placeholder='Select'
                                    value={selectedResultType}
                                    options={resultTypeOptions}
                                    required
                                    onChange={(option) => {
                                        setSelectedResultType(option.target.value.label);
                                    }}
                                    fieldName={"label"}
                                />
                            </span>

                        </div>
                        {selectedResultType === "Text" ? (
                            <div className="refrence-result">
                                <InputField
                                    type='text'
                                    label='Reference Result'
                                    placeholder='Enter Here'
                                    required
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
                                <div className="numericDropdown">
                                    <DropDown
                                        label='Numeric Configuration'
                                        options={numericOptions}
                                        placeholder="Select"
                                        required
                                        value={selectResultNumeric}
                                        onChange={(option) => {
                                            setSelectedResultNumeric(option.target.value.label);
                                        }}
                                        fieldName={"label"}
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
                                            <div className="unitField">
                                                <InputField
                                                    type='text'
                                                    label='Units'
                                                    placeholder='Enter Here'
                                                    required
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
                                                            required
                                                            fieldName="label"
                                                        />
                                                        <InputField
                                                            type="text"
                                                            placeholder={`Enter Low Value for ${gender}`}
                                                        />
                                                    </div>
                                                    <div className="groupFields">
                                                        <div className="groupAgeFields">
                                                            <DropDown
                                                                label="Age Range"
                                                                options={ageRangeOptions}
                                                                required
                                                                placeholder="Year"
                                                                fieldName="label"
                                                            />
                                                            <InputField
                                                                type="text"
                                                                placeholder={`From `}
                                                            />
                                                            <DropDown
                                                                options={ageRangeOptions}
                                                                required
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
                                            <div className="unitField">
                                                <InputField
                                                    type='text'
                                                    label='Units'
                                                    placeholder='Enter Here'
                                                    required
                                                />
                                            </div>
                                        </div>
                                    ) : selectResultNumeric === "Range" ? (
                                        <div className="ageRangeGroup">
                                            <div className="groupAgeFields">
                                                <InputField
                                                    type='text'
                                                    label='Reference Value-Low'
                                                    placeholder={'Enter Low Value '}
                                                    required
                                                />
                                                <InputField
                                                    type='text'
                                                    placeholder={'Enter High Value '}
                                                    label='Reference Value-High'
                                                    required
                                                />
                                            </div>
                                            <div className="unitField">
                                                <InputField
                                                    type='text'
                                                    label='Units'
                                                    placeholder='Enter Here'
                                                    required
                                                />
                                            </div>
                                        </div>
                                    ) : null
                                }
                                <div className="referenceBtns">
                                    <button className="btn btn-primary">
                                        {mode==='edit'?"Update":"Add"}
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
                                <InputField type='text' label='Setting Name' placeholder='Enter Setting' required />
                                <DropDown label='Patient Info' placeholder="Select Patient Info" multiple={true} required />
                                <DropDown label='Font Size' placeholder="Select Patient Info" />
                                <DropDown label='Footer Size' placeholder="Select Patient Info" />
                                <DropDown label='Sign Size' placeholder="Select Patient Info" />
                            </div>
                            <div className="groupFields">
                                <DropDown label='Paper Size' placeholder="Select Paper Size" required />
                                <DropDown label='Font Type' placeholder="Select Patient Info" />
                                <DropDown label='Header Size' placeholder="Select Patient Info" />
                                <DropDown label='Sign Position' placeholder="Select Patient Info" required />
                                <div className='dateFormatContainer'>
                                    <label className="ReportLabel">Date Format<sapn className='required'>*</sapn></label>
                                    <DatePicker
                                        //    selected={startDate}
                                        //    onChange={(update) => {
                                        //        setDateRange(update);
                                        //        handleFilterChange("startDate", update?.[0] || "");
                                        //        handleFilterChange("endDate", update?.[1] || "");
                                        //    }}
                                        //    startDate={startDate}
                                        //    endDate={endDate}                                                    
                                        selectsRange
                                        dateFormat="MM/dd/yyyy"
                                        placeholderText="dd-mm-yyyy"
                                        className="profile-datePicker"
                                    />
                                    <img src={calenderIcon} alt='calendar' className='profileCalender' onClick={() => document.querySelector('.profile-datePicker').focus()} />
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
            {(tab !== 2 && tab !== 0 && (selectedResultType === "Text" || selectResultNumeric !== '')) && (
                <div className="ProfileTable">
                    <LimsTable
                        columns={columns}
                        data={data}
                        actionIcons={["edit", "delete"]} 
                    />
                </div>
            )}
            {tab === 1 && (selectedResultType === "Text" || selectResultNumeric !== '') && (
                <>
                    <div className="ReportText">
                        <InputField
                            type='textarea'
                            label='Remarks'
                            placeholder='Enter Here'
                        />
                    </div>
                </>
            )}

            <div className="form-btns">
                <button className="btn btn-secondary" onClick={handleBack}>Back</button>
                {mode !== 'view' && (
                    <button className="btn-primary" type="submit" onClick={handleSave}>
                        {mode === 'edit' ? (tab === 2 ? 'Update' : 'Update & Next') : (tab === 2 ? 'Save' : 'Save & Next')}
                    </button>
                )}
            </div>
            {popupConfig && <Swal {...popupConfig} />}
        </div>
    );
}
export default AddEditTestConfiguration;