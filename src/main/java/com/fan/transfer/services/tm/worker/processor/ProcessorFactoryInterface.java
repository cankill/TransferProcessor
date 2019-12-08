package com.fan.transfer.services.tm.worker.processor;

import com.fan.transfer.services.tm.worker.model.CommandInterface;

public interface ProcessorFactoryInterface {
    <T extends CommandInterface> Processor<T>  get(Class<T> clazz);
}
