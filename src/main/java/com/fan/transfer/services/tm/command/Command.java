package com.fan.transfer.services.tm.command;

import com.fan.transfer.domain.Transaction;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;

/**
 * Abstract parent class for all Commands in a system
 * Adds main field to identify a transaction this command is triggered for
 * Adds retry field to express a count of allowed retries for a particular command
 */
@Value
@NonFinal
@SuperBuilder
public abstract class Command implements CommandInterface {
    private Transaction.Id transactionId;
    private int retry;
}
