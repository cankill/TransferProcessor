package com.fan.transfer.api.model;

import lombok.Builder;
import lombok.Value;

/**
 * Representation model for API.
 * Response for Balance request for User for Account
 * Contains na account identity, currency and a balance
 */
@Value
@Builder
public class GetBalanceResponse {
    private String accountId;
    private String balance;
    private String currency;
}
