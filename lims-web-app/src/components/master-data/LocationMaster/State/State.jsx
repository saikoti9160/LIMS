import React, { useEffect, useState } from 'react'
import Swal from '../../../Re-usable-components/Swal';
import LimsTable from '../../../LimsTable/LimsTable';
import { locationMasterService } from '../../../../services/locationMasterService';
import { useNavigate } from 'react-router-dom';

const State = () => {
    const [showPopup, setShowPopup] = useState(false);
    const [selectedRow, setSelectedRow] = useState(null);
    const [successPopup, setSuccessPopup] = useState(false);

    const columns = [
        { key: 'slNo', label: 'Sl. No.', width: '80px', align: 'center' },
        { key: 'stateName', label: 'State Name', width: '1fr', align: 'center' },
        { key: 'stateCode', label: 'State Code', width: '1fr', align: 'center' },
        { key: 'countryName', label: 'Country Name', width: '1fr', align: 'center' },
        { key: 'countryCode', label: 'Country Code', width: '1fr', align: 'center' },
        { key: 'action', label: 'Action', width: '160px', height: '40px', align: 'center' }
    ];

    const [states, setStates] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [totalCount, setTotalCount] = useState(0);
    const [popupConfig, setPopupConfig] = useState(null);
    const [startsWith, setStartsWith] = useState('');
    const [sortedBy, setSortedBy] = useState('stateName');
    const [countryNames, setCountryNames] = useState([])

    const navigate = useNavigate();
    

    const fetchAllStates = async (startsWith, countryNames, currentPage, pageSize, sortedBy) =>{
        try{
            let {data, totalCount} = await locationMasterService.getAllStates(
                startsWith,
                countryNames,
                currentPage, 
                pageSize, 
                sortedBy
            );
            setStates(() => data);
            setTotalCount(totalCount);
        } catch(error){
            console.log("Fetching the countries data has been failed: ", error);
        }
       
    }
    
    useEffect(() => {
        fetchAllStates(startsWith,countryNames, currentPage, pageSize, sortedBy);
    },[startsWith,countryNames, currentPage, pageSize, sortedBy]);

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
              navigate('/masters/location/state/add');
          }
      
          const handleEdit = async (row) =>{
                  const {data} = await locationMasterService.getStateById(row.id);
                  const stateDetails = data;
                  navigate(`/masters/location/state/view-edit/${row.id}`, {
                      state:{
                        stateDetails: stateDetails,
                          mode: 'edit'
                      }
                  })
              }
          
              const handleView = async (row) =>{
                  const {data} = await locationMasterService.getStateById(row.id);
                  const stateDetails = data;
                  navigate(`/masters/location/state/view-edit/${row.id}`, {
                      state:{
                        stateDetails: stateDetails,
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
                              await locationMasterService.deleteStateById(row.id);
                              setPopupConfig({
                                  icon: 'success',
                                  title: 'Deleted Successfully',
                                  text: '',
                                  onClose: () => {
                                      setPopupConfig(null);
                                      console.log('deleted');
                                      fetchAllStates(startsWith,countryNames, currentPage, pageSize, sortedBy);
                                      
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
    <div className='StateMaster'>
      {/* Ensure departments is not empty before passing to LimsTable */}
        <LimsTable
          title="Location Master/State"
          columns={columns}
          data={states}
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
          onClose={popupConfig.onClose}
          onButtonClick={popupConfig.onButtonClick}
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

export default State