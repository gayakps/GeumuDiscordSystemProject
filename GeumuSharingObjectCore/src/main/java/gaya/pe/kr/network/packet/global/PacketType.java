package gaya.pe.kr.network.packet.global;

import gaya.pe.kr.network.packet.bound.client.ServerPacketResponse;
import gaya.pe.kr.network.packet.bound.server.PlayerMessage;
import lombok.Getter;

@Getter
public enum PacketType {

    PLAYER_MESSAGE(0x01, PacketStartDirection.SERVER, PlayerMessage.class),
    SERVER_PACKET_RESPONSE(0x02, PacketStartDirection.CLIENT, ServerPacketResponse.class);
//    PLAYER_TITLE(0x02, PacketStartDirection.SERVER, PlayerTitle.class);

    private final byte id;
    private final PacketStartDirection packetStartDirection;
    private final Class<? extends MinecraftPacket> clazz;
    PacketType(int id, PacketStartDirection packetStartDirection, Class<? extends MinecraftPacket> clazz) {
        this.id = (byte) id;
        this.packetStartDirection = packetStartDirection;
        this.clazz = clazz;
    }

    public static PacketType fromId(byte id) {
        for (PacketType type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown packet ID: " + id);
    }

}
