package com.digiworldexpo.lims.lab.dto;

import com.digiworldexpo.lims.constants.PaymentMode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Prepaid {
    private Double prepaidAdvance;
    private PaymentMode paymentMode;
}
