import { useNavigate } from "react-router-dom";
import LimsTable from "../../LimsTable/LimsTable"
import { useEffect, useState } from "react";
import { deletePhlebotomist, getPhlebotomistById, getPhlebotomistMaster } from "../../../services/LabViewServices/PhlebotomistMasterService";
import Swal from "../../Re-usable-components/Swal";


const PhlebotmistMaster = () => {
    const navigate = useNavigate();
    const columns = [
        { key: "slNo", label: "Sl. No." },
        { key: "phlebotmistId", label: "Phlebotmist Id", },
        { key: "phlebotmistName", label: "Phlebotmist Name", },
        { key: "email", label: "Email Id", },
        { key: "action", label: "Action", width: "116px", align: "center" },
    ]
    const handleAdd = () => {
        navigate("/addPhlebotmistMaster");

    }
    const handleView = async(row) => {
        console.log("rowwww",row);
        try {
            const response = await getPhlebotomistById(row.id);
            const phlebotmistData = response.data;
            navigate('/addPhlebotmistMaster', {
                state: {
                    mode: 'view',
                    phlebotmistData : phlebotmistData,
                   
                },
            });
        } catch (error) {
            console.error("Failed to fetch role data:", error);
        }


    }
    const handleEdit = async (row) => {

        try {
            const response = await getPhlebotomistById(row.id);
            const phlebotmistData = response.data;
            navigate('/addPhlebotmistMaster', {
                state: {
                    mode: 'edit',
                    phlebotmistData : phlebotmistData,
                   
                },
            });
        } catch (error) {
            console.error("Failed to fetch role data:", error);
        }

        

    }
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [data, setData] = useState([]);
    const[totalCount,setTotalCount]=useState(0);

    const fetchData = async () => {
        const response = await getPhlebotomistMaster(currentPage, pageSize, keyword);

        if (response.statusCode === "200 OK") {

            const transformedData = response.data.map((user, index) => ({
                id: user.id,
                slNo: currentPage * pageSize + index + 1,
                phlebotmistId: user.phlebotomistSequenceId,
                phlebotmistName: user.name,
                email: user.email,
            }));
            setData(transformedData);
            setTotalCount(response.totalCount);
        }

    }
    const [popupConfig, setPopupConfig] = useState(null);
    const handleDelete=async(row)=>{
        const response=await deletePhlebotomist(row.id);
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
            alert("deleted successfully");
            fetchData();
        }
    }
    const [keyword, setKeyword] = useState('');
    const handleSearch=(query)=>{
        console.log("query",query); 
        setCurrentPage(0);
        setKeyword(query);
    }
    useEffect(() => {
        fetchData();
    }, [keyword, currentPage, pageSize]);
    return (
        <div className="phlebotmist-master-table">
            <LimsTable
                columns={columns}
                data={data}
                showAddButton={true}
                showSearch={true}
                onHandleSearch={handleSearch}
                title="Phlebotmist Master"
                onAdd={handleAdd}
                onView={handleView}
                onEdit={handleEdit} 
                onDelete={handleDelete}
                showPagination={true}
                currentPage={currentPage}
                setCurrentPage={setCurrentPage}
                totalCount={totalCount}
                pageSize={pageSize}/>
                   {popupConfig && (
            <Swal icon={popupConfig.icon} title={popupConfig.title} text={popupConfig.text} onClose={popupConfig.onClose} />
        )}
        
        </div>

     
        
    )
}
export default PhlebotmistMaster;