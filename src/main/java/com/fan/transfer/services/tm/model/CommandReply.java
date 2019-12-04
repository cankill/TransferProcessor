package com.fan.transfer.services.tm.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommandReply {
    private final Command.Id id;
    private final CommandReplyType type;
    private final String message;
}
