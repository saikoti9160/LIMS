import React, { useState } from 'react'
import './AddAppointment.css'
import InputField from '../../Homepage/InputField'
import DropDown from '../../Re-usable-components/DropDown'
import Doctor from '../Doctor-master/Doctor'
import closeIcon from '../../../assets/icons/add-close.svg';
import alert from '../../../assets/icons/alert.svg'
import { useNavigate } from 'react-router-dom'
import Swal from '../../Re-usable-components/Swal'
const AddAppointment = () => {
    const [appointmentDetails, setAppointmentDetails] = useState({
        name: '',
        email: '',
        dob: '',
        country: '',
        city: '',
        address: '',
        referralDoctor: '',
        phoneCode: '',
        phoneNumber: '',
        gender: [],
        age: '',
        state: '',
        pincode: '',
        relation: '',
        organization: '',
        doctor: '',
        date: '',
        booking: '',
        timeSlot: ''
    });
    const [referralDetails, setReferralDetails] = useState({
        refferalName: '',
        email: '',
        phoneNumber: '',
        dateOfBirth: ''
    });
    const [isRefferalModalOpen, setRefferalIsModalOpen] = useState(false);
    const genderOptions = [
        { value: 'male', label: 'Male' },
        { value: 'female', label: 'Female' },
        { value: 'other', label: 'Other' },
    ]
    const navigate = useNavigate();
    const [popupConfig, setPopupConfig] = useState(null);
    const handleInputChange = (event) => {
        const { name, value } = event.target;
        console.log("name", name, "value", value);
        setAppointmentDetails((prevDetails) => ({
            ...prevDetails,
            [name]: value,
        }));
    };
    const handlePhoneChange = (e) => {
        setAppointmentDetails(prev => ({
            ...prev,
            phoneNumber: e.phoneNumber,
            phoneCode: e.countryCode
        }));
    };
    const handleRefferalPhoneChange = (e) => {
        setReferralDetails(prev => ({
            ...prev,
            phoneNumber: e.phoneNumber,
            phoneCode: e.countryCode
        }))

    }
    const handleGenderChange = (e) => {
        console.log("e", e)
        setAppointmentDetails(prev => ({
            ...prev,
            gender: e.target.value
        }));
    };
    const handleInputRefferalChange = (e) => {
        const { name, value } = e.target
        setReferralDetails(prev => ({
            ...prev,
            [name]: value
        }));
    }
    const handleClose=()=>{
        console.log("inslhdfb")
        setPopupConfig(null)
        // navigate('/lab-view/appointment-management/add')
    }
    const handleScheduleButton=()=>{
        setPopupConfig({
            icon: 'success',
            title: 'Appointment Scheduled successfully',
            isButton:false,
            onClose : handleClose,
        });
    }
    const handleConfirmButton=()=>{
        setPopupConfig({
            icon: 'success',
            title: 'Appointment Confirmed successfully',
            isButton:false,
            onClose : handleClose,
        });

    }
    const handleScheduleAppointment = () => {
        setPopupConfig({
            icon: 'alert',
            title: 'Are you sure want to schedule an Appointment?',
            onClose : handleClose,
            buttonText:'Schedule',
            isButton:true,
            onButtonClick: handleScheduleButton 
        });
    }
    const handleConfirmAppointment=()=>{
        setPopupConfig({
            icon: 'alert',
            title: 'Are you sure want to Conform an Appointment?',
            isButton:true,
            onClose: handleClose,
            buttonText:'Confirm',
            onButtonClick:handleConfirmButton
        });

    }


    return (
        <div className='appointment-scheduling-container'>
            <div className='appointment-scheduling-group-1'>
                <span className='appointment-scheduling-header'>Appointment</span>
            </div>
            <div className='appointment-scheduling-group-2'>
                <div className='appointment-scheduling-child-1'>
                    <div className='appointment-scheduling-child'>
                        <span className='appointment-scheduling-child-header'>Patient Details</span>
                    </div>
                    <div className='appointment-scheduling-child-container'>
                        <div className='appointment-scheduling-container-1'>
                            <div>
                                <InputField
                                    label="Name"
                                    type="text"
                                    name={"name"}
                                    value={appointmentDetails.name}
                                    placeholder="Enter Name"
                                    onChange={handleInputChange} />
                            </div>
                            <div>
                                <InputField
                                    label="Email"
                                    type="text"
                                    name={"email"}
                                    value={appointmentDetails.email}
                                    placeholder="Enter email"
                                    onChange={handleInputChange} />
                            </div>
                            <div>
                                <InputField
                                    label="Date of Birth"
                                    type="date"
                                    name={"dob"}
                                    value={appointmentDetails.dob}
                                    placeholder="Enter Name"
                                    onChange={handleInputChange} />
                            </div>

                            <div>
                                <DropDown
                                    label="Country"
                                    placeholder="Select "
                                    name={"country"}
                                    value={appointmentDetails.country}
                                    onChange={handleInputChange} />
                            </div>
                            <div>
                                <DropDown
                                    label="City"
                                    name={"city"}
                                    value={appointmentDetails.city}
                                    placeholder="Select " />
                            </div>
                            <div>
                                <InputField
                                    label="Address"
                                    type="textarea"
                                    name={"address"}
                                    value={appointmentDetails.address}
                                    placeholder="Enter address"
                                    onChange={handleInputChange} />
                            </div>
                            <div>
                                <DropDown
                                    label="Referral Doctor"
                                    name={"referralDoctor"}
                                    value={appointmentDetails.referralDoctor}
                                    placeholder="Select" />
                                <div className='add-referral-tag-container'>
                                    <a href="#" className='add-referral-tag' onClick={() => setRefferalIsModalOpen(true)}>Add referral</a></div>
                                {isRefferalModalOpen && (
                                    <div className="refferal-overlay">
                                        <div className='refferal-model'>
                                            <div className='refferal-model-header'>
                                                <span className='header'>Add Referral</span>
                                                <img src={closeIcon} style={{ cursor: 'pointer' }} alt="close" onClick={() => setRefferalIsModalOpen(false)} />
                                            </div>
                                            <div className='refferal-model-content'>
                                                <div className='appointment-refferal-fields'>
                                                    <InputField label={"Refferal Name"} name={"refferalName"} value={referralDetails.refferalName} type={"text"} placeholder={"Refferal Name"} onChange={handleInputRefferalChange} />
                                                </div>

                                                <div className='appointment-refferal-fields'>
                                                    <InputField label={"Email"} name={"email"} type={"text"} value={referralDetails.email} placeholder={"Enter email"} onChange={handleInputRefferalChange} />
                                                </div>

                                                <div className='appointment-refferal-fields'>
                                                    <InputField label={"Phone Number"} name={"phoneNumber"} value={referralDetails.phoneNumber} type={"phone"} onChange={handleRefferalPhoneChange} />
                                                </div>

                                                <div className='appointment-refferal-fields'>
                                                    <InputField label="Date of Birth" type="date" name={"dateOfBirth"} value={referralDetails.dateOfBirth} placeholder="Enter Name" onChange={handleInputRefferalChange} />
                                                </div>
                                            </div>
                                            <div className='refferal-model-footer'><button className='btn-primary'>Save</button></div>
                                        </div>
                                    </div>
                                )}
                            </div>
                        </div>
                        <div className='appointment-scheduling-container-2'>
                            <div>
                                <InputField
                                    label="Phone Number"
                                    type="phone"
                                    name={"phoneNumber"}
                                    value={appointmentDetails.phoneNumber}
                                    placeholder="Enter Phone Number"
                                    onChange={handlePhoneChange} />
                            </div>
                            <div>
                                <DropDown
                                    label="Gender"
                                    options={genderOptions}
                                    name={"gender"}
                                    value={genderOptions.find(option => option.value === appointmentDetails.gender) || null}
                                    placeholder="Select"
                                    onChange={(e) => handleGenderChange(e)} />
                            </div>
                            <div>
                                <InputField
                                    label="Age"
                                    type="number"
                                    name={"age"}
                                    value={appointmentDetails.age}
                                    placeholder="Enter age"
                                    onChange={handleInputChange} />
                            </div>
                            <div>
                                <DropDown
                                    label="State"
                                    name={"state"}
                                    value={appointmentDetails.state}
                                    placeholder="Select " />
                            </div>
                            <div>
                                <InputField
                                    label="Pincode"
                                    type="number"
                                    name={"pincode"}
                                    value={appointmentDetails.pincode}
                                    placeholder="Enter pin"
                                    onChange={handleInputChange} />
                            </div>
                            <div>
                                <DropDown
                                    label="Relation"
                                    placeholder="Select"
                                    name={"relation"}
                                    value={appointmentDetails.relation}
                                    onChange={handleInputChange} />
                            </div>
                            <div>
                                <DropDown
                                    label="Organization"
                                    name={"organization"}
                                    value={appointmentDetails.organization}
                                    placeholder="Select" />
                            </div>
                        </div>
                    </div>
                </div>
                <div className='appointment-scheduling-child-2'>
                    <div className='apppointment-details-1'>
                        <span className='appointment-details-header'>Apppointment Details</span>
                    </div>
                    <div className='apppointment-details-2'>
                        <div className='apppointment-details-child-1'>
                            <div>
                                <DropDown
                                    label="Doctor"
                                    placeholder="Select"
                                    name={"doctor"}
                                    value={appointmentDetails.doctor} />
                            </div>
                            <div>
                                <InputField
                                    label="Date"
                                    type="date"
                                    name={"date"}
                                    value={appointmentDetails.date}
                                    placeholder="Enter Name"
                                    onChange={handleInputChange} />
                            </div>
                        </div>
                        <div className='apppointment-details-child-2'>
                            <div>
                                <DropDown
                                    label="Booking"
                                    name={"booking"}
                                    value={appointmentDetails.booking}
                                    placeholder="Select" />
                            </div>
                            <div>
                                <DropDown
                                    label="Time-slot"
                                    name={"timeSlot"}
                                    value={appointmentDetails.timeSlot}
                                    placeholder="Select" />

                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div className='appointmrnent-scheduling-group-3'>
                <div className='appointmrnent-scheduling-btns'>
                    <button className='btn-secondary' onClick={handleScheduleAppointment}>Schedule Appointment</button>
                    <button className='btn-primary' onClick={handleConfirmAppointment}>Confirm Apppointment</button>
                </div>

            </div>
            {popupConfig && (
        <Swal
         {...popupConfig}
         
        />
      )}
        </div>
    )
}

export default AddAppointment
