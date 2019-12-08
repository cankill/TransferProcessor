package com.fan.transfer.services.tm.worker.model;

import com.fan.transfer.services.tm.worker.processor.Processor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class SuccessCommitCommand extends SuccessCommand {
    private Processor<SuccessCommitCommand> processor;

    @Override
    public CommandReply execute() {
        return getProcessor().process(this);
    }

    @Override
    public SuccessCommitCommand copy(int retry) {
        return SuccessCommitCommand.builder()
                                     .transactionId(getTransactionId())
                                     .parentTransactionId(getParentTransactionId())
                                     .processor(processor)
                                     .retry(retry)
                                     .build();
    }
}
