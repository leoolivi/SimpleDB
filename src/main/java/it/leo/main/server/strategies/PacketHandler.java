package it.leo.main.server.strategies;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.concurrent.ThreadPoolExecutor;

import it.leo.main.protocol.data.packets.request.RequestPacket;
import it.leo.main.server.persistence.interfaces.DBRepository;

public interface PacketHandler<T extends RequestPacket> {
    public void handlePacket(DataInputStream in, 
        DataOutputStream out,
        ThreadPoolExecutor executor,
        DBRepository<String, String> repository) throws Exception;
}
