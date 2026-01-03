import React, { useEffect, useState } from 'react'
import Swal from '../../Re-usable-components/Swal';
import LimsTable from '../../LimsTable/LimsTable';
import { designationService } from '../../../services/designationService';
import { useNavigate } from 'react-router-dom';

const Designation = () => {
    const [showPopup, setShowPopup] = useState(false);
    const [selectedRow, setSelectedRow] = useState(null);
    const [successPopup, setSuccessPopup] = useState(false);

    const columns = [
        { key: 'slNo', label: 'Sl. No.', width: '80px', align: 'center' },
        { key: 'designationName', label: 'Designation', width: '1fr', align: 'center' },
        { key: 'action', label: 'Action', width: '160px', height: '40px', align: 'center' }
    ];

    const [designations, setDesignations] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [totalCount, setTotalCount] = useState(0);
    const [popupConfig, setPopupConfig] = useState(null);
    const [startsWith, setStartsWith] = useState('');
    const [createdBy, setCreatedBy] = useState('3fa85f64-5717-4562-b3fc-2c963f66afa7');
    // const createdBy = "3fa85f64-5717-4562-b3fc-2c963f66afa7"; // Replace with the actual createdBy value
    const [sortedBy, setSortedBy] = useState('designationName');

    const navigate = useNavigate();
    

    const fetchAllDesignations = async (startsWith, currentPage, pageSize, sortedBy) =>{
        try{
            const {data, totalCount} = await designationService.getAllDesignations(
                startsWith,
                currentPage, 
                pageSize, 
                sortedBy
            );
          setDesignations(() => data);
          setTotalCount(totalCount);
        } catch(error){
            console.log("Fetching all the designations data has been failed: ", error);
        }
       
    }
    
    useEffect(() => {
      fetchAllDesignations(startsWith,currentPage, pageSize, sortedBy);
    },[startsWith,currentPage, pageSize, sortedBy]);

      const onHandleSearch = (value) => {
        setCurrentPage(0);
        setStartsWith(() => value);
      };
      const onPageChange = (value) => {
        console.log('on page changes', value);
        
        if (value >= 0 && value < Math.ceil(totalCount / pageSize)) {
            setCurrentPage(()=>value);
        }
      };
      const onPageSizeChange = (value) => {
        setCurrentPage(0);
        setPageSize(() => value);
      };
    const closePopup = () => {
        setShowPopup(false);
      };
    
      const closeSuccessPopup = () => {
        setSuccessPopup(false);
      };

    const handleAdd = () => {
        navigate('/masters/designation/add');
    }

    const handleEdit = async (row) =>{
        const {data} = await designationService.getDesignationById(row.id);
        const getDesignationData = data;
        navigate(`/masters/designation/view-edit/${row.id}`, {
            state:{
                designationData: getDesignationData,
                mode: 'edit'
            }
        })
    }

    const handleView = async (row) =>{
        const {data} = await designationService.getDesignationById(row.id);
        const getDesignationData = data;
        navigate(`/masters/designation/view-edit/${row.id}`, {
            state:{
              designationData: getDesignationData,
                mode: 'view'
            }
        })
    }

    const handleDelete = (row) => {
      setPopupConfig({
          icon: 'delete',
          title: 'Are you sure?',
          text: 'Do you want to delete this Payment Mode?',
          onButtonClick: async () => {
              try {
                  await designationService.deleteDesignationById(row.id);
                  setPopupConfig({
                      icon: 'success',
                      title: 'Deleted Successfully',
                      text: '',
                      onClose: () => {
                          setPopupConfig(null);
                          fetchAllDesignations(startsWith,currentPage, pageSize, sortedBy);
                      }
                  });
              } catch (error) {
                  console.error("Error deleting designation:", error);
                  setPopupConfig({
                      icon: 'delete',
                      title: 'Failed to delete designation',
                      text: 'Please try again.',
                      onButtonClick: () => setPopupConfig(null),
                      onClose: () => setPopupConfig(null),
                  });
              }
          },
          onClose: () => setPopupConfig(null)  
      });
  };


  return (
    <div className="master-container">

      {/* Ensure designations is not empty before passing to LimsTable */}
 
        <LimsTable
          title="Designation Master"
          columns={columns}
          data={designations}
          totalCount={totalCount}
          pageSize={pageSize}
          showPagination
          showSearch
          showAddButton
          onHandleSearch={onHandleSearch}
          onPageChange={onPageChange}
          onPageSizeChange={onPageSizeChange}
          onAdd={handleAdd}
          onEdit={handleEdit}
          onView={handleView}
          onDelete={handleDelete}
          currentPage={currentPage}
        />
        {successPopup && (
        <Swal
          icon="success"
          title="Deleted Successfully"
          onClose={closeSuccessPopup}
        />
      )}
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

export default Designation