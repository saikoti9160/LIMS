import axios from "../api";
import { Urls } from "../apiURLS";

const BASE_URL = Urls.LAB_MANAGEMENT;

export const saveReferralCommission = async (data, createdBy) => {
  try {
    const response = await axios.post(
      `${BASE_URL}referral-commission/save`,
      data,
      {
        headers: { createdBy: createdBy },
      }
    );
    return response.data;
  } catch (error) {
    console.error("Error saving referral commission:", error.message);
    return {
      error: true,
      message: error.response?.data?.message || "Failed to save data",
    };
  }
};

export const getReferralCommissionById = async (id) => {
  try {
    const response = await axios.get(
      `${BASE_URL}referral-commission/get/${id}`
    );
    return response.data;
  } catch (error) {
    console.error(
      `Error fetching referral commission by ID (${id}):`,
      error.message
    );
    return {
      error: true,
      message: error.response?.data?.message || "Failed to fetch data",
    };
  }
};

export const updateReferralCommission = async (id, data) => {
  try {
    const response = await axios.put(
      `${BASE_URL}referral-commission/${id}`,
      data
    );
    return response.data;
  } catch (error) {
    console.error(`Error updating referral commission (${id}):`, error.message);
    return {
      error: true,
      message: error.response?.data?.message || "Failed to update data",
    };
  }
};

export const deleteReferralCommission = async (id) => {
  try {
    const response = await axios.delete(`${BASE_URL}referral-commission/${id}`);
    return response.data;
  } catch (error) {
    console.error(`Error deleting referral commission (${id}):`, error.message);
    return {
      error: true,
      message: error.response?.data?.message || "Failed to delete data",
    };
  }
};

export const getAllReferralCommission = async (
  createdBy,
  keyword = "",
  flag = null,
  pageNumber = 0,
  pageSize = 10
) => {
  try {
    const params = { createdBy, pageNumber, pageSize };
    if (keyword) params.keyword = keyword;
    if (flag !== null) params.flag = flag;

    const response = await axios.get(`${BASE_URL}referral-commission/get-All`, {
      params,
    });
    return response.data;
  } catch (error) {
    console.error("Error fetching all referral commissions:", error.message);
    return {
      error: true,
      message: error.response?.data?.message || "Failed to fetch data",
    };
  }
};
