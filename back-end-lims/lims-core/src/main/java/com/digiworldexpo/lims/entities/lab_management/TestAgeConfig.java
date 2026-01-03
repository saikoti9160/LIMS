package com.digiworldexpo.lims.entities.lab_management;



import java.sql.Timestamp;
import java.util.UUID;

import com.digiworldexpo.lims.entities.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "test_age_config", schema = "lab")
@Getter
@Setter
public class TestAgeConfig extends BaseEntity  {


	@Column(name="age_group")
    private String ageGroup;
	
	@Column(name="from_year")
    private Timestamp fromYear;
	
	@Column(name="to_year")
    private Timestamp toYear;

	@Column(name="high_value")
    private Integer highValue;
	
	@Column(name="low_value")
    private Integer lowValue;

	
}