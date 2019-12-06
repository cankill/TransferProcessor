package com.fan.transfer.services.tm.coordinator.model;

import com.fan.transfer.services.tm.worker.model.CommandI;
import com.fan.transfer.services.tm.worker.model.ReplyI;
import lombok.Builder;
import lombok.Value;

import java.util.concurrent.ConcurrentLinkedDeque;

@Value
@Builder
public class CoordinatorDescriptor {
    private int bucketCount;
    private String name;
    private ConcurrentLinkedDeque<CommandI> commandsQueue = new ConcurrentLinkedDeque<>();
    private ConcurrentLinkedDeque<ReplyI> repliesQueue = new ConcurrentLinkedDeque<>();

    public boolean queuesAreEmpty() {
        return commandsQueue.isEmpty() && repliesQueue.isEmpty();
    }
}
