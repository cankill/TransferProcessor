package com.fan.transfer.services.tm.worker.model;

import com.fan.transfer.domain.Transaction;
import com.fan.transfer.services.tm.worker.processor.Processor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class RollbackSuccessCommand extends Command {
    private Transaction.Id parentTransactionId;
    private Processor<RollbackSuccessCommand> processor;

    @Override
    public CommandReply execute() {
        return getProcessor().process(this);
    }
}
