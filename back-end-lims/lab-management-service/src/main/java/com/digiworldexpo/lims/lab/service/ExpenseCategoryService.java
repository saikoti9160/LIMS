package com.digiworldexpo.lims.lab.service;

import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.entities.lab_management.ExpenseCategory;
import com.digiworldexpo.lims.lab.model.ResponseModel;

public interface ExpenseCategoryService {

	ResponseModel<ExpenseCategory> createExpense(UUID userId,ExpenseCategory expenseCategory);

	ResponseModel<List<ExpenseCategory>> getAllExpenses(UUID labId,Integer pageNumber, Integer pageSize, String searchText);

	ResponseModel<ExpenseCategory> getExpensesById(UUID id);

	ResponseModel<ExpenseCategory> updateExpenses(UUID id, ExpenseCategory expense);

	ResponseModel<ExpenseCategory> deleteExpenses(UUID id);

}