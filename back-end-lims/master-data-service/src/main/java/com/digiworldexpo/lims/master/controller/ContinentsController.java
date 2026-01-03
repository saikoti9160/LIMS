package com.digiworldexpo.lims.master.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
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

import com.digiworldexpo.lims.entities.master.Continents;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.service.ContinentsService;
import com.digiworldexpo.lims.master.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/continent")
@Slf4j
public class ContinentsController {

	private final ContinentsService continentsService;
	private final HttpStatusCode httpStatusCode;

	public ContinentsController(ContinentsService continentsService, HttpStatusCode httpStatusCode) {
		this.continentsService = continentsService;
		this.httpStatusCode = httpStatusCode;
	}

	@PostMapping("/upload-file")
	public ResponseEntity<ResponseModel<String>> uploadContinentsFile(
			@RequestParam("file") MultipartFile multipartFile) {
		log.info("Begin Continents Controller -> uploadContinentsFile() method");
		ResponseModel<String> responseModel = continentsService.uploadContinentsFile(multipartFile);
		log.info("End Continents Controller -> uploadContinentsFile() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode()))
				.body(responseModel);
	}

	@PostMapping("/get-all")
	public ResponseEntity<ResponseModel<List<Continents>>> getAllContinents(
			@RequestParam(required = false) String startsWith, @RequestParam(defaultValue = "0") int pageNumber,
			@RequestParam(defaultValue = "10") int pageSize,
			@RequestParam(required = false, defaultValue = "continentName") String sortedBy) {
		log.info("Begin of Continents Controller -> getAllContinents() method");
		ResponseModel<List<Continents>> responseModel = continentsService.getAllContinents(startsWith, pageNumber,
				pageSize, sortedBy);
		log.info("End of Continents Controller -> getAllContinents() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode()))
				.body(responseModel);
	}

	@PostMapping("/save")

	public ResponseEntity<ResponseModel<Continents>> saveContinent(@RequestBody Continents continent,
			@RequestParam(required = false, name = "createdBy") UUID createdBy) {
		log.info("Begin of Continents Controller -> saveContinent() method");
		ResponseModel<Continents> responseModel = continentsService.saveContinent(continent, createdBy);
		log.info("End of Continents Controller -> saveContinent() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode()))
				.body(responseModel);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<ResponseModel<Continents>> updateContinentById(@PathVariable UUID id,
			@RequestBody Continents continent, @RequestParam(required = false, name = "modifiedBy") UUID modifiedBy) {
		log.info("Begin of Continents Controller -> updateContinentById() method");
		ResponseModel<Continents> responseModel = continentsService.updateContinentById(id, continent, modifiedBy);
		log.info("End of Continents Controller -> updateContinentById() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode()))
				.body(responseModel);
	}

	@GetMapping("/get/{id}")
	public ResponseEntity<ResponseModel<Continents>> getContinentById(@PathVariable UUID id) {
		log.info("Begin of Continents Controller -> getContinentById() method");
		ResponseModel<Continents> responseModel = continentsService.getContinentById(id);
		log.info("End of Continents Controller -> getContinentById() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode()))
				.body(responseModel);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResponseModel<Continents>> deleteContinentById(@PathVariable UUID id) {
		log.info("Begin of Continents Controller -> deleteContinentById() method");
		ResponseModel<Continents> responseModel = continentsService.deleteContinentById(id);
		log.info("End of Continents Controller -> deleteContinentById() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode()))
				.body(responseModel);
	}
}
