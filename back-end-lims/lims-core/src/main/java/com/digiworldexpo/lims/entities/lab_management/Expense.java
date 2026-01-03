package com.digiworldexpo.lims.entities.lab_management;

import java.time.LocalDate;
import java.util.UUID;

import com.digiworldexpo.lims.entities.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="expense", schema = "lab")
@Entity
public class Expense extends BaseEntity {
	
	@Column(name="expense_amount")
	private Double expenseAmount;
	
	@Column(name="expense_date")
	private LocalDate expenseDate;
	
	 @ManyToOne(fetch = FetchType.LAZY)
	 @JoinColumn(name = "expense_category_id")
//	 @JsonIgnore
	private ExpenseCategory expenseCategory;
	
	@Column(name="upload_file")
	private String uploadFile;
	
	@Column(name = "description")
	private String description;
	
	@Column(name="lab_id")
	private UUID labId;
	

}