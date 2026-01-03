import React, { useEffect, useState } from "react";
import LimsTable from "../../LimsTable/LimsTable";
import {
  supportIssueTypesGetAll,
  getSupportIssueTypeById,
  deleteSupportIssueType,
} from "../../../services/supportedIssueTypeService";
import { useNavigate } from "react-router-dom";
import Swal from "../../Re-usable-components/Swal";

const SupportIssueTypeMaster = () => {
  const [supportIssueTypes, setSupportIssueTypes] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalCount, setTotalCount] = useState(0);
  const [popup, setPopup] = useState(null);
  const [startsWith, setStartsWith] = useState("");
  const navigate = useNavigate();

  const columns = [
    { key: "slNo", label: "Sl. No.", width: "100px", align: "center" },
    { key: "name", label: "Support Issue Type", width: "1fr", align: "center" },
    { key: "action", label: "Action", width: "116px", height: "40px", align: "center" },
  ];

  const fetchSupportIssueTypes = async (page, size, startsWith, sortedBy) => {
    try {
      const response = await supportIssueTypesGetAll(
        page,
        size,
        startsWith,
        sortedBy
      );
      const { data, totalCount } = response;
      setSupportIssueTypes(data || []);
      setTotalCount(totalCount || 0);
    } catch (error) {
      console.error("Error fetching support issue types:", error);
    }
  };

  useEffect(() => {
    fetchSupportIssueTypes(currentPage, pageSize, startsWith);
  }, [currentPage, pageSize, startsWith]);

  const handleSearchChange = async (query) => {
    setStartsWith(query);
    setCurrentPage(0);
  };

  const handleAdd = () => {
    navigate("/masters/support-issue/add");
  };

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < Math.ceil(totalCount / pageSize)) {
      setCurrentPage(newPage);
    }
  };

  const handlePageSizeChange = (newSize) => {
    setPageSize(newSize);
    setCurrentPage(0);
  };

  const handleView = async (row) => {
    try {
      const supportIssueTypeDetails = await getSupportIssueTypeById(row.id);
      navigate("/masters/support-issue/add", {
        state: {
          supportIssueTypeDetails,
          mode: "view",
        },
      });
    } catch (error) {
      console.error("Error fetching support issue type details:", error);
    }
  };

  const handleEdit = async (row) => {
    try {
      const supportIssueTypeDetails = await getSupportIssueTypeById(row.id);
      navigate("/masters/support-issue/add", {
        state: {
          supportIssueTypeDetails,
          mode: "edit",
        },
      });
    } catch (error) {
      console.error("Error fetching support issue type details for editing:", error);
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

  const handleDeleteConfirm = async (row) => {
    try {
      const response = await deleteSupportIssueType(row.id);
      if (response?.statusCode === "200 OK") {
        setSupportIssueTypes((prev) =>
          prev.filter((type) => type.id !== row.id)
        );
        setPopup({
          icon: "success",
          title: "Deleted Successfully",
          onClose: () => setPopup(null),
        });
      } else {
        setPopup({
          icon: "error",
          title: "Error deleting support issue type",
          onClose: () => setPopup(null),
        });
      }
    } catch (error) {
      console.error("Error deleting support issue type:", error);
    }
  };

  return (
    <div className="limsMasterTable">
      <LimsTable
        title="Support Issue Type"
        columns={columns}
        data={supportIssueTypes}
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

export default SupportIssueTypeMaster;
