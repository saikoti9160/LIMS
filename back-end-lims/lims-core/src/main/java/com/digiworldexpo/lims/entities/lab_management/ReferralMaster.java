package com.digiworldexpo.lims.entities.lab_management;

import com.digiworldexpo.lims.entities.BaseEntity;

import com.digiworldexpo.lims.entities.master.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "referral_master", schema = "lab")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReferralMaster extends BaseEntity {

    @Column(name = "referral_name")
    private String referralName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_id", referencedColumnName = "id")
    private Lab lab;

    @Column(name = "email")
    private String email;

    @Column(name = "referral_sequence_id")
    private String referralSequenceId;
}

