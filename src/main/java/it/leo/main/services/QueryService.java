package it.leo.main.services;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import it.leo.main.data.connection.DbConnection;
import it.leo.main.factories.QueryCommandFactory;

public class QueryService {
    private final Scanner scanner;
    private final DbConnection connection;

    public QueryService(DbConnection connection) {
        this.scanner = new Scanner(System.in);
        this.connection = connection;
    }

    private void showHelp() {
        System.out.println("---------- Help message ----------\n");
        QueryCommandFactory.ALL.forEach(command -> {
            System.out.println(String.format("- %s: %s", command.getName(), command.getDescription()));
        });
        System.out.print("\n\n");
    }

    private void sendPacket(String text) throws IOException {
        connection.getPrintWriter().println(text);
        connection.getBufferedReader().lines().forEach(line -> System.out.println(line));
    }



    public void start() throws IOException {
        // Initializing variables
        String input;

        // Showing first help message
        showHelp();
        while(true) {
            System.out.print("> (help to show command list): ");
            input = scanner.nextLine();
            List<String> chunks = Arrays.stream(input.split("[\s]")).toList();

            switch (chunks.getFirst().toLowerCase()) {
                case "help" -> showHelp();
                case "exit" -> System.exit(-1);
                default -> sendPacket(input);
            }
        }
    }
}
