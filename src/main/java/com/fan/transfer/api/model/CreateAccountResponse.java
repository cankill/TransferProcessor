package com.fan.transfer.api.model;

import lombok.Builder;
import lombok.Value;

/**
 * Representation model for API.
 * Response for new Account creation, contains an identity of a newly created Account
 */
@Value
@Builder
public class CreateAccountResponse {
    private String id;
}
