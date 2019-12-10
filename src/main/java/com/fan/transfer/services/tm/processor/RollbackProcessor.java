package com.fan.transfer.services.tm.processor;

import com.fan.transfer.domain.Transaction;
import com.fan.transfer.domain.TransactionStatus;
import com.fan.transfer.domain.TransactionType;
import com.fan.transfer.pereferial.db.Repository;
import com.fan.transfer.services.tm.command.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import static com.fan.transfer.domain.Transaction.byParent;
import static com.fan.transfer.domain.Transaction.byStatus;
import static java.lang.Thread.sleep;

@Slf4j
public class RollbackProcessor implements Processor<RollbackCommand> {
    private final Repository<Transaction.Id, Transaction> transactionRepository;
    private final ProcessorFactoryInterface processorFactory;

    public RollbackProcessor (Repository<Transaction.Id, Transaction> transactionRepository,
                              ProcessorFactoryInterface processorFactory) {
        this.transactionRepository = transactionRepository;
        this.processorFactory = processorFactory;
    }

    /**
     * Process Success preCommit for subTransaction.
     * 1. Get Parent Transaction
     * 2. Add subTransaction to Transaction
     * 3. If All subTransactions reported
     * 3.1. Allow proceed with sub-Transactions Commit
     * 4. If any failed
     * 4.1. Give directions to proceed with Rollback for sub-Transactions
     * All errors processing skipped for simplicity
     * @param command Succes Init Reply command to execute in a processor, contains all parameters
     * @return Next CommandI object
     */
    @Override
    public CommandReply process (RollbackCommand command) {
        log.debug("Processing command '{}'", command);
        var retry = command.getRetry() -1;
        Transaction.Id parentTransactionId = command.getParentTransactionId();
        Transaction parent = transactionRepository.get(parentTransactionId);
        if(parent != null) {
            if(parent.getStatus() == TransactionStatus.DONE) {
                return goToPostRollback(parentTransactionId);
            }

            var patch = Transaction.builder().status(TransactionStatus.ROLLBACK).build();
            transactionRepository.update(parentTransactionId, patch, List.of("children"));

            var subTransactions = transactionRepository.getAllBy(byParent(parentTransactionId)
                                                                 .and(byStatus(TransactionStatus.DONE)
                                                                      .negate()));

            // All subTransactions are in status Done. Go Done parent transaction.
            if(subTransactions.isEmpty()) {
                return goToPostRollback(parentTransactionId);
            }

            var commands = subTransactions.stream().map(subTransaction ->
                                                        subTransaction.getType() == TransactionType.CREDIT
                                                        ? composeRollbackCredit(subTransaction, parentTransactionId)
                                                        : composeRollbackDebit(subTransaction, parentTransactionId)).collect(Collectors.toList());

            return CommandReply.builder()
                    .next(commands)
                    .status(CommandReply.Status.SUCCESS)
                    .build();
        }

        log.error("Parent Transaction '{}' state change failed.", parentTransactionId);
        return retry(command, retry);
    }

    private CommandReply retry (RollbackCommand command, int retry) {
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

    private CommandReply goToPostRollback (Transaction.Id parentTransactionId) {
        return CommandReply.builder()
                           .next(List.of(composeRollbackSuccess(parentTransactionId)))
                           .status(CommandReply.Status.SUCCESS)
                           .build();
    }

    private CommandInterface composeRollbackCredit (Transaction creditSubTransaction, Transaction.Id parentTransactionId) {
        return RollbackCreditCommand.builder()
                                   .processor(processorFactory.get(RollbackCreditCommand.class))
                                   .from(creditSubTransaction.getFrom())
                                   .transactionId(creditSubTransaction.getId())
                                   .parentTransactionId(parentTransactionId)
                                   .retry(3)
                                   .build();
    }

    private CommandInterface composeRollbackDebit (Transaction debitSubTransaction, Transaction.Id parentTransactionId) {
        return RollbackDebitCommand.builder()
                                  .processor(processorFactory.get(RollbackDebitCommand.class))
                                  .from(debitSubTransaction.getFrom())
                                  .transactionId(debitSubTransaction.getId())
                                  .parentTransactionId(parentTransactionId)
                                  .retry(3)
                                  .build();
    }

    private SuccessRollbackCommand composeRollbackSuccess (Transaction.Id parentTransactionId) {
        return SuccessRollbackCommand.builder()
                                     .processor(processorFactory.get(SuccessRollbackCommand.class))
                                     .parentTransactionId(parentTransactionId)
                                     .build();
    }
}
