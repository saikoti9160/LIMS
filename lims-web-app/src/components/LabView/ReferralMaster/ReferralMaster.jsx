import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import LimsTable from '../../LimsTable/LimsTable';
import Swal from '../../Re-usable-components/Swal';
import { referralsGetAll, getReferralById, deleteReferral } from '../../../services/LabViewServices/referralMasterService';
import './AddReferralMaster.css';

const ReferralMaster = () => {
  const navigate = useNavigate();
  const [referrals, setReferrals] = useState([]);
  const [currentPage, setCurrentPage] = useState();
  const [pageSize, setPageSize] = useState();
  const [totalCount, setTotalCount] = useState();
  const [keyword, setKeyword] = useState('');
  const [flag, setFlag] = useState(true);
  const [popup, setPopup] = useState(null);

  const columns = [
    { key: 'slNo', label: 'Sl. No.', width: '50px', align: 'center' },
    { key: 'referralSequenceId', label: 'Referral Id', width: '1fr', align: 'center' },
    { key: 'referralName', label: 'Referral Name', width: '1fr', align: 'center' },
    { key: 'email', label: 'Email Id', width: '1fr', align: 'center' },
    { key: 'action', label: 'Action', width: '116px', align: 'center' }
  ];

  const fetchReferrals = async (currentPage, size, keyword = '', flag = true) => {
    try {
      const createdBy = '3fa85f64-5717-4562-b3fc-2c963f66afa6';
      let response = await referralsGetAll(createdBy, keyword, flag, currentPage, size);
      setReferrals(response.data);
      setTotalCount(response.totalCount);
    } catch (error) {
      console.error('Error fetching referrals:', error);
    }
  };

  useEffect(() => {
    fetchReferrals(currentPage, pageSize, keyword, flag);
  }, [currentPage, pageSize, keyword, flag]);

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < Math.ceil(totalCount / pageSize)) {
      setCurrentPage(newPage);
    }
  };

  const handlePageSizeChange = (newSize) => {
    setPageSize(newSize);
    setCurrentPage(0);
  };

  const handleSearch = (query) => {
    setCurrentPage(0);
    setKeyword(query);
  };

  const handleAdd = () => {
    navigate('/lab-view/referral/add');
  };

  const handleView = async (row) => {
    try {
      const referralDetails = await getReferralById(row.id);
      navigate('/lab-view/referral/add', {
        state: { referralDetails, mode: 'view' },
      });
    } catch (error) {
      console.error('Error fetching referral details:', error);
    }
  };

  const handleEdit = async (row) => {
    try {
      const referralDetails = await getReferralById(row.id);
      navigate('/lab-view/referral/add', {
        state: { referralDetails, mode: 'edit' },
      });
    } catch (error) {
      console.error('Error fetching referral details:', error);
    }
  };

  const handleDelete = (row) => {
    setPopup({
      icon: 'delete',
      title: 'Are you sure?',
      isButton: true,
      buttonText: 'Delete',
      onClose: () => setPopup(null),
      onButtonClick: () => confirmDelete(row),
    });
  };

  const confirmDelete = async (row) => {
    try {
      await deleteReferral(row.id);
      setReferrals((prevReferrals) => prevReferrals.filter((referral) => referral.id !== row.id));
      setPopup({
        icon: 'success',
        title: 'Deleted Successfully',
        onClose: () => setPopup(null),
      });
    } catch (error) {
      console.error('Error deleting referral:', error);
      setPopup({
        icon: 'error',
        title: 'Error deleting referral',
        onClose: () => setPopup(null),
      });
    }
  };

  return (
    <div className="referral-landing">
      <LimsTable
        title="Referral Master"
        columns={columns}
        data={referrals}
        totalCount={totalCount}
        currentPage={currentPage}
        pageSize={pageSize}
        onAdd={handleAdd}
        onView={handleView}
        onEdit={handleEdit}
        onDelete={handleDelete}
        showAddButton
        showSearch
        showPagination
        onPageChange={handlePageChange}
        onPageSizeChange={handlePageSizeChange}
        onHandleSearch={handleSearch}
      />
      {popup && <Swal {...popup} />}
    </div>
  );
};

export default ReferralMaster;
