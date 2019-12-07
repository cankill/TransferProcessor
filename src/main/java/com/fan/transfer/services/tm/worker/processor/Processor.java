package com.fan.transfer.services.tm.worker.processor;

import com.fan.transfer.domain.Transaction;
import com.fan.transfer.domain.TransactionStatus;
import com.fan.transfer.services.tm.worker.model.CommandInterface;
import com.fan.transfer.services.tm.worker.model.CommandReply;

import java.util.function.Predicate;

public interface Processor<T extends CommandInterface> {
    CommandReply process(T command);
}
