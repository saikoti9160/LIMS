import { useEffect, useState } from "react";
import InputField from "../../../Homepage/InputField";
import { useLocation, useNavigate } from "react-router-dom";
import { getAllFontTypes, saveFontType, updateFontType } from "../../../../services/MasterDataService/ReportSettingsMaster/ReportFontTypeService";
import Swal from "../../../Re-usable-components/Swal";

const AddReportFontType = () => {

    const navigate = useNavigate();
    const location = useLocation();

    const [fontType, setFontType] = useState({});
    const [data, setData] = useState([]);
    const [popupConfig, setPopupConfig] = useState(null);
    const [viewMode, setViewMode] = useState(false);
    const [editMode, setEditMode] = useState(false);

    useEffect(() => {
        const fontTypeDetails = location.state
        const mode = location.state?.mode;
        if (mode === 'view') {
            console.log("fontTypeDetails", fontTypeDetails.row);
            
            setViewMode(true);
            setFontType(fontTypeDetails.row);
        } else if (mode === 'Update') {
            setEditMode(true)
            setFontType(fontTypeDetails.row);
        }
    }, [location.state])

    const handleSave = async () => {
        const createdBy = '3fa85f64-5717-4562-b3fc-2c9c63f66afa6';

        try {
            const response = await saveFontType(fontType, createdBy);

            if (response.statusCode === "200 OK") {
                setPopupConfig({
                    icon: 'success',
                    title: 'Added Successfully',
                    onClose: () => navigate('/masters/report-settings/font-type'),
                });
            } else {
                setPopupConfig({
                    icon: 'error', // Use the error icon
                    title: 'Failed to Save',
                    onClose: () => setPopupConfig(null),
                });
            }
        } catch (error) {
            console.error('Error:', error);

            setPopupConfig({
                icon: 'error',  // Use the error icon
                title: 'Failed to Save',
                onClose: () => setPopupConfig(null),
            });
        }
    };



    const handleUpdate = async () => {


        try {
            const response = await updateFontType(fontType.id, fontType, "3fa85f64-5717-4562-b3fc-2c963f66afa6");

            if (response.statusCode === "200 OK") {
                setPopupConfig({
                    icon: 'success',
                    title: 'Updated Successfully',
                    onClose: () => navigate('/masters/report-settings/font-type'),
                });
            } else {
                setPopupConfig({
                    icon: 'error',
                    title: 'Failed to Update',
                    onClose: () => setPopupConfig(null),
                });
            }
        } catch (error) {
            console.error('Error:', error);

            setPopupConfig({
                icon: 'error',  // Use the error icon
                title: 'Failed to Update',
                onClose: () => setPopupConfig(null),
            });
        }
    }


    const handleFontType = (e) => {
        e.preventDefault()
        setFontType({
            ...fontType, fontType: e.target.value
        })
    }

    return (
        <div className="profile-container">
            <div>
                <h2 className="title">
                    {viewMode ? 'View Page Size' : editMode ? 'Edit Page Size' : 'Add Page Size'}
                </h2>
                <div className="report-setting-inner-container">
                    <div className='report-setting-sub-container'>
                        <InputField
                            placeholder="Enter Here"
                            label="Font Type"
                            type="text"
                            className="input-field-content"
                            value={fontType?.fontType}
                            onChange={handleFontType}
                            data={data}
                            disabled={viewMode}
                            readOnly={location.state?.mode === 'View'}
                            required
                        />
                    </div>
                </div>
            </div>
            <div className="report-setting-button-div-container">
                <button className="btn-secondary" onClick={() => navigate('/masters/report-settings/font-type',
                    { state: { searchterm: location.state?.searchTerm } })}>Back</button>

                {!viewMode && !editMode && (
                    <button className="btn-primary" onClick={handleSave}>Save</button>
                )}

                {editMode && (
                    <button className="btn-primary" onClick={handleUpdate}>Update</button>
                )}
            </div>

            {popupConfig && (
                <Swal
                    icon={popupConfig.icon}
                    title={popupConfig.title}
                    text={popupConfig.text}
                    onClose={popupConfig.onClose}
                />
            )}
        </div>
    )
}

export default AddReportFontType;