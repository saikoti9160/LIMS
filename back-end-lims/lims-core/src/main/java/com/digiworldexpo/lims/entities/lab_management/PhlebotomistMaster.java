package com.digiworldexpo.lims.entities.lab_management;

import java.util.List;

import com.digiworldexpo.lims.entities.BaseEntity;
import com.digiworldexpo.lims.entities.master.Role;

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
@Table(name = "phlebotomist_master", schema = "lab")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PhlebotomistMaster extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "employee_id")
    private String employeeId;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "phone_code")
    private String phoneCode;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "email")
    private String email;

    @OneToMany(mappedBy = "phlebotomistMaster", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PhlebotomistAvailability> availabilities;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_id", referencedColumnName = "id")
    private Lab lab;

    @Column(name = "phlebotomist_sequence_id")
    private String phlebotomistSequenceId;
}
