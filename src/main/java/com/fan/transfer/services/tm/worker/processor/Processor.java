package com.fan.transfer.services.tm.worker.processor;

import com.fan.transfer.services.tm.worker.model.CommandInterface;
import com.fan.transfer.services.tm.worker.model.CommandReply;

public interface Processor<T extends CommandInterface> {
    CommandReply process(T command);
}
