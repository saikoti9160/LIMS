import axios from 'axios';

const AUTH_BASE_URL = 'http://localhost:8081/auth/api/';

  export const uploadFile = async function(file, destinationKey) {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('destinationKey', destinationKey);
    const response = await axios.post(`${AUTH_BASE_URL}user/upload`, formData, {
          headers: {
              'Content-Type': 'multipart/form-data',
          },
          
      });
      if (response.data && response.data.data) {
          return response.data.data;
      }
      throw new Error('Invalid response format from server.');
  }


export const fileUploadService = {
    uploadFile:uploadFile  
}