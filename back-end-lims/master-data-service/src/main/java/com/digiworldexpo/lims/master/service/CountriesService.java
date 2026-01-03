package com.digiworldexpo.lims.master.service;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.digiworldexpo.lims.entities.master.Countries;
import com.digiworldexpo.lims.master.model.response.ResponseModel;


public interface CountriesService {

	ResponseModel<String> uploadCountriesFile(MultipartFile multipartFile);
	
	ResponseModel<List<Countries>> getAllCountries(String startsWith,List<String> continentNames, int pageNumber, int pageSize, String sortedBy);
	
	ResponseModel<Countries> saveCountry(Countries country, UUID createdBy);
	
	ResponseModel<Countries> updateCountryById(UUID id, Countries newCountryData, UUID modifiedBy);
	
	ResponseModel<Countries> getCountryById(UUID id);

	ResponseModel<Countries> deleteCountryById(UUID id);
}
