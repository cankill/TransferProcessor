package com.fan.transfer.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Hold {
    private final Id id;
    private final Transaction.Id transactionId;
    private final BigDecimal amount;

    @Value
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Id implements IsId {
        private final String value;

        public static Hold.Id valueOf (String value) {
            return Hold.Id.builder().value(value).build();
        }
    }
}
