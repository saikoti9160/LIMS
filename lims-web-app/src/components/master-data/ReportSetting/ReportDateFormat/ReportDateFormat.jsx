import React, { useEffect, useState } from 'react';
import LimsTable from '../../../LimsTable/LimsTable';
// import { deleteReportDateFormatById, getReportDateFormatById, reportDateFormatService } from "../../../../services/MasterDataService/ReportSettingsMaster/ReportDateFormatService";
import { useNavigate,useLocation } from 'react-router-dom';
import Swal from '../../../Re-usable-components/Swal';
import "../ReportSettings.css";
import { deleteReportDateFormatById, getReportDateFormatById, reportDateFormatService } from '../../../../services/MasterDataService/ReportSettingsMaster/ReportDateFormatService';

const ReportDateFormat = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [dateFormats, setDateFormats] = useState([]);
  const [totalCount, setTotalCount] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [successPopup, setSuccessPopup] = useState(false);
  const [selectedRow, setSelectedRow] = useState(null);
  const [showPopup, setShowPopup] = useState(false);
  const [sortBy, setSortBy] = useState('dateFormat');
  const [createdBy, setCreatedBy] = useState("3fa85f64-5717-4562-b3fc-2c963f66afa6");
  const [startsWith, setStartsWith] = useState(location.state?.searchQuery || '');


  const columns = [
    { key: 'slNo', label: 'Sl. No.', width: '100px', align: 'center' },
    { key: 'dateFormat', label: 'Date Format', width: '1fr', align: 'center' },
    { key: 'action', label: 'Action', width: '116px', height: '40px', align: 'center' },
  ];

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < Math.ceil(totalCount / pageSize)) {
      setCurrentPage(newPage);
    }
  };

  const confirmDelete = async () => {
    try {
      await deleteReportDateFormatById(selectedRow.id);
      setDateFormats((prevFormats) =>
        prevFormats.filter((format) => format.id !== selectedRow.id)
      );
      setShowPopup(false);
      setSuccessPopup(true);
    } catch (error) {
      console.error('Error deleting date format:', error);
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
  };

  const handleAdd = () => {
    navigate('/masters/report-settings/date-format/add');
  };

  const handleView = async (row) => {
    try {
      const response = await getReportDateFormatById(row.id);
      navigate('/masters/report-settings/date-format/add', {
        state: {
          dateFormatDetails: response.data,
          mode: 'view',
          searchQuery: startsWith, 
        },
      });
    } catch (error) {
      console.error('Error fetching date format details:', error);
    }
  };

  const handleEdit = async (row) => {
    try {
      const response = await getReportDateFormatById(row.id);
      navigate('/masters/report-settings/date-format/add', {
        state: {
          dateFormatDetails: response.data,
          mode: 'edit',
          searchQuery: startsWith, 
        },
      });
    } catch (error) {
      console.error('Error fetching date format details:', error);
    }
  };

  const handleDelete = (row) => {
    setSelectedRow(row);
    setShowPopup(true);
  };

  const fetchAllDateFormats = async () => {
    try {
      const response = await reportDateFormatService.getAllReportDateFormats(
        startsWith,
        currentPage,
        pageSize,
        sortBy,
        createdBy
      );
      setDateFormats(response.data);
      setTotalCount(response.totalCount || 0);
    } catch (error) {
      console.log("Fetching all the date format data has failed: ", error);
    }
  };
  
  useEffect(() => {
    fetchAllDateFormats();
  }, [startsWith, currentPage, pageSize, sortBy, createdBy]);

  useEffect(() => {
    if (location.state?.searchQuery) {
      setStartsWith(location.state.searchQuery); // Restore search when navigating back
    } else {
      setStartsWith(''); // Clear search when page is refreshed
    }
  }, []); // Runs only on initial render

  return (
    <div className='report-setting-table' >
      <LimsTable
        title="Date Format"
        columns={columns}
        data={dateFormats}
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
          isButton={true}
          buttonText="Delete"
          onClose={closePopup}
          onButtonClick={confirmDelete}
        />
      )}

      {successPopup && (
        <Swal
          icon="success"
          title="Deleted Successfully"
          onClose={() => {
            closeSuccessPopup();
            fetchAllDateFormats();
          }}
        />
      )}
    </div>
  );
};

export default ReportDateFormat;
