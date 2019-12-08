package com.fan.transfer.di;

import com.fan.transfer.api.CXFConfigurer;
import com.fan.transfer.api.CXFConfigurerImpl;
import com.fan.transfer.api.resources.AccountManagementResource;
import com.fan.transfer.api.resources.AccountManagementResourceImpl;
import com.fan.transfer.api.resources.UserManagementResource;
import com.fan.transfer.api.resources.UserManagementResourceImpl;
import com.fan.transfer.domain.Account;
import com.fan.transfer.domain.Transaction;
import com.fan.transfer.domain.User;
import com.fan.transfer.pereferial.db.Repository;
import com.fan.transfer.pereferial.db.impl.InMemoryTable;
import com.fan.transfer.services.*;
import com.fan.transfer.services.tm.TransferCommandManager;
import com.fan.transfer.services.tm.TransferCommandManagerImpl;
import com.fan.transfer.services.tm.worker.processor.ProcessorFactory;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Named;

public class TransferProcessorModule extends AbstractModule {
    @Override
    protected void configure () {
        install(new FactoryModuleBuilder()
                .implement(AccountManagementResource.class, AccountManagementResourceImpl.class)
                .build(AccountManagementResourceFactory.class));
    }

    @Provides
    @Singleton
    CXFConfigurer getCxfConfigurer (Injector injector) {
        return injector.getInstance(CXFConfigurerImpl.class);
    }

    @Provides
    @Singleton
    UserManagementResource provideUserManagementResource (Injector injector) {
        return injector.getInstance(UserManagementResourceImpl.class);
    }

    @Provides
    @Singleton
    UserQueryManager provideUserQueryManager (Injector injector) {
        return injector.getInstance(UserQueryManagerImpl.class);
    }

    @Provides
    @Singleton
    UserCommandManager provideUserCommandManager (Injector injector) {
        return injector.getInstance(UserCommandManagerImpl.class);
    }

    @Provides
    @Singleton
    AccountQueryManager getAccountQueryManager (Injector injector) {
        return injector.getInstance(AccountQueryManagerImpl.class);
    }

    @Provides
    @Singleton
    AccountCommandManager getAccountCommandManager (Injector injector) {
        return injector.getInstance(AccountCommandManagerImpl.class);
    }

    @Provides
    @Singleton
    ProcessorFactory getProcessorFactory (Injector injector) {
        return injector.getInstance(ProcessorFactory.class).init();
    }
    
    @Provides
    @Singleton
    @Named("userRepository")
    Repository<User.Id, User> getUseRepository () {
        return new InMemoryTable<>();
    }

    @Provides
    @Singleton
    @Named("accountRepository")
    Repository<Account.Id, Account> getAccountRepository () {
        return new InMemoryTable<>();
    }

    @Provides
    @Singleton
    @Named("transactionRepository")
    Repository<Transaction.Id, Transaction> getTransactionRepository () {
        return new InMemoryTable<>();
    }


    @Provides
    @Singleton
    TransferCommandManager provideTransactionCoordinator(Injector inject) {
        return inject.getInstance(TransferCommandManagerImpl.class).init(16);
    }

    @Provides
    @Singleton
    public JacksonJsonProvider jacksonJsonProvider (ObjectMapper objectMapper) {
        return new JacksonJsonProvider(objectMapper);
    }
    
    @Provides
    @Singleton
    public ObjectMapper provideObjectMapper () {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector());
        return objectMapper;
    }
}
