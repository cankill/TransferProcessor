package com.fan.transfer.api.model;

import lombok.Builder;
import lombok.Value;

/**
 * Representation model for API.
 * Base request to create an User with desired name, email and phone.
 * Not implemented: email and phone could be used as alternative identity to User (transfer by phone number)
 */
@Value
@Builder
public class CreateUserRequest {
    private String name;
    private String email;
    private String phone;
}
