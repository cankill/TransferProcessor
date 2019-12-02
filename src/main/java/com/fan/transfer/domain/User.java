package com.fan.transfer.domain;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
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
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private final List<Ref> accounts;
}
