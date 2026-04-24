package it.leo.main.protocol.data.packets.request;

import it.leo.main.protocol.data.packets.Packet;

public abstract class RequestPacket implements Packet {
    protected final byte command;
    protected final byte opcode;
    protected final int chunks_len;

    public RequestPacket(byte command, int chunks_len) {
        this.command = command;
        this.chunks_len = chunks_len;
        this.opcode = 0x01;
    }

    public byte getCommand() {
        return command;
    }

    public byte getOpcode() {
        return opcode;
    }

    public int getChunksLen() {
        return chunks_len;
    }

}
