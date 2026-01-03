import axios from '../api';
import { Urls } from '../apiURLS';


const BASE_URL = Urls.LAB_MANAGEMENT;

export const patientRegistrationSave = async (requestBody, headers) => {
    const response = await axios.post(`${BASE_URL}patient-registration/save`, requestBody, {
        headers: {
            "Content-Type": "application/json",
            createdBy: headers
        }
    });
    return response.data;
};

export const getPatientById = async (id) =>{
  const response = await axios.get(`${BASE_URL}patient-registration/get/${id}`);
  console.log("get api",response)
  return response.data;
}
export const getAllPatients = async (keyword,page,size,headers)=>{
let url=`${BASE_URL}patient-registration/get-all-patients?keyword=${keyword}&pageNumber=${page}&pageSize=${size}`;
const response=await axios.get(url,{
headers: {
  "Content-Type": "application/json",
  createdBy: headers
}
});

 return response.data;
}

export const updatePatient = async (id, data)=>{
  const response = await axios.put(`${BASE_URL}patient-registration/update/${id}`,data);
  return response.data;
}