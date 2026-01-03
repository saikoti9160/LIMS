import LimsTable from "../../../LimsTable/LimsTable"
import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from 'react-router-dom';
import { deleteFontType, getAllFontTypes, getFontTypeById, updateFontType } from "../../../../services/MasterDataService/ReportSettingsMaster/ReportFontTypeService";
import Swal from "../../../Re-usable-components/Swal";
const ReportFontType = () => {
  const [fontTypes, setFontTypes] = useState([]);
  const [pageNumber, setPageNumber] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalCount, setTotalCount] = useState(0);
  const [popupConfig, setPopupConfig] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const { state } = useLocation();

  const navigate = useNavigate();
  const columns = [
    { key: 'slNo', label: 'Sl. No.', width: '100px', align: 'center' },
    { key: 'fontType', label: 'Font Type', width: '1fr', align: 'center' },
    { key: 'action', label: 'Action', width: '116px', height: '40px', align: 'center' },
  ];

  const fetchFontTypes = async (searchTearm) => {
    const createdBy = "3fa85f64-5717-4562-b3fc-2c963f66afa6";
    try {
      const res = await getAllFontTypes(searchTearm, pageNumber, pageSize, "fontType", createdBy);
      setFontTypes(res.data);
      setTotalCount(res.totalCount);
    } catch (error) {
      console.error("Error fetching font types:", error);
    }
  };

  useEffect(() => {
    if (state?.searchterm) {
      setSearchTerm(state?.searchterm);
    }
    fetchFontTypes(searchTerm);
  }, [pageNumber, pageSize]);

  const handleDelete = async (row) => {
    try {
      const response = await deleteFontType(row.id);

      if (response.statusCode === "200 OK") {
        setPopupConfig({
          icon: 'success',
          title: 'Deleted  Successfully',
          text: '',
          onClose: () => {
            setPopupConfig(null);
            fetchFontTypes();
          },
        })
      } else {
        setPopupConfig({
          icon: 'error',
          title: 'Failed to Delete',
          onClose: () => setPopupConfig(null),
        });
      }
    } catch (error) {
    }
  };


  const handleAction = (action, row) => {
    switch (action) {
      case 'view':
        navigate('/masters/report-settings/font-type/add', { state: { row: row, mode: 'view', searchTerm: searchTerm } });
        break;
      case 'add':
        navigate('/masters/report-settings/font-type/add');
        break;
      case 'update':
        navigate('/masters/report-settings/font-type/add',{state: {row: row, mode: 'Update', searchTerm: searchTerm }});
        break;
      case 'delete':
        handleDelete(row);
        break;
      case 'search':
        setPageNumber(0);
        setSearchTerm(row);
        fetchFontTypes(row);
        break;
      default:
        break;
    }
  }

  return (
    <div className="main-content">

      <LimsTable
        title="Font type"
        columns={columns}
        data={fontTypes}
        totalCount={totalCount}
        pageSize={pageSize}
        onView={(row)=>handleAction('view', row)}
        onAdd={(row) => handleAction('add', row)}
        onEdit={(row) => handleAction('update', row)}
        onDelete={(row) => handleAction('delete', row)}
        showAddButton
        showClearButton={false}
        showExportButton={false}
        showSearch={true}
        searchTerm={searchTerm}
        showPagination={true}
        onPageChange={(e) => setPageNumber(e)}
        currentPage={pageNumber}
        onPageSizeChange={(e) => setPageSize(e)}
        onHandleSearch={(e) => handleAction('search',e)}
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
  )
}
export default ReportFontType;