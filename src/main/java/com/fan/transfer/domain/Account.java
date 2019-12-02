package com.fan.transfer.domain;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Account implements HasId {
    private final String id;
    private final Currency currency;
    private final BigDecimal balance;
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private final List<Ref> transactions;
}
