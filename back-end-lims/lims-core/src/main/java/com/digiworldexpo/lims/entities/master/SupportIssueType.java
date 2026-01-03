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
@Table(name = "support_issue_type", schema = "masterdata")
@Getter
@Setter	
@NoArgsConstructor
@AllArgsConstructor
public class SupportIssueType extends BaseEntity{
	
	@Column(name = "name")
	private String name;
}
