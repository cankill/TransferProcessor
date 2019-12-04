package com.fan.transfer.domain;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.*;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Account implements HasId<Account.Id> {
    private final Account.Id id;
    private final User.Id userId;
    private final Currency currency;
    private final BigDecimal balance;
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private final List<Hold> hold = new LinkedList<>();

    @Value
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Id implements IsId {
        private final String value;

        public static Id valueOf (String value) {
            return Id.builder().value(value).build();
        }
    }
}
