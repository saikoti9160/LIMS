import React, { useEffect, useRef, useState } from 'react'
import InputField from '../../Homepage/InputField'
import searchIcon from '../../../assets/icons/search-icon.svg'
import NoListImage from "../../../assets/images/price-list-noList.svg"
import LimsTable from '../../LimsTable/LimsTable'
import { useNavigate } from 'react-router-dom'
import { getAllPatients, getPatientById } from '../../../services/LabViewServices/PatientRegistrationService'
import { fileUploadService } from '../../../services/fileUploadService'
import { set } from 'date-fns'

const ExistingPatient = () => {

const [searchQuery, setSearchQuery] = useState(""); 
const [editMode,setEditMode]=useState(false);
const [patientData,setPatienData]=useState([]);

const [currentPage, setCurrentPage] = useState(0);
const [hasMore, setHasMore] = useState(true);
const pageSize = 10;
const observer = useRef(null);

  const navigate = useNavigate();

    let filteredPatients = patientData.filter(patient =>
      patient.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      patient.phoneNumber.includes(searchQuery) ||
      patient.patientSequenceId.toLowerCase().includes(searchQuery.toLowerCase())
    );

    useEffect(() => {
        if (searchQuery) {

            setCurrentPage(0);
            setHasMore(true);
            fetchPatientDetails(searchQuery, 0, pageSize, true);
        }
    }, [searchQuery]);

  const handleSearch = (e) => {
      const value = e.target.value.trim(); 
    if(!value){
        setSearchQuery("");
        setPatienData([]); 
        return;
    }
    setSearchQuery(value);
};
const fetchPatientDetails = async (keyword,currentPage, size,reset = false) => {
    try{
        const createdBy = '06228e13-e32b-4420-b980-0f6b8744e133';
        const response = await getAllPatients(keyword,currentPage, size, createdBy);
        if (response.data.length < size) {
            setHasMore(false);
        }
        setPatienData((prevData) => reset ? response.data : [...prevData, ...response.data]); 
    }
    catch(error){
        console.error(error,"Error while fetching patient details");
    }
}

const handleEdit=async(patient)=>{
    try{
        const patientDetails=   await getPatientById(patient.id);
         navigate('/lab-view/newPatient', {
         state: { mode: 'edit', patientDetails }
});
    }catch(error){
        console.error('Error fetching patient details:',error);
    }

}

useEffect(() => {
    if (!hasMore) return;

    const lastElement = document.querySelector(".patient-list .patient-card:last-child");
    if (!lastElement) return;

    observer.current = new IntersectionObserver((entries) => {
        if (entries[0].isIntersecting) {
            setCurrentPage((prevPage) => {
                const nextPage = prevPage + 1;
                fetchPatientDetails(searchQuery, nextPage, pageSize);
                return nextPage;
            });
        }
    });

    observer.current.observe(lastElement);

    return () => {
        if (observer.current) observer.current.disconnect();
    };
}, [patientData, hasMore]);

  return (
    <div className='patient-table-container'>
       <div className='title'> Existing Patient</div>
        <div className='patient-search'>
            <img className='patient-search-icon' src={searchIcon} alt='' />
            <input type='text'
            className='patient-input'                                               
            onChange={handleSearch}
            placeholder="Search By Patient Id/Name/MobileNumber"/>
        </div>
        {!searchQuery && (
                <div className='patient-no-list'>
                    <img src={NoListImage} alt="No Patients" />
                </div>
            )}

            {searchQuery && (
                <div className='patient-list'>
                    {filteredPatients.length > 0 ? (
                        filteredPatients.map((patient) => (
                            <div className='patient-card' key={patient.patientSequenceId}>
                                <div className='patient-card-child'>
                                    <div className='patient-name'>{patient.name}</div>
                                    <p className='patient-id'>Patient ID : {patient.patientSequenceId}</p>
                                    <p className='patient-id'>Relation : {patient.relation}</p>
                                    <p className='patient-id'>Phone Number : {patient.phoneNumber}</p>
                                   <button className='select-button btn-primary' onClick={()=>{handleEdit(patient)}}>Select</button>
                                </div>
                            </div>
                        ))
                    ) : (
                        <div className='patient-no-list-one'>
                            <img src={NoListImage} alt="No Patients" />
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};

export default ExistingPatient
