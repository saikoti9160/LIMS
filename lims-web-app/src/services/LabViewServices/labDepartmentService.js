import axios from "axios";
import {Urls} from '../apiURLS';
const BASE_URL=Urls.LAB_MANAGEMENT;

export const getLabDepartments=async()=>{
    const response=await axios.get(`${BASE_URL}lab-department/all`);
    return response.data;
}
 export const getLabDepartmentById=async(id)=>{
    const response=await axios.get(`${BASE_URL}lab-department/get-by-id?id=${id}`);
    return response.data;
 }

 export const deleteLabDepartment=async(id)=>{
    const response=await axios.delete(`${BASE_URL}lab-department/delete?id=${id}`);
    return response.data;
 }

 export const updateLabDepartment=async(data)=>{
    const response=await axios.put(`${BASE_URL}lab-department/update`,data);
    return response.data;
 }
 export const createLabDepartment=async(data)=>{
    const response=await axios.post(`${BASE_URL}lab-department/save`,data);
    return response.data;
 }