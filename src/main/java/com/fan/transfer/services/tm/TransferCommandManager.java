package com.fan.transfer.services.tm;

import com.fan.transfer.domain.Account;
import com.fan.transfer.domain.Transaction;

import java.math.BigDecimal;

public interface TransferCommandManager {
    Transaction.Id transfer (Account.Id from, Account.Id to, BigDecimal amount);
}
