package it.leo.main.protocol.data.packets.response;

import java.io.DataOutputStream;
import java.io.IOException;

public class ErrorResponsePacket extends ResponsePacket {

    public ErrorResponsePacket(byte[] error_msg) {
        super((byte) 0X02, error_msg, 0);
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
