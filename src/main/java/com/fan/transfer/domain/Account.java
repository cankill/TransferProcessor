package com.fan.transfer.domain;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;

@Value
@Builder
public class Account implements HasId<Account.Id> {
    private Id id;
    private User.Id userId;
    private Currency currency;
    private BigDecimal balance;
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    @Builder.Default
    private List<Hold> hold = new LinkedList<>();

    @Value
    @Builder
    public static class Id implements IsId {
        private String value;

        public static Id valueOf (String value) {
            return new Id(value);
        }
    }
}
