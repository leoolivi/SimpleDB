package it.leo.main.protocol;

import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Packet {
    protected final byte command;
    protected final byte opcode;
    protected final byte status;
    protected final int error_msg_len;
    protected final byte[] error_msg;
    protected final int n_chunks;

    
    
    public Packet(byte command, byte opcode, byte status, byte[] error_msg, int n_chunks) {
        this.command = command;
        this.error_msg = error_msg;
        this.error_msg_len = error_msg.length;
        this.n_chunks = n_chunks;
        this.opcode = opcode;
        this.status = status;
    }
    
    /* public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private byte command;
        private byte opcode;
        private byte status;
        private byte[] error_msg;
        private int n_chunks;
    
        public Builder command(byte command) {
            this.command = command;
            return this;
        }
    
        public Builder opcode(byte opcode) {
            this.opcode = opcode;
            return this;
        }

        public Builder status(byte status) {
            this.status = status;
            return this;
        }

        public Builder errorMsg(byte[] error_msg) {
            this.error_msg = error_msg;
            return this;
        }
        
        public Builder nChunks(int n_chunks) {
            this.n_chunks = status;
            return this;
        }
    

        public Packet build() {
            return new Packet(command, opcode, status, error_msg, n_chunks);
        }
    } */

    public abstract void writeTo(DataOutputStream out) throws IOException;

    public byte getCommand() {
        return command;
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

    public int getN_chunks() {
        return n_chunks;
    }
}

