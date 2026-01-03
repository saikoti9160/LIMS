package com.digiworldexpo.lims.lab.service;


import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.entities.lab_management.SignatureMaster;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.request.SignatureMasterRequestDTO;
import com.digiworldexpo.lims.lab.response.SignatureMasterResponseDTO;
import com.digiworldexpo.lims.lab.response.SignatureMasterSearchResponse;

public interface SignatureMasterService {
	
	ResponseModel<SignatureMasterResponseDTO> saveSignature(UUID createdBy, SignatureMasterRequestDTO signatureMasterDTO);
	
    ResponseModel<SignatureMasterResponseDTO> getSignatureById(UUID id);

    ResponseModel<SignatureMasterResponseDTO> updateSignature(UUID id, SignatureMasterRequestDTO signatureMasterDTO);

    ResponseModel<SignatureMaster> deleteSignature(UUID id);
    
    ResponseModel<List<SignatureMasterSearchResponse>> getAllSignature(UUID createdBy,  String keyword, Boolean flag,
    		
    		Integer pageNumber, Integer pageSize);


}

