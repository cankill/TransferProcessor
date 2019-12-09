package com.fan.transfer.api.model;

import com.fan.transfer.domain.Account;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

/**
 * Representation model for API.
 * Request to transfer money from Account to other Account
 */
@Value
@Builder
public class TransferRequest {
    private Account.Id from;
    private Account.Id to;
    private BigDecimal amount;
}
