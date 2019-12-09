package com.fan.transfer.services.tm.worker.processor;

import com.fan.transfer.domain.*;
import com.fan.transfer.pereferial.db.Repository;
import com.fan.transfer.services.tm.worker.model.CommandReply;
import com.fan.transfer.services.tm.worker.model.CommitCommand;
import com.fan.transfer.services.tm.worker.model.RollbackCommand;
import com.fan.transfer.services.tm.worker.model.TransferCommandI;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

/**
 *    Base class for CreditProcessor and DebitProcessor.
 */
@Slf4j
public abstract class InitProcessor<T extends TransferCommandI> implements Processor<T> {
    private final Repository<Transaction.Id, Transaction> transactionRepository;
    private final Repository<Account.Id, Account> accountRepository;
    private  final ProcessorFactory processorFactory;

    public InitProcessor (Repository<Transaction.Id, Transaction> transactionRepository,
                          Repository<Account.Id, Account> accountRepository,
                          ProcessorFactory processorFactory) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.processorFactory = processorFactory;
    }

    /**
     * A -- credits --> B (take amount from A, put on Hold and give to B on Hold)
     * A -- debits --> B (take amount from B, put on Hold and give to A on Hold)
     * Process Debit/Credit command.
     * 1. Create a Hold for transactionId
     * 2.1. Decrease A Balance by amount on Hold for Credit
     * 2.2. Leave A Balance unchanged for Debit
     * @param command Transfer command to execute in a processor, contains all parameters
     * @return ReplyI object
     */
    @Override
    public CommandReply process (T command) {
        log.debug("Processing command '{}'", command);
        Transaction.Id parentTransactionId = command.getTransactionId();
        Transaction.Id transactionId = null;
        var account = accountRepository.get(command.getFrom());
        if (account != null) {
            var currentBalance = account.getBalance();
            // If balance is enough to debit can proceed
            if (balanceIsEnough(currentBalance, command.getAmount())) {
                transactionId = generateId();
                // Store transaction in DB, start process of preCommit
                var transaction = Transaction.builder()
                                                .id(transactionId)
                                                .parentId(parentTransactionId)
                                                .from(command.getFrom())
                                                .to(command.getTo())
                                                .amount(command.getAmount())
                                                .type(hintTransactionType())
                                                .dateTime(LocalDateTime.now(ZoneOffset.UTC))
                                                .status(TransactionStatus.PROGRESS)
                                             .build();
                if (transactionRepository.add(transaction)) {
                    // Move amount on hold
                    var newHold = account.getHold();
                    newHold.add(new Hold(transactionId, transaction.getAmount(), TransactionStatus.PROGRESS));
                    // And Subtract from current balance
                    var newBalance = mapBalanceChange(currentBalance, transaction.getAmount());
                    var patch = Account.builder()
                                       .balance(newBalance)
                                       .hold(newHold)
                                       .build();
                    // Store account changes in DB
                    if (accountRepository.update(account.getId(), patch)) {
                        return CommandReply.builder()
                                           .next(List.of(CommitCommand.builder()
                                                                      .processor(processorFactory.get(CommitCommand.class))
                                                                      .transactionId(transactionId)
                                                                      .parentTransactionId(parentTransactionId)
                                                                      .build()))
                                           .status(CommandReply.Status.SUCCESS)
                                           .build();
                    }
                }
            }
        }
        
        log.error("Account '{}' not ready for transaction", command.getFrom());
        return CommandReply.builder()
                           .next(List.of(RollbackCommand.builder()
                                                           .processor(processorFactory.get(RollbackCommand.class))
                                                           .transactionId(transactionId)
                                                           .parentTransactionId(parentTransactionId)
                                                           .retry(3)
                                                           .build()))
                           .status(CommandReply.Status.FAILURE)
                           .build();
    }

    protected abstract boolean balanceIsEnough (BigDecimal currentBalance, BigDecimal amount);
    protected abstract TransactionType hintTransactionType ();
    protected abstract BigDecimal mapBalanceChange (BigDecimal currentBalance, BigDecimal holdAmount);
    
    private static Transaction.Id generateId () {
        return Transaction.Id.valueOf(UUID.randomUUID().toString());
    }
}
