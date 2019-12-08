package com.fan.transfer.services.tm.worker.model;

import com.fan.transfer.domain.Transaction;
import com.fan.transfer.services.tm.worker.processor.Processor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;

@Value
@NonFinal
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class RollbackCommand extends Command implements HasParentId {
    private Transaction.Id parentTransactionId;
    protected Processor<RollbackCommand> processor;

    @Override
    public CommandReply execute() {
        return getProcessor().process(this);
    }

    public RollbackCommand copy(int retry) {
        return RollbackCommand.builder()
                              .transactionId(getTransactionId())
                              .parentTransactionId(parentTransactionId)
                              .processor(processor)
                              .retry(retry)
                              .build();
    }
}
