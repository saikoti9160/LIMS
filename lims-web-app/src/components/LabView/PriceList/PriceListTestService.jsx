import React, { useEffect, useState } from 'react'
import DropDown from "../../Re-usable-components/DropDown"
import searchIcon from "../../../assets/icons/search-icon.svg"
import NoListImage from "../../../assets/images/price-list-noList.svg"
import { useNavigate } from 'react-router-dom'
import LimsTable from "../../LimsTable/LimsTable"
import { getAllTestConfigurations } from '../../../services/LabViewServices/testConfigurationService'
import { getLabDepartments } from '../../../services/LabViewServices/labDepartmentService'

const PriceListTestService = () => {
    const [noList, setNoList] = useState(true)
    const navigate = useNavigate()
    const [searchText, setSearchText] = useState('')
    const [tableData, setTableData] = useState([])
    const [departments, setDepartments] = useState([])
    const [selectedDepartment, setSelectedDepartment] = useState("")
    const labId = "0e681d43-a8d2-47fd-9298-31c7872c8a59"

    const fetchTestData = async (search, department) => {
        try {
            const response = await getAllTestConfigurations(
                labId, 
                search || null, 
                department || null
            );
            if (response?.data) {         
                const transformedData = response.data.map((item, index) => ({
                    slNo: index + 1,
                    departmentName: item.labDepartment?.departmentName || "N/A",
                    testName: item.sampleMapping?.testName || "N/A",
                    sampleType: item.sampleMapping?.sampleTypes?.map(sample => sample).join(', ') || "N/A",
                    testPrice: item.testPrice || 0
                }));
                setTableData(transformedData);
                setNoList(transformedData.length === 0);
            } else {
                setTableData([]);
                setNoList(true);
            }
        } catch (error) {
            console.error("Error fetching test configurations:", error);
            setTableData([]);
            setNoList(true);
        }
    };

    const handleSearch = (e) => {
        const value = e.target.value; 
        setSearchText(value);
        if (value.trim() || selectedDepartment) {
            fetchTestData(value, selectedDepartment);
        } else {
            setTableData([]);
            setNoList(true);
        }
    };

    const handleDropdownChange = (object) => {
        const selectedDept = object.target.value?.departmentName || "";
        setSelectedDepartment(selectedDept);
        if (selectedDept || searchText.trim()) {
            fetchTestData(searchText, selectedDept);
        } else {
            setTableData([]);
            setNoList(true);
        }
    };

    useEffect(() => {
        const fetchLabDepartments = async () => {
            try {
                const response = await getLabDepartments();
                if (response?.data?.content) {
                    setDepartments(response.data.content);
                }
            } catch (error) {
                console.error("Error fetching lab departments:", error);
            }
        };
        
        fetchLabDepartments();
    }, []);

    const columns = [
        { key: 'slNo', label: 'S.No.', width: '100px', align: 'center' },
        { key: 'departmentName', label: 'Department', width: '1fr', align: 'center' },
        { key: 'testName', label: 'Test Name', width: '1fr', align: 'center' },
        { key: 'sampleType', label: 'Sample Type', width: '1fr', align: 'center' },
        { key: 'testPrice', label: 'Diagnostic Charge', width: '1fr', align: 'center' },
    ];

    return (
        <div className='priceListContainer'>
            <div className='title price-list-title'>Test Service</div>
            <div className='price-list-filter'>
                <div className='price-list-filter-search'>
                    <input
                        type='text'
                        className='price-list-filter-input'
                        placeholder='Search By Test Name'
                        onChange={handleSearch}
                        value={searchText}
                    />
                    <img src={searchIcon} alt='search' className='price-list-search-icon' />
                </div>
                <div className='price-list-filter-dropdown'>
                    <DropDown
                        placeholder='Filter By Department'
                        options={departments}
                        name='department'
                        fieldName="departmentName"
                        onChange={handleDropdownChange}
                        value={selectedDepartment}
                    />
                </div>
            </div>
            <div className='price-list-content'>
                {noList ? (
                    <div className='price-list-no-list'>
                        <img src={NoListImage} alt="No test data" />
                    </div>
                ) : (
                    <div className='price-list-table'>
                        <LimsTable
                            columns={columns}
                            data={tableData}
                        />
                    </div>
                )}
            </div>
            <div className='price-list-buttons'>
                <button onClick={() => { navigate("/lab-view/price-list") }} className='clear'>Back</button>
            </div>
        </div>
    )
}

export default PriceListTestService