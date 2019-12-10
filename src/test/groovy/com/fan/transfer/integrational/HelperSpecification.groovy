package com.fan.transfer.integrational

import com.fan.transfer.domain.Account
import com.fan.transfer.domain.Transaction
import com.fan.transfer.domain.TransactionStatus
import com.fan.transfer.domain.User
import com.fan.transfer.integrational.di.TestModule
import com.fan.transfer.pereferial.db.Repository
import com.google.inject.Inject
import com.google.inject.name.Named
import spock.guice.UseModules
import spock.lang.Shared
import spock.lang.Specification

@UseModules(TestModule)
class HelperSpecification extends Specification {
    @Inject
    @Named("userRepository")
    @Shared
    Repository<User.Id, User> userRepository;

    @Inject
    @Shared
    @Named("accountRepository")
    Repository<Account.Id, Account> accountRepository;

    @Inject
    @Shared
    @Named("transactionRepository")
    Repository<Transaction.Id, Transaction> transactionRepository;

    void waitProcessingToFinish(int sleepFor) {
        Closure<Boolean> checkTransactionsStatus = { sleepTime ->
            def transactions = transactionRepository.getAllBy({ trx -> trx.getStatus() != TransactionStatus.DONE })
            sleep(sleepTime)
            !transactions.isEmpty()
        }

        while (checkTransactionsStatus(sleepFor)) continue
    }
}
