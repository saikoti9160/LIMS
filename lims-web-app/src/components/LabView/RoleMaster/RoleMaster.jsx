import { useNavigate } from "react-router-dom";
import LimsTable from "../../LimsTable/LimsTable";
import "../RoleMaster/Rolemaster.css"
import { useEffect, useState } from "react";
import { deleteRole, getRoleById, getRoles } from "../../../services/RoleService";
import Swal from "../../Re-usable-components/Swal";

const RoleMaster = () => {
    const navigate = useNavigate();
    const [page, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [startsWith, setStartsWith] = useState('');
    const [status, setStatus] = useState('');
    const [createdBy, setCreatedBy] = useState('3fa85f64-5717-4562-b3fc-2c963f66afa6');
    const [totalCount, setTotalCount] = useState(0);
    const [data, setData] = useState([]);
    const columns = [{ key: "slNo", label: "Sl. No.", width: "100px", align: "center" },
    { key: "roleName", label: "Role Name", width: "1fr", align: "center" },
    { key: "active", label: "Status", width: "1fr", align: "center" },
    { key: "action", label: "Action", width: "116px", align: "center" }]
    const handleAdd = () => {
        navigate('/lab-view/addRoleMaster');
    };
    const [popupConfig, setPopupConfig] = useState(null);
    const fetchData = async () => {
        try {
            const response = await getRoles(page, pageSize, startsWith, status, createdBy);
            const formattedData = response.data.map((item) => ({
                ...item,
                active: item.active ? "Active" : "Inactive",
            }));
            setData(formattedData);

            setTotalCount(response.totalCount);
        } catch (error) {
            console.error('Error fetching data:', error);
        }
    }
    const handleSearchChange = (value) => {
        setStartsWith(value);
    };
    const handleStatusChange = (value) => {
        setStatus(value);
    };
    const handlePageChange = (page) => {
        setCurrentPage(page);
    };
    const handlePageSizeChange = (pageSize) => {
        setCurrentPage(0);
        setPageSize(pageSize);
    };
    const handleView = async (row) => {
        try {
            const response = await getRoleById(row.id);
            const roleData = response.data;
            navigate('/lab-view/addRoleMaster', {
                state: {
                    mode: 'view',
                    roleData: roleData,
                },
            });
        } catch (error) {
            console.error("Failed to fetch role data:", error);
        }
    };
    const handleEdit = async (row) => {
        try {
            const response = await getRoleById(row.id);
            const roleData = response.data;
            navigate('/lab-view/addRoleMaster', {
                state: {
                    mode: 'edit',
                    roleData: roleData,
                },
            });
        } catch (error) {
            console.error("Failed to fetch role data:", error);
        }
    }
    const handleDelete = async (row) => {

        const response = await deleteRole(row.id);
        if (response.statusCode === "200 OK") {

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
    useEffect(() => {
        fetchData();
    }, [page, pageSize, startsWith, status, createdBy]);
    return (
        <div className="role-l-container">
            <LimsTable
                columns={columns}
                title="Role master"
                showAddButton
                data={data}
                showStatus
                totalCount={totalCount}
                onEdit={handleEdit}
                onDelete={handleDelete}
                onHandleSearch={handleSearchChange}
                onHandleStatus={handleStatusChange}
                onPageChange={handlePageChange}
                onPageSizeChange={handlePageSizeChange}
                showSearch
                showPagination
                onAdd={() => { handleAdd() }}
                onView={(row) => { handleView(row) }}
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

export default RoleMaster;