import axios from "axios";
import { Urls } from "./apiURLS";

const BASE_URL = Urls.MASTERS;

export const getAllRelations = async (startsWith = '', pageNumber = 0, pageSize = 10, sortedBy = 'relationName') =>{
    const response = await axios.post(`${BASE_URL}relation/get-all?startsWith=${startsWith}&pageNumber=${pageNumber}&pageSize=${pageSize}&sortedBy=${sortedBy}`);
    return response.data;
}

export const addRelation = async (relation, createdBy) =>{
    const response = await axios.post(`${BASE_URL}relation/save?createdBy=${createdBy}`, relation);
    return response.data;
}

export const getRelationById = async (id) =>{
    const response = await axios.get(`${BASE_URL}relation/get/${id}`);
    return response.data;
}

export const updateRelationById = async (id, relation, modifiedBy) =>{
    const response = await axios.put(`${BASE_URL}relation/update/${id}?modifiedBy=${modifiedBy}`, relation);
    return response.data;
}

export const deleteRelationById = async (id) =>{
    const response = await axios.delete(`${BASE_URL}relation/delete/${id}`);
    return response.data;
}

export const relationMasterService = {
    getAllRelations: getAllRelations,
    addRelation: addRelation,
    getRelationById: getRelationById,
    updateRelationById: updateRelationById,
    deleteRelationById: deleteRelationById
}