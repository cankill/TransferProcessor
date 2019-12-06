package com.fan.transfer.services.tm.worker.model;

@FunctionalInterface
public interface CommandI {
    ReplyI execute();
}
