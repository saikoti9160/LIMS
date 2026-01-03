import axios from "axios";
import { Urls } from "./apiURLS";

const BASE_URL = Urls.MASTERS;


//Country Api Endpoints
export const getAllCountries = async (startsWith = '', continentNames = [], pageNumber = 0, pageSize = 10, sortedBy = 'countryName') =>{
    const response = await axios.post(`${BASE_URL}country/get-all?startsWith=${startsWith}&continentNames=${continentNames}&pageNumber=${pageNumber}&pageSize=${pageSize}&sortedBy=${sortedBy}`);    
    console.log('from service ', response);
    
    return response.data;
}

export const saveCountry = async (country, createdBy) =>{
    const response = await axios.post(`${BASE_URL}country/save?createdBy=${createdBy}`, country);
    return response.data;
}

export const updateCountryById = async (id, updatingExistingCountry, modifiedBy) =>{
    const response = await axios.put(`${BASE_URL}country/update/${id}?modifiedBy=${modifiedBy}`, updatingExistingCountry);
    return response.data;
}

export const getCountryById = async (id) =>{
    const response = await axios.get(`${BASE_URL}country/get/${id}`);
    return response.data;
}

export const deleteCountryById = async (id) =>{
    const response = await axios.delete(`${BASE_URL}country/delete/${id}`);
    return response.data;
}

//State Api Endpoints
export const getAllStates = async (startsWith = "", countryNames = [], pageNumber = 0, pageSize = 250, sortedBy = "stateName") =>{
    const response = await axios.post(`${BASE_URL}state/get-all?startsWith=${startsWith}&countryNames=${countryNames}&pageNumber=${pageNumber}&pageSize=${pageSize}&sortedBy=${sortedBy}`);
    return response.data;
}

export const saveState = async (state, createdBy) =>{
    const response = await axios.post(`${BASE_URL}state/save?createdBy=${createdBy}`, state);
    return response.data;
}

export const updateStateById = async (id, updatingExistingStateData, modifiedBy) =>{
    const response = await axios.put(`${BASE_URL}state/update/${id}?modifiedBy=${modifiedBy}`, updatingExistingStateData);
    return response.data;
}

export const getStateById = async (id) =>{
    const response = await axios.get(`${BASE_URL}state/get/${id}`);
    return response.data;
}

export const deleteStateById = async (id) =>{
    const response = await axios.delete(`${BASE_URL}state/delete/${id}`);
    return response.data;
}

//City Api Endpoints
export const getAllCities = async (startsWith = "", stateNames = [], pageNumber = 0, pageSize = 250, sortedBy = "cityName") =>{
    const response = await axios.post(`${BASE_URL}city/get-all?startsWith=${startsWith}&stateNames=${stateNames}&pageNumber=${pageNumber}&pageSize=${pageSize}&sortedBy=${sortedBy}`);
    return response.data;
}

export const saveCity = async (city, createdBy) =>{
    const response = await axios.post(`${BASE_URL}city/save?createdBy=${createdBy}`, city);
    return response.data;
}

export const updateCityById = async (id, updatingExistingCityData, modifiedBy) =>{
    const response = await axios.put(`${BASE_URL}city/update/${id}?modifiedBy=${modifiedBy}`, updatingExistingCityData);
    return response.data;
}

export const getCityById = async (id) =>{
    const response = await axios.get(`${BASE_URL}city/get/${id}`);
    return response.data;
}

export const deleteCityById = async (id) =>{
    const response = await axios.delete(`${BASE_URL}city/delete/${id}`);
    return response.data;
}
export const getAllContinents = async (startsWith = '', pageNumber = 0, pageSize = 10, sortedBy = 'continentName') =>{
    const response = await axios.post(`${BASE_URL}continent/get-all?startsWith=${startsWith}&pageNumber=${pageNumber}&pageSize=${pageSize}&sortedBy=${sortedBy}`);
    return response.data;
}
export const saveContinent = async (continent, createdBy) => {
    const response = await axios.post(`${BASE_URL}continent/save?createdBy=${createdBy}`, continent);
    return response.data;
}
export const updateContinentById = async (id, updatingExistingContinentData, modifiedBy) => {
    const response = await axios.put(`${BASE_URL}continent/update/${id}?modifiedBy=${modifiedBy}`, updatingExistingContinentData);
    return response.data;
}
export const getContinentById = async (id) => {
    const response = await axios.get(`${BASE_URL}continent/get/${id}`);
    return response.data;
}

export const locationMasterService = {
    getAllCountries: getAllCountries,
    saveCountry: saveCountry,
    updateCountryById: updateCountryById,
    getCountryById: getCountryById,
    deleteCountryById: deleteCountryById,
    getAllStates: getAllStates,
    saveState: saveState,
    updateStateById: updateStateById,
    getStateById: getStateById,
    deleteStateById: deleteStateById,
    getAllCities: getAllCities,
    saveCity: saveCity,
    updateCityById: updateCityById,
    getCityById: getCityById,
    deleteCityById: deleteCityById,
    getAllContinents: getAllContinents
}