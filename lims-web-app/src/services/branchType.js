import axios from "axios";
import { Urls } from "./apiURLS";

const BASE_URL = Urls.MASTERS;

export const branchTypesGetAll = async (pageNumber, pageSize, searchKeyword = '', createdBy) => {
    try {
        const response = await axios.get(
            `${BASE_URL}branch/fetchAll?startsWith=${searchKeyword}&pageNumber=${pageNumber}&pageSize=${pageSize}&sortBy=branchTypeName&createdBy=${createdBy}`,
            {}
        );
        return response.data;
    } catch (error) {
        console.error("Error fetching branch types:", error);
        return error;
    }
};

export const addBranchType = async (branchType, createdBy) => {
    try {
        const response = await axios.post(
            `${BASE_URL}branch/save?createdBy=${createdBy}`,
            branchType
        );
        return response.data;
    } catch (error) {
        console.error("Error adding branch type:", error);
        return error;
    }
};

export const getBranchTypeById = async (id) => {
    try {
        const response = await axios.get(`${BASE_URL}branch/fetch/${id}`);
        return response.data;
    } catch (error) {
        console.error("Error fetching branch type by ID:", error);
        return error;
    }
};

export const updateBranchType = async (id, branchType) => {
    const modifiedBy = '3fa85f64-5717-4562-b3fc-2c963f66afa6';
    try {
        const response = await axios.put(
            `${BASE_URL}branch/update/${id}?modifiedBy=${modifiedBy}`,
            branchType,
            {
                headers: {
                    'Content-Type': 'application/json'
                }
            }
        );
        return response.data;
    } catch (error) {
        console.error("Error updating branch type:", error);
        return error;
    }
};

export const deleteBranchType = async (id) => {
    try {
        const response = await axios.delete(`${BASE_URL}branch/delete/${id}`);
        return response.data;
    } catch (error) {
        console.error("Error deleting branch type:", error);
        return error;
    }
};