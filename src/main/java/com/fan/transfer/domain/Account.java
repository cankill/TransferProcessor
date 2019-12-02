package com.fan.transfer.domain;

import lombok.Value;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

@Value
public class Account implements HasId {
    private String id;
    private Currency currency;
    private BigDecimal balance;
    private List<Ref> transactions;
}
