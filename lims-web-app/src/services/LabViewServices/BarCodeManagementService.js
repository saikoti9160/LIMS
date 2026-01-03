import axios from "axios";
import { Urls } from "../apiURLS";

const BASE_URL = Urls.LAB_MANAGEMENT;

export async function generateBarcode(requestDTO, createdBy) {
  try {
    const response = await axios.post(
      `${BASE_URL}api/barcode/generate?createdBy=${createdBy}`,
      requestDTO
    );
    return response.data;
  } catch (error) {
    console.error("Error generating barcode:", error.message);
    return {
      error: true,
      message: error.response?.data?.message || "Failed to generate barcode",
    };
  }
}

export async function getBarcodeByAccessionNumber(accessionNumber) {
  try {
    const response = await axios.get(
      `${BASE_URL}barcode/getByAccessionNumber`,
      {
        params: { accessionNumber },
      }
    );
    return response.data;
  } catch (error) {
    console.error(
      `Error fetching barcode by accession number (${accessionNumber}):`,
      error.message
    );
    return {
      error: true,
      message: error.response?.data?.message || "Failed to retrieve barcode",
    };
  }
}
