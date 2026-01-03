import React, { useEffect, useState } from 'react'
import LimsTable from '../../../LimsTable/LimsTable'
import { deleteReportPaperSizeById, getReportPaperSizeById, reportPaperSizeService } from "../../../../services/MasterDataService/ReportSettingsMaster/ReportPaperSizeService";
import { useLocation, useNavigate } from 'react-router-dom';
import Swal from '../../../Re-usable-components/Swal';
import "../ReportSettings.css";

const ReportPaperSize = () => {
  const [paperSizeName, setPaperSizeName] = useState([]);
  const [totalCount, setTotalCount] = useState(0);
  const navigate = useNavigate();
  const location = useLocation();
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [successPopup, setSuccessPopup] = useState(false);
  const [selectedRow, setSelectedRow] = useState(null);
  const [startsWith, setStartsWith] = useState(location.state?.previousSearch || '');
  const [showPopup, setShowPopup] = useState(false);
  const [sortBy, setSortBy] = useState('paperSize');
  const [createdBy, setCreatedBy] = useState("3fa85f64-5717-4562-b3fc-2c963f66afa6");

 

  const columns = [
    { key: 'slNo', label: 'Sl. No.', width: '100px', align: 'center' },
    { key: 'paperSize', label: 'Paper Size', width: '1fr', align: 'center' },
    { key: 'action', label: 'Action', width: '200px', height: '40px', align: 'center' },
  ];

  const handlePaperChange = (newPaper) => {
    if (newPaper >= 0 && newPaper < Math.ceil(totalCount / pageSize)) {
      setCurrentPage(newPaper);
    }
  };

  const confirmDelete = async () => {
    try {
      await deleteReportPaperSizeById(selectedRow.id);
      setPaperSizeName((prevPaperSize) =>
        prevPaperSize.filter((paperName) => paperName.id !== selectedRow.id)
      );
      setShowPopup(false);
      setSuccessPopup(true);
    } catch (error) {
      console.error('Error deleting Paper size:', error);
      setShowPopup(false);
    }
  };

  const handlePaperSizeChange = (newSize) => {
    setPageSize(newSize);
    setCurrentPage(0);
  };

  const handleSearchChange = async (query) => {
    setStartsWith(query);
    setCurrentPage(0);
  };

  const closePopup = () => {
    setShowPopup(false);
  };

  const closeSuccessPopup = () => {
    setSuccessPopup(false);
    fetchAllPageSize();
  };

  const handleAdd = () => {
    navigate('/masters/report-settings/paper-size/add');
  };

  const handleView = async (row) => {
    try {
      const response = await getReportPaperSizeById(row.id);
      navigate('/masters/report-settings/paper-size/add', {
        state: {
          paperSizeDetails: response.data,
          mode: 'view',
          previousSearch: startsWith 
        },
      });
    } catch (error) {
      console.error('Error fetching Paper Size details:', error);
    }
  };

  const handleEdit = async (row) => {
    try {
      const response = await getReportPaperSizeById(row.id);
      navigate('/masters/report-settings/paper-size/add', {
        state: {
          paperSizeDetails: response.data,
          mode: 'edit',
          previousSearch: startsWith 
        },
      });
    } catch (error) {
      console.error('Error fetching Paper Size details:', error);
    }
  };

  const handleDelete = (row) => {
    setSelectedRow(row);
    setShowPopup(true);
  };

  const fetchAllPageSize = async () => {

    try {
      const response = await reportPaperSizeService.getAllReportPaperSizes(
        startsWith,
        currentPage,
        pageSize,
        sortBy,
        createdBy
      );
      setPaperSizeName(response.data);
      setTotalCount(response.totalCount || 0);
    } catch (error) {
      console.log("Fetching all the papersize data has failed: ", error);
    }
  }

  useEffect(() => {
    fetchAllPageSize();
  }, [startsWith, currentPage, pageSize, sortBy, createdBy]);
    useEffect(() => {
      if (location.state?.previousSearch) {
        setStartsWith(location.state.previousSearch);
      }
    }, [location.state]);

  return (
    <div className='report-setting-table'>
      <LimsTable
        title="Paper Size"
        columns={columns}
        data={paperSizeName}
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
        onPageChange={handlePaperChange}
        onPageSizeChange={handlePaperSizeChange}
        onHandleSearch={handleSearchChange}
        searchTerm={startsWith}
      />

      {showPopup && (
        <Swal
          icon="delete"
          title="Are you sure?"
          isButton={true}
          buttonText={"Delete"}
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
  )
}

export default ReportPaperSize