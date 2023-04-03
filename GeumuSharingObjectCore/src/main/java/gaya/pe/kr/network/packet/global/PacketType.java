package gaya.pe.kr.network.packet.global;

import gaya.pe.kr.network.packet.bound.client.MinecraftOptionReloadRequest;
import gaya.pe.kr.network.packet.bound.client.ServerPacketResponse;
import gaya.pe.kr.network.packet.bound.server.MinecraftOption;
import gaya.pe.kr.network.packet.bound.server.PlayerMessage;
import gaya.pe.kr.network.packet.bound.server.PlayerRequestResponse;
import lombok.Getter;

@Getter
public enum PacketType {

    PLAYER_MESSAGE(0x01, PacketStartDirection.SERVER, PlayerMessage.class),
    SERVER_PACKET_RESPONSE(0x02, PacketStartDirection.CLIENT, ServerPacketResponse.class),
    MINECRAFT_OPTION(0x03, PacketStartDirection.SERVER, MinecraftOption.class),
    MINECRAFT_OPTION_RELOAD_REQUEST(0x04, PacketStartDirection.CLIENT, MinecraftOptionReloadRequest.class),

    PLAYER_REQUEST_RESPONSE(0x05, PacketStartDirection.SERVER, PlayerRequestResponse.class);

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
