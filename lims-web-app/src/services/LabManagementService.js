import axios from "./api";
import { Urls } from "./apiURLS";

const BASE_URL = Urls.LAB_MANAGEMENT;

export const labManagementGetAll = async (page, size, request) => {
  try {
    const response = await axios.post(
      `${BASE_URL}lab/filter?pageNumber=${page}&pageSize=${size}&sortBy=labName`,
      request
    );
    return response.data;
  } catch (error) {
    return error;
  }
};

export const labManagementSave = async (requestBody, header) => {
  try {
    const response = await axios.post(
      `${BASE_URL}lab/save`,
      requestBody,
      header
    );
    return response.data;
  } catch (error) {
    return error;
  }
};

export const labManagementUpdate = async (id, labData, header) => {
  try {
    const response = await axios.put(
      `${BASE_URL}lab/update/${id}`,
      labData,
      header
    );
    return response.data;
  } catch (error) {
    return error;
  }
};

export const labManagementGetById = async (id) => {
  try {
    const response = await axios.get(`${BASE_URL}lab/${id}`);
    return response.data;
  } catch (error) {
    return error;
  }
};

export const getLabByUser = async (id) => {
  try {
    const response = await axios.get(`${BASE_URL}lab/user/${id}`);
    return response.data;
  } catch (error) {
    return error;
  }
};

export const labManagementDelete = async (id) => {
  try {
    const response = await axios.delete(`${BASE_URL}lab/delete/${id}`);
    return response.data;
  } catch (error) {
    return error;
  }
};

export const deleteBranchOrEquipment = async (isBranch, itemId) => {
  try {
    const response = await axios.delete(
      `${BASE_URL}${isBranch ? "branch" : "equipment"}/delete/${
        isBranch ? "branch" : "equipment"
      }/${itemId}`
    );

    return response.data;
  } catch (error) {
    return error;
  }
};

export const deletePOC = async (itemId) => {
  try {
    const response = await axios.delete(`${BASE_URL}pointofcontact/${itemId}`);
    return response.data;
  } catch (error) {
    return error;
  }
};
