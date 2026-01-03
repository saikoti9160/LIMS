import React, { useEffect, useState } from "react";
import { supportPrioritiesGetAll, getSupportPriorityById, deleteSupportPriority } from "../../../services/supportPriorityService";
import { useNavigate } from "react-router-dom";
import Swal from "../../Re-usable-components/Swal";
import LimsTable from "../../LimsTable/LimsTable";

const SupportPriorityMaster = () => {
  const [supportPriorities, setSupportPriorities] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalCount, setTotalCount] = useState(0);
  const [popup, setPopup] = useState(null);
  const [successPopup, setSuccessPopup] = useState(false);
  const [startsWith, setStartsWith] = useState("");
  const navigate = useNavigate();

  const columns = [
    { key: "slNo", label: "Sl. No.", width: "100px", align: "center" },
    { key: "name", label: "Support Priority", width: "1fr", align: "center" },
    { key: "action", label: "Action", width: "116px", height: "40px", align: "center" }
  ];

  const fetchSupportPriorities = async (page, size, startsWith, sortedBy) => {
    try {
      const { data, totalCount } = await supportPrioritiesGetAll(
        page,
        size,
        startsWith,
        sortedBy
      );
      setSupportPriorities([...data]);
      setTotalCount(totalCount);
    } catch (error) {
      console.error("Error fetching support priorities:", error);
    }
  };

  useEffect(() => {
    fetchSupportPriorities(currentPage, pageSize, startsWith);
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
    navigate("/masters/support-priority/add");
  };

  const handleView = async (row) => {
    try {
      const supportPriorityDetails = await getSupportPriorityById(row.id);
      navigate("/masters/support-priority/add", {
        state: {
          supportPriorityDetails,
          mode: "view",
        },
      });
    } catch (error) {
      console.error("Error fetching support priority details:", error);
    }
  };

  const handleEdit = async (row) => {
    try {
      const supportPriorityDetails = await getSupportPriorityById(row.id);
      navigate("/masters/support-priority/add", {
        state: {
          supportPriorityDetails,
          mode: "edit",
        },
      });
    } catch (error) {
      console.error("Error fetching support priority details for editing:", error);
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
      const response = await deleteSupportPriority(row.id);
      if (response?.statusCode === "200 OK") {
        setSupportPriorities((prev) =>
          prev.filter((priority) => priority.id !== row.id)
        );
        setPopup({
          icon: "success",
          title: "Deleted Successfully",
          onClose: () => setPopup(null),
        });
      } else {
        setPopup({
          icon: "error",
          title: "Error deleting support priority",
          onClose: () => setPopup(null),
        });
      }
    } catch (error) {
      console.error("Error deleting support priority", error);
      setPopup({
        icon: "error",
        title: "Error deleting support priority",
        onClose: () => setPopup(null),
      });
    }
  };

  return (
    <div className="limsMasterTable">
      <LimsTable
        title="Support Priority"
        columns={columns}
        data={supportPriorities}
        totalCount={totalCount}
        currentPage={currentPage}
        pageSize={pageSize}
        onAdd={handleAdd}
        onView={handleView}
        onEdit={handleEdit}
        onDelete={handleDelete}
        showSearch
        showAddButton
        showPagination
        onPageChange={handlePageChange}
        onPageSizeChange={handlePageSizeChange}
        onHandleSearch={handleSearchChange}
      />
      {popup && <Swal {...popup} />}
    </div>
  );
};

export default SupportPriorityMaster;
