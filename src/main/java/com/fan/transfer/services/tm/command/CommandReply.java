package com.fan.transfer.services.tm.command;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;

import java.util.LinkedList;
import java.util.List;

@Value
@NonFinal
@SuperBuilder
public class CommandReply {
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    @Builder.Default
    private List<CommandInterface> next = new LinkedList<>();
    private CommandReply.Status status;

    public enum Status {SUCCESS, FAILURE}
}
