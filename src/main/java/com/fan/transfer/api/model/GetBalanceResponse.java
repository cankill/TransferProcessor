package com.fan.transfer.api.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GetBalanceResponse {
    private String accountId;
    private String balance;
}
