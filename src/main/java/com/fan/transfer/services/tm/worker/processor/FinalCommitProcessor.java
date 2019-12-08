package com.fan.transfer.services.tm.worker.processor;

import com.fan.transfer.domain.Account;
import com.fan.transfer.domain.Hold;
import com.fan.transfer.domain.Transaction;
import com.fan.transfer.domain.TransactionStatus;
import com.fan.transfer.pereferial.db.Repository;
import com.fan.transfer.services.tm.worker.model.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

import static com.fan.transfer.domain.TransactionStatus.DONE;

@Slf4j
public abstract class FinalCommitProcessor<T extends CommitCommandI> implements Processor<T> {
    private final Repository<Transaction.Id, Transaction> transactionRepository;
    private final Repository<Account.Id, Account> accountRepository;
    private final ProcessorFactory processorFactory;

    public FinalCommitProcessor (Repository<Transaction.Id, Transaction> transactionRepository,
                                 Repository<Account.Id, Account> accountRepository,
                                 ProcessorFactory processorFactory) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.processorFactory = processorFactory;
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
    public CommandReply process (T command) {
        var transaction = transactionRepository.get(command.getTransactionId());
        if (transaction != null) {
            if (transaction.getStatus() == DONE) {
                return success(transaction.getParentId());
            }

            var account = accountRepository.get(transaction.getFrom());
            if (account != null) {
                // Update transaction in DB, start process of rollback
                var patchRollback = Transaction.builder().status(TransactionStatus.ROLLBACK).build();
                if(transactionRepository.update(command.getTransactionId(), patchRollback)) {
                    // Search transaction amount on Hold
                    var newHold = account.getHold();
                    var hold = newHold.stream()
                                      .filter(Hold.byId(command.getTransactionId())
                                              .and(Hold.byStatus(DONE)
                                                   .negate()))
                                      .findFirst();

                    BigDecimal newBalance = account.getBalance();

                    if (hold.isPresent()) {
                        // Update Hold status
                        newHold.removeIf(Hold.byId(transaction.getId()));
                        newHold.add(Hold.builder()
                                .amount(hold.get().getAmount())
                                .transactionId(hold.get().getTransactionId())
                                .status(DONE)
                                .build());

                        // And add to current balance if needed
                        var patch = Account.builder()
                                           .balance(newBalance)
                                           .hold(newHold)
                                           .build();

                        // Patch Account
                        if(!accountRepository.update(account.getId(), patch)) {
                            return rollback(command);
                        }
                    }
                    // Update transaction in DB, start process of rollback
                    var patchDone = Transaction.builder().status(TransactionStatus.DONE).build();
                    transactionRepository.update(command.getTransactionId(), patchDone);

                    return success(transaction.getParentId());
                }
            }
        }

        return rollback(command);
    }

    private CommandReply rollback (T command) {
        log.error("Transaction '{}' failed. Rollback", command.getTransactionId());
        return CommandReply.builder()
                           .next(List.of(RollbackCommand.builder()
                                                        .processor(processorFactory.get(RollbackCommand.class))
                                                        .parentTransactionId(command.getParentTransactionId())
                                                        .build()))
                           .status(CommandReply.Status.FAILURE)
                           .build();
    }

    private CommandReply success (Transaction.Id parentTransactionId) {
        return CommandReply.builder()
                           .next(List.of(composeCommitSuccess(parentTransactionId)))
                           .status(CommandReply.Status.SUCCESS)
                           .build();
    }

    private SuccessCommitCommand composeCommitSuccess (Transaction.Id parentTransactionId) {
        return SuccessCommitCommand.builder()
                                   .processor(processorFactory.get(SuccessCommitCommand.class))
                                   .parentTransactionId(parentTransactionId)
                                   .build();
    }

    protected abstract BigDecimal mapBalanceChange (BigDecimal currentBalance, BigDecimal holdAmount);
}
