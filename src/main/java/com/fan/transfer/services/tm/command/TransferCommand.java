package com.fan.transfer.services.tm.command;

import com.fan.transfer.domain.Account;
import com.fan.transfer.services.tm.processor.Processor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Value
@NonFinal
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class TransferCommand extends Command  implements HasFrom {
   private Account.Id from;
   private Account.Id to;
   private BigDecimal amount;

   private Processor<TransferCommand> processor;

   @Override
   public CommandReply execute() {
        return getProcessor().process(this);
    }
}
