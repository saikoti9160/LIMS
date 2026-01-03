import React, { useCallback, useEffect, useState } from "react";
import LimsTable from "../../LimsTable/LimsTable";
import { useNavigate } from "react-router-dom";
import Swal from "../../Re-usable-components/Swal";
import {
  deleteProfileConfigById,
  getAllprofiles,
  getProfileConfigById,
} from "../../../services/LabViewServices/ProfileConfigurationService";

const ProfileConfiguration = () => {
  const navigate = useNavigate();
  const navigateUrl = "/lab-view/profile-configuration/add";
  const [profiles, setProfiles] = useState([]);
  const [totalCount, setTotalCount] = useState(0);
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [searchQuery, setSearchQuery] = useState("");
  const [sortBy, setSortBy] = useState("profileName");
  const createdBy = "3fa85f64-5717-4562-b3fc-2c963f66afa6";

  const [popup, setPopup] = useState({
    show: false,
    type: "",
    message: "",
    onConfirm: null,
  });

  const columns = [
    { key: "slNo", label: "Sl. No.", width: "100px", align: "center" },
    {
      key: "profileName",
      label: "Profile Name",
      width: "1fr",
      align: "center",
    },
    { key: "action", label: "Actions", width: "116px", align: "center" },
  ];

  const fetchProfiles = useCallback(async () => {
    try {
      const response = await getAllprofiles(
        createdBy,
        searchQuery,
        currentPage,
        pageSize,
        sortBy
      );
      setProfiles(response.data || []);
      setTotalCount(response.totalCount || 0);
    } catch (error) {
      console.error("Error fetching profiles:", error);
    }
  }, [searchQuery, currentPage, pageSize, sortBy, createdBy]);

  useEffect(() => {
    fetchProfiles();
  }, [fetchProfiles]);

  const handleAdd = () => navigate(navigateUrl);

  const handleView = async (row) => {
    try {
      const response = await getProfileConfigById(row.id);
      navigate(navigateUrl, {
        state: { profileData: response.data, mode: "view" },
      });
    } catch (error) {
      console.error("Error fetching profile details:", error);
    }
  };

  const handleEdit = async (row) => {
    try {
      const response = await getProfileConfigById(row.id);
      navigate(navigateUrl, {
        state: { profileData: response.data, mode: "edit" },
      });
    } catch (error) {
      console.error("Error fetching profile details:", error);
    }
  };

  const handleDelete = (row) => {
    setPopup({
      show: true,
      type: "delete",
      message: "Do you want to delete this Profile Configuration?",
      onConfirm: async () => {
        try {
          await deleteProfileConfigById(row.id);
          setProfiles((prevData) =>
            prevData.filter((profile) => profile.id !== row.id)
          );
          setPopup({
            show: true,
            type: "success",
            message: "Deleted Successfully",
            onConfirm: null,
          });
        } catch (error) {
          console.error("Error deleting profile:", error);
        }
      },
    });
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

  const handleSearchChange = (query) => {
    setSearchQuery(query);
    setCurrentPage(0);
  };

  const closePopup = () =>
    setPopup({ show: false, type: "", message: "", onConfirm: null });

  return (
    <div className="lims-table-container">
      <LimsTable
        title="Profile Configuration"
        columns={columns}
        data={profiles}
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

      {popup.show && (
        <Swal
          icon={popup.type === "delete" ? "delete" : "success"}
          title={popup.type === "delete" ? "Are you sure?" : "Success"}
          text={popup.message}
          onClose={closePopup}
          onButtonClick={popup.onConfirm || closePopup}
        />
      )}
    </div>
  );
};

export default ProfileConfiguration;
