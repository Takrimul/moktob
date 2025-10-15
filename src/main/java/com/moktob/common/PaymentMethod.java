package com.moktob.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentMethod {
    CASH("Cash"),
    BANK_TRANSFER("Bank Transfer"),
    CHECK("Check"),
    CARD("Card"),
    ONLINE("Online");

    private final String displayName;
}
