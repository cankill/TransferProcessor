package com.fan.transfer.services.tm.command;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import java.util.LinkedList;
import java.util.List;

/**
 * Reply for any command execution
 * Can contains a list of commands to execute after finished execution
 * and a status of previous execution (Rollback is considered as Fail, need to rethink)
 */
@Value
@SuperBuilder
public class CommandReply {
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    @Builder.Default
    private List<CommandInterface> next = new LinkedList<>();
    private CommandReply.Status status;

    public enum Status {SUCCESS, FAILURE}
}
