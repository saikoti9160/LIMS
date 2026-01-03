import React, { useEffect, useState } from 'react'
import LimsTable from '../../LimsTable/LimsTable'
import { useNavigate } from 'react-router-dom';
// import { deleteUserMaster, getUserMaster, getUserMasterById } from '../../services/userMasterService';
import Swal from '../../Re-usable-components/Swal';
import { deleteStaffById, getAllStaff, getStaffById, updateStaffById } from '../../../services/staffManagementService';
 
const StaffManagement = () => {
  const column = [
    { key: 'slNo', label: 'Sl. No.', width: '100px', align: 'center' },
    { key: 'displayUserId', label: 'User ID', width: '1fr', align: 'center' },
    { key: 'UserName', label: 'User Name' , width:'1fr',align: 'center' },
    { key: 'role', label: 'Role', width:'1fr', align: 'center' },
    { key: 'emailId', label: 'Email ID', width:'1fr', align: 'center' },
    { key: 'action', label: 'Action', width: '116px', height: '40px', align: 'center' }
  ];
 
  const [userMaster, setUserMaster] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalCount, setTotalCount] = useState(0);
  const navigate = useNavigate();
  const[searchBy,setSearchBy]=useState('');
 
  const [selectedRow, setSelectedRow] = useState(null);
  const [successPopup, setSuccessPopup] = useState(false);
  const [showPopup, setShowPopup] = useState(false);

  let fetchUserMaster = async (currentPage, size) => {
    try {
 
      const response = await getAllStaff(searchBy,currentPage, size);
      // console.log("response", response);
      if (response.statusCode === "200" && response.data) {
        const transformedData = response.data.map((user, index) => ({
          slNo: currentPage * size + index + 1,
          userId: user.id,// Store the original ID
          displayUserId: user.userSequenceId, // Store the user-friendly ID
          UserName: user.firstName+" "+user.lastName,
          role: user.role?.roleName || 'N/A',
          emailId: user.email || 'N/A',
          action: 'Actions',
        }));
        setUserMaster(transformedData);
        setTotalCount(response.totalCount);
      } else {
        console.error('Unexpected response format:', response);
      }
    } catch (error) {
      console.error('Error fetching user master:', error);
    }
  }
  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < Math.ceil(totalCount / pageSize)) {
      setCurrentPage(newPage);
    }
   console.log("usermaster",userMaster)
  };
 
  const handlePageSizeChange = (newSize) => {
    setPageSize(newSize);
    setCurrentPage(0);
  };
  const handleSearch = (e) => {
    setSearchBy(e);
    setCurrentPage(0);
  };
  useEffect(() => {
    fetchUserMaster(currentPage, pageSize);
  },[currentPage, pageSize,searchBy]);
  const handleAdd = () => {
    console.log("user master added")
    navigate("/add")
  }
  const handleView = async (row) => {
    console.log("user master view")
    try {
      const userMasterDetails = await getStaffById(row.userId);
      console.log("userMasterDetails view mode", userMasterDetails)
      navigate('/add', {
        state: {
          userMasterDetails,
          mode: 'view',
        },
      });
    } catch (error) {
      console.error('Error fetching department details:', error);
    }
  }
  const handleEdit = async (row) => {
    console.log("user master edit")
    try {
      const userMasterDetails = await getStaffById(row.userId);
      console.log("userMasterDetails edit mode", userMasterDetails)
      navigate('/add', {
        state: {
          userMasterDetails,
          mode: 'edit',
        },
      });
    } catch (error) {
      console.error('Error fetching department details:', error);
    }
  }
  const handleDelete = (row) => {
    console.log("row",row)
    deleteStaffById(row.userId)
    setSelectedRow(row);
    console.log("Row object on delete:", row); // Debugging row structure
    setShowPopup(true);
  }
  const confirmDelete = async () => {
    if (!selectedRow) return;
    try {
      console.log("Deleting user with ID:", selectedRow.userId);
      await deleteStaffById(selectedRow.userId);
      setUserMaster((prevDepartments) =>
        prevDepartments.filter((user) => user.userId !== selectedRow.userId)
      );
      setShowPopup(false);
      setSuccessPopup(true)
    } catch (error) {
      console.error("Error deleting department:", error);
      setShowPopup(false);
    }
  };
 
  const closePopup = () => {
    setShowPopup(false);
  };
 
  const closeSuccessPopup = () => {
    setSuccessPopup(false);
    fetchUserMaster(currentPage, pageSize);
  };
 
  return (
    <div className="lims-staff-table-container">
      <LimsTable
        title="Staff Management"
        columns={column}
        data={userMaster}
        totalCount={totalCount} // Pass the total count to LimsTable
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
        onHandleSearch={handleSearch}/>
 
      {/* Swal Popup */}
      {showPopup && (
        <Swal
          icon="delete"
          title="Are you sure?"
          text
          onClose={closePopup}
          onButtonClick={confirmDelete}
        />
 
      )}
      {/* Success Swal Popup */}
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
export default StaffManagement
