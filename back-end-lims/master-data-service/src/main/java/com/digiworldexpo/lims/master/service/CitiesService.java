package com.digiworldexpo.lims.master.service;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.digiworldexpo.lims.entities.master.Cities;
import com.digiworldexpo.lims.master.model.request.CityRequest;
import com.digiworldexpo.lims.master.model.response.ResponseModel;

public interface CitiesService {

	ResponseModel<String> uploadCitiesFile(MultipartFile multipartFile);

	ResponseModel<List<Cities>> getAllCities(String startsWith, List<String> stateNames, int pageNumber, int pageSize,
			String sortedBy);

	ResponseModel<Cities> saveCity(Cities cityRequest, UUID createdBy);

	ResponseModel<Cities> updateCityById(UUID id, Cities cityRequest, UUID modifiedBy);

	ResponseModel<Cities> getCityById(UUID id);

	ResponseModel<Cities> deleteCityById(UUID id);
}
