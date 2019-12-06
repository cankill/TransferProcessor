package com.fan.transfer.services.tm.worker.model;

import com.fan.transfer.services.tm.worker.Processor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class CreditCommand extends TransferCommand {
    private Processor<CreditCommand> processor;

    @Override
    public ReplyI execute() {
        return processor.process(this);
    }
}
