package com.pints793;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        Thread restServer = new Thread(() -> SpringApplication.run(Application.class, args));
        restServer.start();
    }
}

