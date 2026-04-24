package it.leo.main.protocol.data.packets;

import java.io.DataOutputStream;
import java.io.IOException;

public interface Packet {
    public void writeTo(DataOutputStream out) throws IOException;
}
