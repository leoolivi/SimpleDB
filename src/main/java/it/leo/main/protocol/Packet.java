package it.leo.main.protocol;

import java.io.IOException;
import java.io.OutputStream;

public class Packet {
    private final byte command;
    private final byte opcode;
    private final int length;
    private final byte[] payload;

    public byte getCommand() {
        return command;
    }
    public byte getOpcode() {
        return opcode;
    }
    public int getLength() {
        return length;
    }
    public byte[] getPayload() {
        return payload;
    }

    public Packet(byte command, byte opcode, byte[] payload) {
        this.command = command;
        this.opcode = opcode;
        this.payload = payload;
        this.length = payload.length;
    }
    public static Builder builder() {
        return new Builder();
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

