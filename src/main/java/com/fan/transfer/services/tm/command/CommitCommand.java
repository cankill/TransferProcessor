package com.fan.transfer.services.tm.command;

import com.fan.transfer.domain.Transaction;
import com.fan.transfer.services.tm.processor.Processor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;

/**
 * Command to Commit a Transaction.
 * Sending from Credit/Debit processor 
 */
@Value
@NonFinal
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class CommitCommand extends Command implements HasParentId {
    private Transaction.Id parentTransactionId;
    private Processor<CommitCommand> processor;

    @Override
    public CommandReply execute() {
        return getProcessor().process(this);
    }
}
