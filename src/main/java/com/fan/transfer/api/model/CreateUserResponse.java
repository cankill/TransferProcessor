package com.fan.transfer.api.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateUserResponse {
    private String id;
}
