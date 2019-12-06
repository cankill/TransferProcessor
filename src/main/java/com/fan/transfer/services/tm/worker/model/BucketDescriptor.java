package com.fan.transfer.services.tm.worker.model;

import lombok.Builder;
import lombok.Value;

import java.util.concurrent.ConcurrentLinkedDeque;

@Value
@Builder
public class BucketDescriptor {
    private int bucket;
    private String name;
    private ConcurrentLinkedDeque<CommandI> commandsQueue = new ConcurrentLinkedDeque<>();
    private ConcurrentLinkedDeque<CommandI> repliesQueue = new ConcurrentLinkedDeque<>();

    public boolean queuesAreEmpty() {
        return commandsQueue.isEmpty() && repliesQueue.isEmpty();
    }
}
