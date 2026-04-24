package it.leo.main.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

import it.leo.main.protocol.DbConnection;
import it.leo.main.server.persistence.interfaces.DBRepository;
import it.leo.main.server.strategies.PacketHandler;
import it.leo.main.server.strategies.PacketHandlerFactory;

public class ConnectionThread implements Runnable {

    private final DbConnection connection;
    private final DBRepository<String, String> dbRepository;
    private final DataOutputStream outputStream;
    private final DataInputStream inputStream;
    private final ThreadPoolExecutor executor;

    public ConnectionThread(DbConnection connection, ThreadPoolExecutor executor, DBRepository<String, String> dbRepository) throws IOException {
        this.connection = connection;
        this.dbRepository = dbRepository;
        this.executor = executor;
        this.inputStream = new DataInputStream(connection.getClientSocket().getInputStream());
        this.outputStream = new DataOutputStream(connection.getClientSocket().getOutputStream());
    }

    // TODO: Add proper exception handling

    @Override
    public void run() {

        // Message loop
        while(!connection.isClosed()) {
            System.out.println("DEBUG: Processando una nuova richiesta per la connessione "+connection.getUUID()+"...");
            try {
                System.out.println("DEBUG: In attesa di readLine...");
                byte commandByte = inputStream.readByte();

                PacketHandler<?> handler = PacketHandlerFactory.HANDLERS[commandByte];
                handler.handlePacket(inputStream, outputStream, executor, dbRepository);

            } catch (Exception ex) { 
                System.getLogger(ConnectionThread.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
            System.out.println("DEBUG: Sto rispondendo alla connessione "+connection.getUUID()+"...");
        }
    }
    
}
