// src/config/apiEndpoints.js
// const API_BASE_URL = 'http://localhost:8081/auth/api';
const API_BASE_URL = 'http://localhost:8081/auth/api';

const API_ENDPOINTS = {
  LOGIN: `${API_BASE_URL}/login`,
  SIGNUP: `${API_BASE_URL}/signup`,
  FORGOT_PASSWORD: `${API_BASE_URL}/forgot-password`,
  RESET_PASSWORD: `${API_BASE_URL}/reset-password`,
  FETCH_PROFILE: `${API_BASE_URL}/user/profile`,
  UPDATE_PROFILE: `${API_BASE_URL}/user/update-profile`,
};

export default API_ENDPOINTS;
