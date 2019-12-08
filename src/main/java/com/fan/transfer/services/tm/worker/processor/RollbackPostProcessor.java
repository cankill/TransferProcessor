package com.fan.transfer.services.tm.worker.processor;

import com.fan.transfer.domain.Transaction;
import com.fan.transfer.pereferial.db.Repository;
import com.fan.transfer.services.tm.worker.model.SuccessRollbackCommand;

public class RollbackPostProcessor extends PostProcessor<SuccessRollbackCommand> {
    public RollbackPostProcessor (Repository<Transaction.Id, Transaction> transactionRepository, ProcessorFactory processorFactory) {
        super(transactionRepository, processorFactory);
    }
}
