package com.fan.transfer.services.tm.processor;

import com.fan.transfer.domain.Transaction;
import com.fan.transfer.pereferial.db.Repository;
import com.fan.transfer.services.tm.command.SuccessCommitCommand;

public class CommitPostProcessor extends PostProcessor<SuccessCommitCommand> {
    public CommitPostProcessor (Repository<Transaction.Id, Transaction> transactionRepository,
                                ProcessorFactoryInterface processorFactory) {
        super(transactionRepository, processorFactory);
    }
}
