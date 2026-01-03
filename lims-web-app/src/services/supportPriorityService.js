import axios from "axios";
import { Urls } from "./apiURLS";

export const supportPrioritiesGetAll = async (page, size, startsWith = '', sortBy = '') => {
  try {
    const response = await axios.post(`${Urls.MASTERS}support-priority/get-all?startsWith=${startsWith}&pageNumber=${page}&pageSize=${size}&sortBy=${sortBy}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching support priorities:', error);
    return error;
  }
};
export const addSupportPriority = async (priority) => {
  try {
    const response = await axios.post(`${Urls.MASTERS}support-priority/save`, priority);
    return response.data;
  } catch (error) {
    console.error('Error adding support priority:', error);
    return error;
  }
};

export const deleteSupportPriority = async (id) => {
  try {
    const response = await axios.delete(`${Urls.MASTERS}support-priority/delete/${id}`);
    return response.data;
  } catch (error) {
    console.error('Error deleting support priority:', error);
    return error;
  }
};

export const updateSupportPriority = async (id, priority) => {
  try {
    const response = await axios.put(`${Urls.MASTERS}support-priority/update/${id}`, priority);
    return response.data;
  } catch (error) {
    console.error('Error updating support priority:', error);
    return error;
  }
};

export const getSupportPriorityById = async (id) => {
  try {
    const response = await axios.get(`${Urls.MASTERS}support-priority/${id}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching support priority by id:', error);
    return error;
  }
};