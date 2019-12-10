package com.fan.transfer.services.tm.command;

import com.fan.transfer.services.tm.processor.Processor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class SuccessRollbackCommand extends SuccessCommand {
    private Processor<SuccessRollbackCommand> processor;

    @Override
    public CommandReply execute() {
        return getProcessor().process(this);
    }


    public SuccessRollbackCommand copy(int retry) {
        return SuccessRollbackCommand.builder()
                                     .transactionId(getTransactionId())
                                     .parentTransactionId(getParentTransactionId())
                                     .processor(processor)
                                     .retry(retry)
                                     .build();
    }
}
