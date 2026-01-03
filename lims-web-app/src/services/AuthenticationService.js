import axios from "axios";
import { Urls } from "./apiURLS";

// const BASE_URL ='http://localhost:8081/auth/api'; 

const BASE_URL = Urls.AUTH;

export const signUp = async (userData) => {
    const response = await axios.post(`${BASE_URL}/signup`, userData);
    return response.data;
};

export const login = async (loginData, token) => {
    const response = await axios.post(`${BASE_URL}/login`, loginData,{
        headers : {
            Authorization: `Bearer ${token}`
        },
    }
    );
    return response.data;
};

export const forgotPassword = async (email,token) => {
    const response = await axios.post(`${BASE_URL}/forgot-password`, {}, { params: { email } ,
        headers: { Authorization: `Bearer ${token}` },
    });
    return response.data;
};

// export const resetPassword = async (passwordChangeRequest,token) => {
//     const response = await axios.post(`${BASE_URL}/reset-password`, passwordChangeRequest, { headers: { Authorization: `Bearer ${token}` } });
//     return response.data;
// };
export const resetPassword = async ({ email , newPassword, verificationCode }) => {
    const response = await axios.post(`${BASE_URL}/reset-password`, {
         email , newPassword, verificationCode
    });
    return response.data;
  };

export const updatePassword = async (updatePasswordRequest, token) => {
    console.log(BASE_URL+"/update-password", updatePasswordRequest, token);
    
    const response = await axios.put(`${BASE_URL}/update-password`, updatePasswordRequest, {
        headers: { Authorization: `Bearer ${token}` },
    });
    return response.data;
};

export const updateProfile = async (profilePayload) => {
    const response = await axios.put(`${BASE_URL}/update-profile`, profilePayload);
    console.log('from auth service', response);
    
    return response.data;
}

// export const updateProfile = (profilePayload) => {
//     axios.put(`${BASE_URL}/update-profile`, profilePayload).then((response) => {
//         console.log(response.data);
//         return response.data;
//     }).catch((err) => {
//         console.log(err);
//     });
// }

export const checkEmailExists = async (email) => {
    const response = await axios.post(`${BASE_URL}/check-email`, {}, { params: { email } });
    return response.data;
};

export const updateStatus = async (email) => {
    const response = await axios.put(`${BASE_URL}/update-status`, {}, { params: { email } });
    return response.data;
};

export const resendVerificationEmail = async (email) => {
    const response = await axios.post(`${BASE_URL}/resend-verification`, {}, { params: { email } });
    return response.data;
};

export const refreshToken = async (refreshToken) => {
    const response = await axios.post(`${BASE_URL}/refresh-token`, {}, {
        headers: { refreshToken },
    });
    return response.data;
};


export const authenticationService = {
    signUp,
    login,
    forgotPassword,
    resetPassword,
    updatePassword,
    checkEmailExists,
    updateStatus,
    resendVerificationEmail,
    refreshToken,
    updateProfile
 
};
