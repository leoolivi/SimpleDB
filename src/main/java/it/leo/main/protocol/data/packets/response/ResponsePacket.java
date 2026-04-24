package it.leo.main.protocol.data.packets.response;

import it.leo.main.protocol.data.packets.Packet;

public abstract class ResponsePacket implements Packet {
    protected final byte opcode;
    protected final byte status;
    protected final int error_msg_len;
    protected final byte[] error_msg;
    protected final int chunks_len;

    
    
    public ResponsePacket(byte status, byte[] error_msg, int chunks_len) {
        this.error_msg = error_msg;
        this.error_msg_len = error_msg.length;
        this.chunks_len = chunks_len;
        this.opcode = 0x02;
        this.status = status;
    }

    public byte getOpcode() {
        return opcode;
    }

    public byte getStatus() {
        return status;
    }

    public int getErrorMsgLen() {
        return error_msg_len;
    }

    public byte[] getErrorMsg() {
        return error_msg;
    }

    public int getChunksLen() {
        return chunks_len;
    }
}

