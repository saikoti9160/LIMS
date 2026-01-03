package com.digiworldexpo.lims.lab.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.digiworldexpo.lims.entities.lab_management.ExpenseCategory;

public interface ExpensecategoryRepository  extends JpaRepository<ExpenseCategory, UUID> {

	@Query("SELECT s FROM ExpenseCategory s WHERE "
            + "(:searchText IS NULL OR LOWER(s.expenseName) LIKE %:searchText%)")
	Page<ExpenseCategory> findByExpenseName(String searchText, Pageable pageable);

	Page<ExpenseCategory> findAllByLabId(UUID labId, Pageable pageable);
	

}