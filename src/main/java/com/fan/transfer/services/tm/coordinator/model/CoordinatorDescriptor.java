package com.fan.transfer.services.tm.coordinator.model;

import com.fan.transfer.services.tm.worker.model.CommandInterface;
import com.fan.transfer.services.tm.worker.model.CommandReply;
import lombok.Builder;
import lombok.Value;

import java.util.concurrent.ConcurrentLinkedDeque;

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
