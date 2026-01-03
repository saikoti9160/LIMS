package com.digiworldexpo.lims.master.service;

import java.util.List;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.digiworldexpo.lims.entities.master.Account;
import com.digiworldexpo.lims.master.model.response.ResponseModel;


@Service
public interface AccountService {

	ResponseModel<Account> saveAccount(Account account, UUID userId);

	ResponseModel<Account> getAccountById(UUID id);

	ResponseModel<List<Account>> getAllAccounts(String startsWith, Boolean status, int pageNumber, int pageSize, String sortedBy);

	ResponseModel<Account> updateAccountById(UUID id, Account newAccountData);

	ResponseModel<Account> deleteAccountById(UUID id);

}