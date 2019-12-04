package com.fan.transfer.api.model;

import com.fan.transfer.domain.Account;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class TransferRequest {
    private final Account.Id from;
    private final Account.Id to;
    private final BigDecimal amount;
}
