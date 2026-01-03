package com.digiworldexpo.lims.constants;

import lombok.Getter;

@Getter
public enum PaymentMode {
    CASH("Cash"),
    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    NET_BANKING("Net Banking"),
    UPI("UPI"),
    CHEQUE("Cheque"),
    DEMAND_DRAFT("Demand Draft");

    private final String value;

    PaymentMode(String value) {
        this.value = value;
    }
}
