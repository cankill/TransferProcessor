package com.fan.transfer.domain;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Value
@Builder
public class Transaction implements HasId<Transaction.Id> {
    private Id id;
    private Account.Id from;
    private Account.Id to;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDateTime dateTime;
    private TransactionStatus status;
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    @Builder.Default
    private List<Transaction.Id> children = new LinkedList<>();

    @Value
    @Builder
    public static class Id implements IsId {
        private String value;

        public static Id valueOf (String value) {
            return new Id(value);
        }
    }
}
