package com.fan.transfer.services;

import com.fan.transfer.domain.Account;
import com.fan.transfer.pereferial.db.Repository;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class AccountQueryManagerImpl implements AccountQueryManager {
    @Inject
    @Named("accountRepository")
    Repository<Account.Id, Account> accountRepository;

    @Override
    public Account get (Account.Id accountId) {
        var account = accountRepository.get(accountId);
        if(account == null) {
            throw new EntityNotFoundException(String.format("Account '%s' was not found", accountId.getValue()));
        }

        return account;
    }
}
