package com.fan.transfer.services.tm.model;

import com.fan.transfer.domain.IsId;
import com.fan.transfer.domain.Transaction;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Command {
    private final Command.Id id;
    private final CommandType type;
    private final Transaction transaction;


    @Value
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Id implements IsId {
        private final String value;

        public static Command.Id valueOf (String value) {
            return Command.Id.builder().value(value).build();
        }
    }
}
