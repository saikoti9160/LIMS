package com.digiworldexpo.lims.entities.lab_management;

import java.util.List;

import com.digiworldexpo.lims.constants.NumericType;
import com.digiworldexpo.lims.entities.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "test_numeric_config", schema = "lab")
@Getter
@Setter
public class TestNumericConfig extends BaseEntity {


    @Enumerated(EnumType.STRING)
    private NumericType numericType;

    @OneToMany(cascade = CascadeType.ALL)
    private List<TestGenderConfig> genderBasedConfigs;

    @OneToMany(cascade = CascadeType.ALL)
    private List<TestAgeConfig> ageBasedConfigs;

    @OneToOne(cascade = CascadeType.ALL)
    private TestRangeConfig rangeBasedConfig;
}