import axios from "axios";
import { Urls } from "./apiURLS";
const BASE_URL = Urls.MASTERS;

export const getAllDesignations = async (startsWith = '', pageNumber = 0, pageSize = 10, sortedBy = 'designationName') =>{
    const response = await axios.post(`${BASE_URL}designation/get-all?startsWith=${startsWith}&pageNumber=${pageNumber}&pageSize=${pageSize}&sortedBy=${sortedBy}`);
    return response.data;
}

export const addDesignation = async (designation, createdBy) =>{
    const response = await axios.post(`${BASE_URL}designation/save?createdBy=${createdBy}`, designation);
    return response.data;
}

export const getDesignationById = async (id) =>{
    const response = await axios.get(`${BASE_URL}designation/get/${id}`);
    return response.data;
}

export const updateDesignationById = async (id, designation, modifiedBy) =>{
    const response = await axios.put(`${BASE_URL}designation/update/${id}?modifiedBy=${modifiedBy}`, designation);
    return response.data;
}

export const deleteDesignationById = async (id) =>{
    const response = await axios.delete(`${BASE_URL}designation/delete/${id}`);
    return response.data;
}

export const designationService = {
    getAllDesignations,
    addDesignation,
    getDesignationById,
    updateDesignationById,
    deleteDesignationById
}