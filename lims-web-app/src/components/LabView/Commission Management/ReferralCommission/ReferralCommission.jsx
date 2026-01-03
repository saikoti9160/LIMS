import React, { useEffect } from 'react'
import LimsTable from '../../../LimsTable/LimsTable'
import { useNavigate } from 'react-router-dom';
import { getAllReferralCommission } from '../../../../services/LabViewServices/ReferralCommissionService';


function ReferralCommission() {
  const navigate = useNavigate();
  const navigateUrl = '/lab-view/referral-commission/add';
  const createdBy = '3fa85f64-5717-4562-b3fc-2c963f66afa6';
  const [referralData, setReferralData] = React.useState([]);

  const columns = [
    { key: 'slNo', label: 'Sl. No.', width: '120px', align: 'center' },
    { key: 'referralName', label: 'Referral', align: 'center' },
    { key: 'referralId', label: 'Referral ID', align: 'center' },
    { key: 'action', label: 'Action', width: '150px' },
  ];

  const fetchReferralData = async () => {
    try {
      const response = await getAllReferralCommission(createdBy);
      if (response.statusCode === '200 OK') {
        const formattedData = response.data.map((item, index) => ({
          slNo: index + 1,
          referralName: item.referralName,
          referralId: item.referralSequenceId,
        }))
        setReferralData(formattedData);
      }
    } catch (error) {
      console.error('Error fetching data:', error);
      setReferralData([]);
    }
  };

  const handleAdd = () => {
    navigate(navigateUrl);
  };

  const handleView = () => {
    navigate(navigateUrl, {
      state: {
        mode: 'view',

      },
    });
  };

  const handleEdit = () => {
    navigate(navigateUrl, {
      state: {
        mode: 'edit',
      }
    });
  };
  const handleDelete = () => {
    navigate(navigateUrl);
  };

  useEffect(() => {
    fetchReferralData();
  }, []);

  return (
    <div className='OrganisationCommisionMapping'>
      <LimsTable
        title="Referral Commission Mapping"
        columns={columns}
        data={referralData}
        showAddButton
        showSearch
        onAdd={handleAdd}
        onView={handleView}
        onEdit={handleEdit}
        onDelete={handleDelete}
        showPagination
      />
    </div>
  )
}

export default ReferralCommission;