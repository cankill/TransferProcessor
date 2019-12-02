package com.fan.transfer.api;

import com.fan.transfer.api.resources.AccauntManagementResource;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.Inject;
import lombok.Getter;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;

import java.util.List;

public class CXFConfigurer {
    @Getter
    private Server server;

    @Inject
    public CXFConfigurer(AccauntManagementResource accauntManager,
                         JacksonJsonProvider jacksonJsonProvider) {
        JAXRSServerFactoryBean factoryBean = new JAXRSServerFactoryBean();
        factoryBean.setResourceProvider(new SingletonResourceProvider(accauntManager));
        factoryBean.setAddress("http://localhost:8080/v1");
        factoryBean.setProviders(List.of(jacksonJsonProvider));

        server = factoryBean.create();
    }
}
