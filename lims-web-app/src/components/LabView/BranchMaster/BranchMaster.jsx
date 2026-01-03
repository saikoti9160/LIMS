import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom';
import LimsTable from '../../LimsTable/LimsTable';
import Swal from '../../Re-usable-components/Swal';
import './BranchMaster.css';

const BranchMaster = () => {
    const navigate = useNavigate();
    const [data, setData] = useState([]);
    const [popupConfig, setPopupConfig] = useState(null);



    const columns = [
        { key: 'slNo', label: 'Sl. No.', width: '100px', align: 'center' },
        { key: 'branchId', label: 'Branch ID', width: '1fr', align: 'center' },
        { key: 'branchName', label: 'Branch Name', width: '1fr', align: 'center' },
        { key: 'branchType', label: 'Branch Type', width: '1fr', align: 'center' },
        { key: 'phoneNumber', label: 'Phone Number', width: '1fr', align: 'center' },
        { key: "action", label: "Action", width: "116px" },
    ];

    const handleAdd = () => {
        navigate('/lab-view/addbranch', { state: { mode: 'add' } });
    };

    const handleView = async (row) => {
        navigate('/lab-view/addbranch', { state: { mode: 'view' } });
    };


    const handleEdit = async (row) => {
        navigate('/lab-view/addbranch', { state: { mode: 'edit' } });
    }
    const handleDelete = async (row) => {

    };

    return (
        <div className='BranchMaster'>
            <LimsTable
                title="Branch Master"
                columns={columns}
                data={data}
                onAdd={handleAdd}
                onView={handleView}
                onEdit={handleEdit}
                onDelete={handleDelete}
                showAddButton
                showSearch
                showPagination
            />
            {popupConfig && (
                <Swal
                    icon={popupConfig.icon}
                    title={popupConfig.title}
                    text={popupConfig.text}
                    onButtonClick={popupConfig.onButtonClick}
                    onClose={popupConfig.onClose}
                />
            )}
        </div>
    );
}

export default BranchMaster;
