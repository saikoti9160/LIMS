package com.digiworldexpo.lims.lab.controller;


import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.digiworldexpo.lims.entities.lab_management.Expense;
import com.digiworldexpo.lims.lab.dto.ExpenseDto;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.service.ExpenseService;
import com.digiworldexpo.lims.lab.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/expense")
public class ExpenseController {

	private final ExpenseService expenseService;

	private final HttpStatusCode httpStatusCode;
	
	public ExpenseController(ExpenseService expenseService, HttpStatusCode httpStatusCode) {
		super();
		this.expenseService = expenseService;
		this.httpStatusCode = httpStatusCode;
	}

	@PostMapping("/save")
	public ResponseEntity<ResponseModel<Expense>> createExpense(@RequestHeader UUID userId,@RequestBody Expense expenseRequest) {
		log.info("Begin ExpenseController -> createExpense() method...!");
		ResponseModel<Expense> responseModel = expenseService.createExpense(userId,expenseRequest);
		log.info("End ExpenseController -> createExpense() Method...!");
		HttpStatus httpStatusfromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusfromCode).body(responseModel);
	}

	@PostMapping("/get-all")
	public ResponseEntity<ResponseModel<List<ExpenseDto>>> getAllExpense(@RequestParam UUID labId,
			@RequestParam(required = false) String searchText, @RequestParam(defaultValue = "0") Integer pageNumber,
			@RequestParam(defaultValue = "10") Integer pageSize) {
		log.info("Begin ExpenseController -> getAllExpense() method...!");
		ResponseModel<List<ExpenseDto>> responseModel = expenseService.getAllExpense(labId,searchText, pageNumber, pageSize);
		log.info("End ExpenseController -> getAllExpense() method...!");
		HttpStatus httpStatusfromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusfromCode).body(responseModel);

	}

	@PutMapping("/update/{id}")
	public ResponseEntity<ResponseModel<ExpenseDto>> updateExpense(@PathVariable UUID id,@RequestBody ExpenseDto expenseRequest) 
	{
		log.info("Begin ExpenseController -> updateExpense() method...!");
		ResponseModel<ExpenseDto> responseModel = expenseService.updateExpense(id, expenseRequest);
		log.info("End ExpenseController -> updateExpense() Method...!");
		HttpStatus httpStatusfromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusfromCode).body(responseModel);
	}
	
	@GetMapping("/get/{id}")
	public ResponseEntity<ResponseModel<ExpenseDto>> getByIdExpense(@PathVariable UUID id) {
		log.info("Begin ExpenseController -> getByIdExpense() method...!");
		ResponseModel<ExpenseDto> responseModel = expenseService.getByIdExpense(id);
		log.info("End ExpenseController -> getByIdExpense() Method...!");
		HttpStatus httpStatusfromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusfromCode).body(responseModel);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResponseModel<Expense>> deleteByIdExpense(@PathVariable UUID id)
	{
		log.info("Begin ExpenseController -> deleteByIdExpense() method...!");
		ResponseModel<Expense> responseModel = expenseService.deleteByIdExpense(id);
		log.info("End ExpenseController -> deleteByIdExpense() Method...!");
		HttpStatus httpStatusfromCode = httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusfromCode).body(responseModel);
	}

}
