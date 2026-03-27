package it.leo.main.echo_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServer {
    
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(9001);
            Socket clientSocket = ss.accept();
            System.out.println("Client connected!!");
            var buffReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            var printWriter = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
            StringBuilder stringBuffer = new StringBuilder();
            String newLine;
            while ((newLine=buffReader.readLine())!=null) {
                System.out.println(newLine);
                printWriter.println("ECHO:"+newLine);
                stringBuffer.append(newLine);
            }
            clientSocket.close();
            System.out.println("Finished Execution");
        } catch (IOException e) {
            System.err.println("IO Exception: "+e.getMessage());
        }
    }
}
