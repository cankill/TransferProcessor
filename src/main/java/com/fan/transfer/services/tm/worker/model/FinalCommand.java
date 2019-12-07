package com.fan.transfer.services.tm.worker.model;

import com.fan.transfer.domain.Account;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;

@Value
@NonFinal
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public abstract class FinalCommand extends Command {
    private Account.Id from;
}
