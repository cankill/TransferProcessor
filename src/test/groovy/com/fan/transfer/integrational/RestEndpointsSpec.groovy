package com.fan.transfer.integrational

import com.fan.transfer.TransferProcessorModule
import com.fan.transfer.api.resources.AccountManagementResource
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

@UseModules(TransferProcessorModule)
@Unroll
class RestEndpointsSpec extends Specification {
    public static final String ENDPOINT_ADDRESS = "http://localhost:8080/v1"
    private static Server server;
    private static WebClient client;

    @Inject
    @Shared
    AccountManagementResource accountManagementResource

    @Inject
    @Shared
    JacksonJsonProvider jacksonJsonProvider

    def setupSpec() {
        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean()
        sf.setResourceProvider(new SingletonResourceProvider(accountManagementResource, true))
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
        "/accountManagement/balance/{userId}" | "andrewFan" || User.builder().id(userId).build()

    }
}