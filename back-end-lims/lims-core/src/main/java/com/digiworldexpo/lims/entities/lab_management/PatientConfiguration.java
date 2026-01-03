package com.digiworldexpo.lims.entities.lab_management;

import com.digiworldexpo.lims.entities.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "patient_configuration" , schema = "lab")
@Getter
@Setter
public class PatientConfiguration extends BaseEntity {
    @Column(name = "send_reports_and_bills")
    private Boolean sendReportsAndBills;

    @Column(name = "send_reports_only")
    private Boolean sendReportsOnly;
    
    @OneToOne
    @JoinColumn(name = "organization_id")
    @JsonBackReference
    private Organization organization;
    
}