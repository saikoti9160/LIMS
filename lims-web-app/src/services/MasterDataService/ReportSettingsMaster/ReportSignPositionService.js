import { Urls } from "../../apiURLS";
import axios from "axios";
const BASE_URL = Urls.MASTERS;
export const getAllReportSignPositions = async (
  startsWith = "",
  pageNumber = 0,
  pageSize = 10,
  sortBy = "paperSize",
  createdBy = ""
) => {
  try {
    const response = await axios.post(
      `${BASE_URL}report-sign-position/get-all?startsWith=${startsWith}&pageNumber=${pageNumber}&pageSize=${pageSize}&sortBy=${sortBy}`,
      {},
      { headers: { createdBy: createdBy } }
    );
    return response.data;
  } catch (error) {
    return error;
  }
};

export const addReportSignPosition = async (ReportSignPosition, createdBy) => {
  try {
    const response = await axios.post(
      `${BASE_URL}report-sign-position/save`,
      ReportSignPosition,
      { headers: { createdBy: createdBy } }
    );
    return response.data;
  } catch (error) {
    return error;
  }
};
export const deleteReportSignPositionById = async (id) => {
  try {
    const response = await axios.delete(
      `${BASE_URL}report-sign-position/${id}`
    );
    return response.data;
  } catch (error) {
    return error;
  }
};
export const getReportSignPositionById = async (id) => {
  try {
    const response = await axios.get(`${BASE_URL}report-sign-position/${id}`);
    return response.data;
  } catch (error) {
    return error;
  }
};
export const updateReportSignPosition = async (
  id,
  updatedReportSignPosition,
  userId
) => {
  try {
    const response = await axios.put(
      `${BASE_URL}report-sign-position/update/${id}`,
      updatedReportSignPosition,
      { headers: { userId: userId } }
    );
    return response.data;
  } catch (error) {
    return error;
  }
};
export const ReportSignPositionService = {
  getAllReportSignPositions,
  addReportSignPosition,
  deleteReportSignPositionById,
  getReportSignPositionById,
  updateReportSignPosition,
};
