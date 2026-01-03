import React, { useEffect, useState } from 'react'
import LimsTable from '../../../LimsTable/LimsTable'
import Swal from '../../../Re-usable-components/Swal';
import { locationMasterService } from '../../../../services/locationMasterService';
import { useNavigate } from 'react-router-dom';

const Country = () => {
    const [showPopup, setShowPopup] = useState(false);
    const [selectedRow, setSelectedRow] = useState(null);
    const [successPopup, setSuccessPopup] = useState(false);

    const columns = [
        { key: 'slNo', label: 'Sl. No.', width: '80px', align: 'center' },
        { key: 'countryName', label: 'Country Name', width: '1fr', align: 'center' },
        { key: 'continentCode', label: 'Continent Code', width: '1fr', align: 'center' },
        { key: 'countryCode', label: 'Country Code', width: '1fr', align: 'center' },
        { key: 'phoneCode', label: 'Phone Code', width: '1fr', align: 'center' },
        { key: 'currencySymbol', label: 'Currency Symbol', align: 'center' },
        { key: 'currency', label: 'Currency', align: 'center' },
        { key: 'action', label: 'Action', width: '160px', height: '40px', align: 'center' }
    ];

    const [countries, setCountries] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [totalCount, setTotalCount] = useState(0);
    const [popupConfig, setPopupConfig] = useState(null);
    const [startsWith, setStartsWith] = useState('');
    const [sortedBy, setSortedBy] = useState('countryName');
    const [continentNames, setContinentNames] = useState([])

    const navigate = useNavigate();
    

    const fetchAllCountries = async (startsWith, continentNames, currentPage, pageSize, sortedBy) =>{
        try{
            const {data, totalCount} = await locationMasterService.getAllCountries(
                startsWith,
                continentNames,
                currentPage, 
                pageSize, 
                sortedBy
            );
           
          setCountries(()=>data);       
          setTotalCount(totalCount);
        } catch(error){
          console.log("Fetching the countries data has been failed: ", error);
        }
        
    }
    
    useEffect(() => {
        fetchAllCountries(startsWith, continentNames, currentPage, pageSize, sortedBy);
    },[startsWith, continentNames, currentPage, pageSize, sortedBy]);

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
        navigate('/masters/location/country/add');
    }

    const handleEdit = async (row) =>{
            const {data} = await locationMasterService.getCountryById(row.id);
            const countryDetails = data;
            navigate(`/masters/location/country/view-edit/${row.id}`, {
                state:{
                  countryDetails: countryDetails,
                    mode: 'edit'
                }
            })
        }
    
        const handleView = async (row) =>{
            const {data} = await locationMasterService.getCountryById(row.id);
            const countryDetails = data;
            navigate(`/masters/location/country/view-edit/${row.id}`, {
                state:{
                  countryDetails: countryDetails,
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
                        await locationMasterService.deleteCountryById(row.id);
                        setPopupConfig({
                            icon: 'success',
                            title: 'Deleted Successfully',
                            text: '',
                            onClose: () => {
                                setPopupConfig(null);
                                fetchAllCountries(startsWith, continentNames, currentPage, pageSize, sortedBy);
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
 
        <LimsTable
          title="Location Master/Country"
          columns={columns}
          data={countries}
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

export default Country