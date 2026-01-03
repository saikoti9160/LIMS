package com.digiworldexpo.lims.lab.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.digiworldexpo.lims.entities.lab_management.ExpenseCategory;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseDto {
	
	private UUID id;
	private UUID labId;
	private LocalDate expenseDate;
	private Double expenseAmount;
	private boolean active;
	private String description;
	private ExpenseCategory expenseCategory;
	private String uploadFile;
}
