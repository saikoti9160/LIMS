import React, { useState } from 'react';
import LimsTable from '../../LimsTable/LimsTable';
import './BarCodeManagement.css';
import { useNavigate } from 'react-router-dom';
import printerIcon from '../../../assets/images/prime_print.svg';
import storageIcon from '../../../assets/images/Storage.svg';

function BarCodeManagement() {
    const [isContextMenuOpen, setIsContextMenuOpen] = useState(false);
    const [contextMenuPosition, setContextMenuPosition] = useState({ x: 0, y: 0 });
    const navigate = useNavigate();
    const navigateUrl = '/lab-view/barcode-management-details';
    const [selectedRowData, setSelectedRowData] = useState(null);


    const columns = [
        { key: 'sNo', label: 'S.No.', align: 'center' },
        { key: 'patientSequenceId', label: 'Patient ID', align: 'center' },
        { key: 'patientName', label: 'Name', align: 'center' },
        { key: 'phoneNumber', label: 'Phone Number', align: 'center' },
        { key: 'gender', label: 'Gender', align: 'center' },
        { key: 'referralName', label: 'Referral', align: 'center' },
        { key: 'name', label: 'Organisation', align: 'center' },
        { key: 'branchName', label: 'Branch', align: 'center' },
        { key: 'billID', label: 'Bill ID', align: 'center' },
        { key: 'billDate', label: 'Bill Date', align: 'center' },
        { key: 'departmentName', label: 'Department', align: 'center' },
        { key: 'testName', label: 'Test', align: 'center' },
        { key: 'sampleType', label: 'Sample Type', align: 'center' },
        { key: 'contextMenu', label: 'Action', align: 'center' }
    ];

    const options = ['branch1', 'branch2', 'branch3'];

    const menuOptions = [
        { label: "Print Barcode", icon: printerIcon, action: "printBarcode" },
        { label: "Storage", icon: storageIcon, action: "storage" }
    ];

    const sampleData = [
        { sNo: 1, patientId: 'P1001', patientName: 'John Doe', phoneNumber: '9876543210', gender: 'Male', referral: 'Dr. Smith', organisationName: 'City Hospital', branchName: 'Downtown', billID: 'B1001', billDate: '2025-03-10', testName: 'Blood Test', sampleType: 'Blood' },
        { sNo: 2, patientId: 'P1002', patientName: 'Jane Smith', phoneNumber: '9876543222', gender: 'Female', referral: 'Dr. Brown', organisationName: 'MediCare Lab', branchName: 'Uptown', billID: 'B1002', billDate: '2025-03-09', testName: 'Urine Test', sampleType: 'Urine' },
        { sNo: 3, patientId: 'P1003', patientName: 'Alice Johnson', phoneNumber: '9876543233', gender: 'Female', referral: 'Dr. Wilson', organisationName: 'Global Health', branchName: 'Central', billID: 'B1003', billDate: '2025-03-08', testName: 'X-Ray', sampleType: 'Radiology' }
    ];
    const handleMenuOption = (action, rowData) => {
        if (action === "printBarcode") {
            navigate(navigateUrl, { state: { patient: rowData } });
        }
        if (action === "storage") {
            navigate(navigateUrl, { state: { patient: rowData } });
        }
    };


    const handleContextMenuOpen = (event, rowData) => {
        event.preventDefault();
        setSelectedRowData(rowData);

        const buttonRect = event.target.getBoundingClientRect();
        setContextMenuPosition({ x: buttonRect.left - 160, y: buttonRect.top });
        setIsContextMenuOpen(true);
    };

    const handleContextMenuClose = () => {
        setIsContextMenuOpen(false);
    };

    const handleStatusChange = () => { };

    return (
        <div className='barCodeManagementConatiner'>
            <LimsTable
                title="Bar Code Management"
                columns={columns}
                data={sampleData}
                contextMenuHandler={(event, rowData) => handleContextMenuOpen(event, rowData)}
                showSearch
                showPagination
                dropDownOptions={options}
                onHandleStatus={handleStatusChange}
                showStatus
                dropDownPlaceholder='Branch'
            />
            {isContextMenuOpen && (
                <div
                    className="context-menu"
                    style={{ top: `${contextMenuPosition.y}px`, left: `${contextMenuPosition.x}px` }}
                    onMouseLeave={handleContextMenuClose}
                >
                    {menuOptions.map((option, index) => (
                        <div
                            key={index}
                            className="context-menu-item"
                            onClick={() => handleMenuOption(option.action, selectedRowData)}
                        >
                            <img src={option.icon} alt={option.label} />
                            {option.label}
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

export default BarCodeManagement;