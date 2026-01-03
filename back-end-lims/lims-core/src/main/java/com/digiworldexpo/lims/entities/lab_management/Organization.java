package com.digiworldexpo.lims.entities.lab_management;

import com.digiworldexpo.lims.entities.BaseEntity;
import com.digiworldexpo.lims.entities.master.Role;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "organization", schema = "lab")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Organization extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone_code")
    private String phoneCode;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "email", nullable = false)
    private String email;

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

    @OneToOne(mappedBy = "organization", cascade = CascadeType.ALL)
    @JsonManagedReference
    private PaymentDetails paymentDetails;  // Embedded, so no need for @GeneratedValue

    @Column(name = "comments")
    private String comments;

    @Column(name = "invoice_generation_frequency")
    private String invoiceGenerationFrequency;

    @Column(name = "custom_frequency")
    private String customFrequency;

    @OneToOne(mappedBy = "organization", cascade = CascadeType.ALL)
    @JsonManagedReference
    private BillAccessConfiguration billAccessConfiguration;

    @OneToOne(mappedBy = "organization", cascade = CascadeType.ALL)
    @JsonManagedReference
    private ReportAccessConfiguration reportAccessConfiguration;

    @OneToOne(mappedBy = "organization", cascade = CascadeType.ALL)
    @JsonManagedReference
    private PatientConfiguration patientConfiguration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_id", referencedColumnName = "id")
    private Lab lab;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "organization_sequence_id")
    private String organizationSequenceId;
}
