import axios from "axios";
import { Urls } from "./../apiURLS";

const BASE_URL = Urls.LAB_MANAGEMENT;

export const saveCenterMaster = async (data, createdBy) => {
  try {
    const response = await axios.post(`${BASE_URL}center-master/save`, data, {
      headers: {
        "Content-Type": "application/json",
        createdBy: createdBy,
      },
    });
    return response.data;
  } catch (error) {
    return error;
  }
};

export const findCenterMasterBylabId = async (id) => {
  try {
    const response = await axios.get(`${BASE_URL}center-master/lab/${id}`);
    return response;
  } catch (error) {
    return error;
  }
};

export const updateCenterMaster = async (id, data) => {
  try {
    const response = await axios.put(
      `${BASE_URL}center-master/update/${id}`,
      data
    );
    return response;
  } catch (error) {
    return error;
  }
};
