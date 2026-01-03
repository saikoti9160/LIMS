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

@ToString
@Entity
@Table(name = "report_paper_size", schema = "masterdata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportPaperSize extends BaseEntity {


	@Column(name = "paper_size")
	private String paperSize;
	 

}