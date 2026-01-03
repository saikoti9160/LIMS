package com.digiworldexpo.lims.master.service;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.digiworldexpo.lims.entities.master.States;
import com.digiworldexpo.lims.master.model.request.StateRequest;
import com.digiworldexpo.lims.master.model.response.ResponseModel;

public interface StatesService {

	ResponseModel<String> uploadStatesFile(MultipartFile multipartFile);


	ResponseModel<List<States>> getAllStates(String startsWith, List<String> countryNames, int pageNumber, int pageSize, String sortedBy);

	ResponseModel<States> saveState(States stateRequest, UUID createdBy);

	ResponseModel<States> updateStateById(UUID id, States stateRequest, UUID modifiedBy);

	ResponseModel<States> getStateById(UUID id);

	ResponseModel<States> deleteStateById(UUID id);
}
