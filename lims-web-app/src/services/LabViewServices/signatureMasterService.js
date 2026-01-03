import axios from 'axios';
import { Urls } from "./../apiURLS";

// const BASE_URL = 'http://localhost:8081/signature-master';
const BASE_URL = Urls.LAB_MANAGEMENT;

export const getAllSignerNames = async (createdBy, keyword = "", flag = true, pageNumber = 0, pageSize = 10) => {
    const response = await axios.get(`${BASE_URL}signature/get-all`, {
        params: { createdBy, keyword, flag, pageNumber, pageSize }
    });
    return response.data;
};

export const saveSignature = async (data,createdBy) => {
    const response = await axios.post(`${BASE_URL}signature/save`, data,  {
        headers: {
          "Content-Type": "application/json",
          createdBy:createdBy,
        },
      });
    return response.data;
};

export const updateSignature = async (id, data) => {
    const response = await axios.put(`${BASE_URL}signature/update/${id}`, data);
    return response.data;
};

export const deleteSignature = async (id) => {
    const response = await axios.delete(`${BASE_URL}signature/delete/${id}`);
    return response.data;
};

export const getSignatureById = async (id) => {
    const response = await axios.get(`${BASE_URL}signature/get/${id}`);
    return response.data;
};

export const signatureMasterService = {
    getAllSignerNames,
    saveSignature,
    updateSignature,
    deleteSignature,
    getSignatureById
};