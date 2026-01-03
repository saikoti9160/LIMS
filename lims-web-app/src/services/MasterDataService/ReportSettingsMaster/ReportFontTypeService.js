import axios from "axios";
import { Urls } from "../../apiURLS";


export const saveFontType = async (payload, createdBy) => {
    const response = await axios.post(`${Urls.MASTERS}font-type/save`, payload, {
        headers :{'createdBy' : createdBy},
    });
    return response.data;
}

export const getAllFontTypes = async (searchTearm, pageNumber, pageSize, sortBy = 'fontType', createdBy) => {
    const response = await axios.post(`${Urls.MASTERS}font-type/get-all`, {}, { 
        params: {
            searchTearm: searchTearm,
            pageNumber: pageNumber, 
            pageSize: pageSize, 
            sortBy: sortBy
        },
        headers: {
            'createdBy': createdBy,
            // 'accept': '*/*'
        }
    });
    return response.data;
};

export const getFontTypeById = async (id) => {
    const response = await axios.get(`${Urls.MASTERS}font-type/${id}`);
    return response.data;
};

export const updateFontType = async (id, payload, userId) => {
    const response = await axios.put(`${Urls.MASTERS}font-type/update/${id}`, payload, {
        headers: {
            'userId': userId,
           
        },
    });
    return response.data;
};

export const deleteFontType = async (id) => {
    const response = await axios.delete(`${Urls.MASTERS}font-type/delete/${id}`);
    return response.data;
};