package com.digiworldexpo.lims.lab.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.lab_management.Expense;


@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    @Query("SELECT e FROM Expense e "
         + "JOIN e.expenseCategory ec "
         + "WHERE (:searchText IS NULL OR :searchText = '' OR LOWER(ec.expenseName) LIKE LOWER(CONCAT('%', :searchText, '%'))) "
         + "ORDER BY ec.expenseName ASC")
    Page<Expense> findExpenseWithSearchText(@Param("searchText") String searchText, Pageable pageable);

	Page<Expense> findAllByLabId(UUID labId, PageRequest pageable);
}
