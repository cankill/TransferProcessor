package com.fan.transfer.services.tm;

import com.fan.transfer.domain.Account;
import com.fan.transfer.domain.Transaction;
import com.fan.transfer.pereferial.db.Repository;
import com.fan.transfer.services.tm.coordinator.TransactionCoordinatorWorker;
import com.fan.transfer.services.tm.coordinator.model.CoordinatorDescriptor;
import com.fan.transfer.services.tm.worker.AccountBucketWorker;
import com.fan.transfer.services.tm.worker.CreditProcessor;
import com.fan.transfer.services.tm.worker.DebitProcessor;
import com.fan.transfer.services.tm.worker.TransferProcessor;
import com.fan.transfer.services.tm.worker.model.*;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class TransferCommandManagerImpl implements TransferCommandManager {
    private CoordinatorDescriptor tcDescriptor;
    private Thread tcThread;
    private Map<Integer, WorkerProcessDescriptor> workers;

    private CreditProcessor creditProcessor;
    private DebitProcessor debitProcessor;
    private TransferProcessor transferProcessor;

    @Inject
    @Named("transactionRepository")
    Repository<Transaction.Id, Transaction> transactionRepository;

    @Inject
    @Named("accountRepository")
    Repository<Account.Id, Account> accountRepository;

    public void init (int bucketsCount) {
        log.info("Init TM process");

        this.creditProcessor = new CreditProcessor(transactionRepository, accountRepository);
        this.debitProcessor = new DebitProcessor(transactionRepository, accountRepository);
        this.transferProcessor = new TransferProcessor(transactionRepository, accountRepository);
        
        workers = IntStream.range(0, bucketsCount).mapToObj(bucketNumber -> {
            String bucketName = String.format("Bucket#%s", bucketNumber);
            log.debug("Init Worker for bucket: {}", bucketName);
            var workerDescriptor = BucketDescriptor.builder()
                    .bucket(bucketNumber)
                    .name(bucketName)
                    .build();
            var worker = new AccountBucketWorker(workerDescriptor);
            var thread = new Thread(worker, bucketName);
            return new TransferCommandManagerImpl.WorkerProcessDescriptor(bucketNumber, worker, workerDescriptor, thread);
        }).collect(Collectors.toMap(TransferCommandManagerImpl.WorkerProcessDescriptor::getBucketId, Function.identity()));

        tcDescriptor = CoordinatorDescriptor.builder().bucketCount(bucketsCount).name("Transaction coordinator").build();
        tcThread = new Thread(new TransactionCoordinatorWorker(tcDescriptor, workers, transactionRepository, accountRepository));
        tcThread.start();
    }

    @Override
    public Transaction.Id transfer (Account.Id from, Account.Id to, BigDecimal amount) {
        var transferCommand = TransferCommand.builder().from(from).to(to).amount(amount).processor(transferProcessor).build();
        var reply = transferCommand.execute();

        if(reply instanceof SuccessReply) {
            SuccessReply successReply = (SuccessReply) reply;
            sendCredit(transferCommand, successReply.getTransactionId());
            sendDebit(transferCommand, successReply.getTransactionId());
            return successReply.getTransactionId();
        }

        return null;
    }

    private void sendCredit (TransferCommand command, Transaction.Id transactionId) {
        var creditFor = command.getFrom();
        var credit = CreditCommand.builder()
                                  .processor(creditProcessor)
                                  .from(creditFor)
                                  .to(command.getTo())
                                  .amount(command.getAmount())
                                  .transactionId(transactionId)
                                  .build();

        var creditBucket = calculateBucketNumber(creditFor, tcDescriptor.getBucketCount());
        var workerForCredit = workers.get(creditBucket);
        workerForCredit.getWorker().getDescriptor().getCommandsQueue().addLast(credit);
        synchronized (workerForCredit.getWorker().getDescriptor()) {
            workerForCredit.getWorker().getDescriptor().notifyAll();
        }
    }

    private void sendDebit (TransferCommand command, Transaction.Id transactionId) {
        var debitFor = command.getTo();
        var debit = DebitCommand.builder()
                                .processor(debitProcessor)
                                .from(debitFor)
                                .to(command.getFrom())
                                .amount(command.getAmount())
                                .transactionId(transactionId)
                                .build();

        var debitBucket = calculateBucketNumber(debitFor, tcDescriptor.getBucketCount());
        var workerForDebit = workers.get(debitBucket);
        workerForDebit.getWorker().getDescriptor().getCommandsQueue().addLast(debit);
        synchronized (workerForDebit.getWorker().getDescriptor()) {
            workerForDebit.getWorker().getDescriptor().notifyAll();
        }
    }

    private int calculateBucketNumber(Object key, int bucketCount) {
        return Math.abs((key.hashCode() & 0x7fffffff) % bucketCount);
    }

    @Value
    @AllArgsConstructor
    public static final class WorkerProcessDescriptor {
        private final int bucketId;
        private final AccountBucketWorker worker;
        private final BucketDescriptor workerDescriptor;
        private final Thread thread;
    }
}
