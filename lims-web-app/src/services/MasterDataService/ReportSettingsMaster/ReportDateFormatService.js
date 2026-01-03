import { Urls } from '../../apiURLS';
import axios from 'axios';

const BASE_URL = Urls.MASTERS;

export const getAllReportDateFormats = async (startsWith = '', pageNumber = 0, pageSize = 10, sortBy = 'format', createdBy = '') => {
    const response = await axios.post(
        `${BASE_URL}report-date-format/get-all?startsWith=${startsWith}&pageNumber=${pageNumber}&pageSize=${pageSize}&sortBy=${sortBy}`, 
        {}, 
        { headers: { 'createdBy': createdBy } }
    );
    return response.data;
};

export const saveReportDateFormat = async (reportDateFormat, createdBy) => {
    const response = await axios.post(
        `${BASE_URL}report-date-format/save`, 
        reportDateFormat, 
        { headers: { 'createdBy': createdBy } }
    );
    return response.data;
};

export const deleteReportDateFormatById = async (id) => {
    const response = await axios.delete(`${BASE_URL}report-date-format/${id}`);
    return response.data;
};

export const getReportDateFormatById = async (id) => {
    const response = await axios.get(`${BASE_URL}report-date-format/${id}`);
    return response.data;
};

export const updateReportDateFormat = async (id, updatedReportDateFormat, userId) => {
    const response = await axios.put(
        `${BASE_URL}report-date-format/update/${id}`, 
        updatedReportDateFormat, 
        { headers: { 'userId': userId } }
    );
    return response.data;
};

export const reportDateFormatService = {
    getAllReportDateFormats,
    saveReportDateFormat,
    deleteReportDateFormatById,
    getReportDateFormatById,
    updateReportDateFormat
};
