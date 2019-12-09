package com.fan.transfer.services.tm.worker.processor;

import com.fan.transfer.domain.Transaction;
import com.fan.transfer.domain.TransactionStatus;
import com.fan.transfer.domain.TransactionType;
import com.fan.transfer.pereferial.db.Repository;
import com.fan.transfer.services.tm.worker.model.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Slf4j
public class TransferProcessor implements Processor<TransferCommand> {
    private final Repository<Transaction.Id, Transaction> transactionRepository;
    private final ProcessorFactory processorFactory;

    public TransferProcessor (final Repository<Transaction.Id, Transaction> transactionRepository,
                              ProcessorFactory processorFactory) {
        this.transactionRepository = transactionRepository;
        this.processorFactory = processorFactory;
    }

    /**
     * Process A->B Transfer command.
     * 1. Create parent transaction for transfer
     * 2. Create Credit command for A
     * 3. Create Debit command for B
     * 4. Return commands to proceed
     * @param command Transfer command to execute in a processor, contains all parameters
     * @return ReplyI object
     */
    @Override
    public CommandReply process (TransferCommand command) {
        log.debug("Processing command '{}'", command);
        Transaction.Id transactionId = generateId();
        var transaction = Transaction.builder()
                                     .id(transactionId)
                                     .from(command.getFrom())
                                     .to(command.getTo())
                                     .amount(command.getAmount())
                                     .type(TransactionType.TM)
                                     .dateTime(LocalDateTime.now(ZoneOffset.UTC))
                                     .status(TransactionStatus.PROGRESS)
                                     .build();
        
        if (!transactionRepository.add(transaction)) {
            var error = String.format("Could not store a transaction '%s'", transaction);
            log.error(error);
            return CommandReply.builder().status(CommandReply.Status.FAILURE).build();
        }

        return CommandReply.builder().next(List.of(composeCredit(command, transactionId),
                                                   composeDebit(command, transactionId)))
                                     .status(CommandReply.Status.SUCCESS)
                                     .build();
    }

    private Command composeCredit (TransferCommand command, Transaction.Id transactionId) {
        return CreditCommand.builder()
                            .processor(processorFactory.get(CreditCommand.class))
                            .from(command.getFrom())
                            .to(command.getTo())
                            .amount(command.getAmount())
                            .transactionId(transactionId)
                            .build();
    }

    private Command composeDebit (TransferCommand command, Transaction.Id transactionId) {
        return DebitCommand.builder()
                           .processor(processorFactory.get(DebitCommand.class))
                           .from(command.getTo())
                           .to(command.getFrom())
                           .amount(command.getAmount())
                           .transactionId(transactionId)
                           .build();
    }

    private static Transaction.Id generateId () {
        return Transaction.Id.valueOf(UUID.randomUUID().toString());
    }
}
