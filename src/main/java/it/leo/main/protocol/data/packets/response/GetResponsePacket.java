package it.leo.main.protocol.data.packets.response;

import java.io.DataOutputStream;
import java.io.IOException;

public class GetResponsePacket extends QueryResponsePacket {

    private final int key_len;
    private final byte[] key_bytes;
    private final int value_len;
    private final byte[] value_bytes;

    public GetResponsePacket(
            byte[] key_bytes, 
            int key_len, 
            byte[] value_bytes, 
            int value_len, 
            byte status) {

        super(status, new byte[0], 2);
        this.key_bytes = key_bytes;
        this.key_len = key_len;
        this.value_bytes = value_bytes;
        this.value_len = value_len;
    }

    @Override
    public void writeTo(DataOutputStream out) throws IOException {
        out.writeByte(opcode);
        out.writeByte(status);
        out.writeInt(error_msg_len);
        out.write(error_msg);
        out.writeInt(chunks_len);
        out.writeInt(key_len);
        out.write(key_bytes);
        out.writeInt(value_len);
        out.write(value_bytes);
    }

}