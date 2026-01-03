import React, { useCallback, useEffect, useState } from 'react';
import LimsTable from '../../../LimsTable/LimsTable';
import { useNavigate } from 'react-router-dom';
import Swal from '../../../Re-usable-components/Swal';
import { deleteReportPatientInfoById, getAllReportPatientInfos, getReportPatientInfoById } from '../../../../services/ReportPatientInfoService';
import "../ReportSettings.css";

const ReportPatientInfo = () => {
    const navigate = useNavigate();
    const [data, setData] = useState([]);
    const [totalCount, setTotalCount] = useState(0);
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [searchQuery, setSearchQuery] = useState('');
    const [sortBy, setSortBy] = useState('patientName');
    const createdBy = "3fa85f64-5717-4562-b3fc-2c963f66afa6";
    const [successPopup, setSuccessPopup] = useState(false);
    const [selectedRow, setSelectedRow] = useState(null);
    const [showPopup, setShowPopup] = useState(false);

    const columns = [
        { key: 'slNo', label: 'Sl. No.', width: '100px', align: 'center' },
        { key: 'patientInfoName', label: 'Patient Info', width: '1fr', align: 'center' },
        { key: 'action', label: 'Action', width: '116px', align: 'center' },
    ];

    const fetchData = useCallback(async () => {
        try {
            const response = await getAllReportPatientInfos(searchQuery, currentPage, pageSize, sortBy, createdBy);
            setData(response.data || []);
            setTotalCount(response.totalCount || 0);
        } catch (error) {
            console.error('Error fetching patient info name:', error);
        }
    }, [searchQuery, currentPage, pageSize, sortBy, createdBy]);

    useEffect(() => {
        fetchData();
    }, [fetchData]);


    const handleAdd = () => navigate('/masters/report-settings/addPatientInfo');

    const handleView = async (row) => {
        try {
            const response = await getReportPatientInfoById(row.id);            
            navigate('/masters/report-settings/addPatientInfo', {            
                state: { patientInfo: response.data, mode: 'view' },
            });
        } catch (error) {
            console.error('Error fetching patient info details:', error);
        }
    };

    const handleEdit = async (row) => {
        try {
            const response = await getReportPatientInfoById(row.id);
            navigate('/masters/report-settings/addPatientInfo', {
                state: { patientInfo: response.data, mode: 'edit' },
            });
        } catch (error) {
            console.error('Error fetching patient info details:', error);
        }
    };

    const handleDelete = (row) => {
        setSelectedRow(row);
        setShowPopup(true);
    };

    const confirmDelete = async () => {
        try {
            await deleteReportPatientInfoById(selectedRow.id);
            setData((prevData) => prevData.filter((info) => info.id !== selectedRow.id));
            setShowPopup(false);
            setSuccessPopup(true);
        } catch (error) {
            console.error('Error deleting patient info:', error);
            setShowPopup(false);
        }
    };

    const handlePageChange = (newPage) => {
        if (newPage >= 0 && newPage < Math.ceil(totalCount / pageSize)) {
            setCurrentPage(newPage);
        }
    };

    const handlePageSizeChange = (newSize) => {
        setPageSize(newSize);
        setCurrentPage(0);
    };

    const handleSearchChange = (query) => {
        setSearchQuery(query);
        setCurrentPage(0);
    };

    const closePopup = () => setShowPopup(false);
    const closeSuccessPopup = () => setSuccessPopup(false);

    return (
        <div className='report-setting-table'>
            <LimsTable
                title="Patient Info"
                columns={columns}
                data={data}
                totalCount={totalCount}
                currentPage={currentPage}
                pageSize={pageSize}
                onAdd={handleAdd}
                onView={handleView}
                onEdit={handleEdit}
                onDelete={handleDelete}
                showAddButton
                showClearButton={false}
                showExportButton={false}
                showSearch
                showPagination
                onPageChange={handlePageChange}
                onPageSizeChange={handlePageSizeChange}
                onHandleSearch={handleSearchChange}
            />

            {showPopup && (
                <Swal
                    icon="delete"
                    title="Are you sure?"
                    text="Do you want to delete this patient info?"
                    onClose={closePopup}
                    onButtonClick={confirmDelete}
                />
            )}

            {successPopup && (
                <Swal
                    icon="success"
                    title="Deleted Successfully"
                    onClose={closeSuccessPopup}
                />
            )}
        </div>
    );
};

export default ReportPatientInfo;
