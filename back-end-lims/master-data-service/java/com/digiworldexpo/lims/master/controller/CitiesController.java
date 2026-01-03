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

import com.digiworldexpo.lims.entities.master.Cities;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.service.CitiesService;
import com.digiworldexpo.lims.master.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/city")
@Slf4j
@CrossOrigin
public class CitiesController {
	
	private final CitiesService citiesService;
	private final HttpStatusCode httpStatusCode;
	
	public CitiesController(CitiesService citiesService, HttpStatusCode httpStatusCode) {
		super();
		this.citiesService = citiesService;
		this.httpStatusCode = httpStatusCode;
	}

	@PostMapping("/upload-file")
	public ResponseEntity<ResponseModel<String>> uploadCitiesFile(@RequestParam("file") MultipartFile multipartFile){
		log.info("Begin of Cities Controller -> uploadCitiesFile() method");
		ResponseModel<String> responseModel = citiesService.uploadCitiesFile(multipartFile);
		log.info("End of Cities Controller -> uploadCitiesFile() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
	
	@PostMapping("/get-all")
	public ResponseEntity<ResponseModel<List<Cities>>> getAllCities(@RequestParam(required = false) String startsWith, @RequestParam(required = false) List<String> stateNames,
			@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize, @RequestParam(required = false, defaultValue = "cityName") String sortedBy ){
		log.info("Begin of Cities Controller -> getAllCities() method");
		ResponseModel<List<Cities>> responseModel = citiesService.getAllCities(startsWith, stateNames, pageNumber, pageSize, sortedBy);
		log.info("End of Cities Controller -> getAllCities() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
	
	@PostMapping("/save")
	public ResponseEntity<ResponseModel<Cities>> saveCity(@RequestBody Cities cityRequest, @RequestParam(required = false, name = "createdBy") UUID createdBy){
		log.info("Begin of Cities Controller -> saveCity() method");
		ResponseModel<Cities> responseModel = citiesService.saveCity( cityRequest, createdBy);
		log.info("End of Cities Controller -> saveCity() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<ResponseModel<Cities>> updateCityById(@PathVariable UUID id, @RequestBody Cities cityRequest, @RequestParam(required = false, name = "modifiedBy") UUID modifiedBy){
		log.info("Begin of Cities Controller -> updateCityById() method");
		ResponseModel<Cities> responseModel = citiesService.updateCityById(id, cityRequest, modifiedBy);
		log.info("End of Cities Controller -> updateCityById() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
	
	@GetMapping("/get/{id}")
	public ResponseEntity<ResponseModel<Cities>> getCityById(@PathVariable UUID id){
		log.info("Begin of Cities Controller -> getCityById() method");
		ResponseModel<Cities> responseModel = citiesService.getCityById(id);
		log.info("End of Cities Controller -> getCityById() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResponseModel<Cities>> deleteCityById(@PathVariable UUID id){
		log.info("Begin of Cities Controller -> deleteCityById() method");
		ResponseModel<Cities> responseModel = citiesService.deleteCityById(id);
		log.info("End of Cities Controller -> deleteCityById() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode())).body(responseModel);
	}
}
