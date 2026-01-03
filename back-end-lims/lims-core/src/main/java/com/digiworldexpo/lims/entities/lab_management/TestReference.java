package com.digiworldexpo.lims.entities.lab_management;

import com.digiworldexpo.lims.entities.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "test_reference", schema = "lab")
@Getter
@Setter
public class TestReference extends BaseEntity{
	


	@Column(name="reference_result")
    private String referenceResult;
}