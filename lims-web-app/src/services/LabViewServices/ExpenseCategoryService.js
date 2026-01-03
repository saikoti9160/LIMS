import axios from "../api";
import { Urls } from "../apiURLS";

const BASE_URL = Urls.LAB_MANAGEMENT;

export const addExpenseCategory = async (requestBody, header) => {
  try {
    const response = await axios.post(
      `${BASE_URL}expense-categories/save`,
      requestBody,
      {
        headers: {
          "Content-Type": "application/json",
          userId: header,
        },
      }
    );
    return response.data;
  } catch (error) {
    return error;
  }
};

export const ExpenseCategoryGetAll = async (id) => {
  try {
    const response = await axios.post(
      `${BASE_URL}expense-categories/get-all?labId=${id}`
    );
    return response.data;
  } catch (error) {
    return error;
  }
};

export const getExpenseCategoryById = async (id) => {
  try {
    const response = await axios.get(`${BASE_URL}expense-categories/get/${id}`);
    return response.data;
  } catch (error) {
    return error;
  }
};

export const deleteExpenseCategoryById = async (id) => {
  try {
    const response = await axios.delete(
      `${BASE_URL}expense-categories/delete/${id}`
    );
    return response.data;
  } catch (error) {
    return error;
  }
};

export const updateExpenseCategoryById = async (id, body) => {
  try {
    const response = await axios.put(
      `${BASE_URL}expense-categories/update/${id}`,
      body
    );
    return response.data;
  } catch (error) {
    return error;
  }
};
