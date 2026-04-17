package it.leo.main.protocol;

import java.io.BufferedReader;
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

    private static final long serialVersionUID = 1036769926986257780L;

    private final String UUID;
    private final String serverVersion;
    private final String charset;
    private transient Socket clientSocket;
    

    
    private transient BufferedReader bufferedReader;
    private transient PrintWriter printWriter;
    
    public DbConnection(String UUID, String charset, Socket clientSocket, String serverVersion) throws IOException {
        this.UUID = UUID;
        this.charset = charset;
        this.clientSocket = clientSocket;
        this.bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.printWriter = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
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
    
    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }
    
    public PrintWriter getPrintWriter() {
        return printWriter;
    }
    
    public Socket getClientSocket() {
        return clientSocket;
    }
    
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject(); // Carica i campi normali (non transient)
        
        // Campi vuoti, verranno popolati client side
        // this.bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        // this.printWriter = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        
        System.out.println("Oggetto ricevuto e reader inizializzato sul Client!");
    }
    
    public void setBufferedReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }
    
    public void setPrintWriter(PrintWriter printWriter) {
        this.printWriter = printWriter;
    }
    
    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

}
