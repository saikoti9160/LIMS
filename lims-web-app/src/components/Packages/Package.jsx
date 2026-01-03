import React, { useState } from "react";
import LimsTable from "../LimsTable/LimsTable";
import { useNavigate } from "react-router-dom";
import "./AddPackage.css";
import Swal from "../Re-usable-components/Swal";

function Package() {
  const navigate = useNavigate();
  const navigateUrl = "/addPackage";
  const [data, setData] = useState([
    {
      id: 1,
      slNo: 1,
      planName: "Basic ",
      monthlyPrice: 100,
      annualPrice: 1200,
      features: ["Notification", "Dashboard"],
    },
    {
      id: 2,
      slNo: 2,
      planName: "Premium",
      monthlyPrice: 200,
      annualPrice: 2400,
      features: ["Dashboard", "Enterprise"],
    },
    {
      id: 3,
      slNo: 3,
      planName: "Gold ",
      monthlyPrice: 300,
      annualPrice: 3600,
      features: ["Enterprise", "Notification"],
    },
  ]);
  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(5);
  const [totalCount, setTotalCount] = useState(data.length);
  const [popupConfig, setPopupConfig] = useState(null);

  const columns = [
    { key: "slNo", label: "Sl. No." },
    { key: "planName", label: "Plan Name", width: "1fr" },
    {
      key: "monthlyPrice",
      label: "Monthly Price",
      width: "1fr",
      align: "center",
    },
    {
      key: "annualPrice",
      label: "Annual Price",
      width: "1fr",
      align: "center",
    },
    { key: "action", label: "Action", width: "116px", align: "center" },
  ];

  const handleAdd = () => {
    navigate(navigateUrl, { state: { mode: "add" } });
  };

  const handleView = (row) => {
    const selectedFeatures = row.features.map((feature) => ({
      value: feature,
      label: feature,
    }));
    console.log("row ", row);
    navigate(navigateUrl, { state: { packageDetails: row, mode: "view" } });
  };

  const handleEdit = (row) => {
    navigate(navigateUrl, { state: { packageDetails: row, mode: "edit" } });
  };

  const handleDelete = (row) => {
    setPopupConfig({
      icon: "delete",
      title: "Are you sure?",
      text: "Delete",
      onButtonClick: () => {
        const updatedData = data.filter((item) => item.id !== row.id);
        setData(updatedData);
        setTotalCount(updatedData.length);
        setPopupConfig({
          icon: "success",
          title: "Deleted Successfully",
          onClose: () => setPopupConfig(null),
        });
      },
      onClose: () => setPopupConfig(null),
    });
  };

  const handleFilter = (filterText) => {
    const filteredData = data.filter((item) =>
      item.name.toLowerCase().includes(filterText.toLowerCase())
    );
    setData(filteredData);
    setTotalCount(filteredData.length);
  };

  const handlePageChange = (page) => {
    setCurrentPage(page);
  };

  const handlePageSizeChange = (size) => {
    setPageSize(size);
  };

  return (
    <div className="package-landing">
      <LimsTable
        title="Packages"
        columns={columns}
        data={data}
        totalCount={totalCount}
        currentPage={currentPage}
        pageSize={pageSize}
        onAdd={handleAdd}
        onView={handleView}
        onEdit={handleEdit}
        onDelete={handleDelete}
        onFilter={handleFilter}
        showAddButton
        showSearch
        showPagination
        onPageChange={handlePageChange}
        onPageSizeChange={handlePageSizeChange}
      />
      {popupConfig && <Swal {...popupConfig} />}
    </div>
  );
}

export default Package;
