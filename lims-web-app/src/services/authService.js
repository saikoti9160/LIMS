import axios from './api';

export const signIn = async (email, password) => {
  const response = await axios.post('/auth/sign-in', { email, password });
  return response.data;
};

export const forgotPassword = async (email) => {
  const response = await axios.post('/auth/forgot-password', { email });
  return response.data;
};
