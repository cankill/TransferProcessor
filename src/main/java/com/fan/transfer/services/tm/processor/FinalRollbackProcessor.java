package com.fan.transfer.services.tm.processor;

import com.fan.transfer.domain.Account;
import com.fan.transfer.domain.Hold;
import com.fan.transfer.domain.Transaction;
import com.fan.transfer.domain.TransactionStatus;
import com.fan.transfer.pereferial.db.Repository;
import com.fan.transfer.services.tm.command.CommandReply;
import com.fan.transfer.services.tm.command.RollbackCommandI;
import com.fan.transfer.services.tm.command.SuccessRollbackCommand;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

import static com.fan.transfer.domain.TransactionStatus.DONE;

@Slf4j
public abstract class FinalRollbackProcessor<T extends RollbackCommandI> implements Processor<T> {
    private final Repository<Transaction.Id, Transaction> transactionRepository;
    private final Repository<Account.Id, Account> accountRepository;
    private final ProcessorFactoryInterface processorFactory;

    public FinalRollbackProcessor (Repository<Transaction.Id, Transaction> transactionRepository,
                                   Repository<Account.Id, Account> accountRepository,
                                   ProcessorFactoryInterface processorFactory) {
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
        log.debug("Processing command '{}'", command);
        var retry = command.getRetry() -1;
        var transaction = transactionRepository.get(command.getTransactionId());
        if (transaction != null) {
            if (transaction.getStatus() == DONE) {
                return success(transaction.getParentId());
            }

            var account = accountRepository.get(transaction.getFrom());
            if (account != null) {
                // Update transaction in DB, start process of rollback
                var patchRollback = Transaction.builder().status(TransactionStatus.ROLLBACK).build();
                transactionRepository.update(command.getTransactionId(), patchRollback, List.of("children"));
                
                // Search transaction amount on Hold
                var hold = account.getHold().stream()
                                            .filter(Hold.byId(command.getTransactionId())
                                                    .and(Hold.byStatus(DONE)
                                                         .negate()))
                                            .findFirst();
                if (hold.isPresent()) {
                    // Update Hold status
                    var newHold = account.getHold();
                    newHold.removeIf(Hold.byId(transaction.getId()));
                    newHold.add(Hold.builder()
                                    .amount(hold.get().getAmount())
                                    .transactionId(hold.get().getTransactionId())
                                    .status(DONE)
                                    .build());

                    // And add to current balance if needed
                    BigDecimal newBalance = mapBalanceChange(account.getBalance(), hold.get().getAmount());
                    var patch = Account.builder()
                                       .balance(newBalance)
                                       .hold(newHold)
                                       .build();

                    // Patch Account
                    if (! accountRepository.update(account.getId(), patch)) {
                        return retry(command, retry);
                    }
                }

                // Update transaction in DB, start process of rollback
                var patchDone = Transaction.builder().status(TransactionStatus.DONE).build();
                transactionRepository.update(command.getTransactionId(), patchDone, List.of("children"));

                return success(transaction.getParentId());
            }
        }

        return retry(command, retry);
    }

    private CommandReply success (Transaction.Id parentTransactionId) {
        return CommandReply.builder()
                .next(List.of(composeRollbackSuccess(parentTransactionId)))
                .status(CommandReply.Status.SUCCESS)
                .build();
    }

    private SuccessRollbackCommand composeRollbackSuccess (Transaction.Id parentTransactionId) {
        return SuccessRollbackCommand.builder()
                .processor(processorFactory.get(SuccessRollbackCommand.class))
                .parentTransactionId(parentTransactionId)
                .build();
    }

    private CommandReply retry (T command, int retry) {
        if(retry > 0) {
            log.info("Retry Transaction '{}'.", command.getParentTransactionId());
            return CommandReply.builder()
                               .next(List.of(command.copy(retry)))
                               .status(CommandReply.Status.FAILURE)
                               .build();
        } else {
            log.error("No more Retry attempts for Transaction '{}'. Give up.", command.getParentTransactionId());
            return CommandReply.builder()
                               .status(CommandReply.Status.FAILURE)
                               .build();
        }
    }

    protected abstract BigDecimal mapBalanceChange (BigDecimal currentBalance, BigDecimal holdAmount);
}
