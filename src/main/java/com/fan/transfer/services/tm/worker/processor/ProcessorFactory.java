package com.fan.transfer.services.tm.worker.processor;

import com.fan.transfer.domain.Account;
import com.fan.transfer.domain.Transaction;
import com.fan.transfer.pereferial.db.Repository;
import com.fan.transfer.services.tm.worker.model.*;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.util.HashMap;
import java.util.Map;

public class ProcessorFactory {
    @Inject
    @Named("transactionRepository")
    Repository<Transaction.Id, Transaction> transactionRepository;

    @Inject
    @Named("accountRepository")
    Repository<Account.Id, Account> accountRepository;
    
    private Map<Class<? extends CommandInterface>, Processor<? extends CommandInterface>> resolver = new HashMap<>();

    public ProcessorFactory init() {
        resolver.put(TransferCommand.class, new TransferProcessor(transactionRepository, this));
        resolver.put(CreditCommand.class, new CreditProcessor(transactionRepository, accountRepository, this));
        resolver.put(DebitCommand.class, new DebitProcessor(transactionRepository, accountRepository, this));

        resolver.put(RollbackCommand.class, new RollbackProcessor(transactionRepository, this));
        resolver.put(RollbackCreditCommand.class, new RollbackCreditProcessor(transactionRepository, accountRepository, this));
        resolver.put(RollbackDebitCommand.class, new RollbackDebitProcessor(transactionRepository, accountRepository, this));
        resolver.put(SuccessRollbackCommand.class, new RollbackPostProcessor(transactionRepository,this));

        resolver.put(CommitCommand.class, new CommitProcessor(transactionRepository, this));
        resolver.put(CommitCreditCommand.class, new CommitCreditProcessor(transactionRepository, accountRepository, this));
        resolver.put(CommitDebitCommand.class, new CommitDebitProcessor(transactionRepository, accountRepository, this));
        resolver.put(SuccessCommitCommand.class, new CommitPostProcessor(transactionRepository, this));

        return this;
    }

    public <T extends CommandInterface> Processor<T>  get(Class<T> clazz) {
        return (Processor<T>) resolver.get(clazz);
    }

}
