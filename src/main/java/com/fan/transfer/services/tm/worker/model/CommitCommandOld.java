package com.fan.transfer.services.tm.worker.model;

import com.fan.transfer.services.tm.worker.processor.Processor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class CommitCommandOld extends FinalCommand {
    private Processor<FinalCommand> processor;

    @Override
    public CommandReply execute() {
        return processor.process(this);
    }
}
