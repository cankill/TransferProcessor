package com.fan.transfer.services.tm.worker.model;

import com.fan.transfer.domain.Account;
import com.fan.transfer.services.tm.worker.processor.Processor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;

@Value
@NonFinal
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class CommitCreditCommand extends CommitCommandI implements HasFrom {
    private Account.Id from;
    private Processor<CommitCreditCommand> processor;

    @Override
    public CommandReply execute() {
        return getProcessor().process(this);
    }
}
