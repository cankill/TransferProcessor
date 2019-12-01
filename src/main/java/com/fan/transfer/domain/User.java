package com.fan.transfer.domain;

import lombok.Value;

import java.util.List;
import java.util.UUID;

@Value
public class User {
    private String id;
    private String name;
    private String email;
    private String phone;
    private List<Account> accounts;
}
