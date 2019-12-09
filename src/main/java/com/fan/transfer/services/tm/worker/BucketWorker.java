package com.fan.transfer.services.tm.worker;

import com.fan.transfer.services.tm.worker.model.BucketDescriptor;
import com.fan.transfer.services.tm.worker.model.CommandInterface;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class BucketWorker implements Runnable {
    @Getter
    private final BucketDescriptor descriptor;

    public BucketWorker (BucketDescriptor descriptor) {
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
        boolean hadReplies = false;
        while (!descriptor.getCommandsQueue().isEmpty() && batchSize > 0) {
            CommandInterface command = descriptor.getCommandsQueue().pollFirst();
            if(command != null) {
                var reply = command.execute();
                descriptor.getTcDescriptor().getRepliesQueue().addLast(reply);
                hadReplies = true;
            }
            batchSize --;
        }

        if(hadReplies) {
            synchronized (descriptor.getTcDescriptor()) {
                descriptor.getTcDescriptor().notifyAll();
            }
        }
    }

    private void sleepWhileEmptyQueues () throws InterruptedException {
        synchronized (descriptor) {
            while (descriptor.queuesAreEmpty()) {
                log.debug("Worker process's '{}' queues are empty, go to wait state", descriptor.getName());
                descriptor.wait(5L * 60L * 1000L);
            }
        }
    }
}
