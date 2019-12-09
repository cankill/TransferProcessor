package com.fan.transfer.api.handlers;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        log.error("Thread throws", e);
    }
}