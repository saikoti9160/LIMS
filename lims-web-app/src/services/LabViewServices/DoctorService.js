import axios from "axios";
import { Urls } from "../apiURLS";

const BASE_URL = Urls.LAB_MANAGEMENT;

export const getAllDoctors = async (
  createdBy,
  keyword = "",
  flag = true,
  pageNumber = 0,
  pageSize = 10
) => {
  try {
    const params = { createdBy, pageNumber, pageSize, flag };
    if (keyword) params.keyword = keyword;

    const response = await axios.get(`${BASE_URL}doctor/get-all`, { params });
    return response.data;
  } catch (error) {
    console.error("Error fetching doctors:", error.message);
    return {
      error: true,
      message: error.response?.data?.message || "Failed to fetch doctors",
    };
  }
};

export const addDoctor = async (data, createdBy) => {
  try {
    const response = await axios.post(`${BASE_URL}doctor/save`, data, {
      headers: { createdBy },
    });
    return response.data;
  } catch (error) {
    console.error("Error adding doctor:", error.message);
    return {
      error: true,
      message: error.response?.data?.message || "Failed to add doctor",
    };
  }
};

export const getDoctorById = async (id) => {
  try {
    const response = await axios.get(`${BASE_URL}doctor/get/${id}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching doctor by ID (${id}):`, error.message);
    return {
      error: true,
      message: error.response?.data?.message || "Failed to fetch doctor",
    };
  }
};

export const updateDoctorById = async (id, data) => {
  try {
    const response = await axios.put(`${BASE_URL}doctor/update/${id}`, data);
    return response.data;
  } catch (error) {
    console.error(`Error updating doctor (${id}):`, error.message);
    return {
      error: true,
      message: error.response?.data?.message || "Failed to update doctor",
    };
  }
};

export const deleteDoctorById = async (id) => {
  try {
    const response = await axios.delete(`${BASE_URL}doctor/delete/${id}`);
    return response.data;
  } catch (error) {
    console.error(`Error deleting doctor (${id}):`, error.message);
    return {
      error: true,
      message: error.response?.data?.message || "Failed to delete doctor",
    };
  }
};
