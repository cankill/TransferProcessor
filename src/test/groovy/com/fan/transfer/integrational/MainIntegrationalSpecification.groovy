package com.fan.transfer.integrational

import com.fan.transfer.api.CXFConfigurer
import com.fan.transfer.domain.Account
import com.fan.transfer.domain.Transaction
import com.fan.transfer.domain.TransactionStatus
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

@UseModules(TestModule)
class MainIntegrationalSpecification extends Specification {
    protected static final String ENDPOINT_ADDRESS = "http://localhost:8080/v1"
    protected static Server server;
    protected static WebClient client;

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

    void waitProcessingToFinish(int sleepFor) {
        Closure<Boolean> checkTransactionsStatus = { sleepTime ->
            def transactions = transactionRepository.getAllBy({ trx -> trx.getStatus() != TransactionStatus.DONE })
            sleep(sleepTime)
            !transactions.isEmpty()
        }

        while (checkTransactionsStatus(sleepFor)) continue
    }
}
