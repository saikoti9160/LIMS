package com.digiworldexpo.lims.entities.master;

import com.digiworldexpo.lims.entities.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "report_date_format", schema = "masterdata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReportDateFormat extends BaseEntity {
	
	@Column(name = "date_format")
	private String dateFormat;

}
