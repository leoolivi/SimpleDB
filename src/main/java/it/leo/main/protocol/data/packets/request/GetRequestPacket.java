package it.leo.main.protocol.data.packets.request;

import java.io.DataOutputStream;
import java.io.IOException;

public class GetRequestPacket extends QueryRequestPacket {

    private final int key_len;
    private final byte[] key_bytes;
    
    
    public GetRequestPacket(byte[] key_bytes) {
        super((byte) 0x02, (byte) 0x01,  1);
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
        // Header
        out.writeByte(command);
        out.writeByte(opcode);
        out.writeByte(queryCommand);

        // PAYLOAD
        out.writeInt(chunks_len);
        out.writeInt(key_len);
        out.write(key_bytes);
    }
    
}
