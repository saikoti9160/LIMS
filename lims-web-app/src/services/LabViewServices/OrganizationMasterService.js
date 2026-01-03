import axios from "../api";
import { Urls } from "../apiURLS";

const BASE_URL = Urls.LAB_MANAGEMENT;

export const organizationMasterSave = async (requestBody, header) => {
  try {
    const response = await axios.post(
      `${BASE_URL}organization/save`,
      requestBody,
      header
    );
    return response.data;
  } catch (error) {
    return error;
  }
};

export const getOrganizationMasterById = async (id) => {
  try {
    const response = await axios.get(`${BASE_URL}organization/get/${id}`);
    return response.data;
  } catch (error) {
    return error;
  }
};

export const getAllOrganizationMaster = async (page, size, createdBy, flag) => {
  let url = `${BASE_URL}organization/get-all?createdBy=${createdBy}&pageNumber=${page}&pageSize=${size}`;
  if (flag !== undefined && flag !== null) {
    url += `&flag=${flag}`;
  }
  try {
    const response = await axios.get(url);
    return response.data;
  } catch (error) {
    return error;
  }
};

export const organizationMasterUpdate = async (id, data) => {
  try {
    const response = await axios.put(
      `${BASE_URL}organization/update/${id}`,
      data
    );
    return response.data;
  } catch (error) {
    return error;
  }
};