import React, { useEffect, useState } from 'react';
import LimsTable from '../../LimsTable/LimsTable';
import { getLabTypes, getLabTypeById, deleteLabType } from '../../../services/labTypeService';
import { useNavigate } from 'react-router-dom';
import Swal from '../../Re-usable-components/Swal';

const LabTypeMaster = () => {
  const [labTypes, setLabTypes] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalCount, setTotalCount] = useState(0);
  const [popup, setPopup] = useState(null);
  const [selectedRow, setSelectedRow] = useState(null);
  const [startsWith, setStartsWith] = useState('');
  const navigate = useNavigate();
  const createdBy = "3fa85f64-5717-4562-b3fc-2c963f66afa6";

  const columns = [
    { key: 'slNo', label: 'Sl. No.', width: '100px', align: 'center' },
    { key: 'name', label: 'Lab Type', width: '1fr', align: 'center' },
    { key: 'action', label: 'Action', width: '116px', height: '40px', align: 'center' },
  ];

  const fetchLabTypes = async () => {
    try {
      const { data, totalCount } = await getLabTypes(startsWith, currentPage, pageSize, createdBy);
      setLabTypes([...data]);
      setTotalCount(totalCount);
    } catch (error) {
      console.error('Error fetching lab types:', error);
    }
  };

  useEffect(() => {
    fetchLabTypes(currentPage, pageSize, startsWith);
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
    navigate('/masters/lab-type/add');
  };

  const handleView = async (row) => {
    try {
      const labTypeDetails = await getLabTypeById(row.id);
      navigate('/masters/lab-type/add', {
        state: { labTypeDetails, mode: 'view' },
      });
    } catch (error) {
      console.error('Error fetching lab type details:', error);
    }
  };

  const handleEdit = async (row) => {
    try {
      const labTypeDetails = await getLabTypeById(row.id);
      navigate('/masters/lab-type/add', {
        state: { labTypeDetails, mode: 'edit' },
      });
    } catch (error) {
      console.error('Error fetching labtype details for editing:', error);
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
    const response = await deleteLabType(row.id);
    if (response?.statusCode === "200 OK") {
      setLabTypes((prevLabTypes) => prevLabTypes.filter((lab) => lab.id !== row.id));
      setPopup({
        icon: "success",
        title: "Deleted Successfully",
        onClose: () => setPopup(null),
      });
    } else {
      setPopup({
        icon: "error",
        title: "Error deleting lab type",
        onClose: () => setPopup(null),
      });
    }
  };

  return (
    <div className='limsMasterTable'>
      <LimsTable
        title="Lab Type"
        columns={columns}
        data={labTypes}
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

export default LabTypeMaster;