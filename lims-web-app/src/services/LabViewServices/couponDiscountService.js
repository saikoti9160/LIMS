import axios from 'axios';
import { Urls } from "./../apiURLS";


const BASE_URL = Urls.LAB_MANAGEMENT;

// export const getAllCouponDiscounts = async (searchTerm = "",flag = true, createdBy, pageNumber = 0, pageSize = 10) => {
//     const response = await axios.get(`${BASE_URL}coupon/get-all`, {
//         params: { searchTerm, flag, createdBy, pageNumber, pageSize }
//     });
//     return response.data;
// };

export const getAllCouponDiscounts = async (searchTerm = "",flag = true, createdBy, pageNumber = 0, pageSize = 10) => {
    const response = await axios.get(`${BASE_URL}coupon/get-all?searchTerm=${searchTerm}&flag=${flag}&createdBy=${createdBy}&pageNumber=${pageNumber}&pageSize=${pageSize}`);
    
    return response.data;
};
export const saveCouponDiscount = async (data,createdBy) => {
    const response = await axios.post(`${BASE_URL}coupon/save`, data,  {
        headers: {
          "Content-Type": "application/json",
           createdBy:createdBy,
        },
      });
    return response.data;
};

export const updateCouponDiscount = async (id, data) => {
    const response = await axios.put(`${BASE_URL}coupon/update/${id}`, data);
    return response.data;
};

export const deleteCouponDiscount = async (id) => {
    const response = await axios.delete(`${BASE_URL}coupon/delete/${id}`);
    return response.data;
};

export const getCouponDiscountById = async (id) => {
    const response = await axios.get(`${BASE_URL}coupon/get/${id}`);
    return response.data;
};

export const couponDiscountService = {  
    getAllCouponDiscounts,
    saveCouponDiscount,
    updateCouponDiscount,    
    deleteCouponDiscount,
    getCouponDiscountById
}