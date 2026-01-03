import Barcode from 'react-barcode';
import { useLocation, useNavigate } from 'react-router-dom';
import Swal from '../../Re-usable-components/Swal';
import { useState } from 'react';

function BarCodeManagementDetails() {
    const location = useLocation();
    const patient = location.state?.patient;
    const navigate = useNavigate();
    const navigateUrl = '/lab-view/barcode-management';
    const [popupConfig, setPopupConfig] = useState(null);

    if (!patient) {
        return <div className='BarCodeManagementDetails'>Loading patient details...</div>;
    }
    const handleBack = () => {
        navigate(navigateUrl);
    };
    const handlePrint = () => {
        setPopupConfig({
            icon: 'success',
            title: 'Printed Successfully',
            onClose: () => setPopupConfig(null),
        })
    };

    return (
        <div className='BarCodeManagementDetails'>
            <div className='BardCode-title'>Bar Code Management</div>
            <div className="barcode-management-container">
                <div className="barcode-card">
                    <p><strong>Name:</strong> {patient.patientName}</p>
                    <p><strong>Age/Gender:</strong> {patient.age}/{patient.gender}</p>
                    <p><strong>Patient ID:</strong> {patient.patientId}</p>
                    <p><strong>Test/Sample Type:</strong> {patient.testName}/{patient.sampleType}</p>
                    <p><strong>Bill ID:</strong> {patient.billID}</p>
                    <p><strong>Date & Time:</strong> {new Date(patient.billDate).toLocaleString()}</p>
                    <Barcode value={patient.patientId} width={2} height={40} />
                </div>
            </div>
            <div className="button-group">
                <button className="btn-secondary" onClick={handleBack}>Back</button>
                <button className="btn-primary" onClick={handlePrint}>Print Barcode</button>
            </div>
            {popupConfig && <Swal {...popupConfig} />}
        </div>
    );
}

export default BarCodeManagementDetails;
