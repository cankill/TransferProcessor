package com.fan.transfer.api.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Currency;

@Value
@Builder
public class CreateAccountRequest {
    private Currency currency;
    private BigDecimal balance;
}
