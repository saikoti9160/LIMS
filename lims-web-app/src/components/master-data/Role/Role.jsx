import { useNavigate } from "react-router-dom";
import LimsTable from "../../LimsTable/LimsTable";
import { useEffect, useState } from "react";
import { deleteRole, getRoleById, getRoles } from "../../../services/RoleService";
import Swal from "../../Re-usable-components/Swal";
import InputField from "../../Homepage/InputField";

const Role = () => {
    const navigate = useNavigate();
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [totalCount, setTotalCount] = useState(0);
    const [data, setData] = useState([]);
    const [startsWith, setStartsWith] = useState('');
    const [status, setStatus] = useState('');
    const [createdBy, setCreatedBy] = useState('3fa85f64-5717-4562-b3fc-2c963f66afa6');

    const columns = [
        { key: 'slNo', label: 'Sl. No.',  },
        { key: 'roleName', label: 'Role',  },
        { 
            key: 'active', 
            label: 'Status', 
        },
        { key: 'action', label: 'Action', width: '116px', height: '40px', align: 'center' }
    ];

    const handleAdd = () => {
        navigate('/Addrole')
    };

    const handleView = async (row) => {
        try {
            const response = await getRoleById(row.id);
            const roleData = response.data;
            navigate('/Addrole', {
                state: {
                    mode: 'view',
                    roleData: roleData,
                },
            });
        } catch (error) {
            console.error("Failed to fetch role data:", error);
        }
    };

    const handleDelete = async(row) => {
  
       
        const response=await deleteRole(row.id);
        if(response.statusCode==="200 OK"){
   
        setPopupConfig({
            icon: 'success',
            title: 'Deleted  Successfully',
            text: '',
            onClose: () => {
            setPopupConfig(null);
            fetchData();
            },
        })
        }
       
    };
    const options = [
        { label: "Active", status:true },
        { label: "InActive", status: false },
    ]
    const handlePageChange = (newPage) => {
        console.log("newPage", newPage)
        if (newPage >= 0 && newPage < Math.ceil(totalCount / pageSize)) {
          setCurrentPage(newPage);
        }
      };
      const handlePageSizeChange = (newSize) => {
        setPageSize(newSize);
        setCurrentPage(0);
      };

    const handleEdit = async (row) => {
        try {
            const response = await getRoleById(row.id);
            const roleData = response.data;  
            navigate('/Addrole', {
                state: {
                    mode: 'edit',
                    roleData: roleData,
                },
            });
        } catch (error) {
            console.error("Failed to fetch role data:", error);        
        }
    };

    const fetchData = async () => {
        try {
            const response = await getRoles(currentPage, pageSize, startsWith, status, createdBy);
            setTotalCount(response.totalCount);
            console.log("response", response.data);
            const formattedData = response.data.map((item) => ({
                ...item,
                active: item.active ? "Active" : "Inactive", 
            }));;
            setData(formattedData);
        } catch (error) {
            console.error('Error fetching data:', error);
        }
    };

    const [popupConfig, setPopupConfig] = useState(null);
    const handleStatusChange = (selectedStatus) => {
        setStatus(selectedStatus.status); 
        fetchData();
    };

    const handleSearchChange = (query) => {
        setStartsWith(query);
        fetchData(); 
    };
    useEffect(() => {
        fetchData();
    }, [status, startsWith, currentPage, pageSize]);

    return (
        <div className="roleContent">
            <LimsTable
                columns={columns}
                title="Role"
                onAdd={handleAdd}
                onView={handleView}
                onEdit={handleEdit}
                totalCount={totalCount} 
                currentPage={currentPage}
                pageSize={pageSize}
                data={data}
                addRoute="/add-role"
                viewRoute="/view-role"
                editRoute="/edit-role"
                deleteRoute="/delete-role"
                showAddButton
                showSearch
                showStatus
                onDelete={handleDelete}
                showPagination
                onHandleSearch={handleSearchChange}
                onHandleStatus={handleStatusChange}
                onPageChange={handlePageChange}
                onPageSizeChange={handlePageSizeChange}
                dropDownOptions={options}
            />

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
export default Role;

