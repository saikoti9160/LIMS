import axios from "axios";
import { Urls } from "../apiURLS";

const BASE_URL = Urls.LAB_MANAGEMENT; // Assuming there's a USERS endpoint in apiURLS
const CREATED_BY = "3fa85f64-5717-4562-b3fc-2c963f66afa6"; // Replace with localStorage when available
// Expense Master API Endpoints
export const getAllExpenses = async (labId, searchText = "", pageNumber = 0, pageSize = 10) => {
  try {
    const response = await axios.post(
      `${BASE_URL}expense/get-all`,
      {},
      {
        params: { labId,searchText, pageNumber, pageSize },
      }
    );
    return response.data;
  } catch (error) {
    console.error("Error fetching expense master:", error);
    throw error;
  }
};

export const saveExpense = async (expenseMaster) => {
  try {
    const response = await axios.post(`${BASE_URL}expense/save`, expenseMaster, {

      headers: { "userId": CREATED_BY },
    });
    return response.data;
  } catch (error) {
    console.error("Error saving expense master:", error);
    throw error;
  }
};

export const updateExpenseById = async (id, updatingExpenseData) => {
  console.log("updatingExpenseData", id, updatingExpenseData);
  try {
    const response = await axios.put(`${BASE_URL}expense/update/${id}`, updatingExpenseData, {
      
    });
    return response.data;
  } catch (error) {
    console.error("Error updating expense master:", error);
    throw error;
  }
};
export const getExpenseById = async (id) => {
  try {
    const response = await axios.get(`${BASE_URL}expense/get/${id}`);
    return response.data;
  } catch (error) {
    console.error("Error fetching expense by ID:", error);
    throw error;
  }
};
export const deleteExpenseById = async (id) => {
  try {
    const response = await axios.delete(`${BASE_URL}expense/delete/${id}`);
    return response.data;
  } catch (error) {
    console.error("Error deleting expense master:", error);
    throw error;
  }
};

// Exporting as a service object
export const expenseManagementService = {
  getAllExpenses,
  saveExpense,
  updateExpenseById,
  getExpenseById,
  deleteExpenseById,
};
