package com.fan.transfer.services.tm.processor;

import com.fan.transfer.services.tm.command.CommandInterface;
import com.fan.transfer.services.tm.command.CommandReply;

public interface Processor<T extends CommandInterface> {
    CommandReply process(T command);
}
