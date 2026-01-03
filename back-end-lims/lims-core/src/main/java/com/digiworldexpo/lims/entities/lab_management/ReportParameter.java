package com.digiworldexpo.lims.entities.lab_management;


import java.util.UUID;

import com.digiworldexpo.lims.constants.ResultType;
import com.digiworldexpo.lims.entities.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "report_parameter", schema = "lab")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportParameter extends BaseEntity {

	@Column(name = "test_parameter")
	private String testParameter;

    @Enumerated(EnumType.STRING)
    private ResultType resultType;

    @OneToOne(cascade = CascadeType.ALL)
    private TestReference textReference;

    @OneToOne(cascade = CascadeType.ALL)
    private TestNumericConfig numericConfiguration;

    @OneToOne
    @JoinColumn(name="test_configuration_id")
    @JsonIgnore
    private TestConfigurationMaster testConfiguration;
    
    @Column(name="unit")
    private String unit;
    
    @Column(name="lab_id")
    private UUID labId;

    @Column(name="remarks")
    private String remarks;

}
