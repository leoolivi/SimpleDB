package it.leo.main.protocol;

import java.io.Serializable;

public record Packet (
    byte command,
    byte opcode,
    int length,
    byte[] payload
) implements Serializable {}
