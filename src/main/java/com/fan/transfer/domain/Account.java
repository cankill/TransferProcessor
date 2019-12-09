package com.fan.transfer.domain;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.LinkedList;
import java.util.List;

/**
 * Domain model for business implementation.
 * An Account to store balance, currency, link to User owning this Account.
 * Contains a list of Holds (instrument to support transactions)
 */
@Value
@Builder
@AllArgsConstructor
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
    @AllArgsConstructor
    public static class Id implements IsId {
        private String value;

        public static Id valueOf (String value) {
            return new Id(value);
        }
    }
}
