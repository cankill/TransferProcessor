package com.fan.transfer.api.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateAccountResponse {
    private String id;
}
