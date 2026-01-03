import axios from "axios";
import { Urls } from "./apiURLS";

// const BASE_URL = "http:localhost:8080/masterdata/paymentMode";

const BASE_URL = Urls.MASTERS;

export const getAllPaymentModes = async (startsWith = "", pageNumber = 0, pageSize = 10, sortedBy = "paymentModeName") => {
    const response = await axios.post(`${BASE_URL}paymentMode/get-all?startsWith=${startsWith}&pageNumber=${pageNumber}&pageSize=${pageSize}&sortedBy=${sortedBy}`);
    
    return response.data;
};

export const addPaymentModes = async (data, createdBy) => {
    const response  = await axios.post(`${BASE_URL}paymentMode/save?createdBy=${createdBy}`,data);
    return response.data;
}

export const editPaymentModes = async (id,data,modifiedBy) => {
    const response = await axios.put(`${BASE_URL}paymentMode/update/${id}?modifiedBy=${modifiedBy}`,data);
    return response.data;
}

export const deletePaymentModes = async (id) => {
    const response = await axios.delete(`${BASE_URL}paymentMode/delete/`+id);
    return response.data;
}

export const viewPaymentMode = async (id) => {
    const response = await axios.get(`${BASE_URL}paymentMode/get/`+id);
    return response.data;
}

export const paymentModeService = {
    getAllPaymentModes:getAllPaymentModes,
    addPaymentModes:addPaymentModes,
    editPaymentModes:editPaymentModes,
    deletePaymentModes:deletePaymentModes,
    viewPaymentMode:viewPaymentMode
}