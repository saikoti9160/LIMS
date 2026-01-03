import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./couponDiscountMaster.css";
import LimsTable from "../../LimsTable/LimsTable";
import { couponDiscountService } from "../../../services/LabViewServices/couponDiscountService";
import Swal from "../../Re-usable-components/Swal";

const CouponAndDiscountMaster = () => {
    const navigate = useNavigate();
    const [coupons, setCoupons] = useState([]);
    const [pageNumber, setPageNumber] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [totalCount, setTotalCount] = useState(0);
    const [searchTerm, setSearchTerm] = useState("");
    const [flag, setFlag] = useState(true);
    const [popupConfig, setPopupConfig] = useState(null);

    const createdBy = "20cd288a-d7ed-4dde-9b1a-a520dd11e111";

    const columns = [
        { key: 'slNo', label: 'Sl. No.', width: '80px', align: 'center' },
        { key: 'couponName', label: 'Coupon Name', width: '1fr', align: 'center' },
        { 
            key: 'startDate', 
            label: 'Valid From', 
            width: '1fr', 
            align: 'center',
            format: (value) => formatDate(value)
        },
        { 
            key: 'endDate', 
            label: 'Valid To', 
            width: '1fr', 
            align: 'center',
            format: (value) => formatDate(value)
        },
        { key: 'action', label: 'Action', width: '160px', align: 'center' }
    ];
   

    useEffect(() => {
        fetchAllCoupons();
    }, [searchTerm, flag, createdBy, pageNumber, pageSize]);

    const fetchAllCoupons = async () => {
        try {
            const { data, totalCount } = await couponDiscountService.getAllCouponDiscounts(
                searchTerm,
                flag,
                createdBy,
                pageNumber,
                pageSize
            );
            setCoupons(data);
            setTotalCount(totalCount);
        } catch (error) {
            setCoupons([]);
            setTotalCount(0);
        }
    };
    

    const handleEdit = async (row) => {
        try {
            const couponDetails = await couponDiscountService.getCouponDiscountById(row.id);
            navigate(`/add-edit-coupon-discount/${row.id}`, { state: { couponDetails, mode: 'edit' } });
        } catch (error) {
            console.error('Error fetching coupon details for editing:', error);
        }
    };
 const formatDate = (dateString) => {
    if (!dateString) return ''; 
    const date = new Date(dateString);
    if (isNaN(date.getTime())) return '';
    return date.toLocaleDateString('en-GB', { 
        day: '2-digit', 
        month: '2-digit', 
        year: 'numeric' 
    });
};

    const handleView = async (row) => {
        try {
            const couponDetails = await couponDiscountService.getCouponDiscountById(row.id);
            navigate(`/add-edit-coupon-discount/${row.id}`, { state: { couponDetails, mode: 'view' } });
        } catch (error) {
            console.error('Error fetching coupon details:', error);
        }
    };

    const handleDelete = (row) => {
        setPopupConfig({
            icon: 'delete',
            title: 'Are you sure?',
            text: 'Do you want to delete this Coupon?',
            onButtonClick: async () => {
                try {
                    await couponDiscountService.deleteCouponDiscount(row.id);
                    setPopupConfig({
                        icon: 'success',
                        title: 'Deleted Successfully',
                        text: '',
                        onClose: () => {
                            setPopupConfig(null);
                            fetchAllCoupons();
                        }
                    });
                } catch (error) {
                    setPopupConfig({
                        icon: 'error',
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
        navigate('/add-edit-coupon-discount');
    };

    return (
        <div className="master-container">
            <LimsTable
                title="Coupon and Discount Master"
                totalCount={totalCount}
                showSearch={true}
                showAddButton={true}
                showPagination={true}
                columns={columns}
                data={coupons}
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

export default CouponAndDiscountMaster;