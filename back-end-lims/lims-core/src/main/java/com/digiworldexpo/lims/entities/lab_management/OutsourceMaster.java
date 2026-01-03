package com.digiworldexpo.lims.entities.lab_management;

import java.util.List;
import java.util.UUID;

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
@Table(name = "outsource_master", schema = "lab")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OutsourceMaster extends BaseEntity {

    @Column(name = "outsource_center_name", nullable = false)
    private String outsourceCenterName;

    @Column(name = "contact_person_name")
    private String contactPersonName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "country")
    private String country;

    @Column(name = "state")
    private String state;

    @Column(name = "city")
    private String city;

    @Column(name = "pin_code")
    private String pinCode;

    @Column(name = "address")
    private String address;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_id", referencedColumnName = "id")
    private Lab lab;

    @Column(name = "test_id")
    private List<UUID> tests;
    
    @Column(name = "profile_id")
    private List<UUID> profiles;

    @Column(name = "email", nullable = false)
    private String email;
    
    @Column(name = "outsource_sequence_id")
    private String outsourceSequenceId;
    
}	