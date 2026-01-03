package com.digiworldexpo.lims.entities.lab_management;

import java.util.List;

import com.digiworldexpo.lims.constants.PaymentMode;
import com.digiworldexpo.lims.entities.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payment_details", schema = "lab")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDetails extends BaseEntity {
    @Column(name = "prepaid_values")
    private List<Double> prepaidValues;

    @Column(name = "postpaid_values")
    private List<Double> postpaidValues;

    @Enumerated(EnumType.STRING)
    @Column(name = "prepaid_payment_modes")
    private List<PaymentMode> prepaidPaymentModes;

    @Enumerated(EnumType.STRING)
    @Column(name = "postpaid_payment_modes")
    private List<PaymentMode> postpaidPaymentModes;

    @OneToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;
}