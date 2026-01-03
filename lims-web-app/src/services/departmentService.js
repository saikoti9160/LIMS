import axios from "axios";
import { Urls } from "./apiURLS";

export const departmentsGetAll = async (pageNumber, pageSize, startsWith = '', createdBy) => {
  try {
    const response = await axios.post(
      `${Urls.MASTERS}department/get-all?pageNumber=${pageNumber}&pageSize=${pageSize}&sortBy=name&startsWith=${startsWith}`, {}, {
      headers: {
        createdBy
      }
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching departments:', error);
    return error;
  }
};

export const addDepartment = async (department, createdBy) => {
  try {
    const response = await axios.post(`${Urls.MASTERS}department/save`, department, {
      headers: {
        'createdBy': createdBy
      }
    });
    return response.data;
  } catch (error) {
    console.error('Error adding department:', error);
    return error;
  }
};

export const deleteDepartment = async (id) => {
  try {
    const response = await axios.delete(`${Urls.MASTERS}department/${id}`);
    return response?.data;
  } catch (error) {
    console.error('Error deleting department:', error);
    return error;
  }
};

export const updateDepartment = async (id, department, userId) => {
  try {
    const response = await axios.put(`${Urls.MASTERS}department/update/${id}`, department, {
      headers: {
        'userId': userId
      }
    });
    return response.data;
  } catch (error) {
    console.error('Error updating department:', error);
    return error;
  }
};

export const getDepartmentById = async (id) => {
  try {
    const response = await axios.get(`${Urls.MASTERS}department/${id}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching department by id:', error);
    return error;
  }
};