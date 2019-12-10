package com.fan.transfer.services.tm.processor;

import com.fan.transfer.services.tm.command.CommandInterface;

public interface ProcessorFactoryInterface {
    <T extends CommandInterface> Processor<T>  get(Class<T> clazz);
}
