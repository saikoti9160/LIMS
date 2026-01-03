import React, { useEffect, useState } from 'react';
import LimsTable from '../../LimsTable/LimsTable';
import "./SampleMaster.css";
import { useNavigate } from 'react-router-dom';
import { deleteSample, getAllSamples, getSampleById } from '../../../services/LabViewServices/SampleMasterService';
import Swal from '../../Re-usable-components/Swal';

const SampleMaster = () => {
    const [sample, setSample] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [totalCount, setTotalCount] = useState(0);
    const [searchText, setSearchText] = useState("");
    const [showPopup, setShowPopup] = useState(false);
    const [successPopup, setSuccessPopup] = useState(false);
    const [selectedRow, setSelectedRow] = useState(null);
    const navigate = useNavigate();

    const columns = [
        { key: 'slNo', label: 'Sl. No.', width: '50px', align: 'center' },
        { key: 'sampleName', label: 'Sample Name', width: '1fr', align: 'center' },
        { key: 'sampleType', label: 'Sample Type', width: '1fr', align: 'center' },
        { key: 'action', label: 'Action', width: '150px', align: 'center' }
    ];

    const labId = "b090a206-aa2b-4b6f-8581-123de3a75996";

    const fetchAll = async (labId, pageSize, currentPage, searchText) => {
        try {
            const response = await getAllSamples(labId, currentPage, pageSize, searchText);
            console.log('response', response);
            const formattedData = response.data.map((item) => ({
                ...item,
                sampleType: Array.isArray(item.sampleType) ? item.sampleType.join(', ') : item.sampleType,
            }));
            setSample(formattedData);
            setTotalCount(response.totalCount || 0);

        } catch (error) {
            console.error("Error fetching samples:", error);
        }
    };

    useEffect(() => {
        fetchAll(labId, pageSize, currentPage, searchText);
    }, [labId, pageSize, currentPage, searchText]);

    const handlePageChange = (newPage) => {
        if (newPage >= 0 && newPage < Math.ceil(totalCount / pageSize)) {
            setCurrentPage(newPage);
        }
    };

    const handlePageSizeChange = (newSize) => {
        setPageSize(newSize);
        setCurrentPage(0);
    };

    const handleAdd = () => {
        navigate("/lab-view/addSample");
    };

    const handleView = async (row) => {
        try {
            const sampleData = await getSampleById(row.id);
            navigate('/lab-view/addSample', {
                state: {
                    sampleData,
                    mode: 'view',
                },
            });
        } catch (error) {
            console.error('Error fetching Sample details:', error);
        }
    };

    const handleDelete = (row) => {
        setSelectedRow(row);
        setShowPopup(true);

    };

    const confirmDelete = async () => {
        try {
            await deleteSample(selectedRow.id);
            setSample((prevDiscounts) =>
                prevDiscounts.filter((disc) => disc.id !== selectedRow.id)
            );
            setShowPopup(false);
            setSuccessPopup(true);
        } catch (error) {
            console.error("Error deleting discount:", error);
            setShowPopup(false);
        }
    };

    const closePopup = () => {
        setShowPopup(false);
    };

    const closeSuccessPopup = () => {
        setSuccessPopup(false);
        fetchAll(labId, pageSize, currentPage, searchText);
    };

    const handleEdit = async (row) => {
        try {
            const sampleData = await getSampleById(row.id);
            navigate(`/lab-view/addSample/`, {
                state: {
                    sampleData,
                    mode: 'edit',
                },
            });
        } catch (error) {
            console.error('Error fetching sample details for editing:', error);
        }
    };

    const handleSearch = (query) => {
        setCurrentPage(0);
        setSearchText(() => query);
    };

    return (
        <div className='sample-table'>
            <LimsTable
                columns={columns}
                data={sample}
                currentPage={currentPage}
                pageSize={pageSize}
                totalCount={totalCount}
                title={"Sample Master"}
                onAdd={handleAdd}
                onDelete={handleDelete}
                onView={handleView}
                onEdit={handleEdit}
                onHandleSearch={handleSearch}
                showSearch
                showAddButton
                showPagination
                onPageChange={handlePageChange}
                onPageSizeChange={handlePageSizeChange}
            />
            {showPopup && (
                <Swal
                    icon="delete"
                    title="Are you sure?"
                    text
                    onClose={closePopup}
                    onButtonClick={confirmDelete}
                />
            )}

            {/* Success Popup */}
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

export default SampleMaster;
