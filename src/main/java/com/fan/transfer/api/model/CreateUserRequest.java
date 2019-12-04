package com.fan.transfer.api.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateUserRequest {
    private final String name;
    private final String email;
    private final String phone;
}
