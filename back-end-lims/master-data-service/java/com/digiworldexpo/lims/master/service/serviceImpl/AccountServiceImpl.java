package com.digiworldexpo.lims.master.service.serviceImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.digiworldexpo.lims.entities.master.Account;
import com.digiworldexpo.lims.master.exception.BadRequestException;
import com.digiworldexpo.lims.master.exception.RecordNotFoundException;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.repository.AccountRepository;
import com.digiworldexpo.lims.master.service.AccountService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

	private final AccountRepository accountRepository;

	public AccountServiceImpl(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	@Override
	public ResponseModel<Account> saveAccount(Account account, UUID userId) {
		log.info("Begin of AccountServiceImpl -> saveAccount() method");
		ResponseModel<Account> responseModel = new ResponseModel<>();

		try {
			if (userId == null) {
				throw new IllegalArgumentException("UserId cannot be null");
			}

			if (account == null || account.getAccountName() == null || account.getAccountName().isEmpty()) {
				throw new BadRequestException("Provided Account name field must be filled");
			}

			if (account.getAccountName().trim().isEmpty() || account.getAccountName().charAt(0) == ' ') {
				throw new IllegalArgumentException("Account name must not start with a space or be empty");
			}

			account.setCreatedBy(userId);

			// Save the Account object in the database
			accountRepository.save(account);

			responseModel.setStatusCode(HttpStatus.CREATED.toString());
			responseModel.setMessage("Account has been saved successfully in the database");
			responseModel.setData(account);

		} catch (IllegalArgumentException illegalArgumentException) {
			log.error("Error occurred due to invalid argument: {}", illegalArgumentException.getMessage());
			throw illegalArgumentException;

		} catch (BadRequestException badRequestException) {
			log.error("Validation failed for account name input: {}", badRequestException.getMessage());
			throw badRequestException;

		} catch (Exception exception) {
			log.error("Error in saveAccount(): {}", exception.getMessage());
			throw exception;
		}

		log.info("End of AccountServiceImpl -> saveAccount() method");
		return responseModel;
	}

	@Override
	public ResponseModel<Account> getAccountById(UUID id) {
		log.info("Begin of AccountServiceImpl -> getAccountById() method");
		ResponseModel<Account> responseModel = new ResponseModel<>();
		try {
			Account account = accountRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("No Account found for Id: " + id));
			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Account data retrieved successfully");
			responseModel.setData(account);
		} catch (RecordNotFoundException recordNotFoundException) {
			log.error("Error: {}", recordNotFoundException.getMessage());
			throw recordNotFoundException;
		} catch (Exception exception) {
			log.error("Unexpected error occurred: {}", exception.getMessage());
			throw exception;
		}
		log.info("End of AccountServiceImpl -> getAccountById() method");
		return responseModel;
	}

	@Override
	public ResponseModel<List<Account>> getAllAccounts(String startsWith, Boolean status, int pageNumber, int pageSize,
			String sortedBy) {
		log.info("Begin of AccountServiceImpl -> getAllAccounts() method");
		ResponseModel<List<Account>> responseModel = new ResponseModel<>();
		try {
			Sort sort = Sort.by(Sort.Direction.ASC, sortedBy);
			Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
			List<Account> accounts = accountRepository.findAll(pageable).getContent();
			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Account data retrieved successfully");
			responseModel.setData(accounts);
		} catch (Exception exception) {
			log.error("Unexpected error occurred in getAllAccounts(): {}", exception.getMessage());
			throw exception;
		}
		log.info("End of AccountServiceImpl -> getAllAccounts() method");
		return responseModel;
	}

	@Override
	public ResponseModel<Account> updateAccountById(UUID id, Account newAccountData) {
		log.info("Begin of AccountServiceImpl -> updateAccountById() method");
		ResponseModel<Account> responseModel = new ResponseModel<>();
		try {
			Account account = accountRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("No Account found for Id: " + id));
			account.setAccountName(newAccountData.getAccountName());
			accountRepository.saveAndFlush(account);
			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Account has been updated successfully");
			responseModel.setData(account);
		} catch (RecordNotFoundException recordNotFoundException) {
			log.error("Error: {}", recordNotFoundException.getMessage());
			throw recordNotFoundException;
		} catch (Exception exception) {
			log.error("Unexpected error occurred in updateAccountById(): {}", exception.getMessage());
			throw exception;
		}
		log.info("End of AccountServiceImpl -> updateAccountById() method");
		return responseModel;
	}

	@Override
	public ResponseModel<Account> deleteAccountById(UUID id) {
		log.info("Begin of AccountServiceImpl -> deleteAccountById() method");
		ResponseModel<Account> responseModel = new ResponseModel<>();
		try {
			Account account = accountRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("No Account found for Id: " + id));
			accountRepository.delete(account);
			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Account has been deleted successfully");
			responseModel.setData(account);
		} catch (RecordNotFoundException recordNotFoundException) {
			log.error("Error: {}", recordNotFoundException.getMessage());
			throw recordNotFoundException;
		} catch (Exception exception) {
			log.error("Unexpected error occurred in deleteAccountById(): {}", exception.getMessage());
			throw exception;
		}
		log.info("End of AccountServiceImpl -> deleteAccountById() method");
		return responseModel;
	}
}