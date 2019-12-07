package com.fan.transfer.services.tm.worker.processor;

import com.fan.transfer.domain.*;
import com.fan.transfer.pereferial.db.Repository;
import com.fan.transfer.services.tm.worker.model.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CommitProcessor implements Processor<CommitCommand> {
    private final Repository<Transaction.Id, Transaction> transactionRepository;
    private final ProcessorFactory processorFactory;

    public CommitProcessor (Repository<Transaction.Id, Transaction> transactionRepository,
                            ProcessorFactory processorFactory) {
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
    public CommandReply process (CommitCommand command) {
        Transaction.Id parentTransactionId = command.getParentTransactionId();
        Transaction parent = transactionRepository.get(parentTransactionId);
        if(parent != null) {
            Transaction.Id childTransactionId = command.getTransactionId();
            var newChildren = parent.getChildren();
            newChildren.add(childTransactionId);
            var newParentTransaction = Transaction.builder().children(newChildren).build();
            if(transactionRepository.update(parentTransactionId, newParentTransaction)) {
                if(newChildren.size() == 2) {
                    log.debug("Transaction '{}' could be committed", parentTransactionId);
                    var subTransactions = transactionRepository.getAll(newChildren);
                    if(subTransactions.size() ==2) {
                        var commands = subTransactions.stream().map(subTransaction ->
                            subTransaction.getType() == TransactionType.CREDIT
                            ? composeCommitCredit(subTransaction, parentTransactionId)
                            : composeCommitDebit(subTransaction, parentTransactionId)).collect(Collectors.toList());
                        return CommandReply.builder()
                                .next(commands)
                                .status(CommandReply.Status.SUCCESS)
                                .build();
                    }
                } else {
                    log.debug("Waiting to all participant to finish. Wait for: '{}'", 2 - newChildren.size());
                    return CommandReply.builder().status(CommandReply.Status.SUCCESS).build();
                }
            }
        }

        log.error("Parent Transaction '{}' state change failed. Rollback.", parentTransactionId);
        return CommandReply.builder()
                .next(List.of(composerRollback(parentTransactionId)))
                .status(CommandReply.Status.FAILURE)
                .build();
    }

    private CommandInterface composeCommitCredit (Transaction creditSubTransaction, Transaction.Id parentTransactionId) {
        return CommitCreditCommand.builder()
                                  .processor(processorFactory.get(CommitCreditCommand.class))
                                  .transactionId(creditSubTransaction.getId())
                                  .parentTransactionId(parentTransactionId)
                                  .build();
    }

    private CommandInterface composeCommitDebit (Transaction debitSubTransaction, Transaction.Id parentTransactionId) {
        return DebitCreditCommand.builder()
                                 .processor(processorFactory.get(DebitCreditCommand.class))
                                 .transactionId(debitSubTransaction.getId())
                                 .parentTransactionId(parentTransactionId)
                                 .build();
    }

    private RollbackCommand composerRollback (Transaction.Id parentTransactionId) {
        return RollbackCommand.builder()
                              .processor(processorFactory.get(RollbackCommand.class))
                              .parentTransactionId(parentTransactionId)
                              .build();
    }
}
