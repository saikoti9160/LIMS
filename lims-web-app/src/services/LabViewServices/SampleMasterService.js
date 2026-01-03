import axios from "axios";

//const BASE_URL = Urls.LAB_MANAGEMENT;
const BASE_URL="http://localhost:8082/lab-management/"
 export const saveSampleMaster= async (data,header) =>{
   const response= await axios.post(`${BASE_URL}api/samples/save`, data,  {
        headers: {
          "Content-Type": "application/json",
          userId:header
        },
      });
      return response.data;
 }

 const labId = "06228e13-e32b-4420-b980-0f6b8744e170";

 export const getAllSamples= async(labId,  pageNumber = 0, pageSize = 10,searchText = "") =>
 {
    const response=await axios.post(`
      ${BASE_URL}api/samples/get-all?labId=${labId}&pageNumber=${pageNumber}&pageSize=${pageSize}&searchText=${searchText}`)
    return response.data;
 }

 export const getSampleById=async (id)=>{
    const response=await axios.get(`${BASE_URL}api/samples/get/${id}`)
    return response.data;
 }

 export const updateSample= async (id,sample) =>{
    const response=await axios.put(`${BASE_URL}api/samples/update/${id}`,sample);
    return response.data;
 }

 export const deleteSample=async (id)=>{
    const response=await axios.delete(`${BASE_URL}api/samples/delete/${id}`);
    return response.data;
 }
 export const getSamplesNames = async (sampleName) => {
   console.log("ganesh")
   const response = await axios.get(`${BASE_URL}api/samples/get/sample-names?SampleName=${sampleName}`);
   return response.data;
};
