package com.fan.transfer.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ErrorResponse {
    private String error;
}
