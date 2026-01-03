import React, { useEffect, useState } from "react";
import LimsTable from "../../LimsTable/LimsTable";
import "./OrganizationMaster.css";
import { useNavigate } from "react-router-dom";
import Swal from "../../Re-usable-components/Swal";
import { use } from "react";
import { getAllOrganizationMaster, getOrganizationMasterById, organizationMasterGetById } from "../../../services/LabViewServices/OrganizationMasterService";

const OrganizationMaster = () => {
  const [popup, setPopup] = useState(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalCount, setTotalCount] = useState(0);
  const navigate = useNavigate();
  const [data, setData] = useState([]);

  const columns = [
    { key: "slNo", label: "Sl. No.", width: "100px", align: "center"},
    { key: "organizationSequenceId", label: "Organization Id", width: "1fr", align: "center"},
    { key: "name", label: "Organization Name", width: "1fr", align: "center"},
    { key: "phoneNumber", label: "Contact Number", width: "1fr", align: "center"},
    { key: "email", label: "Email Id", width: "1fr", align: "center"},
    { key: "action", label: "Action", width: "116px", height: "40px", align: "center"},
  ];

  const handleAdd = () => {
    navigate("/lab-view/organization-master/add");
  };
  const handleView = async (row) => {
    const organizationDetails = await getOrganizationMasterById(row.id);
    navigate("/lab-view/organization-master/add", {
      state: {
        mode: "view",
        organizationDetails,
      },
    });
  };
  const handleEdit = async (row) => {
    const organizationDetails = await getOrganizationMasterById(row.id);
    navigate("/lab-view/organization-master/add", {
      state: {
        mode: "edit",
        organizationDetails,
      },
    });
  };
  const handleDelete = () => {
    setPopup({
      icon: "delete",
      title: "Are you sure?",
      isButton: true,
      buttonText: "Delete",
      onButtonClick: () => handleDeleteConfirm(),
      onClose: () => {
        setPopup(null);
      },
    });
  };
  const handleDeleteConfirm = () => {
    setPopup({
      icon: "success",
      title: "Deleted Successfully",
      onClose: () => {
        setPopup(null);
      },
    });
  };

  const fetchAllOrganizations = async () => {
    const createdBy = "69323c18-6e5f-4d35-af42-fe5a898def41";
    const response = await getAllOrganizationMaster(
      currentPage,
      pageSize,
      createdBy
    );
    setData(response.data);
  };

  useEffect(() => {
    fetchAllOrganizations();
  }, []);
  return (
    <div className="organization-master">
      <div className="title organization-master-title">Organization Master</div>
      <div className="organization-master-table">
        <LimsTable
          columns={columns}
          data={data}
          onAdd={handleAdd}
          onView={handleView}
          onEdit={handleEdit}
          onDelete={handleDelete}
          showAddButton
          showSearch
        />
      </div>
      {popup && <Swal {...popup} />}
    </div>
  );
};

export default OrganizationMaster;
