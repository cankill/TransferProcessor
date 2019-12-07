package com.fan.transfer.services.tm.worker.model;

import com.fan.transfer.services.tm.worker.processor.Processor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;

@Value
@NonFinal
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class SuccessReply extends Reply {
    private Processor<SuccessReply> processor;

    @Override
    public CommandInterface execute() {
        return processor.process(this);
    }
}
