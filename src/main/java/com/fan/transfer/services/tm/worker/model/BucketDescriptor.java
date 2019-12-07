package com.fan.transfer.services.tm.worker.model;

import com.fan.transfer.services.tm.coordinator.model.CoordinatorDescriptor;
import lombok.Builder;
import lombok.Value;

import java.util.concurrent.ConcurrentLinkedDeque;

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
