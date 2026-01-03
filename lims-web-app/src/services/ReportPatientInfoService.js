import { Urls } from './apiURLS';
import axios from 'axios';

const BASE_URL = Urls.MASTERS;

export async function getAllReportPatientInfos(startsWith = '', pageNumber = 0, pageSize = 10, sortBy = 'patientInfoName', createdby = '') {
    const response = await axios.post(`${BASE_URL}report-patient-info/get-all`,
        { startsWith, pageNumber, pageSize, sortBy },
        { headers: { 'createdBy': createdby } }
    );
    return response.data;
}

export async function addReportPatientInfo(reportPatientInfo, createdby) {
    const response = await axios.post(`${BASE_URL}report-patient-info/save`, reportPatientInfo,
        { headers: { 'createdBy': createdby } });
    return response.data;
}

export async function deleteReportPatientInfoById(id) {
    const response = await axios.delete(`${BASE_URL}report-patient-info/delete/${id}`);
    return response.data;
}

export async function getReportPatientInfoById(id) {
    const response = await axios.get(`${BASE_URL}report-patient-info/get/${id}`);
    return response.data;
}

export async function updateReportPatientInfo(id, updatedReportPatientInfo, modifiedby) {
    const response = await axios.put(`${BASE_URL}report-patient-info/update/${id}`, updatedReportPatientInfo, { headers: { 'modifiedBy': modifiedby } });
    return response.data;
}
