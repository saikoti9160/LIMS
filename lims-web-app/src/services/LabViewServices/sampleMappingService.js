import axios from "axios";
import { Urls } from "./../apiURLS";

const BASE_URL = "http://localhost:8082/lab-management/"; 

export const getAllSampleMappings = async (labId, pageNumber = 0, pageSize = 10, searchText = "") => {
    const response = await axios.post(
        `${BASE_URL}api/sample-mapping/get-all?ladId=${labId}&pageNumber=${pageNumber}&pageSize=${pageSize}&searchText=${searchText}`
    );
    return response.data;
};

export const addSampleMapping = async (data, userId) => {
    const response = await axios.post(`${BASE_URL}api/sample-mapping/save`, data, {
        headers: { userId: userId }
    });
    return response.data;
};

export const editSampleMapping = async (id, data) => {
    const response = await axios.put(`${BASE_URL}api/sample-mapping/update/${id}`, data);
    return response.data;
};

export const deleteSampleMapping = async (id) => {
    const response = await axios.delete(`${BASE_URL}api/sample-mapping/delete/${id}`);
    return response.data;
};

export const viewSampleMapping = async (id) => {
    const response = await axios.get(`${BASE_URL}api/sample/get/${id}`);
    return response.data;
};

export const sampleMappingService = {
    getAllSampleMappings,
    addSampleMapping,
    editSampleMapping,
    deleteSampleMapping,
    viewSampleMapping
};
