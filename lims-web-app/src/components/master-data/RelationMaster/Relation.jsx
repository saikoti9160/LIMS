import React, { useEffect, useState } from 'react'
import { relationMasterService } from '../../../services/relationMasterService';
import LimsTable from '../../LimsTable/LimsTable';
import Swal from '../../Re-usable-components/Swal';
import { useNavigate } from 'react-router-dom';

const Relation = () => {
    const [showPopup, setShowPopup] = useState(false);
    const [selectedRow, setSelectedRow] = useState(null);
    const [successPopup, setSuccessPopup] = useState(false);

    const columns = [
        { key: 'slNo', label: 'Sl. No.', width: '80px', align: 'center' },
        { key: 'relationName', label: 'Relation Name', width: '1fr', align: 'center' },
        { key: 'action', label: 'Action', width: '160px', height: '40px', align: 'center' }
    ];

    const [relations, setRelations] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [totalCount, setTotalCount] = useState(0);
    const [popupConfig, setPopupConfig] = useState(null);
    const [startsWith, setStartsWith] = useState('');
    const [sortedBy, setSortedBy] = useState('relationName');

    const navigate = useNavigate();
    

    const fetchAllRelations = async (startsWith, currentPage, pageSize, sortedBy) =>{
        try{
            const {data, totalCount} = await relationMasterService.getAllRelations(
                startsWith,
                currentPage, 
                pageSize, 
                sortedBy
            );
            setRelations(() => data);
          setTotalCount(totalCount);
        } catch(error){
            console.log("Fetching all the relations data has been failed: ", error);
        }
       
    }
    
    useEffect(() => {
        fetchAllRelations(startsWith,currentPage, pageSize, sortedBy);
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
        navigate('/masters/relation/add');
    }

    const handleEdit = async (row) =>{
        const {data} = await relationMasterService.getRelationById(row.id);
        const getRelationData = data;
        navigate(`/masters/relation/view-edit/${row.id}`, {
            state:{
                relationData: getRelationData,
                mode: 'edit'
            }
        })
    }

    const handleView = async (row) =>{
        const {data} = await relationMasterService.getRelationById(row.id);
        const getRelationData = data;
        navigate(`/masters/relation/view-edit/${row.id}`, {
            state:{
                relationData: getRelationData,
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
                      await relationMasterService.deleteRelationById(row.id);
                      setPopupConfig({
                          icon: 'success',
                          title: 'Deleted Successfully',
                          text: '',
                          onClose: () => {
                              setPopupConfig(null);
                              fetchAllRelations(startsWith,currentPage, pageSize, sortedBy);
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

      {/* Ensure relations is not empty before passing to LimsTable */}
 
        <LimsTable
          title="Relation Master"
          columns={columns}
          data={relations}
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

         {/* Swal Popup */}
         {popupConfig && (
        <Swal
          icon={popupConfig.icon}
          title={popupConfig.title}
          text={popupConfig.text}
          onButtonClick={popupConfig.onButtonClick}
          onClose={popupConfig.onClose}
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

export default Relation