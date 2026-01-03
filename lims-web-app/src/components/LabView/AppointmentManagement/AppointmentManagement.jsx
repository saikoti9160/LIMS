import React, { useState } from 'react'
import './AppointmentManagement.css'
import LimsTable from '../../LimsTable/LimsTable'
import { useNavigate } from 'react-router-dom';

const AppointmentManagement = () => {
  const column = [
    { key: 'slNo', label: 'Sl. No.', width: '100px', align: 'center' },
    { key: 'patientId', label: 'Patient ID', width: '1fr', align: 'center' },
    { key: 'name', label: 'Name', width: '1fr', align: 'center' },
    { key: 'phoneNumber', label: 'Phone Number', width: '1fr', align: 'center' },
    { key: 'gender', label: 'Gender', width: '1fr', align: 'center' },
    { key: 'referral', label: 'Refferal', width: '116px', height: '40px', align: 'center' },
    { key: 'organisation', label: 'Organisation', width: '116px', height: '40px', align: 'center' },
    { key: 'branch', label: 'Branch', width: '116px', height: '40px', align: 'center' },
    { key: 'billId', label: 'Bill ID', width: '116px', height: '40px', align: 'center' },
    { key: 'appointmentDate', label: 'Appointment Date', width: '116px', height: '40px', align: 'center' },
    { key: 'timeSlot', label: 'Time slot', width: '116px', height: '40px', align: 'center' },
    { key: 'doctor', label: 'Doctor', width: '116px', height: '40px', align: 'center' },
    { key: 'bookedVia', label: 'Booked Via', width: '116px', height: '40px', align: 'center' },
    { key: 'status', label: 'Status', width: '116px', height: '40px', align: 'center' },
    { key: 'action', label: 'Actions', width: '116px', height: '40px', align: 'center' },
  ];
  const data = [
    {
      slNo: 1,
      patientId: 'P001',
      name: 'John Doe',
      phoneNumber: '123-456-7890',
      gender: 'Male',
      referral: 'Dr. Smith',
      organisation: 'XYZ Hospital',
      branch: 'Branch 1',
      billId: 'B12345',
      appointmentDate: '2025-03-15',
      timeSlot: '10:30 AM',
      doctor: 'Dr. Williams',
      bookedVia: 'Online',
      status: 'Confirmed',
      actions: 'Edit | Delete'
    },
    {
      slNo: 2,
      patientId: 'P002',
      name: 'Jane Smith',
      phoneNumber: '987-654-3210',
      gender: 'Female',
      referral: 'Dr. Johnson',
      organisation: 'ABC Clinic',
      branch: 'Branch 2',
      billId: 'B67890',
      appointmentDate: '2025-03-16',
      timeSlot: '2:00 PM',
      doctor: 'Dr. Brown',
      bookedVia: 'Phone',
      status: 'Scheduled',
      actions: 'Edit | Delete'
    },
    {
      slNo: 3,
      patientId: 'P003',
      name: 'Michael Johnson',
      phoneNumber: '456-789-0123',
      gender: 'Male',
      referral: 'Dr. Adams',
      organisation: 'MediCare Center',
      branch: 'Branch 3',
      billId: 'B23456',
      appointmentDate: '2025-03-17',
      timeSlot: '11:00 AM',
      doctor: 'Dr. Lee',
      bookedVia: 'In-person',
      status: 'Cancelled',
      actions: 'Edit | Delete'
    },
    {
      slNo: 4,
      patientId: 'P004',
      name: 'Emily Davis',
      phoneNumber: '321-654-9870',
      gender: 'Female',
      referral: 'Dr. White',
      organisation: 'HealthCare Group',
      branch: 'Branch 4',
      billId: 'B34567',
      appointmentDate: '2025-03-18',
      timeSlot: '3:30 PM',
      doctor: 'Dr. Green',
      bookedVia: 'App',
      status: 'Confirmed',
      actions: 'Edit | Delete'
    },
  ];
  const navigate = useNavigate()
  const handleAdd = () => {
    navigate("/lab-view/appointment-management/add")
  }
  const [tab, setTab] = useState(0);
  return (
    <>
      {/* <div className='appointment-management-container'>
        <div className='appointment-management-parent'>
            <span className='appointment-management-header'>Appointment</span>
      </div>
      <div>
        <img/>
        <img/>
      </div>
    </div> */}

      <div>
        <div className="infos-parent-appointment">
          <span className={`${tab === 0 ? "active-tab" : "inactive-tab"}`} onClick={() => { setTab(0); }} > All </span>
          <span className={`${tab === 1 ? "active-tab" : "inactive-tab"}`} onClick={() => { setTab(1) }}> Scheduled</span>
          <span className={`${tab === 2 ? "active-tab" : "inactive-tab"}`} onClick={() => { setTab(2); }}> Confirmed</span>
          <span className={`${tab === 3 ? "active-tab" : "inactive-tab"}`} onClick={() => { setTab(3); }}> Cancelled</span>
        </div>
        <div>
          <LimsTable
            title='Appointment'
            columns={column}
            data={data}
            onAdd={handleAdd}
            // showSearch 
            showAddButton

          />
        </div>

      </div>
    </>
  )
}

export default AppointmentManagement
