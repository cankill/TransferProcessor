package com.fan.transfer.api.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CreateUserRequest {
    private String name;
    private String email;
    private String phone;
}
