package com.digiworldexpo.lims.lab.dto;

import com.digiworldexpo.lims.constants.PaymentMode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostPaid {
    private Double postPaidCreditLimit;
    private PaymentMode paymentMode;
}
