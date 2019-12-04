package com.fan.transfer.api.model;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Currency;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateAccountRequest {
    private final Currency currency;
    private final BigDecimal balance;
}
