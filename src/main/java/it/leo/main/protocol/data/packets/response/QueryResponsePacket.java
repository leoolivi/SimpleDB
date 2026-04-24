package it.leo.main.protocol.data.packets.response;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class QueryResponsePacket extends ResponsePacket {

    public QueryResponsePacket(
            byte status, 
            byte[] error_msg, 
            int chunks_len) {
        super(status, error_msg, chunks_len);
    }

    @Override
    public void writeTo(DataOutputStream out) throws IOException {
        out.writeByte(opcode);
        out.writeByte(status);
        out.writeInt(error_msg_len);
        out.write(error_msg);
        out.writeInt(chunks_len);
    }
}
