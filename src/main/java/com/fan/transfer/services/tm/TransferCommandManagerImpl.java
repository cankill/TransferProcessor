package com.fan.transfer.services.tm;

import com.fan.transfer.domain.Account;
import com.fan.transfer.domain.Transaction;
import com.fan.transfer.pereferial.db.Repository;
import com.fan.transfer.services.tm.coordinator.TransactionCoordinatorWorker;
import com.fan.transfer.services.tm.coordinator.model.CoordinatorDescriptor;
import com.fan.transfer.services.tm.worker.model.*;
import com.fan.transfer.services.tm.worker.processor.*;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class TransferCommandManagerImpl implements TransferCommandManager {
    private CoordinatorDescriptor tcDescriptor;
    private Thread tcThread;

    @Inject
    @Named("transactionRepository")
    Repository<Transaction.Id, Transaction> transactionRepository;

    @Inject
    @Named("accountRepository")
    Repository<Account.Id, Account> accountRepository;

    @Inject
    ProcessorFactory processorFactory;

    public void init (int bucketsCount) {
        log.info("Init TM process");

        tcDescriptor = CoordinatorDescriptor.builder().bucketCount(bucketsCount).name("Transaction coordinator").build();
        tcThread = new Thread(new TransactionCoordinatorWorker(tcDescriptor, transactionRepository, accountRepository));
        tcThread.start();
    }

    @Override
    public void transfer (Account.Id from, Account.Id to, BigDecimal amount) {
        var transferCommand = TransferCommand.builder()
                                             .processor(processorFactory.get(TransferCommand.class))
                                             .from(from)
                                             .to(to)
                                             .amount(amount)
                                             .build();
        tcDescriptor.getCommandsQueue().addLast(transferCommand);
        synchronized (tcDescriptor) {
            tcDescriptor.notifyAll();
        }
    }

}
