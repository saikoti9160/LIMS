import React, { useState, useEffect } from 'react';
import {
  LineChart, BarChart, PieChart, XAxis, YAxis, CartesianGrid,
  Tooltip, Legend, ResponsiveContainer, Line, Bar, Pie, Cell
} from 'recharts';
import './Lab.css';
import DatePicker from 'react-datepicker';
import calenderIcon from "../../assets/icons/Vector.svg";
import LimsTable from '../LimsTable/LimsTable';

const COLORS = ['#F4A300', '#4C6EF5'];

function LabDashboard() {
  const [data, setData] = useState({
    userLoginSummary: { labLogins: 95000, patientLogins: 60000 },
    collectionType: { cash: 12500, creditCard: 2000, debitCard: 100, upi: 200, other: 100 },
    financialCollectionTrendAnalysis: { jan: { totalBilling: 100, collection: 50 }, feb: { totalBilling: 150, collection: 75 }, mar: { totalBilling: 200, collection: 120 } },
    reportsSummary: { approvedReports: 25000, rejectedReports: 3000, pendingReports: 15012 },
    testsAndRevenue: { jan: { revenue: 1000 }, feb: { revenue: 1500 }, mar: { revenue: 2000 } },
    patientTypeAnalysis: { newPatients: 11023, repeatedPatients: 121023 },
    appointments: { total: 11023, homeCollection: 121023 },
    sampleStatus: { samples: 20000, collected: 15000, received: 10000, uncollected: 5000, dismissed: 2000, redrawn: 1000 },
    expensesBreakdown: { referralCommission: 10000, supplierExpenses: 20000, otherExpenses: 15000 },
    supplierExpenses: [{ slNo: 1, supplierName: 'Fisher Scientific', expense: 18000 }, { slNo: 2, supplierName: 'VWR International', expense: 16000 }],
  });

  const columns = [
    { key: 'slNo', label: 'Sl. No.', width: '100px', align: 'center' },
    { key: 'supplierName', label: 'Supplier Name', width: '1fr', align: 'center' },
    { key: 'expenses', label: 'Expenses', width: '1fr', align: 'center' },
  ];

  const [dateRange, setDateRange] = useState([null, null]);
  const [startDate, endDate] = dateRange;

  const loginData = [
    { name: 'Lab Logins', value: data.userLoginSummary.labLogins },
    { name: 'Patient Logins', value: data.userLoginSummary.patientLogins }
  ];

  const totalLogins = loginData.reduce((acc, curr) => acc + curr.value, 0);

  const renderLabel = ({ percent }) => `${(percent * 100).toFixed(0)}%`;

  const response = [
    { slNo: 1, supplierName: 'Fisher Scientific', expenses: 18000 },
    { slNo: 2, supplierName: 'VWR International', expenses: 16000 },
    { slNo: 3, supplierName: 'Becton Dickinson', expenses: 14000 },
    { slNo: 4, supplierName: 'Abbott Laboratories', expenses: 12000 },
    { slNo: 5, supplierName: 'Roche', expenses: 10000 },
    { slNo: 6, supplierName: 'Merck', expenses: 8000 },
    { slNo: 7, supplierName: 'Pfizer', expenses: 6000 },
  ];

  return (
    <div className="lab-dashboard">
      <div className="lmd-heading">
        <span className="lmd-title">Welcome, Lab Admin</span>
      </div>
      <div className='searchContainer'>
        <div className='lmdf-date'>
          <DatePicker
            selected={startDate}
            onChange={(update) => setDateRange(update)}
            startDate={startDate}
            endDate={endDate}
            selectsRange
            dateFormat="MM/dd/yyyy"
            placeholderText="Select Date Range"
            className="custum-datepicker"
          />
          <img src={calenderIcon} alt="search" className='lmdf-calender-icon' onClick={() => document.querySelector('.custum-datepicker').focus()} />
        </div>
        <button onClick={() => setDateRange([null, null])} className="clear-btn">Clear</button>
      </div>
      <div className="section-row">
        <div className="section">
          <h2 className="section-title text-center text-blue-600 font-semibold">User Login Summary</h2>
          <ResponsiveContainer width="100%" height={250}>
            <PieChart>
              <Pie
                data={loginData}
                dataKey="value"
                nameKey="name"
                cx="50%"
                cy="50%"
                innerRadius={60}
                outerRadius={90}
                label={renderLabel}
                labelLine={false}
                className='pie'
              >
                {loginData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip />
              <Legend verticalAlign="bottom" iconType="circle" />
              <text
                x="50%"
                y="50%"
                textAnchor="middle"
                dominantBaseline="middle"
                fontSize="20"
                fontWeight="bold"
              >
                {totalLogins.toLocaleString()}
              </text>
            </PieChart>
          </ResponsiveContainer>
        </div>

        <div className="section">
          <h2 className="section-title">Collection Type</h2>
          <ResponsiveContainer width="100%" height={200}>
            <BarChart data={Object.entries(data.collectionType).map(([key, value]) => ({ name: key, value }))}>
              <XAxis dataKey="name" />
              <YAxis />
              <CartesianGrid />
              <Tooltip />
              <Legend />
              <Bar dataKey="value" fill="#8884d8" />
            </BarChart>
          </ResponsiveContainer>
        </div>

        <div className="section">
          <h2 className="section-title">Financial Collection Trend Analysis</h2>
          <ResponsiveContainer width="100%" height={200}>
            <LineChart data={Object.entries(data.financialCollectionTrendAnalysis).map(([month, { totalBilling, collection }]) => ({ name: month, totalBilling, collection }))}>
              <XAxis dataKey="name" />
              <YAxis />
              <CartesianGrid />
              <Tooltip />
              <Legend />
              <Line type="monotone" dataKey="totalBilling" stroke="#8884d8" activeDot={{ r: 8 }} />
              <Line type="monotone" dataKey="collection" stroke="#82ca9d" />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </div>

      <div className="section-row">
        <div className="section">
          <h2 className="section-title">Reports Summary</h2>
          <ResponsiveContainer width="100%" height={200}>
            <BarChart data={[
              { name: 'Approved', value: data.reportsSummary.approvedReports },
              { name: 'Rejected', value: data.reportsSummary.rejectedReports },
              { name: 'Pending', value: data.reportsSummary.pendingReports },
            ]}>
            </BarChart>
          </ResponsiveContainer>
        </div>

        <div className="section">
          <h2 className="section-title">Tests and Revenue</h2>
          <ResponsiveContainer width="100%" height={200}>
            <LineChart data={Object.entries(data.testsAndRevenue).map(([month, { revenue }]) => ({ name: month, revenue }))}>
            </LineChart>
          </ResponsiveContainer>
        </div>

        <div className="sectionDetails">
          <div className="section">
            <h2 className="section-title">Patient Type Analysis</h2>
            <ResponsiveContainer width="100%" height={100}>
              <div className="firstinnerSection">
                <span className="patientElement">
                  New Patients
                  <div className="value">{data.patientTypeAnalysis.newPatients.toLocaleString()}</div>
                </span>
                <span className="patientElement">
                  Repeated Patients
                  <div className="value">{data.patientTypeAnalysis.repeatedPatients.toLocaleString()}</div>
                </span>
              </div>
            </ResponsiveContainer>
          </div>

          <div className="section">
            <h2 className="section-title">Appointments</h2>
            <ResponsiveContainer width="100%" height={100}>
              <div className="innerSection">
                <span className="totalElement">
                  Total
                  <div className="value">{data.appointments.total.toLocaleString()}</div>
                </span>
                <span className="totalElement">
                  Home Collection
                  <div className="value">{data.appointments.homeCollection.toLocaleString()}</div>
                </span>
              </div>
            </ResponsiveContainer>
          </div>
        </div>
      </div>

      <div className="row-section">
        <div className="section-container">
          <h2 className="section-title">Sample Status</h2>
          <ResponsiveContainer width="100%" height={200}>
            <BarChart data={Object.entries(data.sampleStatus).map(([status, value]) => ({ name: status, value }))}>
            </BarChart>
          </ResponsiveContainer>
        </div>

        <div className="section-container">
          <h2 className="section-title">Expenses Breakdown Category</h2>
          <ResponsiveContainer width="100%" height={200}>
            <BarChart data={Object.entries(data.expensesBreakdown).map(([expenseType, value]) => ({ name: expenseType, value }))}>
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      <div className='table-container'>
        <div className="table-title">Supplier Expenses</div>
        <div className="custom-table">
          <LimsTable
            columns={columns}
            data={response}
          />
        </div>
      </div>
    </div>
  );
}

export default LabDashboard;