import axios from "axios";
import { environment } from "../environments/environment-local";

const BASE_URL = environment.MASTERS;

export const getAllCountries = async (startsWith = '', continentNames = [], pageNumber = 0, pageSize = 10, sortedBy = 'countryName') =>{
    console.log('from masters.js');
    
    const response = await axios.post(`${BASE_URL}country/get-all?startsWith=${startsWith}&continentNames=${continentNames}&pageNumber=${pageNumber}&pageSize=${pageSize}&sortedBy=${sortedBy}`);    
    return response.data;
}