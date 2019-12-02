package com.fan.transfer;

import com.fan.transfer.api.CXFConfigurer;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.cxf.endpoint.Server;

public class App {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new TransferProcessorModule());

        CXFConfigurer cxfConfigurer = injector.getInstance(CXFConfigurer.class);

        System.out.println("Started");

        Server server = cxfConfigurer.getServer();

        var isStarted = server.isStarted();

        server.start();
    }
}
