package com.digiworldexpo.lims.entities.lab_management;


import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.digiworldexpo.lims.entities.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "test_configuration", schema = "lab")
public class TestConfigurationMaster extends BaseEntity {

	
	@Column(name = "time_to_sharereport")
	private Timestamp timeToShareReport;


	@Column(name = "test_price")
	private Double testPrice;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "lab_department_id")
	private LabDepartment labDepartment;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_mapping_id")
    private SampleMapping sampleMapping;
    
    @OneToOne(mappedBy = "testConfiguration", cascade = CascadeType.ALL)
//    @JsonBackReference
    private ReportParameter reportParameters;
	
	@Column(name = "outsource")
	private Boolean isOutsource;
	
	@Column(name = "test_description")
	private String description;
	
	@Column(name = "instructions")
	private String instructions;
	
	@Column(name = "analytical_days", nullable = true)
	private Integer analyticalDays;
	
	@Column(name = "analytical_hours", nullable = true)
	private Integer analyticalHours;
	
	@Column(name = "analytical_minutes", nullable = true)
	private Integer analyticalMinutes;
	
	@Column(name = "lab_id")
	private UUID labId;


}		