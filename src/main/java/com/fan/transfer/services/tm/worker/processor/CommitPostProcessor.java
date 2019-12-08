package com.fan.transfer.services.tm.worker.processor;

import com.fan.transfer.domain.Transaction;
import com.fan.transfer.pereferial.db.Repository;
import com.fan.transfer.services.tm.worker.model.SuccessCommitCommand;

public class CommitPostProcessor extends PostProcessor<SuccessCommitCommand> {
    public CommitPostProcessor (Repository<Transaction.Id, Transaction> transactionRepository, ProcessorFactory processorFactory) {
        super(transactionRepository, processorFactory);
    }
}
