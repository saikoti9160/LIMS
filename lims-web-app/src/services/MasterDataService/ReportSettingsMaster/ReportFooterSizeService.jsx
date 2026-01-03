import { Urls } from '../../apiURLS'
import axios from 'axios';
const BASE_URL=Urls.MASTERS;
export const getAllReportFooterSizes = async (startsWith = '', pageNumber = 0, pageSize = 10, sortBy = 'footerSize', createdBy = '') => {
    const response = await axios.post(`${BASE_URL}report-footer-size/get-all?startsWith=${startsWith}&pageNumber=${pageNumber}&pageSize=${pageSize}&sortBy=${sortBy}`, {}, { headers: { 'createdBy': createdBy } });
    return response.data;
};

export const addReportFooterSize = async (reportFooterSize, createdBy) => {
    const response = await axios.post(`${BASE_URL}report-footer-size/save`, reportFooterSize, { headers: { 'createdBy': createdBy } });
    return response.data;
};
export const deleteReportFooterSizeById= async(id)=>{
    const response= await axios.delete(`${BASE_URL}report-footer-size/delete/${id}`);
    return response.data;
}
export const getReportFooterSizeById=async(id)=>{
    const response= await axios.get(`${BASE_URL}report-footer-size/get/${id}`);
    return response.data;
}
export const updateReportFooterSize=async(id, updatedReportFooterSize, userId) => {
    const response=await axios.put(`${BASE_URL}report-footer-size/update/${id}`, updatedReportFooterSize, { headers: { 'userId': userId } });
    return response.data;
}
export const reportFooterSizeService = {
    getAllReportFooterSizes,
    addReportFooterSize,
    deleteReportFooterSizeById,
    getReportFooterSizeById,
    updateReportFooterSize
};