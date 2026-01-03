import axios from "axios";
import { Urls } from './apiURLS';
const BASE_URL = Urls.MASTERS;
export const addRole=async(data,createdBy)=>{
  const response=await axios.post(`${BASE_URL}role/save?createdBy=${createdBy}`,data);  
  return response.data;  
}

export const getRoles = async (page = 0, size = 10, startsWith = "", status = "", createdBy = "") => {
  const url = `${BASE_URL}role/get-all?startsWith=${startsWith}&status=${status}&createdBy=${createdBy}&pageNumber=${page}&pageSize=${size}&sortedBy=roleName`;

  const response = await axios.post(url);
  return response.data;
};

export const getRoleById=async(id)=>{
  const getReponse=await axios.get(`${BASE_URL}role/get/${id}`);   
  return getReponse.data;
}
export const updateRole=async(id,data,modifiedBy)=>{
  const getReponse=await axios.put(`${BASE_URL}role/update/${id}?modifiedBy=${modifiedBy}`,data);  
  return getReponse.data;
}
export const deleteRole=async(id)=>{
  const getReponse=await axios.delete(`${BASE_URL}role/delete/${id}`);  
  return getReponse.data;
}