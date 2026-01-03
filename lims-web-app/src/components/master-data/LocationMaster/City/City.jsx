import React, { useEffect, useState } from 'react'
import Swal from '../../../Re-usable-components/Swal';
import LimsTable from '../../../LimsTable/LimsTable';
import { locationMasterService } from '../../../../services/locationMasterService';
import { useNavigate } from 'react-router-dom';

const City = () => {
    const [showPopup, setShowPopup] = useState(false);
    const [selectedRow, setSelectedRow] = useState(null);
    const [successPopup, setSuccessPopup] = useState(false);

    const columns = [
        { key: 'slNo', label: 'Sl. No.', width: '80px', align: 'center' },
        { key: 'cityName', label: 'City Name', width: '1fr', align: 'center' },
        { key: 'stateName', label: 'State Name', width: '1fr', align: 'center' },
        { key: 'action', label: 'Action', width: '160px', height: '40px', align: 'center' }
    ];

    const [cities, setCities] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [totalCount, setTotalCount] = useState(0);
    const [popupConfig, setPopupConfig] = useState(null);
    const [startsWith, setStartsWith] = useState('');
    const [sortedBy, setSortedBy] = useState('cityName');
    const [stateNames, setStateNames] = useState([])

    const navigate = useNavigate();
    

    const fetchAllCities = async (startsWith, stateNames, currentPage, pageSize, sortedBy) =>{
        try{
            let {data, totalCount} = await locationMasterService.getAllCities(
                startsWith,
                stateNames,
                currentPage, 
                pageSize, 
                sortedBy
            );
            setCities(() => data);
            setTotalCount(totalCount);
        } catch(error){
            console.log("Fetching the cities data has been failed: ", error);
        }
       
    }
    
    useEffect(() => {
        fetchAllCities(startsWith, stateNames, currentPage, pageSize, sortedBy);
    },[startsWith, stateNames, currentPage, pageSize, sortedBy]);

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
              navigate('/masters/location/city/add');
          }
      
          const handleEdit = async (row) =>{
                  const {data} = await locationMasterService.getCityById(row.id);
                  const cityDetails = data;
                  navigate(`/masters/location/city/view-edit/${row.id}`, {
                      state:{
                        cityDetails: cityDetails,
                          mode: 'edit'
                      }
                  })
              }
          
              const handleView = async (row) =>{
                  const {data} = await locationMasterService.getCityById(row.id);
                  const cityDetails = data;
                  navigate(`/masters/location/city/view-edit/${row.id}`, {
                      state:{
                        cityDetails: cityDetails,
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
                            await locationMasterService.deleteCityById(row.id);
                            setPopupConfig({
                                icon: 'success',
                                title: 'Deleted Successfully',
                                text: '',
                                onClose: () => {
                                    setPopupConfig(null);
                                    fetchAllCities(startsWith, stateNames, currentPage, pageSize, sortedBy);
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
    <div className='countryMaster'>

      {/* Ensure departments is not empty before passing to LimsTable */}
        <LimsTable
          title="Location Master/City"
          columns={columns}
          data={cities}
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

export default City