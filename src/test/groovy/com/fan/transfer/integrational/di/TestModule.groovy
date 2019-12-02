package com.fan.transfer.integrational.di

import com.fan.transfer.domain.Account
import com.fan.transfer.domain.Transaction
import com.fan.transfer.domain.User
import com.fan.transfer.pereferial.db.Repository
import com.fan.transfer.pereferial.db.impl.InMemoryTable
import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import com.google.inject.name.Named

class TestModule extends AbstractModule {
    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    @Named("userRepository")
    Repository<User> getUseRepository() {
        new InMemoryTable<>(User.class);
    }

    @Provides
    @Singleton
    @Named("accountRepository")
    Repository<Account> getAccountRepository() {
        new InMemoryTable<>(Account.class);
    }

    @Provides
    @Singleton
    @Named("transactionRepository")
    Repository<Transaction> getTransactionRepository() {
        new InMemoryTable<>(Transaction.class);
    }
}
