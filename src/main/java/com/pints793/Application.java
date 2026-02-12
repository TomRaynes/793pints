package com.pints793;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        CellarManager cellarManager = context.getBean(CellarManager.class);
        Thread applicationDaemon = new Thread(cellarManager::run);
        applicationDaemon.setDaemon(true);
        applicationDaemon.start();
    }
}
