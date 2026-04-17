package it.leo.main.client;

// THIS IS THE MAIN ENTRY POINT FOR THE CLIENT SIDE ----------------------------------------------

public class MainClient {
    
    public static void main(String[] args) {

        // Starting CLI
        CLIService cliService = new CLIService();
        cliService.start();
    }
}
