import React, { useEffect, useState } from 'react'
import LimsTable from '../../../LimsTable/LimsTable'
import { deleteReportFooterSizeById, getReportFooterSizeById, reportFooterSizeService } from "../../../../services/MasterDataService/ReportSettingsMaster/ReportFooterSizeService";
import { useNavigate,useLocation } from 'react-router-dom';
import Swal from '../../../Re-usable-components/Swal';
import "../ReportSettings.css";

const ReportFooterSize = () => {
  const [footerSizeName, setFooterSizeName] = useState([]);
  const [totalCount, setTotalCount] = useState(0);
  const navigate = useNavigate();
  const location = useLocation();
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [successPopup, setSuccessPopup] = useState(false);
  const [selectedRow, setSelectedRow] = useState(null);
  const [showPopup, setShowPopup] = useState(false);
  const [sortBy, setSortBy] = useState('footerSize');
  const [createdBy, setCreatedBy] = useState("3fa85f64-5717-4562-b3fc-2c963f66afa6");
  const [startsWith, setStartsWith] = useState(
    location.state?.previousSearch || ''
  );

 

  const columns = [
    { key: 'slNo', label: 'Sl. No.', width: '80px', align: 'center' },
    { key: 'footerSize', label: 'Footer Size', width: '1fr', align: 'center' },
    { key: 'action', label: 'Action', width: '200px', height: '40px', align: 'center' },
  ];

  useEffect(() => {
    fetchAllFooterSize();
  }, [startsWith, currentPage, pageSize, sortBy, createdBy]);
 
  useEffect(() => {
    if (location.state?.previousSearch) {
      setStartsWith(location.state.previousSearch);
    }
  }, [location.state]);

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < Math.ceil(totalCount / pageSize)) {
      setCurrentPage(newPage);
    }
  };

  const confirmDelete = async () => {
    try {
      await deleteReportFooterSizeById(selectedRow.id);
      setFooterSizeName((prevPageSize) =>
        prevPageSize.filter((pageName) => pageName.id !== selectedRow.id)
      );
      setTotalCount(prevTotalCount => prevTotalCount - 1);
      setShowPopup(false);
      setSuccessPopup(true);
    } catch (error) {
      console.error('Error deleting Page size:', error);
      setShowPopup(false);
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

  const closePopup = () => {
    setShowPopup(false);
  };

  const closeSuccessPopup = () => {
    setSuccessPopup(false);
    fetchAllFooterSize();
  };

  const handleAdd = () => {
    navigate('/masters/report-settings/footer-size/add');
  };

  const handleView = async (row) => {
    try {
      const response = await getReportFooterSizeById(row.id);
      navigate('/masters/report-settings/footer-size/add', {
        state: {
          footerSizeDetails: response.data,
          mode: 'view',
          previousSearch: startsWith 
        },
      });
    } catch (error) {
      console.error('Error fetching Footer Size details:', error);
    }
  };

  const handleEdit = async (row) => {
    console.log("row", row)
    try {
      const response = await getReportFooterSizeById(row.id);
      navigate('/masters/report-settings/footer-size/add', {
        state: {
          footerSizeDetails: response.data,
          mode: 'edit',
          previousSearch: startsWith 
        },
      });
    } catch (error) {
      console.error('Error fetching Footer Size details:', error);
    }
  };

  const handleDelete = (row) => {
    setSelectedRow(row);
    setShowPopup(true);
  };

  const fetchAllFooterSize = async () => {
    try {
      const response = await reportFooterSizeService.getAllReportFooterSizes(
        startsWith,
        currentPage,
        pageSize,
        sortBy,
        createdBy
      );
      setFooterSizeName(response.data);
      setTotalCount(response.totalCount || 0);
    } catch (error) {
      console.log("Fetching all the footer size data has failed: ", error);
    }
  }

  return (
    <div className='report-setting-table'>
      <LimsTable
        title="Footer Size"
        columns={columns}
        data={footerSizeName}
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
        searchTerm={startsWith}
      />

      {showPopup && (
        <Swal
          icon="delete"
          title="Are you sure?"
          onClose={closePopup}
          isButton={true}
          buttonText={"Delete"}
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

export default ReportFooterSize