import axios from "axios";
import { Urls } from "../apiURLS";

const BASE_URL = Urls.LAB_MANAGEMENT;

export const referralsGetAll = async (createdBy, keyword = '', flag = true, pageNumber = 0, pageSize = 10) => {
  try {
    const response = await axios.get(`${BASE_URL}referral/get-All`, {
      params: { createdBy, keyword, flag, pageNumber, pageSize }
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching referrals:', error);
    return error;
  }
}

export const addReferral = async (referral, createdBy) => {
  try {
    const response = await axios.post(`${BASE_URL}referral/save`, referral, {
      headers: {
        'createdBy': createdBy
      }
    });
    return response.data;
  } catch (error) {
    console.error('Error adding referral:', error);
    return error;
  }
}

export const getReferralById = async (id) => {
  try {
    const response = await axios.get(`${BASE_URL}referral/get/${id}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching referral by id:', error);
    return error;
  }
}

export const updateReferral = async (id, referral) => {
  try {
    const response = await axios.put(`${BASE_URL}referral/update/${id}`, referral);
    return response.data;
  } catch (error) {
    console.error('Error updating referral:', error);
    return error;
  }
}

export const deleteReferral = async (id) => {
  try {
    const response = await axios.delete(`${BASE_URL}referral/delete/${id}`);
    return response.data;
  } catch (error) {
    console.error('Error deleting referral:', error);
    return error;
  }
}