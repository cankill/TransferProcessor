package com.fan.transfer.services.tm.worker.model;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;

@Value
@NonFinal
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class FailureReply extends Reply {
    private String message;
}
