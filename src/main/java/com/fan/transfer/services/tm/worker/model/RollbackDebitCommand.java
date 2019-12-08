package com.fan.transfer.services.tm.worker.model;

import com.fan.transfer.domain.Account;
import com.fan.transfer.services.tm.worker.processor.Processor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class RollbackDebitCommand extends RollbackCommandI implements HasFrom {
    private Account.Id from;
    protected Processor<RollbackDebitCommand> processor;

    @Override
    public CommandReply execute() {
        return getProcessor().process(this);
    }

    @Override
    public RollbackDebitCommand copy(int retry) {
        return RollbackDebitCommand.builder()
                                   .transactionId(getTransactionId())
                                   .parentTransactionId(getParentTransactionId())
                                   .processor(processor)
                                   .retry(retry)
                                   .build();
    }
}
