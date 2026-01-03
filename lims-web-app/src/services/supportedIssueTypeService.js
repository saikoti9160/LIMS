import axios from "axios";
import { Urls } from "./apiURLS";

export const supportIssueTypesGetAll = async (page, size, startsWith = "", sortBy = '') => {
  try {
    const response = await axios.post(
      `${Urls.MASTERS}support-issue-types/get-all?startsWith=${startsWith}&pageNumber=${page}&pageSize=${size}&sortBy=${sortBy}`,
    );
    return response.data;
  } catch (error) {
    console.error('Error fetching support issue types:', error);
    return error;
  }
};

export const addSupportIssueType = async (supportIssueType) => {
  try {
    const response = await axios.post(`${Urls.MASTERS}support-issue-types/save`, supportIssueType);
    return response.data;
  } catch (error) {
    console.error('Error adding support issue type:', error);
    return error;
  }
};

export const deleteSupportIssueType = async (id) => {
  try {
    const response = await axios.delete(`${Urls.MASTERS}support-issue-types/${id}`);
    return response.data;
  } catch (error) {
    console.error('Error deleting support issue type:', error);
    return error;
  }
};

export const updateSupportIssueType = async (id, supportIssueType) => {
  try {
    const response = await axios.put(`${Urls.MASTERS}support-issue-types/${id}`, supportIssueType);
    return response.data;
  } catch (error) {
    console.error('Error updating support issue type:', error);
    return error;
  }
};

export const getSupportIssueTypeById = async (id) => {
  try {
    const response = await axios.get(`${Urls.MASTERS}support-issue-types/${id}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching support issue type by id:', error);
    return error;
  }
};