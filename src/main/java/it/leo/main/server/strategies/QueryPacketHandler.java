package it.leo.main.server.strategies;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.concurrent.ThreadPoolExecutor;

import it.leo.main.config.ProtocolConfig;
import it.leo.main.config.enums.QueryType;
import it.leo.main.protocol.data.packets.Packet;
import it.leo.main.protocol.data.packets.request.QueryRequestPacket;
import it.leo.main.protocol.data.packets.response.ErrorResponsePacket;
import it.leo.main.protocol.data.packets.response.GetResponsePacket;
import it.leo.main.protocol.utils.SerializerUtil;
import it.leo.main.server.enums.ResponseStatus;
import it.leo.main.server.persistence.interfaces.DBRepository;
import it.leo.main.server.tasks.GetRecordTask;

public class QueryPacketHandler implements PacketHandler<QueryRequestPacket> {

    @Override
    public void handlePacket(DataInputStream inputStream, 
            DataOutputStream outputStream, 
            ThreadPoolExecutor poolExecutor,
            DBRepository<String, String> repository) throws Exception {
        byte opCodeByte = inputStream.readByte();
        byte queryTypeByte = inputStream.readByte();
        QueryType queryType = ProtocolConfig.getQueryType(queryTypeByte);
        
        if (opCodeByte != 0x01) {
            System.err.println("ERROR: Invalid packet being processed with opcode "+opCodeByte);
        }

        switch (queryType) {
            case QueryType.GET -> {
                var nchunks = inputStream.readByte();

                if (nchunks != 1) return;
                
                var key_len = inputStream.readInt();
                var key = inputStream.readNBytes(key_len);
                var task = new GetRecordTask((String) SerializerUtil.convertBytesToObject(key), (DBRepository<String, String>) repository);
                var resFuture = poolExecutor.submit(task);
                var dbRes = resFuture.get();
                
                Packet resPacket;
                if (dbRes.status() != ResponseStatus.ERROR) {
                    String errorMsg = "ERROR: " + dbRes.msg();
                    resPacket = new ErrorResponsePacket(errorMsg.getBytes());
                } else {
                    var record = dbRes.rows().getFirst();
                    byte[] value_bytes = record.getValue().getBytes();
                    resPacket = new GetResponsePacket(
                        key, 
                        key_len, 
                        value_bytes, 
                        value_bytes.length, 
                        (byte) 0x01);
                }
                
                resPacket.writeTo(outputStream);
            }
            default -> System.getLogger(QueryPacketHandler.class.getName()).log(System.Logger.Level.ERROR, (String) null, "Unsupported query command");
        }

    }
    
}
