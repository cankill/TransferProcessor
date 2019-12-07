package com.fan.transfer.services.tm.worker.processor;

import com.fan.transfer.domain.Account;
import com.fan.transfer.domain.Transaction;
import com.fan.transfer.pereferial.db.Repository;
import com.fan.transfer.services.tm.worker.model.*;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import java.util.Map;

public class ProcessorFactory {
    @Inject
    @Named("transactionRepository")
    Repository<Transaction.Id, Transaction> transactionRepository;

    @Inject
    @Named("accountRepository")
    Repository<Account.Id, Account> accountRepository;
    
    private Map<Class<? extends CommandInterface>, Processor<? extends CommandInterface>> resolver = Map.of(
            TransferCommand.class, new TransferProcessor(transactionRepository, this),
            CreditCommand.class, new CreditProcessor(transactionRepository, accountRepository, this),
            DebitCommand.class, new DebitProcessor(transactionRepository, accountRepository, this),
            CommitCommand.class, new CommitProcessor(transactionRepository, this),
            RollbackCommand.class, new RollbackProcessor(transactionRepository, accountRepository, this),
            CommitCreditCommand.class, new CommitCreditProcessor(transactionRepository, accountRepository, this),
            CommitDebitCommand.class, new CommitDebitProcessor(transactionRepository, accountRepository, this),
            RollbackCreditCommand.class, new RollbackCreditProcessor(transactionRepository, accountRepository, this),
            RollbackDebitCommand.class, new RollbackDebitProcessor(transactionRepository, accountRepository, this),
            CommitSuccessCommand.class, new PostCommitProcessor(transactionRepository, this),
            RollbackSuccessCommand.class, new PostRollbackProcessor(transactionRepository,this)
    );

    public <T extends CommandInterface> Processor<T>  get(Class<T> clazz) {
        return (Processor<T>) resolver.get(clazz);
    }

}
