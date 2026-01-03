import axios from 'axios';

// Create an Axios instance
const axiosInstance = axios.create({
  baseURL: 'https://dev.medworldexpo.com/gw-au/auth/api/user/', // Replace with your API base URL
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor for adding tokens
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    // if (token) {
      // config.headers.Authorization = `Bearer ${token}`;
      config.headers.Authorization = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJndWVzdCIsImV4cCI6MTczNjYzNzkwNiwicm9sZSI6Imd1ZXN0In0=.bWVkd29ybGQtYXBwLXNlY3JldC1rZXlleUpoYkdjaU9pSklVekkxTmlJc0luUjVjQ0k2SWtwWFZDSjlleUp6ZFdJaU9pSm5kV1Z6ZENJc0ltVjRjQ0k2TVRjek5qWXpOemt3Tml3aWNtOXNaU0k2SW1kMVpYTjBJbjA9";
      // config.headers.Access-Control-Allow-Origin ;
      config.headers['x-guest-token'] = 'true';
    // }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response Interceptor for handling errors
axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Handle unauthorized access, e.g., redirect to login
      localStorage.removeItem('authToken');
      window.location.href = '/sign-in';
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;
