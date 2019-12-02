package com.fan.transfer.integrational

import com.fan.transfer.TransferProcessorModule
import com.fan.transfer.api.resources.AccauntManagementResource
import com.fan.transfer.api.resources.AccauntManagementResourceImpl
import com.fan.transfer.domain.User
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import com.google.inject.Inject
import org.apache.cxf.endpoint.Server
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean
import org.apache.cxf.jaxrs.client.WebClient
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider
import org.apache.cxf.transport.http.HTTPConduit
import spock.guice.UseModules
import spock.lang.*

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import java.util.concurrent.Executors
import java.util.concurrent.Flow
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor

@UseModules(TransferProcessorModule)
@Unroll
class RestEndpointsSpec extends Specification {
    public static final String ENDPOINT_ADDRESS = "http://localhost:8080/v1"
    private static Server server;
    private static WebClient client;

    @Inject
    @Shared
    AccauntManagementResource accauntManagementResource

    @Inject
    @Shared
    JacksonJsonProvider jacksonJsonProvider

    def setupSpec() {
        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean()
        sf.setResourceProvider(new SingletonResourceProvider(accauntManagementResource, true))
        sf.setAddress(ENDPOINT_ADDRESS)
        sf.setProviders([jacksonJsonProvider])
        server = sf.create()
        
        client = WebClient.create(ENDPOINT_ADDRESS, [jacksonJsonProvider])
        WebClient.getConfig(client).getRequestContext().put("use.async.http.conduit", Boolean.TRUE)
        HTTPConduit conduit = (HTTPConduit) WebClient.getConfig(client).getConduit()
        conduit.getClient().setReceiveTimeout(150000)
        client.accept(MediaType.APPLICATION_JSON_TYPE)
    }

    def cleanupSpec() {
        client.close()
        server.stop()
        server.destroy()
    }

    def "get '#userId' balance"() {
        setup:
        client.path(path, userId)
        Response resp = client.get()

        expect:
        resp.readEntity(User.class) == user

        where:
        path | userId || user
        "/accauntManagement/balance/{userId}" | "andrewFan" || User.builder().id(userId).build()

    }

    def "test Publisher"() {
        setup:
        def executor1 = new ThreadPoolExecutor()

        def executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4)

        ThreadFactory threadFactory = new ThreadFactory() {
            @Override
            Thread newThread(Runnable r) {
                return null
            }
        }

        executor.setThreadFactory(threadFactory)
    }
//
//    def "maximum of two numbers"() {
//        expect:
//        Math.max(a, b) == c
//
//        where:
//        a << [3, 5, 9]
//        b << [7, 4, 9]
//        c << [7, 5, 9]
//    }
//
//    def "minimum of #a and #b is #c"() {
//        expect:
//        Math.min(a, b) == c
//
//        where:
//        a | b || c
//        3 | 7 || 4
//        5 | 4 || 4
//        9 | 9 || 9
//    }
//
//    def "#person.name is a #sex.toLowerCase() person"() {
//        expect:
//        person.getSex() == sex
//
//        where:
//        person                    || sex
//        new Person(name: "Fred")  || "Male"
//        new Person(name: "Wilma") || "Female"
//    }
//
//    static class Person {
//        String name
//        String getSex() {
//            name == "Fred" ? "Male" : "Female"
//        }
//    }
}