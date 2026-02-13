package com.pints793;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        ApplicationManager applicationManager = context.getBean(ApplicationManager.class);
        Thread applicationDaemon = new Thread(applicationManager::run);
        applicationDaemon.setDaemon(true);
        applicationDaemon.start();
    }
}
