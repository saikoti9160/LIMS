import axios from "axios"
import { Urls } from "../../apiURLS"

export const saveFontSize = async (payload, createdBy) => {
    const response = await axios.post(`${Urls.MASTERS}font-size/save`, payload, {
        headers: {
            'createdBy': createdBy
        },
    });
    return response.data;
}

export const getAllFontSizes = async (createdBy, pageSize, pageNumber, searchTerm = "") => {
    const response = await axios.get(`${Urls.MASTERS}font-size/${createdBy}?pageSize=${pageSize}&pageNumber=${pageNumber}&searchTerm=${searchTerm}`);
    return response.data;
}


export const updateFontSize = async (id, payload) => {
    const response = await axios.put(`${Urls.MASTERS}font-size/update/${id}`, payload);
    return response.data;
}

export const deleteFontSize = async (id) => {
    const response = await axios.delete(`${Urls.MASTERS}font-size/delete/${id}`);
    return response.data;
}