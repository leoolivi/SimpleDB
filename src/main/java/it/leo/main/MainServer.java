package it.leo.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import it.leo.main.config.ApplicationConfig;
import it.leo.main.data.DBResponse;
import it.leo.main.data.enums.ResponseStatus;

public class MainServer {

    private static ApplicationConfig appConfig;
    
    public static void main(String[] args) throws IOException {
        appConfig = new ApplicationConfig();
        var queryHandler = appConfig.getQueryHandler();
        DBResponse<String, String> response;

        try (ServerSocket ss = new ServerSocket(ApplicationConfig.SERVER_PORT)) {
            Socket clientSocket = ss.accept();
            System.out.println("Client connected!!");
            var buffReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            var printWriter = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
            String newLine;
            while ((newLine=buffReader.readLine())!=null) {
                response = queryHandler.handleQuery(newLine);
                if(response.status() == ResponseStatus.ERROR) {
                    printWriter.println(response.msg());
                    printWriter.println("EOF");
                } else {
                    List<String> lines = response.toLines();
                    lines.forEach(printWriter::println);
                }
                printWriter.flush();
            }
            System.out.println("Finished Execution");
        } catch (IOException e) {
            System.err.println("IO Exception: "+e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
