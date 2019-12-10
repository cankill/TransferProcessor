package com.fan.transfer.services.tm.command;

/**
 * Interface class for all Commands in a system
 * It describes an execute method for Command Pattern support
 */
public interface CommandInterface {
    CommandReply execute();
}
