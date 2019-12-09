package com.fan.transfer.services.tm.worker.model;

import com.fan.transfer.domain.Transaction;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;

@Value
@NonFinal
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public abstract class CommitCommandI extends Command implements HasParentId {
    private Transaction.Id parentTransactionId;
}
