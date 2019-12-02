package com.fan.transfer.integrational

import com.fan.transfer.TransferProcessorModule
import com.fan.transfer.api.resources.AccauntManagementResourceImpl
import com.fan.transfer.domain.User
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import org.apache.cxf.endpoint.Server
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean
import org.apache.cxf.jaxrs.client.WebClient
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider
import org.apache.cxf.transport.http.HTTPConduit
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

import static org.assertj.core.api.Assertions.*;

class TestRestEndpoints {
    public static final String ENDPOINT_ADDRESS = "http://localhost:8080/v1"
    private static Server server;

    private static WebClient client;
    private static TransferProcessorModule transferProcessorModule = new TransferProcessorModule()

    @BeforeClass
    protected void setUp() {
        def jacksonJsonProvider = new JacksonJsonProvider()
        client = WebClient.create(ENDPOINT_ADDRESS, [jacksonJsonProvider])
        WebClient.getConfig(client).getRequestContext().put("use.async.http.conduit", Boolean.TRUE)
        HTTPConduit conduit = WebClient.getConfig(client).getConduit()
        conduit.getClient().setReceiveTimeout(150000)

        JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean()
//        sf.setResourceClasses(MyJaxrsResource.class);

//        List<Object> providers = new ArrayList<Object>();
//        // add custom providers if any
//        sf.setProviders(providers);

        sf.setResourceProvider(new SingletonResourceProvider(new AccauntManagementResourceImpl(), true))
        sf.setAddress(ENDPOINT_ADDRESS)
        sf.setProviders([transferProcessorModule.jacksonJsonProvider()])

        server = sf.create()
    }

    @AfterClass
    protected void tearDown() {
        client.close()
        server.stop()
        server.destroy()
    }

    @Test
    public void testGetBalance() {
        client.accept(MediaType.APPLICATION_JSON_TYPE)
        client.path("/accauntManagement/balance/{userId}", "andrewFan")
        Response resp = client.get()

        def objectMapper = new ObjectMapper()
//        objectMapper.findAndRegisterModules();

        println(resp.getEntity().getClass())

        def user = objectMapper.readValue(resp.getEntity(), User.class)

//        User user = resp.readEntity(User.class)

        assertThat(user).isEqualToComparingFieldByField(User.builder().id("andrewFan").build())
    }
}
