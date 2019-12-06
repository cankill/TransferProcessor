package com.fan.transfer.services.tm.worker;

import com.fan.transfer.services.tm.worker.model.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class AccountBucketWorker implements Runnable {
    @Getter
    private final BucketDescriptor descriptor;

    public AccountBucketWorker (BucketDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public void run () {
        log.info("Worker process '{}' started", descriptor.getName());
        try {
            while (!Thread.interrupted()) {
                processCommandsBatch(50);

                sleepWhileEmptyQueues();
            }
        } catch (InterruptedException ignore) {
            log.info("Worker process '{}' was interrupted", descriptor.getName());
        }
    }

    private void processCommandsBatch (int batchSize) throws InterruptedException {
        while (!descriptor.getCommandsQueue().isEmpty() && batchSize > 0) {
            CommandI command = descriptor.getCommandsQueue().pollFirst();
            var reply = command.execute();
            descriptor.getRepliesQueue().addLast(reply);
            batchSize --;
        }
    }

    private void sleepWhileEmptyQueues () throws InterruptedException {
        synchronized (descriptor) {
            while (descriptor.queuesAreEmpty()) {
                log.debug("Worker process's '{}' queues are empty, go to wait state", descriptor.getName());
                descriptor.wait(5*60*1000);
            }
        }
    }
}
