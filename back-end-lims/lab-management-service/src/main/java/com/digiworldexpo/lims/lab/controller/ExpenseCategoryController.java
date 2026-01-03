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

import com.digiworldexpo.lims.entities.lab_management.ExpenseCategory;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.service.ExpenseCategoryService;
import com.digiworldexpo.lims.lab.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@RequestMapping("/api/expense-categories")
@RestController
@Slf4j
public class ExpenseCategoryController {
	

	private final ExpenseCategoryService expenseCategoryService;
	
	private final HttpStatusCode httpStatusCode;



	public ExpenseCategoryController(ExpenseCategoryService expenseCategoryService, HttpStatusCode httpStatusCode) {
		super();
		this.expenseCategoryService = expenseCategoryService;
		this.httpStatusCode = httpStatusCode;
	}

	@PostMapping("/save")
	public ResponseEntity<ResponseModel<ExpenseCategory>> createExpense(@RequestHeader UUID userId,@RequestBody ExpenseCategory expenseCategory)
	{
		log.info("Begin ExpenseCategoryController --> createExpense() method...");
		ResponseModel<ExpenseCategory> responseModel=expenseCategoryService.createExpense(userId,expenseCategory);
		log.info("End ExpenseCategoryController --> createExpense() method...");
		HttpStatus httpStatusfromCode=httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusfromCode).body(responseModel);
	}
	
	@PostMapping("/get-all")
	public ResponseEntity<ResponseModel<List<ExpenseCategory>>> getAllExpenses( @RequestParam UUID labId,@RequestParam(required = false) String searchText,
																		    					@RequestParam(defaultValue = "0") Integer pageNumber,
																							@RequestParam(defaultValue = "10") Integer pageSize)
	{
		log.info("Begin ExpenseCategoryController-->  getAllExpenses()....!");
		ResponseModel<List<ExpenseCategory>> responseModel=expenseCategoryService.getAllExpenses(labId,pageNumber,pageSize,searchText);
		log.info("Begin ExpenseCategoryController-->  getAllExpenses()....!");
		HttpStatus httpStatusfromCode=httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusfromCode).body(responseModel);
	}
	
	@GetMapping("/get/{id}")
	public ResponseEntity<ResponseModel<ExpenseCategory>> getExpensesById(@PathVariable UUID id)
	{
		log.info("Begin ExpenseCategoryController-->  getExpensesById()....!");
		ResponseModel<ExpenseCategory> responseModel=expenseCategoryService.getExpensesById(id);
		log.info("Begin ExpenseCategoryController-->  getExpensesById()....!");
		HttpStatus httpStatusfromCode=httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusfromCode).body(responseModel);
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<ResponseModel<ExpenseCategory>> updateExpenses(@PathVariable UUID id,@RequestBody ExpenseCategory expenseCategory)
	{
		log.info("Begin ExpenseCategoryController-->  updateExpenses()....!");
		ResponseModel<ExpenseCategory> responseModel=expenseCategoryService.updateExpenses(id,expenseCategory);
		log.info("Begin ExpenseCategoryController-->  updateExpenses()....!");
		HttpStatus httpStatusfromCode=httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusfromCode).body(responseModel);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResponseModel<ExpenseCategory>> deleteExpenses(@PathVariable UUID id)
	{
		log.info("Begin ExpenseCategoryController-->  deleteExpenses()....!");
		ResponseModel<ExpenseCategory> responseModel=expenseCategoryService.deleteExpenses(id);
		log.info("Begin ExpenseCategoryController-->  deleteExpenses()....!");
		HttpStatus httpStatusfromCode=httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode());
		return ResponseEntity.status(httpStatusfromCode).body(responseModel);
	}
	
	

}
