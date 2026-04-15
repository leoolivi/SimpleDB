package it.leo.main;

import it.leo.main.config.ApplicationConfig;
import it.leo.main.services.CLIService;

public class MainClient {

    private static ApplicationConfig appConfig;
    
    public static void main(String[] args) {
        /*
        try (Socket clientSocket = new Socket(ApplicationConfig.SERVER_HOST, ApplicationConfig.SERVER_PORT)){
            System.out.print("Welcome to the database client!!");
            System.out.println("Client connected to server "+clientSocket.getInetAddress());
            
            
            appConfig = new ApplicationConfig();
            var commands = QueryCommandFactory.ALL;
            var scanner = appConfig.getScanner();

            Consumer<QueryCommand> printCommandDetails = new Consumer<QueryCommand>() {
                @Override
                public void accept(QueryCommand c) {
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
        */
       CLIService.start();
        
    }
}
