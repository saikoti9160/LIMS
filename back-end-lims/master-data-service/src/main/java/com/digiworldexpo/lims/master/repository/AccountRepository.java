package com.digiworldexpo.lims.master.repository;

import java.util.Optional;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.master.Account;



@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
	
	Optional<Account> findByAccountName(String accountName);


}