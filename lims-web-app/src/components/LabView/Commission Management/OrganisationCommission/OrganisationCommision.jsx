import React, { useState } from 'react'
import LimsTable from '../../../LimsTable/LimsTable'
import "./OrganisationCommission.css"
import { useNavigate } from 'react-router-dom'
import Swal from "../../../Re-usable-components/Swal";

const OrganisationCommision = () => {
  const navigate=useNavigate()
  const [popup,setPopup]=useState(null)
  const columns = [
    { key: 'slNo', label: 'Sl. No.', width: '80px', align: 'center' },
    { key: 'Organisation', label: 'Organisation', width: '120px', align: 'center' },
    { key: 'OrganisationId', label: 'Organisation ID', width: '150px', align: 'center' },
    { key: 'action', label: 'Action', width: '120px', height: '40px', align: 'center' }
];

  const OrganisationData = [
    {
      "Organisation": "ABC Labs",
      "OrganisationId": "ORG123"
    },
    {
      "Organisation": "XYZ Diagnostics",
      "OrganisationId": "ORG456"
    }
  ]

  const handleAdd=()=>{
    navigate("/lab-view/organisation-commission/add");
  }
  const handleView=()=>{
    let details;
    navigate("/lab-view/organisation-commission/add",{
      state:{
        mode : "view",
        details
      }
    });
  }
  const handleEdit=()=>{
    let details;
    navigate("/lab-view/organisation-commission/add",{
      state:{
        mode : "edit",
        details
      }
    });
  }
  const handleDelete=()=>{
   setPopup({
    icon:"delete",
    title:"are you sure ?",
    isButton : true,
    buttonText:"Delete",
    onClose : () => { setPopup(null) },
    onButtonClick:handleDeleteConfirm
   })
  }
  const handleDeleteConfirm=()=>{
    setPopup({
      icon:"success",
      title:"Deleted Successfully",
      onClose : () => { setPopup(null) },
     })
  };
  return (
    <div className='OrganisationCommisionMapping'>
        <div className='title'>Organisation Commission Mapping</div>
        <div className='OCM-Table'>
            <LimsTable 
            columns={columns}
            data={OrganisationData}
            showSearch
            showAddButton
            onAdd={handleAdd}
            onView={handleView}
            onEdit={handleEdit}
            onDelete={handleDelete}
            />
        </div>
        {popup && <Swal {...popup} />}
    </div>
  )
}

export default OrganisationCommision