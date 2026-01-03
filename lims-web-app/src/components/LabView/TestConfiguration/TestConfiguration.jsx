import React, { useEffect, useState } from "react";
import "./TestConfiguration.css";
import LimsTable from "../../LimsTable/LimsTable";
import { testConfigurationService } from "../../../services/LabViewServices/testConfigurationService";
import Swal from "../../Re-usable-components/Swal";
import { useNavigate } from "react-router-dom";

const TestConfiguration = () => {
    const navigate = useNavigate();
    const [tests, setTests] = useState([]);
    const [pageNumber, setPageNumber] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [totalCount, setTotalCount] = useState(0);
    const [searchTerm, setSearchTerm] = useState("");
    const [popupConfig, setPopupConfig] = useState(null);

    // Assuming we have the labId from context or localStorage
    // const labId = localStorage.getItem("labId") || "default-lab-id";
    const labId="20cd288a-d7ed-4dde-9b1a-a520dd11e9c8";
    //const createdBy = localStorage.getItem("userId") || "20cd288a-d7ed-4dde-9b1a-a520dd11e111";

    const columns = [
        { key: 'slNo', label: 'Sl. No.', width: '80px', align: 'center' },
        { key: 'testName', label: 'Test Name', width: '1fr', align: 'center' },
        { key: 'action', label: 'Action', width: '160px', align: 'center' }
    ];

    useEffect(() => {
        fetchAllTestConfigurations();
    }, [searchTerm, pageNumber, pageSize]);

    const fetchAllTestConfigurations = async () => {
        try {
            const response = await testConfigurationService.getAllTestConfigurations(
                labId,
                searchTerm,
                pageNumber,
                pageSize
            );
            
            if (response && response.data) {
                setTests(response.data.map((test, index) => ({
                    ...test,
                    slNo: pageNumber * pageSize + index + 1
                })));
                setTotalCount(response.totalCount || response.data.length);
            } else {
                setTests([]);
                setTotalCount(0);
            }
        } catch (error) {
            console.error('Error fetching test configurations:', error);
            setTests([]);
            setTotalCount(0);
        }
    };

    const handleEdit = async (row) => {
        try {
            const testDetails = await testConfigurationService.getTestConfigurationById(row.id);
            navigate(`/add-edit-test-configuration/${row.id}`, { state: { testDetails, mode: 'edit' } });
        } catch (error) {
            console.error('Error fetching test configuration details for editing:', error);
        }
    };

    const handleView = async (row) => {
        try {
            const testDetails = await testConfigurationService.getTestConfigurationById(row.id);
            navigate(`/add-edit-test-configuration/${row.id}`, { state: { testDetails, mode: 'view' } });
        } catch (error) {
            console.error('Error fetching test configuration details:', error);
        }
    };

    const handleDelete = (row) => {
        setPopupConfig({
            icon: 'delete',
            title: 'Are you sure?',
            text: 'Do you want to delete this Test Configuration?',
            onButtonClick: async () => {
                try {
                    await testConfigurationService.deleteTestConfiguration(row.id);
                    setPopupConfig({
                        icon: 'success',
                        title: 'Deleted Successfully',
                        text: '',
                        onClose: () => {
                            setPopupConfig(null);
                            fetchAllTestConfigurations();
                        }
                    });
                } catch (error) {
                    setPopupConfig({
                        icon: 'delete',
                        title: 'Failed to delete',
                        text: 'Please try again.',
                        onClose: () => setPopupConfig(null),
                    });
                }
            },
            onClose: () => setPopupConfig(null)
        });
    };

    const handleSearch = (query) => {
        setPageNumber(0);
        setSearchTerm(query);
    };

    const onPageChange = (value) => {
        if (value >= 0 && value < Math.ceil(totalCount / pageSize)) {
            setPageNumber(value);
        }
    };
    
    const onPageSizeChange = (value) => {
        setPageNumber(0);
        setPageSize(value);
    };

    const handleAdd = () => {
        navigate('/add-edit-test-configuration', { state: { mode: 'add' } });
    };

    return (
        <div className="master-container">
            <LimsTable
                title="Test Configuration"
                totalCount={totalCount}
                showSearch={true}
                showAddButton={true}
                showPagination={true}
                columns={columns}
                data={tests}
                onAdd={handleAdd}
                onView={handleView}
                onEdit={handleEdit}
                onHandleSearch={handleSearch}
                onDelete={handleDelete}
                currentPage={pageNumber}
                onPageChange={onPageChange}
                onPageSizeChange={onPageSizeChange}
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
};

export default TestConfiguration;