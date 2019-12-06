package com.fan.transfer.domain;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Value
    @Builder
    public static class Id implements IsId {
        private String value;

        public static Id valueOf (String value) {
            return new Id(value);
        }
    }
}
