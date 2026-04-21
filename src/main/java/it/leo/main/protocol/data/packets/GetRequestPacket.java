package it.leo.main.protocol.data.packets;

import java.io.DataOutputStream;
import java.io.IOException;

import it.leo.main.protocol.Packet;

public class GetRequestPacket extends Packet {

    private final int key_len;
    private final byte[] key_bytes;
    
    
    public GetRequestPacket(byte[] key_bytes) {
        super((byte) 0x02, (byte) 0x01, (byte) 0x01, null, 1);
        this.key_bytes = key_bytes;        
        this.key_len = key_bytes.length;
    }
    
    public int getKey_len() {
        return key_len;
    }

    public byte[] getKey_bytes() {
        return key_bytes;
    }
    
    @Override
    public void writeTo(DataOutputStream out) throws IOException {
        out.writeByte(command);
        out.writeByte(opcode);
        out.writeByte(status);
        out.writeInt(error_msg_len);
        if (error_msg_len > 0) out.write(error_msg);
        out.writeInt(key_len);
        out.write(key_bytes);
    }
    
}
