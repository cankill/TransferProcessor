package com.fan.transfer.domain;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Transaction implements HasId<Transaction.Id> {
    private Id id;
    private Account.Id from;
    private Account.Id to;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDateTime dateTime;
    private TransactionStatus status;

    @Value
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Id implements IsId {
        private final String value;

        public static Transaction.Id valueOf (String value) {
            return Transaction.Id.builder().value(value).build();
        }
    }
}
