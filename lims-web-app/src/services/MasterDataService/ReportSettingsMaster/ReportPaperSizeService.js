
import { Urls } from '../../apiURLS'
import axios from 'axios';
const BASE_URL=Urls.MASTERS;
export const getAllReportPaperSizes = async (startsWith = '', pageNumber = 0, pageSize = 10, sortBy = 'paperSize', createdBy = '') => {
    const response = await axios.post(`${BASE_URL}report-paper-size/get-all?startsWith=${startsWith}&pageNumber=${pageNumber}&pageSize=${pageSize}&sortBy=${sortBy}`, {}, { headers: { 'createdBy': createdBy } });
    return response.data;
};

export const addReportPaperSize = async (reportPaperSize, createdBy) => {
    const response = await axios.post(`${BASE_URL}report-paper-size/save`, reportPaperSize, { headers: { 'createdBy': createdBy } });
    return response.data;
};
export const deleteReportPaperSizeById= async(id)=>{
    const response= await axios.delete(`${BASE_URL}report-paper-size/${id}`);
    return response.data;
}
export const getReportPaperSizeById=async(id)=>{
    const response= await axios.get(`${BASE_URL}report-paper-size/${id}`);
    return response.data;
}
export const updateReportPaperSize=async(id, updatedReportPaperSize, userId) => {
    const response=await axios.put(`${BASE_URL}report-paper-size/update/${id}`, updatedReportPaperSize, { headers: { 'userId': userId } });
    return response.data;
}
export const reportPaperSizeService = {
    getAllReportPaperSizes,
    addReportPaperSize,
    deleteReportPaperSizeById,
    getReportPaperSizeById,
    updateReportPaperSize
};