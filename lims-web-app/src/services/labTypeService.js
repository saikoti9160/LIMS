import axios from "axios";
import { Urls } from "./apiURLS";

export const getLabTypes = async (startsWith = '', pageNumber, pageSize, createdBy) => {
  try {
    const response = await axios.post(
      `${Urls.MASTERS}lab-type/get-all?pageNumber=${pageNumber}&pageSize=${pageSize}&sortBy=name&startsWith=${startsWith}`, {}, {
      headers: {
        createdBy
      }
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching lab types:', error);
    return error;
  }
};

export const addLabType = async (labType, createdBy) => {
  try {
    const response = await axios.post(`${Urls.MASTERS}lab-type/save`, labType, {
      headers: {
        'createdBy': createdBy
      }
    });
    return response.data;
  } catch (error) {
    console.error('Error adding lab type:', error);
    return error;
  }
};

export const updateLabType = async (id, labType, userId) => {
  try {
    const response = await axios.put(`${Urls.MASTERS}lab-type/update/${id}`, labType, {
      headers: {
        'userId': userId
      }
    });
    return response.data;
  } catch (error) {
    console.error('Error updating lab type:', error);
    return error;
  }
};

export const deleteLabType = async (id) => {
  try {
    const response = await axios.delete(`${Urls.MASTERS}lab-type/${id}`);
    return response.data;
  } catch (error) {
    console.error('Error deleting lab type:', error);
    return error;
  }
};

export const getLabTypeById = async (id) => {
  try {
    const response = await axios.get(`${Urls.MASTERS}lab-type/${id}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching lab type by id:', error);
    return error;
  }
};