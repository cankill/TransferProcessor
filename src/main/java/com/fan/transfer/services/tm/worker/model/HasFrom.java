package com.fan.transfer.services.tm.worker.model;

import com.fan.transfer.domain.Account;

public interface HasFrom {
    Account.Id getFrom();
}
