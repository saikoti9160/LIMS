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
@Table(name = "report_footer_size", schema = "masterdata")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportFooterSize extends BaseEntity {

	@Column(name = "footer_size")
	private String footerSize;

}