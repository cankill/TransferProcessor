package com.fan.transfer.services.tm.command;

import com.fan.transfer.domain.Transaction;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;

@Value
@NonFinal
@SuperBuilder
public abstract class Command implements CommandInterface {
    private Transaction.Id transactionId;
    private int retry;
}