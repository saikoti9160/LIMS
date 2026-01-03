import axios from "axios";
import { Urls } from "../apiURLS";

const BASE_URL = Urls.LAB_MANAGEMENT;

export async function addProfileconfiguration(requestDTO, createdBy) {
  try {
    const response = await axios.post(
      `${BASE_URL}profile-configuration/save`,
      requestDTO,
      {
        headers: { createdBy: createdBy },
      }
    );
    return response.data;
  } catch (error) {
    console.error("Error adding profile configuration:", error.message);
    return {
      error: true,
      message:
        error.response?.data?.message || "Failed to save profile configuration",
    };
  }
}

export async function getAllprofiles(
  createdBy,
  keyword = "",
  pageNumber = 0,
  pageSize = 10
) {
  try {
    const params = { keyword, pageNumber, pageSize };
    const response = await axios.get(
      `${BASE_URL}profile-configuration/get-All-Profiles`,
      {
        params,
        headers: { createdBy: createdBy },
      }
    );
    return response.data;
  } catch (error) {
    console.error("Error fetching all profiles:", error.message);
    return {
      error: true,
      message: error.response?.data?.message || "Failed to fetch profiles",
    };
  }
}

export async function getProfileConfigById(id) {
  try {
    const response = await axios.get(
      `${BASE_URL}profile-configuration/get/${id}`
    );
    return response.data;
  } catch (error) {
    console.error(
      `Error fetching profile configuration by ID (${id}):`,
      error.message
    );
    return {
      error: true,
      message:
        error.response?.data?.message ||
        "Failed to fetch profile configuration",
    };
  }
}

export async function updateProfileConfigById(
  id,
  profileConfigurationRequestDTO
) {
  try {
    const response = await axios.put(
      `${BASE_URL}profile-configuration/update/${id}`,
      profileConfigurationRequestDTO
    );
    return response.data;
  } catch (error) {
    console.error(
      `Error updating profile configuration (${id}):`,
      error.message
    );
    return {
      error: true,
      message:
        error.response?.data?.message ||
        "Failed to update profile configuration",
    };
  }
}

export async function deleteProfileConfigById(id) {
  try {
    const response = await axios.delete(
      `${BASE_URL}profile-configuration/delete/${id}`
    );
    return response.data;
  } catch (error) {
    console.error(
      `Error deleting profile configuration (${id}):`,
      error.message
    );
    return {
      error: true,
      message:
        error.response?.data?.message ||
        "Failed to delete profile configuration",
    };
  }
}
