package com.fan.transfer.services.tm.worker;

import com.fan.transfer.domain.*;
import com.fan.transfer.pereferial.db.Repository;
import com.fan.transfer.services.tm.worker.model.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Slf4j
public abstract class InitProcessor implements Processor<ModifyCommand> {
    private final Repository<Transaction.Id, Transaction> transactionRepository;
    private final Repository<Account.Id, Account> accountRepository;
    private final PostInitProcessor postInitProcessor;

    public InitProcessor (Repository<Transaction.Id, Transaction> transactionRepository,
                          Repository<Account.Id, Account> accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.postInitProcessor = new PostInitProcessor(transactionRepository);
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
    public CommandI process (ModifyCommand command) {
        var account = accountRepository.get(command.getFrom());
        if (account != null) {
            var currentBalance = account.getBalance();
            // If balance is enough to debit can proceed
            if (balanceIsEnough(currentBalance, command.getAmount())) {
                // Store transaction in DB, start process of preCommit
                var transaction = Transaction.builder()
                                                .id(generateId())
                                                .from(command.getFrom())
                                                .to(command.getTo())
                                                .amount(command.getAmount())
                                                .type(hintTransactionType())
                                                .dateTime(LocalDateTime.now(ZoneOffset.UTC))
                                                .status(TransactionStatus.IN_PROGRESS)
                                             .build();
                if (transactionRepository.add(transaction)) {
                    // Move amount on hold
                    var newHold = account.getHold();
                    newHold.add(new Hold(transaction.getId(), transaction.getAmount()));
                    // And Subtract from current balance
                    var newBalance = mapBalanceChange(currentBalance, transaction.getAmount());
                    var patch = Account.builder()
                                       .balance(newBalance)
                                       .hold(newHold)
                                       .build();
                    // Store account changes in DB
                    if (accountRepository.update(account.getId(), patch)) {
                        return SuccessInitReply.builder()
                                               .transactionId(transaction.getId())
                                               .processor(postInitProcessor)
                                               .parentTransactionId(command.getTransactionId())
                                               .build();
                    } else {
                        var error = String.format("Account '%s' not updated", transaction.getFrom());
                        log.error(error);
                        return FailureInitReply.builder()
                                               .transactionId(transaction.getId())
                                               .parentTransactionId(command.getTransactionId())
                                               .message(error)
                                               .build();
                    }
                } else {
                    var error = String.format("Could not store a transaction '%s'", transaction);
                    log.error(error);
                    return FailureInitReply.builder()
                                           .transactionId(transaction.getId())
                                           .parentTransactionId(command.getTransactionId())
                                           .message(error)
                                           .build();
                }
            } else {
                var error = String.format("Account '%s' has not enough balance", account);
                log.error(error);
                return FailureInitReply.builder()
                                       .parentTransactionId(command.getTransactionId())
                                       .message(error)
                                       .build();
            }
        }

        var error = String.format("Account '%s' not found", command.getFrom());
        log.error(error);
        return FailureInitReply.builder()
                               .parentTransactionId(command.getTransactionId())
                               .message(error)
                               .build();
    }

    protected abstract boolean balanceIsEnough (BigDecimal currentBalance, BigDecimal amount);
    protected abstract TransactionType hintTransactionType ();
    protected abstract BigDecimal mapBalanceChange (BigDecimal currentBalance, BigDecimal holdAmount);
    
    private static Transaction.Id generateId () {
        return Transaction.Id.valueOf(UUID.randomUUID().toString());
    }
}
