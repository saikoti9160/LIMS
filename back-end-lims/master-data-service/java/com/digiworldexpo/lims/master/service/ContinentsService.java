package com.digiworldexpo.lims.master.service;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.digiworldexpo.lims.entities.master.Continents;
import com.digiworldexpo.lims.master.model.response.ResponseModel;

public interface ContinentsService {

	ResponseModel<String> uploadContinentsFile(MultipartFile multipartFile);

	ResponseModel<List<Continents>> getAllContinents(String startsWith, int pageNumber, int pageSize, String sortedBy);

	ResponseModel<Continents> saveContinent(Continents continent, UUID createdBy);

	ResponseModel<Continents> updateContinentById(UUID id, Continents continent, UUID modifiedBy);

	ResponseModel<Continents> getContinentById(UUID id);

	ResponseModel<Continents> deleteContinentById(UUID id);
}
