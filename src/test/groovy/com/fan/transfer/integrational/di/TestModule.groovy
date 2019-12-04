package com.fan.transfer.integrational.di

import com.fan.transfer.di.TransferProcessorModule
import com.fan.transfer.integrational.utils.RestClient
import com.fan.transfer.integrational.utils.RestClientInterface
import com.google.inject.AbstractModule
import com.google.inject.assistedinject.FactoryModuleBuilder

class TestModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new TransferProcessorModule())
        install(new FactoryModuleBuilder()
                .implement(RestClientInterface.class, RestClient.class)
                .build(RestClientFactory.class))
    }
}
