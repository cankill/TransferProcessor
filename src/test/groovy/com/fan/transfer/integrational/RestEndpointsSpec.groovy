package com.fan.transfer.integrational

import com.fan.transfer.api.CXFConfigurer
import com.fan.transfer.api.model.CreateAccountResponse
import com.fan.transfer.api.model.CreateUserResponse
import com.fan.transfer.domain.Account
import com.fan.transfer.domain.ErrorResponse
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

import javax.ws.rs.core.GenericType
import javax.ws.rs.core.Response

@UseModules(TestModule)
@Unroll
class RestEndpointsSpec extends Specification {
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
    }

    def cleanup() {
        client.close()
    }

    def "create account for user '#userId'"() {
        setup:
        client.path(userPath)
        Response userResp = client.post(userCreateRequest)
        def userId = userResp.readEntity(CreateUserResponse.class)
        User user = userRepository.get(new User.Id(userId.id))

        client.path(accountPath, userId.id)
        Response accountResp = client.post(accountCreateRequest)
        def accountId = accountResp.readEntity(CreateAccountResponse)
        Account account = accountRepository.get(new Account.Id(accountId.id))

        expect:
        user.name == userName
        user.email == userEmail
        user.phone == userPhone
        
        account.balance == accountBalance 
        account.currency.currencyCode == accountCurrency

        where:
        userPath << ["/user"]
        userName << ["AndrewFan"]
        userEmail << ["filyaniny@gmail.com"]
        userPhone << ["+35797648671"]
        userCreateRequest << [[name: userName, email: userEmail, phone: userPhone]]
        accountPath << ["/{userId}/account"]
        accountBalance << [100.09]
        accountCurrency << ["USD"]
        accountCreateRequest << [[balance: accountBalance, currency: accountCurrency]]
        accounts << [["accountId" : "abc"]]
    }

    def "get '#userId' balance"() {
        setup:
        userRepository.add(user)
        userRepository.add(account)
        
        client.path(path, userId)
        Response resp = client.get()

        expect:
        resp.readEntity(new GenericType<List<Account>>() {}) == accounts

        where:
        userId << "userId1"
        path << "/user/{userId}/account/{accountId}/balance"

        "/account/balance/{accountId}" | "andrewFan" || []
//        "/account/balance/{userId}" | null        || User.builder().id(userId).build()
    }

    def "get unknown '#userId' balance"() {
        setup:
        client.path(path, accountId)
        Response resp = client.get()

        expect:
        resp.readEntity(ErrorResponse.class) == error

        where:
        path                                     | accountId    || error
        "/account/balance/{accountId}" | "accountId1" || ErrorResponse.builder().error("Account accountId1 was not found").build()
    }
}