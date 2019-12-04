package com.fan.transfer.api;

import com.fan.transfer.api.handlers.ApiExceptionHandler;
import com.fan.transfer.api.resources.UserManagementResource;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.Inject;
import lombok.Getter;
import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;

import java.util.List;

public class CXFConfigurerImpl implements CXFConfigurer {
    @Getter
    private Server server;

    @Getter
    private Bus bus;

    @Inject
    public CXFConfigurerImpl (UserManagementResource userManagementResource,
                              JacksonJsonProvider jacksonJsonProvider) {
        JAXRSServerFactoryBean factoryBean = new JAXRSServerFactoryBean();

        factoryBean.setResourceProvider(new SingletonResourceProvider(userManagementResource, Boolean.TRUE));

        factoryBean.setAddress("http://localhost:8080/v1");
        factoryBean.setProviders(List.of(jacksonJsonProvider, new ApiExceptionHandler()));

        server = factoryBean.create();
        bus = factoryBean.getBus();
    }
}
