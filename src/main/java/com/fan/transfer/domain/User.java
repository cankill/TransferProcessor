package com.fan.transfer.domain;

import lombok.Builder;
import lombok.Value;

/**
 * Domain model for business implementation.
 * A User to store identities.
 * Not implemented in this solution: name, email and phone
 * can be alternative identity for transfer procedure (transfer by phone etc.)
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
