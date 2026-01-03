package com.digiworldexpo.lims.master.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.digiworldexpo.lims.entities.master.Account;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.service.AccountService;
import com.digiworldexpo.lims.master.util.HttpStatusCode;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/account")
@Slf4j
public class AccountController {

	private final AccountService accountService;
	private final HttpStatusCode httpStatusCode;

	public AccountController(AccountService accountService, HttpStatusCode httpStatusCode) {
		this.accountService = accountService;
		this.httpStatusCode = httpStatusCode;
	}

	@PostMapping("/save")
	public ResponseEntity<ResponseModel<Account>> saveAccount(@RequestBody Account account,  @RequestHeader UUID userId) {
		log.info("Begin of AccountController -> saveAccount() method");
		ResponseModel<Account> responseModel = accountService.saveAccount(account, userId);
		log.info("End of AccountController -> saveAccount() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode()))
				.body(responseModel);
	}

	@GetMapping("/get/{id}")
	public ResponseEntity<ResponseModel<Account>> getAccountById(@PathVariable UUID id) {
		log.info("Begin of AccountController -> getAccountById() method");
		ResponseModel<Account> responseModel = accountService.getAccountById(id);
		log.info("End of AccountController -> getAccountById() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode()))
				.body(responseModel);
	}

	@PostMapping("/get-all")
	public ResponseEntity<ResponseModel<List<Account>>> getAllAccounts(
			@RequestParam(required = false) String startsWith, @RequestParam(required = false) Boolean status,
			@RequestParam(defaultValue = "0") int pageNumber, @RequestParam(defaultValue = "10") int pageSize,
			@RequestParam(required = false, defaultValue = "accountName") String sortedBy) {
		log.info("Begin of AccountController -> getAllAccounts() method");
		ResponseModel<List<Account>> responseModel = accountService.getAllAccounts(startsWith, status, pageNumber,
				pageSize, sortedBy);
		log.info("End of AccountController -> getAllAccounts() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode()))
				.body(responseModel);
	}

	@PutMapping("/update/{id}")
	public ResponseEntity<ResponseModel<Account>> updateAccountById(@PathVariable UUID id,
			@RequestBody Account newAccountData) {
		log.info("Begin of AccountController -> updateAccountById() method");
		ResponseModel<Account> responseModel = accountService.updateAccountById(id, newAccountData);
		log.info("End of AccountController -> updateAccountById() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode()))
				.body(responseModel);
	}

	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ResponseModel<Account>> deleteAccountById(@PathVariable UUID id) {
		log.info("Begin of AccountController -> deleteAccountById() method");
		ResponseModel<Account> responseModel = accountService.deleteAccountById(id);
		log.info("End of AccountController -> deleteAccountById() method");
		return ResponseEntity.status(httpStatusCode.getHttpStatusFromCode(responseModel.getStatusCode()))
				.body(responseModel);
	}
}
