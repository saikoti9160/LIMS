package com.digiworldexpo.lims.entities.lab_management;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.entities.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "profile_configuration", schema = "lab")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileConfiguration extends BaseEntity {

    @Column(name = "profile_name")
    private String profileName;

    @Column(name = "out_source")
    private Boolean outSource;

    @Column(name = "time_to_share_report")
    private Timestamp timeToShareReport; 
    
    @Column(name = "turnaround_time_days")
    private Integer turnaroundTimeDays;

    @Column(name = "turnaround_time_hours")
    private Integer turnaroundTimeHours;

    @Column(name = "turnaround_time_minutes")
    private Integer turnaroundTimeMinutes;
    
    @Column(name = "tests")
    private List<UUID> tests;

    @Column(name = "profile_description")
    private String profileDescription;

    @Column(name = "profile_instructions")
    private String profileInstructions;

    @Column(name = "total_amount")
    private Double totalAmount;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_id", referencedColumnName = "id")
    private Lab lab;
    
//    @OneToMany(mappedBy = "profileConfiguration", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<TestParameter> testParameters;
}