import axios from "axios";
import { Urls } from '../apiURLS';
const BASE_URL = Urls.LAB_MANAGEMENT;

export const getPhlebotomistMaster = async (page = 0, size = 10, startsWith = "", status = "", createdBy = "6a4ba7df-baa9-4038-8b50-5f52b378a716") => {
    const url = `${BASE_URL}phlebotomist/get-all?startsWith=${startsWith}&status=${status}&createdBy=${createdBy}&pageNumber=${page}&pageSize=${size}&sortedBy=phlebotomistName`;
    const response = await axios.get(url);
    return response.data;
};

export const addPhlebotomist = async (data, createdBy) => {
    const response = await axios.post(`${BASE_URL}phlebotomist/save`, data,{
        headers: {
            "Content-Type": "application/json",
            "createdBy": createdBy
        },
    });
    return response.data;
};
export const getPhlebotomistById = async (id) => {
    const response = await axios.get(`${BASE_URL}phlebotomist/get/${id}`);
    return response.data;
}
export const UpdatePhlebotomist = async (data, id, createdBy) => {
    const response = await axios.put(`${BASE_URL}phlebotomist/update/${id}`, data, 
   );
    return response.data;
}

export const  deletePhlebotomist = async (id) => {
    const response = await axios.delete(`${BASE_URL}phlebotomist/delete/${id}`);
    return response.data;
}
  




