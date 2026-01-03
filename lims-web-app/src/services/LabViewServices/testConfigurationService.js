import axios from "axios";
import { Urls } from "../apiURLS";

const BASE_URL = Urls.LAB_MANAGEMENT + "api/test-configuration/";

export const getAllTestConfigurations = async (labId, searchText = null, departmentName = null, pageNumber = 0, pageSize = 10) => {
    const searchParams = new URLSearchParams();
    searchParams.append('labId', labId);
    if (searchText) {
        searchParams.append('searchText', searchText);
    }
    if (departmentName) {
        searchParams.append('departmentName', departmentName);
    }
    searchParams.append('pageNumber', pageNumber);
    searchParams.append('pageSize', pageSize);
    const queryString = searchParams.toString();
    const response = await axios.post(`${BASE_URL}get-all?${queryString}`);
    return response.data;
};


export const saveTestConfiguration = async (data,createdBy) => {
    const response = await axios.post(`${BASE_URL}save`, data,  {
        headers: {
          "Content-Type": "application/json",
           createdBy:createdBy,
        },
      });
    return response.data;
};

export const updateTestConfiguration = async (id, data) => {
    const response = await axios.put(`${BASE_URL}update/${id}`, data);
    return response.data;
};

export const deleteTestConfiguration = async (id) => {
    const response = await axios.delete(`${BASE_URL}delete/${id}`);
    return response.data;
};

export const getTestConfigurationById = async (id) => {
    const response = await axios.get(`${BASE_URL}get/${id}`);
    return response.data;
};

export const testConfigurationService = {  
    getAllTestConfigurations,
    saveTestConfiguration,
    updateTestConfiguration,    
    deleteTestConfiguration,
    getTestConfigurationById
}