package com.fan.transfer.services.tm.worker;

import com.fan.transfer.domain.*;
import com.fan.transfer.pereferial.db.Repository;
import com.fan.transfer.services.tm.worker.model.CommandI;
import com.fan.transfer.services.tm.worker.model.FailureInitReply;
import com.fan.transfer.services.tm.worker.model.ModifyCommand;
import com.fan.transfer.services.tm.worker.model.SuccessInitReply;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Slf4j
public class PostInitProcessor implements Processor<SuccessInitReply> {
    private final Repository<Transaction.Id, Transaction> transactionRepository;

    public PostInitProcessor (Repository<Transaction.Id, Transaction> transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * A -- credits --> B (take amount from A, put on Hold and give to B on Hold)
     * A -- debits --> B (take amount from B, put on Hold and give to A on Hold)
     * Process Debit/Credit command.
     * 1. Create a Hold for transactionId
     * 2.1. Decrease A Balance by amount on Hold for Credit
     * 2.2. Leave A Balance unchanged for Debit
     * All errors processing skiped for simplicity
     * @param command Transfer command to execute in a processor, contains all parameters
     * @return ReplyI object
     */
    @Override
    public CommandI process (SuccessInitReply command) {
        Transaction.Id parentTransactionId = command.getParentTransactionId();
        Transaction parent = transactionRepository.get(parentTransactionId);
        if(parent != null) {
            Transaction.Id childTransactionId = command.getTransactionId();
            var newChildren = parent.getChildren();
            newChildren.add(childTransactionId);
            var newParentTransaction = Transaction.builder().children(newChildren).build();
            if(transactionRepository.update(parentTransactionId, newParentTransaction)) {
                if(newChildren.size() == 2) {
                    log.debug(String.format("Transaction '%s' finished", parentTransactionId));
                    return SuccessInitReply.builder().transactionId(parentTransactionId).build();
                } else {
                    var error = String.format("Waiting to all participant to finish. Wait for: '%d'", 2 - newChildren.size());
                    log.debug(error);
                    return FailureInitReply.builder()
                                           .parentTransactionId(command.getParentTransactionId())
                                           .transactionId(command.getTransactionId())
                                           .message(error)
                                           .build();
                }
            } else {
                var error = String.format("Parent Transaction '%s' not updated", parentTransactionId);
                log.error(error);
                return FailureInitReply.builder()
                                       .parentTransactionId(command.getParentTransactionId())
                                       .transactionId(command.getTransactionId())
                                       .message(error)
                                       .build();
            }
        }

        var error = String.format("Parent Transaction '%s' not found", parentTransactionId);
        log.error(error);
        return FailureInitReply.builder()
                               .parentTransactionId(command.getParentTransactionId())
                               .transactionId(command.getTransactionId())
                               .message(error)
                               .build();
    }
}
