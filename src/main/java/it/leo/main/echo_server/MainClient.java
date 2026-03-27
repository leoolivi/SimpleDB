package it.leo.main.echo_server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MainClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 9001;

    public static void main(String[] args) {
        try {
            Socket clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
            System.out.println("Client connected to server "+clientSocket.getInetAddress());
            String testMsg = "this is a test message from client";
            var outStream = clientSocket.getOutputStream();
            var buffReader = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
            var buffWriter = new PrintWriter(outStream, true);
            String resLine;
            for (int i = 0;i<=10;i++) {
                buffWriter.println(testMsg);
                System.out.println("Sending: "+testMsg);
                if ((resLine=buffReader.readLine()) != null) {
                    System.out.println(resLine);
                }
                Thread.sleep(100);
            }
            clientSocket.close();
        }  catch (Exception e) {
            e.printStackTrace();
        }
    }
}
