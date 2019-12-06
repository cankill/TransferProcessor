package com.fan.transfer.services.tm.worker.model;

@FunctionalInterface
public interface CommandI {
    CommandI execute();
}
