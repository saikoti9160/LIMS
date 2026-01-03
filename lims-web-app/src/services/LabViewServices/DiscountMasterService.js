import axios from "axios";

const BASE_URL = "http://localhost:8082/lab-management/discount"; 

export const addDiscount = async (discountData, headers) => {
    const response = await axios.post(`${BASE_URL}/save`, discountData, { headers });
    return response.data;
};

export const updateDiscount = async (discountData, discountId) => {
    const response = await axios.put(`${BASE_URL}/update/${discountId}`, discountData);
    return response.data;
};

export const getAllDiscounts = async (createdBy, keyword = '', flag = true, page, size) => {
    const response = await axios.get(`${BASE_URL}/get/all`, {
        params: {
            createdBy, 
            keyword, 
            flag, 
            pageNumber: page, 
            pageSize: size
        }
    });
    return response.data;
};


export const deleteDiscount = async (id) => {
    const response = await axios.delete(`${BASE_URL}/delete/${id}`, {
        headers: {
            'Content-Type': 'application/json',
        },
    });
    return response.data;
};

export const getDiscountById = async (discountId) => {
    const response = await axios.get(`${BASE_URL}/get/${discountId}`);
    return response.data;
};

export const discountService = {
    addDiscount,
    updateDiscount,
    getAllDiscounts,
    deleteDiscount,
    getDiscountById
};
