package com.fan.transfer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.function.Predicate;

/**
 * Domain model for business implementation.
 * A Hold class to store related Transaction referenca as amount of transfer and current status of a Hold.
 */

@Value
@Builder
@AllArgsConstructor
public class Hold {
    private Transaction.Id transactionId;
    private BigDecimal amount;
    private TransactionStatus status;

    public static Predicate<Hold> byId (Transaction.Id id) {
        return hold -> hold.getTransactionId().equals(id);
    }

    public static Predicate<Hold> byStatus (TransactionStatus status) {
        return hold -> hold.getStatus().equals(status);
    }
}
