package it.leo.main.client;

import it.leo.main.config.ApplicationConfig;

public class MainClient {

    private static ApplicationConfig appConfig;
    
    public static void main(String[] args) {
        CLIService.start();
    }
}
