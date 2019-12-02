package com.fan.transfer.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Transaction implements HasId {
    private String id;
    private String from;
    private String to;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDateTime dateTime;
    private TransactionStatus status;
}
