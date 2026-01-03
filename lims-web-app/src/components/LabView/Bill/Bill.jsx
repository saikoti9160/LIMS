import React, { useState } from 'react'
import InputField from '../../Homepage/InputField'
import LimsTable from '../../LimsTable/LimsTable';
import { useNavigate } from 'react-router-dom';
import './AddBill.css'

const Bill = () => {

  const navigate = useNavigate();

  const columns = [
    { key: 'slNo', label: 'Sl. No.', width: '50px', align: 'center' },
    { key: 'patientId', label: 'Patient ID', width: '1fr', align: 'center' },
    { key: 'name', label: 'Name', width: '1fr', align: 'center' },
    { key: 'phoneNumber', label: 'Phone Number', width: '1fr', align: 'center' },
    { key: 'dateOfBirth', label: 'Date Of Birth', width: '116px', align: 'center' },
    { key: 'age', label: 'Age', width: '1fr', align: 'center' },
    { key: 'referral', label: 'Referral', width: '1fr', align: 'center' },
    { key: 'organization', label: 'Organization', width: '1fr', align: 'center' },
    { key: 'branch', label: 'Branch', width: '1fr', align: 'center' },
    { key: 'registrationDate', label: 'Registration Date', width: '1fr', align: 'center' },
    { key: 'buttonAction', label: 'Action', width: '116px', align: 'center' }
  ];

  const data = [
    {
      slNo: 1,
      patientId: 'P12345',
      name: 'John Doe',
      phoneNumber: '9876543210',
      dateOfBirth: '1990-05-12',
      age: 35,
      referral: 'Dr. Smith',
      organization: 'ABC Corp',
      branch: 'Branch 1',
      registrationDate: '2023-11-12',
      buttonAction: '',
    },
    {
      slNo: 2,
      patientId: 'P67890',
      name: 'Jane Smith',
      phoneNumber: '8765432109',
      dateOfBirth: '1985-07-25',
      age: 40,
      referral: 'Dr. Brown',
      organization: 'FDS Corp',
      branch: 'Branch 2',
      registrationDate: '2023-11-10',
      buttonAction: 'P67890'

    },
    {
      slNo: 3,
      patientId: 'P11223',
      name: 'Michael Johnson',
      phoneNumber: '7654321098',
      dateOfBirth: '1995-01-15',
      age: 30,
      referral: 'Dr. Lee',
      organization: 'WellCare',
      branch: 'Branch 3',
      registrationDate: '2023-11-08',
      buttonAction: 'P11223'
    }
  ];

  const onButtonAction = (patientId) => {
    navigate('/lab-view/bill/add');
  };

  return (
    <div className='billTable'>
      <LimsTable
        title="Bill"
        columns={columns}
        data={data}
        showAddButton={false}
        onView={null}
        onEdit={null}
        onDelete={null}
        showSearch={true}
        showPagination={true}
        onButtonAction={onButtonAction}
        buttonName="Proceed To Bill"
        showStatus
      />
    </div>
  )
}

export default Bill
