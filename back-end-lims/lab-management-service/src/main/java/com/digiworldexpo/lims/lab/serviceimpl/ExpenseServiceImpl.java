package com.digiworldexpo.lims.lab.serviceimpl;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.digiworldexpo.lims.entities.lab_management.Expense;
import com.digiworldexpo.lims.entities.lab_management.ExpenseCategory;
import com.digiworldexpo.lims.lab.dto.ExpenseDto;
import com.digiworldexpo.lims.lab.exception.RecordNotFoundException;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.repository.ExpenseRepository;
import com.digiworldexpo.lims.lab.repository.ExpensecategoryRepository;
import com.digiworldexpo.lims.lab.service.ExpenseService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExpenseServiceImpl implements ExpenseService {

	private final ExpenseRepository expenseRepository;

	private final ExpensecategoryRepository expensecategoryRepository;

	public ExpenseServiceImpl(ExpenseRepository expenseRepository,
			ExpensecategoryRepository expensecategoryRepository) {
		super();
		this.expenseRepository = expenseRepository;
		this.expensecategoryRepository = expensecategoryRepository;
	}

	@Transactional
	@Override
	public ResponseModel<Expense> createExpense(UUID userId, Expense expenseRequest) {
		log.info("Begin ExpenseServiceImpl ->createExpense() method....! ");
		ResponseModel<Expense> responseModel = new ResponseModel<>();
		try {
			log.info("Begin saving expense data...");

			ExpenseCategory expenseCategory = expenseRequest.getExpenseCategory();
			log.info("Retrieved ExpenseCategory : {}");

			if (expenseCategory != null && expenseCategory.getId() != null) {
				log.info("Fetching ExpenseCategory from database for ID: {}", expenseCategory.getId());

				expenseCategory = expensecategoryRepository.findById(expenseCategory.getId()).orElseThrow(() -> {
					log.error("Invalid Expense Category ID: {}");
					return new IllegalArgumentException("Invalid Expense Category ID provided.");
				});

				log.info("ExpenseCategory data is valid and assigned to expense.");
				expenseRequest.setExpenseCategory(expenseCategory);
			}

			expenseRequest.setCreatedBy(userId);
			expenseRequest.setCreatedOn(new Timestamp(System.currentTimeMillis()));

			log.info("Saving Expense object to the database...");
			Expense savedExpense = expenseRepository.save(expenseRequest);

			log.info("Expense successfully saved with ID: {}", savedExpense.getId());
			Expense save = expenseRepository.save(expenseRequest);
			responseModel.setData(save);
			responseModel.setMessage("Expense object succeesfully saved");
			responseModel.setStatusCode(HttpStatus.OK.toString());
		} catch (Exception e) {
			log.error("Error occurred while creating Expense: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Failed to create expense");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}
		log.info("End ExpenseServiceImpl -> createExpense() method....!");
		return responseModel;
	}

	@Transactional
	@Override
	public ResponseModel<List<ExpenseDto>> getAllExpense(UUID labId, String searchText, Integer pageNumber,
			Integer pageSize) {
		log.info("Begin ExpenseServiceImpl -> getAllExpense() method...!");

		ResponseModel<List<ExpenseDto>> responseModel = new ResponseModel<>();

		try {
			if (pageNumber == null || pageNumber < 0 || pageSize == null || pageSize <= 0) {
				log.warn("Invalid pagination parameters: PageNumber = {}, PageSize = {}");
				throw new IllegalArgumentException("Page number and size must be positive integers.");
			}

			PageRequest pageable = PageRequest.of(pageNumber, pageSize,
					Sort.by(Direction.ASC, "expenseCategory.expenseName"));

			Page<Expense> expensePage;

			if (searchText != null && !searchText.isEmpty()) {
				log.info("Executing search query for text: '{}'", searchText);
				expensePage = expenseRepository.findExpenseWithSearchText(searchText, pageable);
			} else {
				log.info("Fetching expenses for Lab ID: {}", labId);
				expensePage = expenseRepository.findAllByLabId(labId, pageable);
			}

			if (expensePage.isEmpty()) {
				log.warn("No expenses found for Lab ID: {} with searchText: '{}'", labId, searchText);
				responseModel.setData(Collections.emptyList());
				responseModel.setMessage("No Expense records found.");
				responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
				return responseModel;
			}

			// Convert entity to DTO
			List<ExpenseDto> responseDto = expensePage.getContent().stream().map(this::convertEntityToDto)
					.collect(Collectors.toList());

			responseModel.setData(responseDto);
			responseModel.setMessage("Expense records retrieved successfully");
			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setTotalCount((int) expensePage.getTotalElements());
			responseModel.setPageNumber(expensePage.getNumber());
			responseModel.setPageSize(expensePage.getSize());

			log.info("Successfully retrieved {} expense records.", responseDto.size());

		} catch (IllegalArgumentException e) {
			log.error("Invalid input for pagination: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Invalid pagination parameters: " + e.getMessage());
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
		} catch (Exception e) {
			log.error("Error occurred while fetching Expense: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Failed to fetch Expense due to an internal error.");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End ExpenseServiceImpl -> getAllExpense() method....! ");
		return responseModel;
	}

	@Transactional
	@Override
	public ResponseModel<ExpenseDto> updateExpense(UUID id, ExpenseDto expenseRequest) {
		log.info("Begin ExpenseServiceImpl -> updateExpense() method....! ");
		ResponseModel<ExpenseDto> responseModel = new ResponseModel<>();
		try {

			if (!expenseRepository.existsById(id)) {
				log.warn("Update failed: No record found with ID: {}", id);
				responseModel.setData(null);
				responseModel.setMessage("NO record found with this id: " + id);
				responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
			}

			log.info("Updating expense with ID: {}", id);
			expenseRequest.setId(id);

			Expense toEntity = convertDtoToEntity(expenseRequest);
			toEntity.setModifiedBy(id);
			toEntity.setModifiedOn(new Timestamp(System.currentTimeMillis()));
			toEntity.setCreatedBy(id);
			toEntity.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			Expense savedExpense = expenseRepository.save(toEntity);
			ExpenseDto toDto = convertEntityToDto(savedExpense);

			responseModel.setData(toDto);
			responseModel.setMessage("Expense updated successfully...!");
			responseModel.setStatusCode(HttpStatus.OK.toString());

		} catch (Exception e) {
			log.error("Error while updating expense: {}", e.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Failed to update expense: " + e.getMessage());
			responseModel.setData(null);
		}
		log.info("End ExpenseServiceImpl -> updateExpense() method....! ");
		return responseModel;
	}

	@Transactional
	@Override
	public ResponseModel<ExpenseDto> getByIdExpense(UUID id) {
		log.info("Begin ExpenseServiceImpl -> getByIdExpense() method....! ");

		ResponseModel<ExpenseDto> responseModel = new ResponseModel<>();

		try {
			log.info("Checking if provided Expense ID is null...");

			if (id == null) {
				log.warn("Expense ID is null. Returning BAD_REQUEST response.");
				responseModel.setData(null);
				responseModel.setMessage("Expense Id cannot be null");
				responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
				return responseModel;
			}

			log.info("Fetching expense record for ID: {}", id);

			Expense responseEntity = expenseRepository.findById(id).orElseThrow(() -> {
				log.error("No record found for Expense ID: {}", id);
				return new RecordNotFoundException("No record found by this ID: " + id);
			});

			log.info("Successfully fetched expense record: {}", responseEntity);

			ExpenseDto responseDto = convertEntityToDto(responseEntity);
			responseModel.setData(responseDto);
			responseModel.setMessage("Expense fetch successfully...!");
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
		log.info("Begin ExpenseServiceImpl -> getByIdExpense() method....! ");
		return responseModel;
	}

	@Transactional
	@Override
	public ResponseModel<Expense> deleteByIdExpense(UUID id) {
		log.info("Begin ExpenseServiceImpl -> getByIdExpense() method....! ");

		ResponseModel<Expense> responseModel = new ResponseModel<>();

		try {
			log.info("Checking if provided Expense ID is null...");

			if (id == null) {
				log.warn("Expense ID is null. Throwing IllegalArgumentException.");
				throw new IllegalArgumentException("Expense ID cannot be null");
			}

			log.info("Verifying existence of Expense record with ID: {}", id);

			Expense expenseData = expenseRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("No record found with this ID: " + id));

//			  expenseData.setActive(false);
//			  expenseRepository.save(expenseData);

			expenseRepository.deleteById(id);

			log.info("Successfully deleted Expense record with ID: {}", id);
			responseModel.setData(expenseData);
			responseModel.setMessage("Expense deleted successfully for ID: " + id);
			responseModel.setStatusCode(HttpStatus.OK.toString());
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
		log.info("End ExpenseServiceImpl -> getByIdExpense() method....! ");
		return responseModel;
	}

	private Expense convertDtoToEntity(ExpenseDto expenseDto) {
		Expense expense = new Expense();
		BeanUtils.copyProperties(expenseDto, expense);

		if (expenseDto.getExpenseCategory() != null) {
			ExpenseCategory category = new ExpenseCategory();
			BeanUtils.copyProperties(expenseDto.getExpenseCategory(), category);
			expense.setExpenseCategory(category);
		}
		return expense;
	}

	private ExpenseDto convertEntityToDto(Expense expense) {
		ExpenseDto dto = new ExpenseDto();
		BeanUtils.copyProperties(expense, dto);

		if (expense.getExpenseCategory() != null) {
			ExpenseCategory category = new ExpenseCategory(); // Assuming you have a DTO for ExpenseCategory
			BeanUtils.copyProperties(expense.getExpenseCategory(), category);
			dto.setExpenseCategory(category);
		}
		return dto;
	}

}
