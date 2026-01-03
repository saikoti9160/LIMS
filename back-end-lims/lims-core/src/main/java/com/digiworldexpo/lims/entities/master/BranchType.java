package com.digiworldexpo.lims.entities.master;

import com.digiworldexpo.lims.entities.BaseEntity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="branch_type",schema = "masterdata")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BranchType extends BaseEntity {
	
	@Column(name="branch_type_name")
	private String branchTypeName;

}
