package com.digiworldexpo.lims.entities.lab_management;

import com.digiworldexpo.lims.entities.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "test_range_config", schema = "lab")
@Getter
@Setter
public class TestRangeConfig extends BaseEntity {



	@Column(name="reference_low_value")
    private Integer referenceLowValue;
	
	@Column(name="reference_high_value")
    private Integer referenceHighValue;
}