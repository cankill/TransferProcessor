package com.fan.transfer.api.model;

import lombok.Builder;
import lombok.Value;

/**
 * Representation model for API.
 * Response for new User creation, contains an identity of a newly created User
 */
@Value
@Builder
public class CreateUserResponse {
    private String id;
}
