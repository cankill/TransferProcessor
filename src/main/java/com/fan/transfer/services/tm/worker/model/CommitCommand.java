package com.fan.transfer.services.tm.worker.model;

import com.fan.transfer.domain.Account;
import com.fan.transfer.domain.Transaction;
import com.fan.transfer.services.tm.worker.processor.Processor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;

@Value
@NonFinal
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class CommitCommand extends Command {
    private Transaction.Id parentTransactionId;
    private Processor<CommitCommand> processor;

    @Override
    public CommandReply execute() {
        return getProcessor().process(this);
    }
}
