package com.fan.transfer.services.tm.command;

import com.fan.transfer.domain.Account;

public interface HasFrom {
    Account.Id getFrom();
}
