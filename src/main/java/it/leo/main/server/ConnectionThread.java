package it.leo.main.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.ThreadPoolExecutor;

import it.leo.main.protocol.DbConnection;
import it.leo.main.server.enums.ResponseStatus;
import it.leo.main.server.persistence.interfaces.DBRepository;

public class ConnectionThread implements Runnable {

    private final DbConnection connection;
    private final PacketProcessor processor;

    public ConnectionThread(DbConnection connection, ThreadPoolExecutor threadPoolExecutor, DBRepository<String, String> dbRepository) {
        this.connection = connection;
        this.processor = new PacketProcessor(connection.getBufferedReader(), 
            connection.getPrintWriter(), 
            dbRepository, 
            threadPoolExecutor);
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(connection.getClientSocket().getOutputStream());
            outputStream.writeObject(connection);
        } catch (IOException ex) {
            System.getLogger(ConnectionThread.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
        while(!connection.isClosed()) {
            System.out.println("DEBUG: Processando una nuova richiesta per la connessione "+connection.getUUID()+"...");
            var response = processor.processNextPacket();
            System.out.println("DEBUG: Sto rispondendo alla connessione "+connection.getUUID()+"...");
            switch (response.status()) {
                case ResponseStatus.DATA -> response.toLines().forEach(line -> connection.getPrintWriter().println(line));
                default -> {
                    connection.getPrintWriter().println(response.msg());
                    connection.getPrintWriter().println("EOF");
                }
            }
            connection.getPrintWriter().flush();
        }
    }
    
}
