package com.digiworldexpo.lims.entities.lab_management;

import java.util.UUID;

import com.digiworldexpo.lims.entities.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name="expense_category", schema = "lab")
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseCategory extends BaseEntity {
	
	@Column(name="expense_name")
	private String expenseName; 

	    @Column(name = "lab_id")
	    private UUID labId;
}
 