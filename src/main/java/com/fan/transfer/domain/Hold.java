package com.fan.transfer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.function.Predicate;

@Value
@Builder
@AllArgsConstructor
public class Hold {
    private Transaction.Id transactionId;
    private BigDecimal amount;

    public static Predicate<Hold> byId (Transaction.Id id) {
        return hold -> hold.getTransactionId().equals(id);
    }
}
