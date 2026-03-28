package it.leo.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import it.leo.main.config.ApplicationConfig;
import it.leo.main.factories.CommandFactory;

public class MainClient {

    private static ApplicationConfig appConfig;    
    
    public static String askForQuery(List<Command> commands, Scanner scanner){
        var command = "";
        try {
            System.out.print("Enter a command from the above list (enter EXIT to close): ");
            while(true) {
                if (scanner.hasNext()) {
                    command=scanner.nextLine();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        if (command.equals("EXIT")) System.exit(1);
        return command;
    }
    
    public static void main(String[] args) {
        try (Socket clientSocket = new Socket(ApplicationConfig.SERVER_HOST, ApplicationConfig.SERVER_PORT)){
            System.out.print("Welcome to the database client!!");
            System.out.println("Client connected to server "+clientSocket.getInetAddress());
            
            
            appConfig = new ApplicationConfig();
            var commands = CommandFactory.ALL;
            var scanner = appConfig.getScanner();

            Consumer<Command> printCommandDetails = new Consumer<Command>() {
                @Override
                public void accept(Command c) {
                    System.out.println("Name: "+c.getName());
                    System.out.println("Has arguments: "+c.isHasArg());
                    System.out.println("Description: "+c.getDescription());
                    System.out.println("\n---\n");
                    c.getSubCommands().forEach(this);
                }
            };  commands.stream().forEach(printCommandDetails);

            while(true) {
                String query = askForQuery(commands, scanner);
    
                var outStream = clientSocket.getOutputStream();
                var buffReader = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
                var buffWriter = new PrintWriter(outStream, true);
                String resLine;
                
                buffWriter.println(query);
                System.out.println("Sending query...");
                while ((resLine=buffReader.readLine()) != null) {
                    if (resLine.equals("EOF")) break;
                    System.out.println(resLine);
                }
                
                Thread.sleep(10);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        
    }
}
