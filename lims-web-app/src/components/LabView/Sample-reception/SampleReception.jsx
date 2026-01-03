import React, { useState } from 'react'
import "./SampleReception.css"
import DropDown from "../../Re-usable-components/DropDown"
import LimsTable from "../../LimsTable/LimsTable"
import Swal from "../../Re-usable-components/Swal"
import { set } from 'date-fns'
const SampleReception = () => {
    const [popup,setPopup]=useState(false)
    const [buttonText,setButtonText]=useState("Receive Sample")
    const columns = [
        { key: 'slNo', label: 'S. No.', width: '80px', align: 'center' },
        { key: 'patientId', label: 'Patient ID', width: '120px', align: 'center' },
        { key: 'name', label: 'Name', width: '150px', align: 'center' },
        { key: 'phoneNumber', label: 'Phone Number', width: '150px', align: 'center' },
        { key: 'gender', label: 'Gender', width: '100px', align: 'center' },
        { key: 'referral', label: 'Referral', width: '150px', align: 'center' },
        { key: 'organisation', label: 'Organisation', width: '180px', align: 'center' },
        { key: 'buttonAction', label: 'Action', width: '120px', height: '40px', align: 'center' }
    ];

    const [data,setdata]=useState([
        {
            "slNo": 1,
            "patientId": "BR-001",
            "name": "Krishna",
            "phoneNumber": "9446488855",
            "gender": "Male",
            "referral": "Dr. Lili",
            "buttonText": "Receive Sample",
            "organisation": "ABC Corp",
        },
        {
            "slNo": 2,
            "patientId": "BR-002",
            "name": "Arjun",
            "phoneNumber": "9876543210",
            "gender": "Male",
            "referral": "Dr. Meera",
            "buttonText": "Receive Sample",
            "organisation": "XYZ Labs",
        }
    ])
    const handleButton = (row) => {
        const updatedData = data.map(item => {
            if (item.patientId === row.patientId) {
                return {
                    ...item,
                    buttonText: "Received Sample"
                };
            }
            return item;
        });
        setdata(updatedData);
        setPopup({
            icon: "success",
            title: "Sample Received Successfully",
            onClose: () => {setPopup(false)},
        });
    }
    
    return (
        <div className='sample-reception'>
            <div className='title'>Sample Reception</div>
            <div className='sample-receiption-search'>
                <div className='sample-receiption-search-one'>
                    <input type="text" placeholder='Search By Patient Id/Name/Phone No/Bill ID/Referral/Organisation' className='sample-reception-input'/>
                </div>
                <div className='sample-receiption-search-two'>
                    <div className='sample-receiption-dropdown'>
                        <DropDown 
                        placeholder='Processed From'
                        />
                    </div>
                    <div className='sample-receiption-dropdown'>
                        <DropDown 
                        placeholder='Processed To'
                        />
                    </div>
                </div>
            </div>
            <div className='sample-reception-table'>
                <LimsTable
                columns={columns}
                data={data}
                onButtonAction={handleButton}
                buttonTextFromKey={"buttonText"}
                />
            </div>
        {popup && <Swal {...popup}/>}
        </div>
    )
} 

export default SampleReception