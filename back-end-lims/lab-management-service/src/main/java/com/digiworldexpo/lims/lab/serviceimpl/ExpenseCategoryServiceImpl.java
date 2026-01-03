package com.digiworldexpo.lims.lab.serviceimpl;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.digiworldexpo.lims.entities.lab_management.ExpenseCategory;
import com.digiworldexpo.lims.lab.exception.RecordNotFoundException;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.repository.ExpensecategoryRepository;
import com.digiworldexpo.lims.lab.repository.LabRepository;
import com.digiworldexpo.lims.lab.service.ExpenseCategoryService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExpenseCategoryServiceImpl implements ExpenseCategoryService {

	private final ExpensecategoryRepository expenseRepository;
	private final LabRepository labRepository;

	public ExpenseCategoryServiceImpl(ExpensecategoryRepository expenseRepository, LabRepository labRepository) {
		super();
		this.expenseRepository = expenseRepository;
		this.labRepository = labRepository;
	}

	@Transactional
	@Override
	public ResponseModel<ExpenseCategory> createExpense(UUID userId, ExpenseCategory expenseCategory) {
		log.info("Begin ExpenseCategoryServiceImpl --> createExpense() method");

		ResponseModel<ExpenseCategory> responseModel = new ResponseModel<>();

		try {
			log.info("Saving ExpenseCategory: {}");

			ExpenseCategory savedExpenseCategory = expenseRepository.save(expenseCategory);

			log.info("Successfully saved ExpenseCategory with ID: {}", savedExpenseCategory.getId());

			expenseCategory.setCreatedBy(userId);
			expenseCategory.setCreatedOn(new Timestamp(System.currentTimeMillis()));

			responseModel.setData(savedExpenseCategory);
			responseModel.setMessage("Expense created successfully");
			responseModel.setStatusCode(HttpStatus.OK.toString());
		} catch (Exception e) {
			log.error("Error occurred while creating expense category: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Failed to create expense category");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}
		log.info("End ExpenseCategoryServiceImpl --> createExpense() method");
		return responseModel;
	}

	@Transactional
	@Override
	public ResponseModel<List<ExpenseCategory>> getAllExpenses(UUID labId, Integer pageNumber, Integer pageSize,
			String searchText) {
		log.info("Begin ExpenseCategoryServiceImpl --> getAllExpenses() method");

		ResponseModel<List<ExpenseCategory>> responseModel = new ResponseModel<>();

		try {

			log.debug("Validating page number and size: pageNumber={}, pageSize={}");
			if (pageNumber < 0 || pageSize <= 0) {
				log.warn("Invalid page number or size. Returning bad request.");
				responseModel.setData(null);
				responseModel.setMessage("Page number and size must be positive.");
				responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
				
			}

			log.debug("Creating pageable with pageNumber={}, pageSize={}", pageNumber, pageSize);
			Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Direction.ASC, "expenseName"));
			Page<ExpenseCategory> expensePage;

			if (searchText != null && !searchText.isEmpty()) {
				log.debug("Search text provided. Filtering by expenseName={}", searchText);
				expensePage = expenseRepository.findByExpenseName(searchText, pageable);
			} else {
				log.debug("No search text provided. Filtering by labId={}", labId);
				expensePage = expenseRepository.findAllByLabId(labId, pageable);
			}

			List<ExpenseCategory> expenseList = expensePage.getContent();
			log.debug("Found {} expense records.", expenseList.size());

			if (expenseList.isEmpty()) {
				log.info("No expense records found.");
				responseModel.setData(null);
				responseModel.setMessage("No expense records found");
				responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
				
			}

			log.info("Expense records retrieved successfully.");

			responseModel.setData(expenseList);
			responseModel.setMessage("Expense records retrieved successfully");
			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setTotalCount((int) expensePage.getTotalElements());
			responseModel.setPageNumber(expensePage.getNumber());
			responseModel.setPageSize(expensePage.getSize());

		} catch (Exception e) {
			log.error("Error occurred while fetching expense records: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Failed to fetch expense records due to an internal error");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End ExpenseCategoryServiceImpl --> getAllExpenses() method");
		return responseModel;
	}

	@Transactional
	@Override
	public ResponseModel<ExpenseCategory> getExpensesById(UUID id) {
		log.info("Begin ExpenseCategoryServiceImpl --> getExpensesById() method");

		ResponseModel<ExpenseCategory> responseModel = new ResponseModel<>();

		try {
			log.debug("Checking if expense ID is null: id={}", id);
			if (id == null) {
				log.warn("Expense ID is null. Throwing IllegalArgumentException.");
				throw new IllegalArgumentException("Expense ID cannot be null");
			}

			log.debug("Fetching expense with ID={}", id);
			ExpenseCategory expenseResponse = expenseRepository.findById(id).orElseThrow(() -> {
				log.error("No expense found with ID={}", id);
				return new RecordNotFoundException("No Expense found with this ID: " + id);
			});

			log.info("Expense found with ID={}. Returning response.", id);
			responseModel.setData(expenseResponse);
			responseModel.setMessage("Expense record retrieved successfully for ID: " + id);
			responseModel.setStatusCode(HttpStatus.OK.toString());

		} catch (RecordNotFoundException e) {
			log.error("Record not found: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());

		} catch (IllegalArgumentException e) {
			log.error("Invalid argument: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());

		} catch (Exception e) {
			log.error("Error occurred while fetching expense: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Failed to retrieve expense due to an internal error.");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End ExpenseCategoryServiceImpl --> getExpensesById() method ");
		return responseModel;
	}

	@Transactional
	@Override
	public ResponseModel<ExpenseCategory> updateExpenses(UUID id, ExpenseCategory expenseRequest) {
		log.info("Begin ExpenseCategoryServiceImpl --> updateExpenses() method");

		ResponseModel<ExpenseCategory> responseModel = new ResponseModel<>();

		try {
		    log.debug("Checking if expense with ID={} exists in the database", id);
		    if (!expenseRepository.existsById(id)) {
		        log.warn("No expense found with ID={}. Returning not found response.", id);
		        responseModel.setData(null);
		        responseModel.setMessage("No expense found with ID: " + id);
		        responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
		    }

		    log.debug("Expense with ID={} found. Setting the expense ID and updating.", id);
		    expenseRequest.setId(id);
		    
		    log.debug("Saving and flushing the updated expense record.");
		    ExpenseCategory expenseResponse = expenseRepository.save(expenseRequest);

		    log.info("Expense with ID={} updated successfully. Returning updated response.", id);
		    
		    responseModel.setData(expenseResponse);
			responseModel.setMessage("Expense data updated successfully");
			responseModel.setStatusCode(HttpStatus.OK.toString());

		} catch (Exception e) {
			log.error("Error while updating expense: {}", e.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Failed to update expense: " + e.getMessage());
			responseModel.setData(null);
		}

		log.info("End ExpenseCategoryServiceImpl --> updateExpenses() method");
		return responseModel;
	}

	@Transactional
	@Override
	public ResponseModel<ExpenseCategory> deleteExpenses(UUID id) {
		log.info("Begin ExpenseCategoryServiceImpl --> deleteExpenses() method...");

		ResponseModel<ExpenseCategory> responseModel = new ResponseModel<>();

		try {
		    log.debug("Checking if expense ID is null: id={}", id);
		    if (id == null) {
		        log.warn("Expense ID is null. Throwing IllegalArgumentException.");
		        throw new IllegalArgumentException("Expense ID cannot be null");
		    }

		    log.debug("Checking if expense with ID={} exists in the database", id);
		    if (!expenseRepository.existsById(id)) {
		        log.error("No expense found with ID={}. Throwing RecordNotFoundException.", id);
		        throw new RecordNotFoundException("No record found with this ID: " + id);
		    }

		    log.debug("Expense with ID={} found. ", id);
//		    expenseRepository.deleteById(id);
		  ExpenseCategory expenseData = expenseRepository.findById(id)
		    		.orElseThrow(()->new RecordNotFoundException("No record found with this ID: " + id));
		    		
		  expenseData.setActive(false);
		  expenseRepository.save(expenseData);

		  		responseModel.setMessage("ExpenseCategory deleted successfully for ID: " + id);
			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setData(expenseData);
		} catch (IllegalArgumentException | RecordNotFoundException e) {
			log.info("Error occurred: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage(e.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
		} catch (Exception e) {
			log.info("Error occurred while deleting Expense: {}", e.getMessage());
			responseModel.setMessage("Failed to Expense samle");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}
		log.info("End ExpenseCategoryServiceImpl --> deleteExpenses() method...");
		return responseModel;
	}

}
