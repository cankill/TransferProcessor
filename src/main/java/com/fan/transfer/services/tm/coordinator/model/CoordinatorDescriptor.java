package com.fan.transfer.services.tm.coordinator.model;

import com.fan.transfer.services.tm.command.CommandInterface;
import com.fan.transfer.services.tm.command.CommandReply;
import lombok.Builder;
import lombok.Value;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Model to aggregate common resources for Coordinator thread.
 * It contains a bucketCount to spawn configured amount of Workers.
 * The name of the Thread, to do a personalized log print.
 * Inbound Queue for inter-process communication.
 * Outbound Queue for inter-process communication.
 */
@Value
@Builder
public class CoordinatorDescriptor {
    private int bucketCount;
    private String name;
    private ConcurrentLinkedDeque<CommandInterface> commandsQueue = new ConcurrentLinkedDeque<>();
    private ConcurrentLinkedDeque<CommandReply> repliesQueue = new ConcurrentLinkedDeque<>();

    public boolean queuesAreEmpty() {
        return commandsQueue.isEmpty() && repliesQueue.isEmpty();
    }
}
