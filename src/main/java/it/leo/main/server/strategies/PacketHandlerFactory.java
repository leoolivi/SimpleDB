package it.leo.main.server.strategies;

import it.leo.main.protocol.data.packets.request.RequestPacket;

public class PacketHandlerFactory {
    public static final PacketHandler<? extends RequestPacket>[] HANDLERS = new PacketHandler<?>[255];

    static {
        HANDLERS[0x01] = new ConnectionPacketHandler();
        HANDLERS[0x02] = new QueryPacketHandler();
    }
}
