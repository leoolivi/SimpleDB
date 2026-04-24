package it.leo.main.server.strategies;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.concurrent.ThreadPoolExecutor;

import it.leo.main.protocol.data.packets.request.RequestPacket;
import it.leo.main.server.persistence.interfaces.DBRepository;

// TODO: Complete with auth layer (Auth manager component)

public class ConnectionPacketHandler implements PacketHandler<RequestPacket> {

    @Override
    public void handlePacket(DataInputStream inputStream, DataOutputStream outputStream, ThreadPoolExecutor executor,
            DBRepository<String, String> repository) throws Exception {
        byte opCodeByte = inputStream.readByte();
        byte queryTypeByte = inputStream.readByte();
        
        
        if (opCodeByte != 0x01) {
            System.err.println("ERROR: Invalid packet being processed with opcode "+opCodeByte);
        }
    }
    
}
