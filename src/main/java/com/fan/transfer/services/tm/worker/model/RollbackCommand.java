package com.fan.transfer.services.tm.worker.model;

import com.fan.transfer.domain.Account;
import com.fan.transfer.services.tm.worker.Processor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.SuperBuilder;

@Value
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class RollbackCommand extends FinalCommand {
    private Processor<RollbackCommand> processor;

    @Override
    public ReplyI execute() {
        return processor.process(this);
    }
}
