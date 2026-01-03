package com.digiworldexpo.lims.entities.lab_management;

import java.util.List;

import com.digiworldexpo.lims.entities.BaseEntity;
import com.digiworldexpo.lims.entities.master.Role;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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
@Table(name = "doctor_master", schema = "lab")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DoctorMaster extends BaseEntity {

    @Column(name = "doctor_name")
    private String doctorName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "phone_code")
    private String phoneCode;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private LabDepartment department;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "email")
    private String email;

    @Column(name = "set_password")
    private String setPassword;

    @Column(name = "show_on_appointment")
    private Boolean showOnAppointment;

    @Column(name = "doctor_passkey")
    private String doctorPasskey;

    @Column(name = "is_report_approver")
    private Boolean isReportApprover;

    @OneToMany(mappedBy = "doctorMaster", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference 
    private List<DoctorAvailability> availabilities;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_id", referencedColumnName = "id")
    private Lab lab;

    @Column(name = "doctor_sequence_id")
    private String doctorSequenceId;
}

