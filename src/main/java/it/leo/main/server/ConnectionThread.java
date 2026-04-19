package it.leo.main.server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.ThreadPoolExecutor;

import it.leo.main.config.ProtocolConfig;
import it.leo.main.config.enums.CommandType;
import it.leo.main.config.enums.OpCodeType;
import it.leo.main.protocol.DbConnection;
import it.leo.main.protocol.Packet;
import it.leo.main.protocol.utils.SerializerUtil;
import it.leo.main.server.persistence.interfaces.DBRepository;

public class ConnectionThread implements Runnable {

    private final DbConnection connection;
    private final PacketProcessor processor;

    public ConnectionThread(DbConnection connection, ThreadPoolExecutor threadPoolExecutor, DBRepository<String, String> dbRepository) {
        this.connection = connection;
        this.processor = new PacketProcessor(
            dbRepository,
            connection.getInputStream(),
            threadPoolExecutor);
    }

    @Override
    public void run() {
        // Connection Procedure
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(connection.getClientSocket().getOutputStream());
            outputStream.writeObject(connection);
        } catch (IOException ex) {
            System.getLogger(ConnectionThread.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }

        // Message loop
        while(!connection.isClosed()) {
            try {
                System.out.println("DEBUG: Processando una nuova richiesta per la connessione "+connection.getUUID()+"...");
                var response = processor.processNextPacket();
                System.out.println("DEBUG: Sto rispondendo alla connessione "+connection.getUUID()+"...");
                var packet = Packet.builder()
                        .command(ProtocolConfig.getCommandByte(CommandType.QUERY))
                        .opcode(ProtocolConfig.getOpCodeByte(OpCodeType.RESPONSE))
                        .payload(SerializerUtil.convertObjectToBytes(response))
                        .build();
                packet.writeTo(connection.getOutputStream());
            } catch (ClassNotFoundException | IOException ex) {
                System.getLogger(ConnectionThread.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        }
    }
    
}
