package com.fan.transfer.services.tm.worker.model;

import com.fan.transfer.services.tm.command.CommandInterface;
import com.fan.transfer.services.tm.command.CommandReply;
import com.fan.transfer.services.tm.coordinator.model.CoordinatorDescriptor;
import lombok.Builder;
import lombok.Value;

import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Model to aggregate common resources for Worker thread.
 * It contains a descriptor for parent Coordinator thread.
 * The name of the Thread, to do a personalized log print.
 * Inbound Queue for inter-process communication.
 * Outbound Queue for inter-process communication.
 */
@Value
@Builder
public class BucketDescriptor {
    private String name;
    private CoordinatorDescriptor tcDescriptor;
    private ConcurrentLinkedDeque<CommandInterface> commandsQueue = new ConcurrentLinkedDeque<>();
    private ConcurrentLinkedDeque<CommandReply> repliesQueue = new ConcurrentLinkedDeque<>();

    public boolean queuesAreEmpty() {
        return commandsQueue.isEmpty() && repliesQueue.isEmpty();
    }
}
