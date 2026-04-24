package it.leo.main.protocol.data.packets.request;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class QueryRequestPacket extends RequestPacket{
    protected final byte queryCommand;

    public QueryRequestPacket(byte command, byte queryCommand, int chunks_len) {
        super(command, chunks_len);
        this.queryCommand = queryCommand;
    }

    @Override
    public abstract void writeTo(DataOutputStream out) throws IOException;

}
