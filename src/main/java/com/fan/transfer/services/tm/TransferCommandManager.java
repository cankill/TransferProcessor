package com.fan.transfer.services.tm;

import com.fan.transfer.domain.Account;

import java.math.BigDecimal;

public interface TransferCommandManager {
    void transfer (Account.Id from, Account.Id to, BigDecimal amount);
}
