import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Swal from '../../Re-usable-components/Swal';
import "./DiscountMaster.css";
import LimsTable from '../../LimsTable/LimsTable';
import { getAllDiscounts, getDiscountById, deleteDiscount } from '../../../services/LabViewServices/DiscountMasterService';

const DiscountMaster = () => {
    const [discounts, setDiscounts] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [totalCount, setTotalCount] = useState(0);
    const [selectedDiscount, setSelectedDiscount] = useState(null);
    const [showPopup, setShowPopup] = useState(false);
    const [selectedRow, setSelectedRow] = useState(null);
    const [successPopup, setSuccessPopup] = useState(false);
    const navigate = useNavigate();
    const [flag, setFlag] = useState(true);
    const [keyword, setKeyword] = useState("");
    const createdBy = "3fa85f64-5717-4562-b3fc-2c963f66af11"; //taken as reference will remove later

    const columns = [
        { key: 'slNo', label: 'Sl. No.', width: '50px', align: 'center' },
        { key: 'discountName', label: 'Discount Name', width: '1fr', align: 'center' },
        { key: 'discountType', label: 'Discount Type', width: '1fr', align: 'center' },
        { key: 'action', label: 'Action', width: '150px', align: 'center' }
    ];

    useEffect(() => {
        fetchDiscounts(currentPage, pageSize, keyword, flag);
    }, [createdBy, keyword, flag, currentPage, pageSize]);

    const fetchDiscounts = async (page, size, keyword = '', flag) => {
        try {
            const { data, totalCount } = await getAllDiscounts(
                createdBy,
                keyword,
                flag,
                page,
                size
            );
    
            setDiscounts(data);
            setTotalCount(totalCount);
        } catch (error) {
            setDiscounts([]);
            setTotalCount(0);
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


    const handleAdd = () => {
        navigate('/addDiscountMaster');
    };

    const handleView = async (row) => {
        try {
            const discountDetails = await getDiscountById(row.id);
            navigate('/addDiscountMaster', {
                state: {
                    discountDetails,
                    mode: 'view',
                },
            });
        } catch (error) {
            console.error('Error fetching discount details:', error);
        }
    };

    const handleEdit = async (row) => {
        try {
            const discountDetails = await getDiscountById(row.id);
            navigate(`/addDiscountMaster/${row.id}`, {
                state: {
                    discountDetails: discountDetails,
                    mode: 'edit',
                    discountId: row.id
                },
            });
        } catch (error) {
            console.error('Error fetching discount details for editing:', error);
        }
    };

    const handleSearch = (query) => {
        setCurrentPage(0); 
        setKeyword(query); 
    };
    
    const handleDelete = (row) => {
        setSelectedRow(row);
        
        setShowPopup(true);
    };

    const confirmDelete = async () => {
        try {
            await deleteDiscount(selectedRow.id);
            setDiscounts((prevDiscounts) =>
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
    };

    const formattedDiscounts = discounts.map((discount, index) => ({
        slNo: index + 1,
        id: discount.id,
        discountName: discount.discountName,
        discountType: discount.discountType,
    }));

    return (
        <div className='discount-table'>
            <LimsTable
                title="Discount Master"
                columns={columns}
                data={formattedDiscounts}
                totalCount={totalCount}
                currentPage={currentPage}
                pageSize={pageSize}
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

            {/* Delete Confirmation Popup */}
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

export default DiscountMaster;
