package com.fan.transfer.services.tm.command;

import com.fan.transfer.domain.Account;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Value
@NonFinal
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public abstract class TransferCommandI extends Command implements HasFrom {
   private Account.Id from;
   private Account.Id to;
   private BigDecimal amount;
}
