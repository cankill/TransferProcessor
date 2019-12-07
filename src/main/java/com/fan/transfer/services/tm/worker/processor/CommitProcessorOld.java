package com.fan.transfer.services.tm.worker.processor;

import com.fan.transfer.domain.Account;
import com.fan.transfer.domain.Hold;
import com.fan.transfer.domain.Transaction;
import com.fan.transfer.domain.TransactionStatus;
import com.fan.transfer.pereferial.db.Repository;
import com.fan.transfer.services.tm.worker.model.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class CommitProcessorOld implements Processor<CommitCommandOld> {
    private final Repository<Transaction.Id, Transaction> transactionRepository;
    private final Repository<Account.Id, Account> accountRepository;

    public CommitProcessorOld (Repository<Transaction.Id, Transaction> transactionRepository,
                               Repository<Account.Id, Account> accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * A -- debits --> B (take amount from B, put on Hold and give to A on Hold)
     * A -- credit --> B (take amount from A, put on Hold and give to B on Hold)
     * Process a commit for Debit/Credit.
     * 1. Find a Hold for transactionId
     * 2.1. Increase A Balance by amount on Hold for Debit
     * 2.2. Leave A Balance unchanged for Credit
     * 3. Remove a Hold
     * @param command Debit command to execute in a processor, contains all parameters
     * @return ReplyI object
     */
    @Override
    public CommandReply process (CommitCommandOld command) {
        // Update transaction in DB, start process of commit
        var transaction = Transaction.builder().status(hintTransactionStatus()).build();
        if (transactionRepository.update(command.getTransactionId(), transaction)) {
            // Search transaction amount on Hold
            var hold = account.getHold().stream().filter(Hold.byId(command.getTransactionId())).findFirst();
            if (hold.isPresent()) {
                // Remove amount from Hold
                var newHold = account.getHold();
                newHold.removeIf(Hold.byId(transaction.getId()));
                // And add to current balance if needed
                BigDecimal newBalance = mapBalanceChange(account.getBalance(), hold.get().getAmount());
                var patch = Account.builder()
                                   .balance(newBalance)
                                   .hold(newHold)
                                   .build();
                // Patch Account
                if (accountRepository.update(account.getId(), patch)) {
                    return SuccessReply.builder()
                                       .transactionId(transaction.getId())
                                       .parentTransactionId(command.getTransactionId())
                                       .build();
                } else {
                    var error = String.format("Account '%s' not updated", transaction.getFrom());
                    log.error(error);
                    return FailureReply.builder()
                                       .transactionId(transaction.getId())
                                       .parentTransactionId(command.getTransactionId())
                                       .message(error)
                                       .build();
                }
            } else {
                var error = String.format("Could not find a hold for transaction '%s' on account '%s'", transaction, account);
                log.error(error);
                return FailureReply.builder()
                                   .transactionId(transaction.getId())
                                   .parentTransactionId(command.getTransactionId())
                                   .message(error)
                                   .build();
            }
        } else {
            var error = String.format("Could not store a transaction '%s'", transaction);
            log.error(error);
            return FailureReply.builder()
                                   .transactionId(transaction.getId())
                                   .parentTransactionId(command.getTransactionId())
                                   .message(error)
                                   .build();
        }
    }

    var error = String.format("Account '%s' not found", command.getFrom());
    log.error(error);
    return FailureReply.builder()
                       .parentTransactionId(command.getTransactionId())
                       .message(error)
                       .build();
    }

    protected TransactionStatus hintTransactionStatus () {
        return TransactionStatus.COMMIT;
    }

    protected BigDecimal mapBalanceChange (BigDecimal currentBalance, BigDecimal holdAmount) {
        return currentBalance;
    }
}
