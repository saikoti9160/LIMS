import { useLocation, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import InputField from "../../../Homepage/InputField";
import { saveHeaderSize, updateHeaderSize } from "../../../../services/MasterDataService/ReportSettingsMaster/ReportHeaderSizeService.js"
import Swal from "../../../Re-usable-components/Swal.jsx";

const AddEditViewReportHeaderSize = () => {

    const navigate = useNavigate();

    const [HeaderSize, setHeaderSize] = useState("");
    const { state } = useLocation();
    const [swal, setSwal] = useState(false);
    
    useEffect(() => {
        if(state?.mode === 'Update' || state?.mode === "View"){
            setHeaderSize(state?.row?.reportHeaderSize);
        }
    }, [state]);

    const handleHeaderSize = (e) => {
        setHeaderSize(e.target.value);
    }

    const handleSave = async() => {

        const payload = {
            reportHeaderSize: HeaderSize
        }
        const createdBy = '3fa85f64-5717-4562-b3fc-2c963f66afa6'; 
        try {

            if(state?.mode === 'Update'){
                const res = await updateHeaderSize(state?.row?.id, payload);
                if(res.statusCode === '200 OK'){
                    setSwal({icon: 'success', title: 'Updated Successfully', text: ''});
                }
            }
            else{
                const res = await saveHeaderSize(payload, createdBy);
                if(res.statusCode === '200 OK'){
                    setSwal({icon: 'success', title: 'Added Successfully', text: ''});
                }
            }
        }                            
        catch (error) {
            setSwal({icon: 'error', title: 'Something went wrong', text: ''});
        }
        
    }
    

    const handleClicks = (name) =>{
        switch (name) {
            case 'Back':
                setSwal(false);
                navigate('/masters/report-settings/header-size', {state: {searchterm: state?.searchTerm}});
                break;
            case "Save":
                handleSave();                
                break;

            default:
                break;
        }
    }

    return (
        <div className="profile-container">
            <div>
                <h2 className="title">Add Font Size</h2> 
                <div className="report-setting-inner-container">
                <div className='report-setting-sub-container'>
                    <InputField
                        placeholder="Enter Here"
                        label="Page Size"
                        type="text"
                        className="input-field-content"
                        value={HeaderSize}
                        onChange={handleHeaderSize}
                        readOnly={state?.mode === 'View'}
                        required
                    />
                </div>
                </div>
            </div>
            <div className="report-setting-button-div-container">
                <button className="btn-secondary" onClick={()=>handleClicks('Back')} >Back</button>
                {
                    !(state?.mode === 'View') &&
                    <button className="btn-primary" onClick={() => handleClicks('Save')} > {state?.mode === 'Update' ? 'Update' : 'Save' }</button>
                }
            </div>
            {
                swal &&
                <Swal
                    icon = {swal.icon}
                    title = {swal.title}
                    text = {swal.text}
                    onClose={()=>{handleClicks('Back');}}
                />
            }
        </div>
    )
}

export default AddEditViewReportHeaderSize;