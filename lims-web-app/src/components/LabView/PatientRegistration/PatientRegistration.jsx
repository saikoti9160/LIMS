import React from 'react'

import './PatientRegistration.css'
import ImageCard from '../../Re-usable-components/ImageCard'
import logo1 from "../../../assets/images/price-list-logo-one.svg"
import logo2 from "../../../assets/images/price-list-logo-two.svg"
import image1 from "../../../assets/images/Patient-photo-one.svg";
import image2 from "../../../assets/images/Patient-photo-two.svg";

import { useNavigate } from 'react-router-dom';
const PatientRegistration = () => {
    const navigate = useNavigate();

    const handleNewPatient  =   () => {
        navigate("/lab-view/newPatient")
    }
    const handleExistingPatient = ()=>{
        navigate("/lab-view/existing-patient")
    }
    return ( 
        <div className='parent-registration'>

            <span className='title'>
                Patient Registration
            </span>
            <div className='price-list-image-container'>
            <div className='price-list-first-image'>
            <ImageCard
                backgroundImage={image1}
                logoSrc={logo1}
                serviceName="New Patient"
                onArrowClick={handleNewPatient}
            />
            </div>
            <div className='price-list-second-image'>
            <ImageCard
                backgroundImage={image2}
                logoSrc={logo2}
                serviceName="Existing Patient"
                onArrowClick={handleExistingPatient}
            />
            </div>
            </div>
        </div>
    )
}

export default PatientRegistration;
