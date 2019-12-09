package com.fan.transfer.api.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Currency;

/**
 * Representation model for API.
 * Base request to create an Account with desired currency and balance
 */
@Value
@Builder
public class CreateAccountRequest {
    private Currency currency;
    private BigDecimal balance;
}
