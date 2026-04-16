package it.leo.main.data.connection;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.ThreadPoolExecutor;

import it.leo.main.data.enums.ResponseStatus;
import it.leo.main.persistence.interfaces.DBRepository;
import it.leo.main.processors.PacketProcessor;

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
                case ResponseStatus.DATA -> connection.getPrintWriter().print(response.toLines());
                default -> connection.getPrintWriter().print(response.msg());
            }
        }
    }
    
}
