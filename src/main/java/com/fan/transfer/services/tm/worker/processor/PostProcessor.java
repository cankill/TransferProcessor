package com.fan.transfer.services.tm.worker.processor;

import com.fan.transfer.domain.Transaction;
import com.fan.transfer.domain.TransactionStatus;
import com.fan.transfer.pereferial.db.Repository;
import com.fan.transfer.services.tm.worker.model.CommandReply;
import com.fan.transfer.services.tm.worker.model.SuccessCommand;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.fan.transfer.domain.Transaction.byParent;
import static com.fan.transfer.domain.Transaction.byStatus;

@Slf4j
public class PostProcessor<T extends SuccessCommand> implements Processor<T> {
    private final Repository<Transaction.Id, Transaction> transactionRepository;
    private final ProcessorFactory processorFactory;

    public PostProcessor (Repository<Transaction.Id, Transaction> transactionRepository,
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
     *
     * @param command Succes Init Reply command to execute in a processor, contains all parameters
     * @return Next CommandI object
     */
    @Override
    public CommandReply process (T command) {
        int retry = command.getRetry();
        Transaction.Id parentTransactionId = command.getParentTransactionId();

        var subTransactions = transactionRepository.getAllBy(byParent(parentTransactionId)
                                                             .and(byStatus(TransactionStatus.DONE)));

        if (subTransactions.size() == 2) {
            var patchStatus = Transaction.builder().status(TransactionStatus.DONE).build();
            if (!transactionRepository.update(parentTransactionId, patchStatus)) {
                return retry(command, retry);
            }
            log.debug("Transaction '{}' finished", parentTransactionId);
        } else {
            log.debug("Waiting to all participant to finish. Wait for: '{}'", 2 - subTransactions.size());
        }

        return CommandReply.builder().next(List.of()).status(CommandReply.Status.SUCCESS).build();
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
}
