package com.fan.transfer.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class User implements HasId<User.Id> {
    private final User.Id id;
    private final String name;
    private final String email;
    private final String phone;

    @Value
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class Id implements IsId {
        private final String value;

        public static Id valueOf (String value) {
            return Id.builder().value(value).build();
        }
    }
}
