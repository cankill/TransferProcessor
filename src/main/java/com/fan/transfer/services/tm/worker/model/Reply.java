package com.fan.transfer.services.tm.worker.model;

import com.fan.transfer.domain.Transaction;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;

@Value
@NonFinal
@SuperBuilder
public abstract class Reply implements CommandInterface {
    private Transaction.Id transactionId;
    private Transaction.Id parentTransactionId;
}
