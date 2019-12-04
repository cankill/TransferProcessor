package com.fan.transfer.services;

import com.fan.transfer.domain.Account;

public interface AccountQueryManager {
    Account get (Account.Id accountId);
}
