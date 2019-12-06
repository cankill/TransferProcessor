package com.fan.transfer.services.tm.worker;

import com.fan.transfer.domain.*;
import com.fan.transfer.pereferial.db.Repository;
import com.fan.transfer.services.tm.worker.model.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Slf4j
public class TransferProcessor implements Processor<TransferCommand> {
    private final Repository<Transaction.Id, Transaction> transactionRepository;

    public TransferProcessor (final Repository<Transaction.Id, Transaction> transactionRepository,
                              final Repository<Account.Id, Account> accountRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Process A->B Transfer command.
     * 1. Create parent transaction for transfer
     * 2. Create Credit command for A
     * 3. Create Debit command for B
     * 4. Send commands to it's threads to process
     * @param command Transfer command to execute in a processor, contains all parameters
     * @return ReplyI object
     */
    @Override
    public ReplyI process (TransferCommand command) {
        log.debug("Processing Transfer command '{}'", command);
        Transaction.Id transactionId = generateId();
        var transaction = Transaction.builder()
                                     .id(transactionId)
                                     .from(command.getFrom())
                                     .to(command.getTo())
                                     .amount(command.getAmount())
                                     .type(TransactionType.TM)
                                     .dateTime(LocalDateTime.now(ZoneOffset.UTC))
                                     .status(TransactionStatus.IN_PROGRESS)
                                     .build();
        
        if (!transactionRepository.add(transaction)) {
            var error = String.format("Could not store a transaction '%s'", transaction);
            log.error(error);
            return FailureReply.builder().message(error).build();
        }

        return SuccessReply.builder().transactionId(transactionId).build();
    }

    private static Transaction.Id generateId () {
        return Transaction.Id.valueOf(UUID.randomUUID().toString());
    }
}
