import React, { useEffect, useState } from "react";
import "./LabManagement.css";
import LimsTable from "../../../LimsTable/LimsTable";
import searchIcon from "../../../../assets/icons/search-icon.svg";
import { useDispatch, useSelector } from "react-redux";
import { setCities, setCountries, setStates} from "../../../../store/slices/locationMasterSlice";
import "react-datepicker/dist/react-datepicker.css";
import calenderIcon from "../../../../assets/icons/Vector.svg";
import DropDown from "../../../Re-usable-components/DropDown";
import { useNavigate } from "react-router-dom";
import DatePicker from "react-datepicker";
import { getAllCountries,  locationMasterService,  getAllStates,  getAllCities} from "../../../../services/locationMasterService";
import { labManagementGetById, labManagementDelete, labManagementGetAll} from "../../../../services/LabManagementService";
import Swal from "../../../Re-usable-components/Swal";

const LabManagement = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [labs, setLabs] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalCount, setTotalCount] = useState(0);
  const [dateRange, setDateRange] = useState([null, null]);
  const [startDate, endDate] = dateRange;
  const [activeLabs, setActiveLabs] = useState(0);
  const [inActiveLabs, setInActiveLabs] = useState(0);
  const [totalLabs, setTotalLabs] = useState(0);
  const [registeredLabs, setTotalregisteredLabs] = useState(0);
  const [request, setRequest] = useState({});
  const [popup, setPopup] = useState(false);
  const countries = useSelector((state) => state.locationMaster.countries);
  const fetchLabs = async (currentPage, size, request) => {
    try {
      const response = await labManagementGetAll(currentPage, size, request);
      const formattedData = response.data.lab.map((item) => ({
        ...item,
        createdOn: item.createdOn.split("T")[0],
        active: item.active ? "Active" : "Inactive",
      }));
      setLabs(formattedData);
      setActiveLabs(response.data.activeLabs);
      setInActiveLabs(response.data.totalinactivelabs);
      setTotalLabs(response.data.totalLabs);
      setTotalregisteredLabs(response.data.registeredLabs);
      setTotalCount(response.totalCount);
    } catch (error) {
      console.error("Error fetching labs:", error);
    }
  };

  const columns = [
    { key: "slNo", label: "Sl. No.", width: "100px", align: "center" },
    { key: "labName", label: "Lab Name", width: "1fr", align: "center" },
    { key: "email", label: "Email", width: "1fr", align: "center" },
    { key: "createdOn", label: "Created On", width: "1fr", align: "center" },
    { key: "createdBy", label: "Created By", width: "1fr", align: "center" },
    { key: "country", label: "Country", width: "1fr", align: "center" },
    { key: "active", label: "Status", width: "1fr", align: "center" },
    { key: "action", label: "Action", width: "116px", height: "40px", align: "center" },
  ];

  const handleAdd = () => {
    navigate("/lab-management/add", {
      state: {
        mode: "add",
      },
    });
  };

  const [dropdownStatus, setDropDownStatus] = useState("");
  const handleStatusChange = (a) => {
    setDropDownStatus(a.target.value.status);
  };

  const handleView = async (row) => {
    try {
      const labDetails = await labManagementGetById(row.id);
      const response = labDetails.data;
      navigate("/lab-management/add", {
        state: {
          response,
          mode: "view",
        },
      });
    } catch (error) {
      console.error("Error fetching lab details:", error);
    }
  };

  const handleEdit = async (row) => {
    try {
      const labDetails = await labManagementGetById(row.id);
      const response = labDetails.data;
      navigate("/lab-management/add", {
        state: {
          response,
          mode: "edit",
        },
      });
    } catch (error) {
      console.error("Error fetching lab details:", error);
    }
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

  const fetchLocation = async () => {
    const countryResponse = await getAllCountries("", [], 0, 250, "countryName");
    dispatch(setCountries(countryResponse.data));
    const stateRespnse = await getAllStates("", [], 0, 250, "stateName");
    dispatch(setStates(stateRespnse.data));
    const cityResponse = await getAllCities("", [], 0, 250, "cityName");
    dispatch(setCities(cityResponse.data));
  };

  useEffect(() => {
    fetchLocation();
  }, [dispatch]);

  useEffect(() => {
    fetchLabs(currentPage, pageSize, request);
  }, [currentPage, pageSize, request]);

  const handleDelete = async (row) => {
    setPopup({
      icon: "delete",
      title: "Are you sure?",
      isButton: true,
      buttonText: "Delete",
      onButtonClick: () => handleDeleteConfirm(row),
      onClose: handleDeleteClose,
    });
  };

  const handleDeleteClose = () => {
    setPopup(false);
  };

  const handleDeleteConfirm = async (row) => {
    let response = await labManagementDelete(row.id);
    if (response?.statusCode === "200 OK") {
      setPopup({
        icon: "success",
        title: "Deleted Successfully",
        onClose: handleDeleteClose,
      });
      fetchLabs(currentPage, pageSize, request);
    } else {
      setPopup({
        icon: "error",
        title: "error deleting lab",
        onClose: handleDeleteClose,
      });
    }
  };

  const handleFilterChange = (field, value) => {
    setRequest((prev) => ({ ...prev, [field]: value }));
  };

  const handleClear = () => {
    setRequest({});
    setDateRange([null, null]);
    setDropDownStatus("");
    document.querySelector(".lmdf-search-input").value = "";
  };

  return (
    <div className="lm-dashboard">
      <div className="lmd-heading">
        <span className="lmd-title">Lab Management</span>
      </div>
      <div className="lmd-filter">
        <div className="lmdf-search">
          <input
            type="text"
            className="lmdf-search-input"
            placeholder="Search"
            value={request.searchKey}
            onChange={(e) => handleFilterChange("searchKey", e.target.value)}
          />
          <img src={searchIcon} alt="search" className="lmdf-search-icon" />
        </div>
        <div className="lmdf-date">
          <DatePicker
            selected={startDate}
            onChange={(update) => {
              setDateRange(update);
              handleFilterChange("startDate", update?.[0] || "");
              handleFilterChange("endDate", update?.[1] || "");
            }}
            startDate={startDate}
            endDate={endDate}
            selectsRange
            dateFormat="MM/dd/yyyy"
            placeholderText="Select Date Range"
            className="custum-datepicker"
          />
          <img
            src={calenderIcon}
            alt="calendar"
            className="lmdf-calender-icon"
            onClick={() => document.querySelector(".custum-datepicker").focus()}
          />
        </div>
        <div className="lmdf-status">
          <DropDown
            placeholder="Status"
            options={[
              { label: true, status: "Active" },
              { label: false, status: "Inactive" },
            ]}
            name="status"
            fieldName={"status"}
            style={{ height: "35px" }}
            onChange={(selectedOption) => {
              handleFilterChange("status", selectedOption.target.value.label);
              handleStatusChange(selectedOption);
            }}
            value={dropdownStatus}
          />
        </div>
        <div className="lmdf-country">
          <DropDown
            placeholder="Country"
            options={countries || []}
            name="country"
            style={{ height: "35px" }}
            fieldName="countryName"
            value={request.country}
            onChange={(selectedOption) => {
              handleFilterChange("country", selectedOption?.target?.value?.countryName);
            }}
          />
        </div>
        <div className="lmdf-buttons">
          <button className="btn-clear" onClick={handleClear}>
            Clear
          </button>
          <button className="btn-add" onClick={handleAdd}>
            Add
          </button>
        </div>
      </div>
      <div className="lmd-content">
        <div className="lmdc-one">
          <span className="lmdc-title">Total Labs</span>
          <span className="lmdc-count">{totalLabs}</span>
        </div>
        <div className="lmdc-two">
          <span className="lmdc-title">Total Active Labs</span>
          <span className="lmdc-count">{activeLabs}</span>
        </div>
        <div className="lmdc-three">
          <span className="lmdc-title">Total Inactive Labs</span>
          <span className="lmdc-count">{inActiveLabs}</span>
        </div>
        <div className="lmdc-four">
          <span className="lmdc-title">Total Registered Labs</span>
          <span className="lmdc-count">{registeredLabs}</span>
        </div>
      </div>
      <LimsTable
        columns={columns}
        data={labs}
        showPagination
        onView={handleView}
        onEdit={handleEdit}
        onDelete={handleDelete}
        onPageChange={handlePageChange}
        onPageSizeChange={handlePageSizeChange}
        totalCount={totalCount}
        currentPage={currentPage}
        pageSize={pageSize}
      />
      {popup && <Swal {...popup} />}
    </div>
  );
};

export default LabManagement;
