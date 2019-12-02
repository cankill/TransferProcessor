package com.fan.transfer.domain;

import lombok.*;
import java.util.List;

@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class User implements HasId {
    private final String id;
    private final String name;
    private final String email;
    private final String phone;
    private final List<Ref> accounts;
}
