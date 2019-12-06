package com.fan.transfer.services.tm.worker;

import com.fan.transfer.services.tm.worker.model.CommandI;
import com.fan.transfer.services.tm.worker.model.ReplyI;

public interface Processor<T extends CommandI> {
    ReplyI process(T command);
}
