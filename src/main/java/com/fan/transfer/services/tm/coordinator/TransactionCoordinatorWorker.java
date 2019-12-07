package com.fan.transfer.services.tm.coordinator;

import com.fan.transfer.domain.Account;
import com.fan.transfer.domain.Transaction;
import com.fan.transfer.pereferial.db.Repository;
import com.fan.transfer.services.tm.coordinator.model.CoordinatorDescriptor;
import com.fan.transfer.services.tm.worker.*;
import com.fan.transfer.services.tm.worker.model.*;
import com.fan.transfer.services.tm.worker.processor.CommitCreditProcessor;
import com.fan.transfer.services.tm.worker.processor.CommitDebitProcessor;
import com.fan.transfer.services.tm.worker.processor.RollbackCreditProcessor;
import com.fan.transfer.services.tm.worker.processor.RollbackDebitProcessor;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class TransactionCoordinatorWorker implements Runnable {
    private final CoordinatorDescriptor tcDescriptor;
    private Map<Integer, WorkerProcessDescriptor> workers;
    
    private CommitCreditProcessor commitCreditProcessor;
    private CommitDebitProcessor commitDebitProcessor;
    private RollbackCreditProcessor rollbackCreditProcessor;
    private RollbackDebitProcessor rollbackDebitProcessor;


    public TransactionCoordinatorWorker (final CoordinatorDescriptor tcDescriptor,
                                         final Repository<Transaction.Id, Transaction> transactionRepository,
                                         final Repository<Account.Id, Account> accountRepository) {
        this.tcDescriptor = tcDescriptor;

        this.commitCreditProcessor = new CommitCreditProcessor(transactionRepository, accountRepository, this);
        this.commitDebitProcessor = new CommitDebitProcessor(transactionRepository, accountRepository, this);
        this.rollbackCreditProcessor = new RollbackCreditProcessor(transactionRepository, accountRepository, this);
        this.rollbackDebitProcessor = new RollbackDebitProcessor(transactionRepository, accountRepository, this);
    }

    @Override
    public void run () {
        runWorkersPool();
        workers.values().forEach(workerDescription -> workerDescription.getThread().start());
        try {
            while (!Thread.interrupted()) {
                processCommandsBatch(tcDescriptor.getBucketCount());

                processRepliesBatch(tcDescriptor.getBucketCount());

                sleepWhileEmptyQueues();
            }
        } catch (InterruptedException ignore) {
            log.info("Coordinator process '{}' was interrupted", tcDescriptor.getName());
        }
    }

    private void runWorkersPool () {
        workers = IntStream.range(0, tcDescriptor.getBucketCount()).mapToObj(bucketNumber -> {
            String bucketName = String.format("Bucket#%s", bucketNumber);
            log.debug("Init Worker for bucket: {}", bucketName);
            var bucketDescriptor = BucketDescriptor.builder()
                    .name(bucketName)
                    .tcDescriptor(tcDescriptor)
                    .build();
            var worker = new BucketWorker(bucketDescriptor);
            var thread = new Thread(worker, bucketName);
            return new WorkerProcessDescriptor(bucketNumber, bucketDescriptor, thread);
        }).collect(Collectors.toMap(WorkerProcessDescriptor::getBucketId, Function.identity()));
    }

    private void processCommandsBatch (int batchSize) throws InterruptedException {
        while (!tcDescriptor.getCommandsQueue().isEmpty() && batchSize > 0) {
            CommandInterface command = tcDescriptor.getCommandsQueue().pollFirst();
            sendCommandToWorker(command);
            batchSize --;
        }
    }

    private void processRepliesBatch (int batchSize) throws InterruptedException {
        while (!tcDescriptor.getRepliesQueue().isEmpty() && batchSize > 0) {
            CommandReply reply = tcDescriptor.getRepliesQueue().pollFirst();
            reply.getNext().forEach(replyCommand -> tcDescriptor.getCommandsQueue().addLast(replyCommand));
            batchSize --;
        }
    }

    private void sleepWhileEmptyQueues () throws InterruptedException {
        synchronized (tcDescriptor) {
            while (tcDescriptor.queuesAreEmpty()) {
                log.debug("Transaction Coordinator process's '{}' queues are empty, go to wait state", tcDescriptor.getName());
                tcDescriptor.wait(5*60*1000);
            }
        }
    }

    private void sendCommandToWorker (CommandInterface command) {
        var bucket = calculateBucketNumber(command.getFrom(), tcDescriptor.getBucketCount());
        var worker = workers.get(bucket);
        worker.getBucketDescriptor().getCommandsQueue().addLast(command);
        synchronized (worker.getBucketDescriptor()) {
            worker.getBucketDescriptor().notifyAll();
        }
    }

    private int calculateBucketNumber(Object key, int bucketCount) {
        return Math.abs((key.hashCode() & 0x7fffffff) % bucketCount);
    }

    @Value
    @AllArgsConstructor
    public static class WorkerProcessDescriptor {
        private int bucketId;
        private BucketDescriptor bucketDescriptor;
        private Thread thread;
    }
}
