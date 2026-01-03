import axios from "axios";
import { Urls } from "./apiURLS";
 
const BASE_URL = Urls.AUTH; // Assuming there's a USERS endpoint in apiURLS
const CREATED_BY = "3fa85f64-5717-4562-b3fc-2c963f66afa6"; // Replace with localStorage when available
 
// User Master API Endpoints
export const getAllStaff = async (searchBy = '', pageNumber = 0, pageSize = 10, sortBy = 'userSequenceId') => {
  const response = await axios.post(
    `${BASE_URL}/staff/get-all`,
  {},
    {
      params: {searchBy, pageNumber, pageSize, sortBy }, // Query parameters
      headers: {
        'createdBy': CREATED_BY,
      }
    }
  );
  // console.log("response", response);
  return response.data;
};
 
export const saveStaff = async (userMaster) => {
  const response = await axios.post(`${BASE_URL}/staff/add`, userMaster, {
    headers: {
      'createdBy': CREATED_BY,
     
    },
  });
  return response.data;
};
 
export const updateStaffById = async (id, updatingStaffData) => {
  try {
    const response = await axios.put(`${BASE_URL}/staff/${id}`, updatingStaffData, {
      headers: {
        'userId': CREATED_BY,
      },
    });
    return response.data;
  } catch (error) {
    console.error("Error updating user master:", error);
    throw error;
  }
};
 
export const getStaffById = async (id) => {
  const response = await axios.get(`${BASE_URL}/staff/${id}`);
  return response.data;
};
 
export const deleteStaffById = async (id) => {
  const response = await axios.delete(`${BASE_URL}/staff/${id}`);
  return response.data;
};
 
// Exporting as a service object
export const staffManagementService = {
  getAllStaff:getAllStaff,
  saveStaff:saveStaff,
  updateStaffById:updateStaffById,
  getStaffById:getStaffById,
  deleteStaffById:deleteStaffById
};
