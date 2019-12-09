package com.fan.transfer.integrational.utils

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider
import com.google.inject.assistedinject.Assisted
import com.google.inject.assistedinject.AssistedInject
import org.apache.cxf.jaxrs.client.WebClient
import org.apache.cxf.transport.http.HTTPConduit

import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.MediaType

class RestClient implements RestClientInterface {
    private WebClient client

    @AssistedInject
    RestClient(JacksonJsonProvider jacksonJsonProvider, @Assisted String endpoint) {
        client = WebClient.create(endpoint, [jacksonJsonProvider])
        WebClient.getConfig(client).getRequestContext().put("use.async.http.conduit", Boolean.TRUE)
        HTTPConduit conduit = (HTTPConduit) WebClient.getConfig(client).getConduit()
        conduit.getClient().setReceiveTimeout(15000000)
        conduit.getClient().setAsyncExecuteTimeout(15000000)
        client.accept(MediaType.APPLICATION_JSON_TYPE)
        client.replaceHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_TYPE)
    }

    @Override
    WebClient getClient() {
        client
    }
}
