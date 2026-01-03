import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import LimsTable from '../../LimsTable/LimsTable';
import { branchTypesGetAll, getBranchTypeById, deleteBranchType } from '../../../services/branchType';
import Swal from '../../Re-usable-components/Swal';

const BranchType = () => {
  const [branchTypes, setBranchTypes] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalCount, setTotalCount] = useState(0);
  const [popup, setPopup] = useState(null);
  const [startsWith, setStartsWith] = useState('');
  const navigate = useNavigate();
  const createdBy = '3fa85f64-5717-4562-b3fc-2c963f66afa6';

  const columns = [
    { key: 'slNo', label: 'Sl. No.', width: '100px', align: 'center' },
    { key: 'branchTypeName', label: 'Branch Type', width: '1fr', align: 'center' },
    { key: 'action', label: 'Action', width: '116px', height: '40px', align: 'center' },
  ];

  const fetchBranchTypes = async (currentPage, size, startsWith = '') => {
    try {
      const { data, totalCount } = await branchTypesGetAll(currentPage, size, startsWith, createdBy);
      setBranchTypes(data || []);
      setTotalCount(totalCount || 0);
    } catch (error) {
      console.error('Error fetching branch types:', error);
    }
  };

  useEffect(() => {
    fetchBranchTypes(currentPage, pageSize, startsWith);
  }, [currentPage, pageSize, startsWith]);

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < Math.ceil(totalCount / pageSize)) {
      setCurrentPage(newPage);
    }
  };

  const handlePageSizeChange = (newSize) => {
    setPageSize(newSize);
    setCurrentPage(0);
  };

  const handleSearchChange = async (query) => {
    setStartsWith(query);
    setCurrentPage(0);
  };

  const handleAdd = () => {
    navigate('/masters/branch-type/add');
  };

  const handleView = async (row) => {
    try {
      const branchTypeDetails = await getBranchTypeById(row.id);
      navigate('/masters/branch-type/add', {
        state: {
          branchTypeDetails,
          mode: 'view',
        },
      });
    } catch (error) {
      console.error('Error fetching branch type details:', error);
    }
  };

  const handleEdit = async (row) => {
    try {
      const branchTypeDetails = await getBranchTypeById(row.id);
      navigate('/masters/branch-type/add', {
        state: {
          branchTypeDetails,
          mode: 'edit',
        },
      });
    } catch (error) {
      console.error('Error fetching branch type details for editing:', error);
    }
  };

  const handleDeleteConfirm = async (row) => {
    try {
      const response = await deleteBranchType(row.id);
      if (response?.statusCode === "200 OK") {
        setBranchTypes((prevBranchTypes) =>
          prevBranchTypes.filter((branch) => branch.id !== row.id)
        );
        setPopup({
          icon: "success",
          title: "Deleted Successfully",
          onClose: () => setPopup(null),
        });
      }
    } catch (error) {
      console.error("Error deleting branch type:", error);
      setPopup({
        icon: "error",
        title: "Error deleting branch type",
        onClose: () => setPopup(null),
      });
    }
  };

  const handleDelete = (row) => {
    setPopup({
      icon: "delete",
      title: "Are you sure?",
      isButton: true,
      buttonText: "Delete",
      onClose: () => setPopup(null),
      onButtonClick: () => handleDeleteConfirm(row),
    });
  };

  return (
    <div className='limsMasterTable'>
      <LimsTable
        title="Branch Type"
        columns={columns}
        data={branchTypes}
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
        onHandleSearch={handleSearchChange}
      />
      {popup && <Swal {...popup} />}
    </div>
  );
};

export default BranchType;