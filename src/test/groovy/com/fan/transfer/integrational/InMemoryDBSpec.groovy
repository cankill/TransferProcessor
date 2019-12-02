package com.fan.transfer.integrational

import com.fan.transfer.domain.Account
import com.fan.transfer.domain.Transaction
import com.fan.transfer.domain.User
import com.fan.transfer.integrational.di.TestModule
import com.fan.transfer.pereferial.db.Repository
import com.google.inject.Inject
import com.google.inject.name.Named
import spock.guice.UseModules
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@UseModules(TestModule)
@Unroll
class InMemoryDBSpec extends Specification {
    @Inject
    @Named("userRepository")
    @Shared
    Repository<User> userRepository

    @Inject
    @Named("accountRepository")
    @Shared
    Repository<Account> accountRepository

    @Inject
    @Named("transactionRepository")
    @Shared
    Repository<Transaction> transactionRepository

    def "create Users"() {
        setup:
        userRepository.add(user)

        expect:
        def repoUser = userRepository.get(userId)
        repoUser == expectedUser

        where:
        userId << ["andrewFan"]
        user << [User.builder().id(userId).build()]
        expectedUser << [User.builder().id(userId).build()]
    }
}