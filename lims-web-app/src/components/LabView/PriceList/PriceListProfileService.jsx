import React, { useEffect, useState } from 'react';
import searchIcon from "../../../assets/icons/search-icon.svg";
import { useNavigate } from 'react-router-dom';
import NoListImage from "../../../assets/images/price-list-noList.svg";
import { getAllprofiles } from "../../../services/LabViewServices/ProfileConfigurationService";
import LimsTable from '../../LimsTable/LimsTable';

const PriceListProfileService = () => {
    const [noList, setNoList] = useState(true);
    const navigate = useNavigate();
    const [searchText, setSearchText] = useState('');
    const [profileData, setProfileData] = useState([]);

    const handleSearch = (e) => {
        const value = e.target.value.trim();
        setSearchText(value);

        if (!value) {
            setNoList(true);
            setProfileData([]); 
        }
    };

    const fetchProfiles = async () => {
        if (!searchText) return;

        const createdBy = "3fa85f64-5717-4562-b3fc-2c963f66af77";
        const response = await getAllprofiles(createdBy, searchText);

        if (response.data?.length > 0) {
            setNoList(false);
            setProfileData(response.data);
        } else {
            setNoList(true);
            setProfileData([]);
        }
    };

    useEffect(() => {
        if (searchText) {
            fetchProfiles();
        }
    }, [searchText]);

    const transformedData = profileData.map((profile, index) => {
        const testList = profile.tests.map((test, i) => `${i + 1}. ${test.testName}`).join(" ");
        const sampleList = profile.tests.map((test, i) => `${i + 1}. ${test.sampleTypes.join(", ")}`).join(" ");

        return {
            profileName: profile.profileName,
            testName: testList,
            sampleType: sampleList,
            totalAmount: profile.totalAmount.toLocaleString(),
        };
    });

    const columns = [
        { key: 'slNo', label: 'S.No.', width: '100px', align: 'center' },
        { key: 'profileName', label: 'Profile Name', width: '1fr', align: 'center' },
        { key: 'testName', label: 'Tests', width: '1fr', align: 'center' },
        { key: 'sampleType', label: 'Sample Type', width: '1fr', align: 'center' },
        { key: 'totalAmount', label: 'Diagnostic Charge', width: '1fr', align: 'center' },
    ];

    return (
        <div className='priceListContainer'>
            <div className='title price-list-title'>Profile Service</div>
            <div className='price-list-filter'>
                <div className='price-list-filter-search'>
                    <input
                        type='text'
                        className='price-list-filter-input'
                        placeholder='Search By Profile Name'
                        onChange={handleSearch}
                        value={searchText}
                    />
                    <img src={searchIcon} alt='search' className='price-list-search-icon' />
                </div>
            </div>
            <div className='price-list-content'>
                {noList ? (
                    <div className='price-list-no-list'>
                        <img src={NoListImage} alt='' />
                    </div>
                ) : (
                    <LimsTable columns={columns} data={transformedData} />
                )}
            </div>
            <div className='price-list-buttons'>
                <button onClick={() => navigate("/lab-view/price-list")} className='clear'>Back</button>
            </div>
        </div>
    );
};

export default PriceListProfileService;
