package com.digiworldexpo.lims.lab.service;

import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.entities.lab_management.Expense;
import com.digiworldexpo.lims.lab.dto.ExpenseDto;
import com.digiworldexpo.lims.lab.model.ResponseModel;

public interface ExpenseService {

	ResponseModel<Expense> createExpense(UUID userId,Expense expenseRequest);

	ResponseModel<List<ExpenseDto>> getAllExpense(UUID labId,String searchText, Integer pageNumber, Integer pageSize);

	ResponseModel<ExpenseDto> updateExpense(UUID id,ExpenseDto expenseRequest);

	ResponseModel<ExpenseDto> getByIdExpense(UUID id);

	ResponseModel<Expense> deleteByIdExpense(UUID id);
	
	

}