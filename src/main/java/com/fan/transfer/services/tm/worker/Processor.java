package com.fan.transfer.services.tm.worker;

import com.fan.transfer.services.tm.worker.model.CommandI;

public interface Processor<T extends CommandI> {
    CommandI process(T command);
}
