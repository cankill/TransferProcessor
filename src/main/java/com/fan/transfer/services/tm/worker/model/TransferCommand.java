package com.fan.transfer.services.tm.worker.model;

import com.fan.transfer.domain.Account;
import com.fan.transfer.services.tm.worker.Processor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Value
@NonFinal
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class TransferCommand extends Command {
   private Account.Id from;
   private Account.Id to;
   private BigDecimal amount;
   
   private Processor<TransferCommand> processor;

   @Override
   public ReplyI execute() {
        return processor.process(this);
    }
}
