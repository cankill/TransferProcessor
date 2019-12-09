package com.fan.transfer;

import com.fan.transfer.api.CXFConfigurer;
import com.fan.transfer.api.handlers.ThreadExceptionHandler;
import com.fan.transfer.di.TransferProcessorModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.endpoint.Server;

import java.io.IOException;
import java.util.Scanner;

@Slf4j
public class App {
    public static void main (String[] args) throws IOException {
        Thread.setDefaultUncaughtExceptionHandler(new ThreadExceptionHandler());
        Injector injector = Guice.createInjector(new TransferProcessorModule());
        CXFConfigurer cxfConfigurer = injector.getInstance(CXFConfigurer.class);
        Server server = cxfConfigurer.getServer();

        if (server.isStarted()) {
            log.debug("Started");
            waitForQuit();
            log.debug("Exiting...");

            server.stop();
            server.destroy();
            cxfConfigurer.getBus().shutdown(true);
        } else {
            log.error("Not started");
        }
    }

    private static void waitForQuit () {
        Scanner keyboard = new Scanner(System.in);
        do {
            System.out.println("Enter command (quit to exit):");
        } while (!"quit".equals(keyboard.nextLine()) && keyboard.hasNextLine());
        keyboard.close();
    }
}
