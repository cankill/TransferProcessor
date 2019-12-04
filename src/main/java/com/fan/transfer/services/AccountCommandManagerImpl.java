package com.fan.transfer.services;

import com.fan.transfer.domain.Account;
import com.fan.transfer.pereferial.db.Repository;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class AccountCommandManagerImpl implements AccountCommandManager {
    @Inject
    @Named("accountRepository")
    Repository<Account.Id, Account> accountRepository;

    @Override
    public Account create (Account account) {
        var created = accountRepository.add(account);
        return created ? account : null;
    }
}
