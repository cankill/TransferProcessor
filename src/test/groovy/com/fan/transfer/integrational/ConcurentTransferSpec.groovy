package com.fan.transfer.integrational

import com.fan.transfer.api.CXFConfigurer
import com.fan.transfer.api.model.CreateAccountResponse
import com.fan.transfer.api.model.CreateUserResponse
import com.fan.transfer.domain.Account
import com.fan.transfer.domain.ErrorResponse
import com.fan.transfer.domain.Transaction
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
import org.apache.cxf.jaxrs.client.WebClient
import spock.guice.UseModules
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import javax.ws.rs.core.Response

@UseModules(TestModule)
@Unroll
class ConcurentTransferSpec extends Specification {
    public static final String ENDPOINT_ADDRESS = "http://localhost:8080/v1"
    private static Server server;
    private static WebClient client;

    @Inject
    @Named("userRepository")
    Repository<User.Id, User> userRepository;

    @Inject
    @Named("accountRepository")
    Repository<Account.Id, Account> accountRepository;

    @Inject
    @Shared
    RestClientFactory restClientFactory

    @Inject
    @Shared
    CXFConfigurer cxfConfigurer

    @Inject
    @Shared
    ObjectMapper objectMapper

    private static userId = User.Id.builder().value("userId").build()
    private static user = User.builder().id(userId).name("User Fan").email("filyaniny@gmail.com").build()
    private static accountId1 = Account.Id.builder().value("accountId1").build()
    private static account1 = Account.builder().id(accountId1).userId(userId).balance(BigDecimal.valueOf(10000)).build()
    private static accountId2 = Account.Id.builder().value("accountId2").build()
    private static account2 = Account.builder().id(accountId2).userId(userId).balance(BigDecimal.valueOf(20000)).build()

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
        def restClient = restClientFactory.create(ENDPOINT_ADDRESS)
        client = restClient.getClient()

        userRepository.add(user)
        accountRepository.add(account1)
        accountRepository.add(account2)
    }

    def cleanup() {
        client.close()
        userRepository.removeAll()
        accountRepository.removeAll()
    }

    def "create account for user '#userId'"() {
        setup:
        client.path(userPath)
        Response userResp = client.post(userCreateRequest)
        def userId = userResp.readEntity(CreateUserResponse.class)
        User user = userRepository.get(new User.Id(userId.id))

        client.path(accountPath, userId.id)

        Response accountResp1 = client.post(accountCreateRequest[0])
        def accountId1 = accountResp1.readEntity(CreateAccountResponse)
        Account account1 = accountRepository.get(new Account.Id(accountId1.id))

        Response accountResp2 = client.post(accountCreateRequest[1])
        def accountId2 = accountResp2.readEntity(CreateAccountResponse)
        Account account2 = accountRepository.get(new Account.Id(accountId2.id))

        client.path(transferPath, accountId1.id)
        Response transferResp = client.post([from: accountId1.id, to: accountId2.id, amount: 14.01])

        account1 = accountRepository.get(new Account.Id(accountId1.id))
        account2 = accountRepository.get(new Account.Id(accountId2.id))

        expect:
        user.name == userName
        user.email == userEmail
        user.phone == userPhone

        sleep(3000)

        account1.balance == accountBalance1 - 14.01
        account1.currency.currencyCode == accountCurrency

        account2.balance == accountBalance2 + 14.01
        account2.currency.currencyCode == accountCurrency

        where:
        userPath << ["/user"]
        userName << ["AndrewFan"]
        userEmail << ["filyaniny@gmail.com"]
        userPhone << ["+35797648671"]
        userCreateRequest << [[name: userName, email: userEmail, phone: userPhone]]
        accountPath << ["/{userId}/account"]
        accountBalance1 << [100.09]
        accountBalance2 << [33.77]
        accountCurrency << ["USD"]
        accountCreateRequest << [
                [[balance: accountBalance1, currency: accountCurrency], [balance: accountBalance2, currency: accountCurrency]]
        ]
        transferPath << ["/{accountId}/transfer"]
        transferRequest << [
                []
        ]
    }
}