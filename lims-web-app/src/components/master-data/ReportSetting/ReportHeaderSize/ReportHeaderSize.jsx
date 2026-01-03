import { useLocation, useNavigate } from "react-router-dom";
import Limstable from "../../../LimsTable/LimsTable";
import { useEffect, useState } from "react";
import { deleteHeaderSize, getAllHeaderSizes } from "../../../../services/MasterDataService/ReportSettingsMaster/ReportHeaderSizeService.js";
import Swal from "../../../Re-usable-components/Swal.jsx";

export const ReportHeaderSize = () => {

    const navigate = useNavigate();
    const createdBy = '3fa85f64-5717-4562-b3fc-2c963f66afa6';
    const [pageSize, setPageSize] = useState(10);
    const [pageNumber, setPageNumber] = useState(0);
    const [swal, setSwal] = useState(false);
    const [data, setData] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const { state } = useLocation();  

    const handleLoad = async (term) => {
        try {
            const response = await getAllHeaderSizes(createdBy, pageSize, pageNumber, term);
            setData(response);
        }
        catch (error) {
        }
    }

    useEffect(() => {
        if(state?.searchterm) {
            setSearchTerm(state?.searchterm);
        }
        handleLoad(state?.searchterm || '');
    }, [pageSize, pageNumber]);

    const columns = [
        {key: 'slNo', label: 'Sr. No.'},
        {key: 'reportHeaderSize', label: 'Header Size'},
        {key: 'action', label: 'Action'},
    ]

    const handleDelete = async (row) => {
        try {
            const res = await deleteHeaderSize(row.id);
            if (res.statusCode === '200 OK') {
                setSwal({icon: 'success', title: 'Deleted Successfully', text: ''});
                handleLoad('');
            }
        }
        catch (error) {
            setSwal({icon: 'error', title: 'Error', text: 'Error while deleting'});
        }
    }

    const handleAction = (action, row) => {
        switch (action) {
            case 'add':
                navigate('/masters/report-settings/header-size/add');
                break;
            case 'Update':            
                navigate('/masters/report-settings/header-size/add', {state: {row: row, mode: 'Update', searchTerm: searchTerm}});
                break;
            case "View":
                navigate('/masters/report-settings/header-size/add', {state: {row: row, mode: 'View', searchTerm: searchTerm}});
                break;
            case 'Delete':
                handleDelete(row);
                break;
            case "Search":
                setSearchTerm(row);
                handleLoad(row);
                break;
            default:
                break;
        }
    }

    const handlePagination = (action, val) => {        
        switch (action) {
            case 'pageNumber':
                console.log(data.totalCount%pageSize, pageSize*(pageNumber+1));
                if (pageSize * (pageNumber + 1) < data?.totalCount) {
                    setPageNumber(val);
                }
                
                break;
            case 'pageSize':
                setPageSize(val);
                break;
            default:
                break;
        }
    }
    
    return (
        <div className="main-content">
            <Limstable
                title="Header Size"
                showSearch={true}
                showAddButton={true}
                columns={columns}
                data={data.data}
                onAdd={()=>handleAction('add')}
                showPagination = {true}
                onPageChange={(e)=>handlePagination('pageNumber', e)}
                currentPage={pageNumber}
                onPageSizeChange={(e)=>handlePagination('pageSize', e)}
                onEdit={(row)=>handleAction('Update', row)}
                onView={(row)=>handleAction('View', row)}
                totalCount={data.totalCount}
                onHandleSearch={(e)=>handleAction('Search', e)}
                searchTerm={searchTerm}
                onDelete={(row)=>handleAction('Delete', row)}
            />
            {
                swal &&
                <Swal
                    icon = {swal.icon}
                    title = {swal.title}
                    text = {swal.text}
                    onClose={()=>setSwal(false)}
                />
            }
        </div>
    )

}
