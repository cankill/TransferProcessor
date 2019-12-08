package com.fan.transfer.services.tm.worker.model;

import com.fan.transfer.services.tm.worker.processor.Processor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class DebitCommand extends TransferCommandI {
    private Processor<DebitCommand> processor;

    @Override
    public CommandReply execute() {
        return getProcessor().process(this);
    }
}
