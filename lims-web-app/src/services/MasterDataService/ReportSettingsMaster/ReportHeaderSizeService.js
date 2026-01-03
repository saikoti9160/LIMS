import axios from "axios"
import { Urls } from "../../apiURLS"

export const saveHeaderSize = async (payload, createdBy) => {
    const response = await axios.post(`${Urls.MASTERS}report-header-size/save`, payload, {
        headers: {
            'createdBy': createdBy
        },
    });
    return response.data;
}

export const getAllHeaderSizes = async (createdBy, pageSize, pageNumber, searchTerm) => {
    const response = await axios.get(`${Urls.MASTERS}report-header-size/${createdBy}?pageSize=${pageSize}&pageNumber=${pageNumber}&searchTerm=${searchTerm}`);
    return response.data;
}

export const updateHeaderSize = async (id, payload) => {
    const response = await axios.put(`${Urls.MASTERS}report-header-size/update/${id}`, payload);
    return response.data;
}

export const deleteHeaderSize = async (id) => {
    const response = await axios.delete(`${Urls.MASTERS}report-header-size/delete/${id}`);
    return response.data;
}