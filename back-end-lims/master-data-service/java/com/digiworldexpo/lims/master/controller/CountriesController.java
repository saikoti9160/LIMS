package com.digiworldexpo.lims.master.controller;


import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.digiworldexpo.lims.entities.master.Countries;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.service.CountriesService;
import com.digiworldexpo.lims.master.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/country")
@Slf4j
@CrossOrigin
public class CountriesController {
	
	private final CountriesService countriesService;
	private final HttpStatusCode httpStatusCode;
	
	
	public CountriesController(CountriesService countriesService, HttpStatusCode httpStatusCode) {
		this.countriesService = countriesService;
		this.httpStatusCode = httpStatusCode;
	}

	@PostMapping("/upload-file")
	public ResponseEntity<ResponseModel<String>> uploadCountriesFile(@RequestParam("file") MultipartFile multipartFile){
		log.info("Begin of Countries Controller -> uploadCountriesFile() method");
		ResponseModel<String> responseModel = countriesService.uploadCountriesFile(multipartFile);
		log.info("End of Countries Controller -> uploadCountriesFile() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
	
	@PostMapping("/get-all")
	public ResponseEntity<ResponseModel<List<Countries>>> getAllCountries(@RequestParam(required = false) String startsWith, @RequestParam(required = false) List<String> continentNames,
			@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize, @RequestParam(required = false, defaultValue = "countryName") String sortedBy){
		log.info("Begin of Countries Controller -> getAllCountries() method");
		ResponseModel<List<Countries>> responseModel = countriesService.getAllCountries(startsWith,continentNames, pageNumber, pageSize, sortedBy);
		log.info("End of Countries Controller -> getAllCountries() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
	
	@PostMapping("/save")
	public ResponseEntity<ResponseModel<Countries>> saveCountry(@RequestBody Countries countries, @RequestParam(required = false, name="createdBy") UUID createdBy){
		log.info("Begin of Countries Controller -> saveCountry() method");
		ResponseModel<Countries> responseModel = countriesService.saveCountry(countries, createdBy);
		log.info("End of Countries Controller -> saveCountry() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<ResponseModel<Countries>> updateCountryById(@PathVariable UUID id, @RequestBody Countries newCountryData, @RequestParam(required = false, name="modifiedBy") UUID modifiedBy){
		log.info("Begin of Countries Controller -> updateCountryById() method");
		ResponseModel<Countries> responseModel = countriesService.updateCountryById(id, newCountryData, modifiedBy);
		log.info("End of Countries Controller -> updateCountryById() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
	
	@GetMapping("/get/{id}")
	public ResponseEntity<ResponseModel<Countries>> getCountryById(@PathVariable UUID id){
		log.info("Begin of Countries Controller -> getCountryById() method");
		ResponseModel<Countries> responseModel = countriesService.getCountryById(id);
		log.info("End of Countries Controller -> getCountryById() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResponseModel<Countries>> deleteCountryById(@PathVariable UUID id){
		log.info("Begin of Countries Controller -> deleteCountryById() method");
		ResponseModel<Countries> responseModel = countriesService.deleteCountryById(id);
		log.info("End of Countries Controller -> deleteCountryById() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
}
