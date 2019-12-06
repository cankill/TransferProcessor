package com.fan.transfer.services.tm.worker.model;

import com.fan.transfer.services.tm.worker.Processor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class CreditCommand extends ModifyCommand {
    private Processor<ModifyCommand> processor;

    @Override
    public CommandI execute() {
        return processor.process(this);
    }
}
