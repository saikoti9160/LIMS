package com.digiworldexpo.lims.entities.lab_management;

import com.digiworldexpo.lims.constants.Gender;
import com.digiworldexpo.lims.entities.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "test_gender_config", schema = "lab")
@Getter
@Setter
public class TestGenderConfig extends BaseEntity {
	

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name="high_value")
    private Integer highValue;
    
    @Column(name="low_value")
    private Integer lowValue;
}