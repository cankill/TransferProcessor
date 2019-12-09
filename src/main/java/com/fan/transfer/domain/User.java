package com.fan.transfer.domain;

import lombok.Builder;
import lombok.Value;

/**
 * Domain model for business implementation.
 * A User to store identities
 */
@Value
@Builder
public class User implements HasId<User.Id> {
    private Id id;
    private String name;
    private String email;
    private String phone;

    @Value
    @Builder
    public static class Id implements IsId {
        private String value;

        public static Id valueOf (String value) {
            return new Id(value);
        }
    }
}
