package it.leo.main.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import it.leo.main.config.ApplicationConfig;
import it.leo.main.protocol.CLICommandFactory;
import it.leo.main.protocol.DbConnection;

public class CLIService {

    private Scanner scanner;

    private void showHelp() {
        System.out.println("---------- Help message ----------\n");
        CLICommandFactory.ALL.forEach(command -> {
            System.out.println(String.format("- %s: %s", command.getName(), command.getDescription()));
        });
        System.out.print("\n\n");
    }

    private void handleDBConnection(String args) {
        System.out.println("Connecting to the database...");

        var argsMap = ArgParser.parseArgs(args);
        
        if (argsMap.size() != 2) {
            System.err.println("Invalid command sintax: Expected 2 arguments got "+argsMap.size());
        } else {

        }

        try (Socket client = new Socket(ApplicationConfig.SERVER_HOST, ApplicationConfig.SERVER_PORT)) {
            // TODO: Send a packet with credentials once the socket is opened
            // TODO: If the auth goes successfully just retrieve connection and store in the object
            // TODO: Save the connection status somewhere
            ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());
            DbConnection connection = (DbConnection) objectInputStream.readObject();
            System.out.println("Ricevuta conn: "+connection);
            connection.setClientSocket(client);
            var queryService = new QueryService(connection);
            queryService.start();
        } catch (IOException | ClassNotFoundException ex) {
            System.getLogger(CLIService.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    } 

    public void start() {
        // Initializing variables
        scanner = new Scanner(System.in);
        String input;

        // Showing first help message
        showHelp();
        while(true) {
            System.out.print("Enter a command (help to show command list): ");
            input = scanner.nextLine();
            List<String> chunks = Arrays.stream(input.split("[\s]")).toList();

            switch (chunks.getFirst().toLowerCase()) {
                case "help" -> showHelp();
                case "connect" -> handleDBConnection(input.substring(6));
                case "exit" -> System.exit(-1);
                default -> System.err.println("Invalid command: "+chunks.getFirst().toLowerCase());
            }
        }
    }
    
}
