package it.leo.main.protocol;

import java.io.IOException;
import java.io.OutputStream;

public abstract class Packet {
    private final byte command;
    private final byte opcode;
    private final byte status;
    private final int error_msg_len;
    private final byte[] error_msg;
    private final int n_chunks;

    
    public static Builder builder() {
        return new Builder();
    }

    public Packet(byte command, byte[] error_msg, int error_msg_len, int n_chunks, byte opcode, byte status) {
        this.command = command;
        this.error_msg = error_msg;
        this.error_msg_len = error_msg_len;
        this.n_chunks = n_chunks;
        this.opcode = opcode;
        this.status = status;
    }
    
    public static class Builder {
        private byte command;
        private byte opcode;
        private byte[] payload;
    
        public Builder command(byte command) {
            this.command = command;
            return this;
        }
    
        public Builder opcode(byte opcode) {
            this.opcode = opcode;
            return this;
        }
    
        public Builder payload(byte[] payload) {
            this.payload = payload;
            return this;
        }
    
        public Packet build() {
            return new Packet(command, opcode, payload);
        }
    }

    public void writeTo(OutputStream out) throws IOException {
        out.write(command);
        out.write(opcode);
        out.write(length);
        out.write(payload);
    }
}

