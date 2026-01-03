import React, { useEffect, useState } from 'react';
import { departmentsGetAll, getDepartmentById, deleteDepartment } from '../../../services/departmentService';
import { useNavigate } from 'react-router-dom';
import Swal from '../../Re-usable-components/Swal';
import LimsTable from '../../LimsTable/LimsTable';

const DepartmentMaster = () => {
  const navigate = useNavigate();
  const [popup, setPopup] = useState(null);
  const [selectedRow, setSelectedRow] = useState(null);
  const [startsWith, setStartsWith] = useState('');
  const [departments, setDepartments] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalCount, setTotalCount] = useState(0);
  const createdBy = "3fa85f64-5717-4562-b3fc-2c963f66afa6";

  const columns = [
    { key: 'slNo', label: 'Sl. No.', width: '100px', align: 'center' },
    { key: 'name', label: 'Department Name', width: '1fr', align: 'center' },
    { key: 'action', label: 'Action', width: '116px', height: '40px', align: 'center' }
  ];

  const fetchDepartments = async () => {
    try {
      let { data, totalCount } = await departmentsGetAll(currentPage, pageSize, startsWith, createdBy);
      setDepartments([...data]);
      setTotalCount(totalCount);
    } catch (error) {
      console.error('Error fetching departments:', error);
    }
  };

  useEffect(() => {
    fetchDepartments();
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

  const handleSearchChange = (query) => {
    setStartsWith(query);
    setCurrentPage(0);
  };

  const handleAdd = () => {
    navigate('/masters/department/add');
  };

  const handleView = async (row) => {
    try {
      const departmentDetails = await getDepartmentById(row.id);
      navigate('/masters/department/add', {
        state: { departmentDetails, mode: 'view' },
      });
    } catch (error) {
      console.error('Error fetching department details:', error);
    }
  };

  const handleEdit = async (row) => {
    try {
      const departmentDetails = await getDepartmentById(row.id);
      navigate('/masters/department/add', {
        state: { departmentDetails, mode: 'edit' },
      });
    } catch (error) {
      console.error('Error fetching department details for editing:', error);
    }
  };

  const handleDelete = (row) => {
    setSelectedRow(row);
    setPopup({
      icon: "delete",
      title: "Are you sure?",
      isButton: true,
      buttonText: "Delete",
      onClose: () => setPopup(null),
      onButtonClick: () => handleDeleteConfirm(row),
    });
  };

  const handleDeleteConfirm = async (row) => {
    const response = await deleteDepartment(row.id);
    if (response?.statusCode === "200 OK") {
      setDepartments((prevDepartments) => prevDepartments.filter((dept) => dept.id !== row.id));
      setPopup({
        icon: "success",
        title: "Deleted Successfully",
        onClose: () => setPopup(null),
      });
    } else {
      setPopup({
        icon: "error",
        title: "Error deleting department",
        onClose: () => setPopup(null),
      });
    }
  };

  return (
    <div className='limsMasterTable'>
      <LimsTable
        title="Department Master"
        columns={columns}
        data={departments}
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

export default DepartmentMaster;