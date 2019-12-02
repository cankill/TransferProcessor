package com.fan.transfer;

import com.fan.transfer.api.resources.AccauntManagementResource;
import com.fan.transfer.api.resources.AccauntManagementResourceImpl;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class TransferProcessorModule extends AbstractModule {
    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    AccauntManagementResource provideAccauntManagementResource() {
        return new AccauntManagementResourceImpl();
    }

    @Provides
    @Singleton
    public JacksonJsonProvider jacksonJsonProvider() {
        JacksonJsonProvider jacksonJsonProvider = new JacksonJsonProvider(objectMapper());
        return jacksonJsonProvider;
    }

    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        AnnotationIntrospector introsepctor = new JacksonAnnotationIntrospector();
        objectMapper.setAnnotationIntrospector(introsepctor);
        return objectMapper;
    }
}
