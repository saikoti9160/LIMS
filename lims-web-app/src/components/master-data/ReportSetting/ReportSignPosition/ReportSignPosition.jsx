import React, { useEffect, useState } from "react";
import Swal from "../../../Re-usable-components/Swal";
import LimsTable from "../../../LimsTable/LimsTable";
import { useLocation, useNavigate } from "react-router-dom";
import {
  deleteReportSignPositionById,
  getAllReportSignPositions,
  getReportSignPositionById,
} from "../../../../services/MasterDataService/ReportSettingsMaster/ReportSignPositionService";

const ReportSignPosition = () => {
  const [signPosition, setSignPosition] = useState([]);
  const [totalCount, setTotalCount] = useState(0);
  const navigate = useNavigate();
  const location = useLocation();
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [successPopup, setSuccessPopup] = useState(false);
  const [selectedRow, setSelectedRow] = useState(null);
  const [startsWith, setStartsWith] = useState(
    location.state?.previousSearch || ""
  );
  const [showPopup, setShowPopup] = useState(false);
  const [sortBy, setSortBy] = useState("signPosition");
  const createdBy = "07131f7b-50c0-4889-bfae-777579d19f31";

  const columns = [
    { key: "slNo", label: "Sl. No.", width: "100px", align: "center" },
    {
      key: "signPosition",
      label: "Sign Position",
      width: "1fr",
      align: "center",
    },
    {
      key: "action",
      label: "Action",
      width: "116px",
      height: "40px",
      align: "center",
    },
  ];

  const handlePaperChange = (newPaper) => {
    if (newPaper >= 0 && newPaper < Math.ceil(totalCount / pageSize)) {
      setCurrentPage(newPaper);
    }
  };

  const confirmDelete = async () => {
    try {
      await deleteReportSignPositionById(selectedRow.id);
      setSignPosition((prev) =>
        prev.filter((signPosition) => signPosition.id !== selectedRow.id)
      );
      setShowPopup(false);
      setSuccessPopup(true);
    } catch (error) {
      console.error("Error deleting sign position:", error);
      setShowPopup(false);
    }
  };

  const handlesignPositionChange = (newSize) => {
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
    fetchAllSignPosition();
  };

  const handleAdd = () => {
    navigate("/masters/report-settings/sign-position/add");
  };

  const handleView = async (row) => {
    try {
      const response = await getReportSignPositionById(row.id);
      navigate("/masters/report-settings/sign-position/add", {
        state: {
          signPositionDetails: response.data,
          mode: "view",
          previousSearch: startsWith,
        },
      });
    } catch (error) {
      console.error("Error fetching sign position details:", error);
    }
  };

  const handleEdit = async (row) => {
    try {
      const response = await getReportSignPositionById(row.id);
      navigate("/masters/report-settings/sign-position/add", {
        state: {
          signPositionDetails: response.data,
          mode: "edit",
          previousSearch: startsWith,
        },
      });
    } catch (error) {
      console.error("Error fetching Paper Size details:", error);
    }
  };

  const handleDelete = (row) => {
    setSelectedRow(row);
    setShowPopup(true);
  };

  const fetchAllSignPosition = async () => {
    try {
      const response = await getAllReportSignPositions(
        startsWith,
        currentPage,
        pageSize,
        sortBy,
        createdBy
      );
      setSignPosition(response.data);
      setTotalCount(response.totalCount || 0);
    } catch (error) {
      console.log("Fetching all the papersize data has failed: ", error);
    }
  };

  useEffect(() => {
    fetchAllSignPosition();
  }, [startsWith, currentPage, pageSize, sortBy, createdBy]);
  useEffect(() => {
    if (location.state?.previousSearch) {
      setStartsWith(location.state.previousSearch);
    }
  }, [location.state]);
  return (
    <div className="report-setting-table">
      <LimsTable
        title="Sign Position"
        columns={columns}
        data={signPosition}
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
        onPageChange={handlePaperChange}
        onPageSizeChange={handlesignPositionChange}
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
  );
};

export default ReportSignPosition;
