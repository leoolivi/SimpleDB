package it.leo.main.protocol;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;

/*

Fields to store in a DB Conn
- Session ID to bind the client to a specific thread
- Server version
- Charset es. UTF8, Latin1
- Limits (optional): max rows returnable, timeouts
- Socket
- I/O Streams

*/
public final class DbConnection implements AutoCloseable, Serializable {

    private final String UUID;
    private final String serverVersion;
    private final String charset;
    private transient Socket clientSocket;
    
    
    public DbConnection(String UUID, String charset, Socket clientSocket, String serverVersion) throws IOException {
        this.UUID = UUID;
        this.charset = charset;
        this.clientSocket = clientSocket;
        this.serverVersion = serverVersion;
    }
    
    public String getUUID() {
        return UUID;
    }
    
    public boolean isClosed() {
        return clientSocket.isClosed();
    }
    
    public String getServerVersion() {
        return serverVersion;
    }
    
    public String getCharset() {
        return charset;
    }
    
    @Override
    public void close() throws Exception {
        clientSocket.close();
    }

    public Socket getClientSocket() {
        return clientSocket;
    }
    
    /*
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject(); // Carica i campi normali (non transient)
        
        // Campi vuoti, verranno popolati client side
        // this.bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        // this.printWriter = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        
        System.out.println("Oggetto ricevuto e reader inizializzato sul Client!");
    } */
    
    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public String toString() {
        return String.format("DB CONNECTION uuid=%s server_version=%s charset=%s", UUID, serverVersion, charset);
    }
}
