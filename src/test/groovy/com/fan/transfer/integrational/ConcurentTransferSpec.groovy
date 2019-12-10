package com.fan.transfer.integrational

import com.fan.transfer.api.CXFConfigurer
import com.fan.transfer.domain.Account
import com.fan.transfer.domain.User
import com.fan.transfer.integrational.di.RestClientFactory
import com.fan.transfer.integrational.di.TestModule
import com.fan.transfer.integrational.utils.MapJsonSerializer
import com.fan.transfer.pereferial.db.Repository
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.google.inject.Inject
import com.google.inject.name.Named
import org.apache.cxf.endpoint.Server
import spock.guice.UseModules
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class ConcurentTransferSpec extends HelperSpecification {
    public static final String ENDPOINT_ADDRESS = "http://localhost:8080/v1"
    private static Server server;

    @Inject
    @Shared
    RestClientFactory restClientFactory

    @Inject
    @Shared
    CXFConfigurer cxfConfigurer

    @Inject
    @Shared
    ObjectMapper objectMapper
    User.Id userId
    User user
    def account1InitialBalance = BigDecimal.valueOf(30000)
    Account.Id accountId1
    Account account1
    def account2InitialBalance = BigDecimal.valueOf(20000)
    Account.Id accountId2
    Account account2
    def account3InitialBalance = BigDecimal.valueOf(10000)
    Account.Id accountId3
    Account account3

    def numberOfIterations = 100

    def setupSpec() {
        server = cxfConfigurer.getServer()

        SimpleModule module = new SimpleModule("MyTestModule", new Version(1, 0, 0, null));
        module.addSerializer(LinkedHashMap.class, new MapJsonSerializer());
        objectMapper.registerModule(module);
    }

    def cleanupSpec() {
        server.stop()
        server.destroy()
    }

    def setup() {
        userId = User.Id.builder().value("userId").build()
        user = User.builder().id(userId).name("User Fan").email("filyaniny@gmail.com").build()
        accountId1 = Account.Id.builder().value("accountId1").build()
        account1 = Account.builder().id(accountId1).userId(userId).balance(account1InitialBalance).build()
        accountId2 = Account.Id.builder().value("accountId2").build()
        account2 = Account.builder().id(accountId2).userId(userId).balance(account2InitialBalance).build()
        accountId3 = Account.Id.builder().value("accountId3").build()
        account3 = Account.builder().id(accountId3).userId(userId).balance(account3InitialBalance).build()

        userRepository.add(user)
        accountRepository.add(account1)
        accountRepository.add(account2)
        accountRepository.add(account3)
    }

    def cleanup() {
        userRepository.removeAll()
        accountRepository.removeAll()
    }

    // Transfer money concurrently cyclic A -> B -> C -> A, different amount
    def "Transfer money concurrently"() {
        setup: "Spawn 3 threads to transfer (A -> B), (B -> C), (C -> A, C -> A amount greater than balance)"
        def restClient1 = restClientFactory.create(ENDPOINT_ADDRESS)
        def client1 = restClient1.getClient()
        client1.path(transferPath, userId.value, accountId1.value)

        def restClient2 = restClientFactory.create(ENDPOINT_ADDRESS)
        def client2 = restClient2.getClient()
        client2.path(transferPath, userId.value, accountId2.value)

        def restClient3 = restClientFactory.create(ENDPOINT_ADDRESS)
        def client3 = restClient3.getClient()
        client3.path(transferPath, userId.value, accountId3.value)

        def thread1 = new Thread(new Runnable() {
            @Override
            void run() {
                (1..numberOfIterations).each {
                    client1.post([from: accountId1, to: accountId2, amount: 30.07])
                }
            }
        })

        def thread2 = new Thread(new Runnable() {
            @Override
            void run() {
                (1..numberOfIterations).each {
                    client2.post([from: accountId2, to: accountId3, amount: 20.07])
                }
            }
        })

        def thread3 = new Thread(new Runnable() {
            @Override
            void run() {
                (1..numberOfIterations).each {
                    client3.post([from: accountId3, to: accountId1, amount: 10.07])
                    client3.post([from: accountId3, to: accountId1, amount: account1InitialBalance * 2])
                }
            }
        })

        thread1.start()
        thread2.start()
        thread3.start()

        thread1.join()
        thread2.join()
        thread3.join()

        waitProcessingToFinish(30)

        def account1result = accountRepository.get(accountId1)
        def account2result = accountRepository.get(accountId2)
        def account3result = accountRepository.get(accountId3)

        expect: "As a cycle count is known and the amount of transfer is known we can predict a final balances for accounts"
        account1result.balance == account1InitialBalance - 30.07 * numberOfIterations + 10.07 * numberOfIterations
        account2result.balance == account2InitialBalance - 20.07 * numberOfIterations + 30.07 * numberOfIterations
        account3result.balance == account3InitialBalance - 10.07 * numberOfIterations + 20.07 * numberOfIterations


        where:
        transferPath << ["/user/{userId}/account/{accountId}/transfer"]
    }
}