package com.digiworldexpo.lims.lab.service;

import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.request.DoctorMasterRequestDTO;
import com.digiworldexpo.lims.lab.response.DoctorMasterResponseDTO;
import com.digiworldexpo.lims.lab.response.DoctorSearchResponseDTO;

public interface DoctorMasterService {

	ResponseModel<DoctorMasterResponseDTO> saveDoctorMaster(UUID createdBy, DoctorMasterRequestDTO doctorMasterDTORequestDTO);
	
	ResponseModel<DoctorMasterResponseDTO> getDoctorById(UUID id);
	
	ResponseModel<DoctorMasterResponseDTO> updateDoctorById(UUID id, DoctorMasterRequestDTO doctorMasterDTORequestDTO);
	
	ResponseModel<DoctorMasterResponseDTO> deleteDoctorById(UUID id);
	
	ResponseModel<List<DoctorSearchResponseDTO>> getAllDoctors(UUID createdBy, String keyword, Boolean flag, Integer pageNumber, Integer pageSize);


}