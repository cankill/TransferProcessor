package com.fan.transfer.integrational

import com.fan.transfer.domain.Account
import com.fan.transfer.domain.Ref
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
    Repository<User.Id, User> userRepository

    @Inject
    @Named("accountRepository")
    @Shared
    Repository<Account.Id, Account> accountRepository

    @Inject
    @Named("transactionRepository")
    @Shared
    Repository<Transaction.Id, Transaction> transactionRepository

    def setup() {
        userRepository.removeAll()
        accountRepository.removeAll()
        transactionRepository.removeAll()
    }

    def "create Elements"() {
        setup:
        userRepository.add(user)
        accountRepository.add(account)
        transactionRepository.add(transaction)

        expect:
        def repoUser = userRepository.get(userId)
        repoUser == expectedUser
        def repoAccount = accountRepository.get(repoUser.getAccounts().get(0).getId())
        repoAccount == expectedAccount
        def repoTransaction = transactionRepository.get(repoAccount.getTransactions().get(0).getId())
        repoTransaction == expectedTransaction

        where:
        userId << [new User.Id("andrewFan")]
        accountId << [new Account.Id("acountId1")]
        transactionId << [new Transaction.Id("transactionId1")]
        user << [User.builder().id(userId).accounts([new Ref(accountId)]).build()]
        account << [Account.builder().id(accountId).transactions([new Ref(transactionId)]).build()]
        transaction << [Transaction.builder().id(transactionId).build()]
        expectedUser << [User.builder().id(userId).accounts([new Ref(accountId)]).build()]
        expectedAccount << [Account.builder().id(accountId).transactions([new Ref(transactionId)]).build()]
        expectedTransaction << [Transaction.builder().id(transactionId).build()]
    }

    def "update User"() {
        setup:
        userRepository.add(user)

        expect:
        def repoUser = userRepository.get(userId)
        repoUser == expectedUser
        userRepository.update(userId, userUpdate)
        def updatedRepoUser = userRepository.get(userId)
        updatedRepoUser == updatedUser

        where:
        userId << [new User.Id("andrewFan")]
        user << [User.builder().id(userId).build()]
        expectedUser << [User.builder().id(userId).build()]
        userUpdate << [User.builder().id(new User.Id("userIdWrong")).name("Andrew FAN").build()]
        updatedUser << [User.builder().id(userId).name("Andrew FAN").build()]
    }

    def "get All Users"() {
        setup:
        users.each { userRepository.add(it) }

        expect:
        def repoUsers = userRepository.getAll(userIds)
        repoUsers == expectedUsers

        where:
        userIds << [[new User.Id("user1"), new User.Id("user2"), new User.Id("user3")]]
        users << [userIds.collect { createUser(it) }]
        expectedUsers << [userIds.collect { createUser(it) }]
    }

    def "delete User"() {
        setup:
        userRepository.add(user)

        expect:
        def repoUser = userRepository.get(userId)
        repoUser == expectedUser
        userRepository.remove(userId)
        def deletedRepoUser = userRepository.get(userId)
        deletedRepoUser == deletedUser

        where:
        userId << [new User.Id("andrewFan")]
        user << [User.builder().id(userId).build()]
        expectedUser << [User.builder().id(userId).build()]
        deletedUser << [null]
    }

    def "delete All Users"() {
        setup:
        users.each { userRepository.add(it) }

        expect:
        def repoUsers = userIds.collect { userRepository.get(it) }
        repoUsers == expectedUsers
        userIds.collect { userRepository.remove(it) }

        def deletedRepoUsers = userIds.collect { userRepository.get(it) }
        deletedRepoUsers == deletedUsers

        where:
        userIds << [[new User.Id("user1"), new User.Id("user2"), new User.Id("user3")]]
        users << [userIds.collect { createUser(it) }]
        expectedUsers << [userIds.collect { createUser(it) }]
        deletedUsers << [[null, null, null]]
    }

    User createUser(userId) {
        User.builder().id(userId).build()
    }
}