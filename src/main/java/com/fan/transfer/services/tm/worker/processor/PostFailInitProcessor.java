package com.fan.transfer.services.tm.worker.processor;

import com.fan.transfer.domain.Transaction;
import com.fan.transfer.pereferial.db.Repository;
import com.fan.transfer.services.tm.worker.model.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PostFailInitProcessor implements Processor<RollbackCommand> {
    private final Repository<Transaction.Id, Transaction> transactionRepository;

    public PostFailInitProcessor (Repository<Transaction.Id, Transaction> transactionRepository, ProcessorFactory processorFactory) {
        this.transactionRepository = transactionRepository;
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
        Transaction.Id parentTransactionId = command.getParentTransactionId();
        Transaction parent = transactionRepository.get(parentTransactionId);
        if(parent != null) {
            Transaction.Id childTransactionId = command.getTransactionId();
            var newChildren = parent.getChildren();
            newChildren.add(childTransactionId);
            var newParentTransaction = Transaction.builder().children(newChildren).build();
            if(transactionRepository.update(parentTransactionId, newParentTransaction)) {
                if(newChildren.size() == 2) {
                    log.debug("Transaction '{}' finished", parentTransactionId);
                    return CommitCommandOld.builder().transactionId(parentTransactionId).build();
                } else {
                    log.debug("Waiting to all participant to finish. Wait for: '{}'", 2 - newChildren.size());
                    return WaitingInitReply.builder().parentTransactionId(parentTransactionId).build();
                }
            }
        }

        log.error("Transaction '{}' state change failed", parentTransactionId);
        return FailureInitReply.builder().parentTransactionId(parentTransactionId).build();
    }
}
