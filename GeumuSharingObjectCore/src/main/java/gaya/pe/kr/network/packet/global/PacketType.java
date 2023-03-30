package gaya.pe.kr.network.packet.global;

import lombok.Getter;

@Getter
public enum PacketType {

    PLAYER_MESSAGE(0x01, PacketStartDirection.SERVER),
    PLAYER_TITLE(0x02, PacketStartDirection.SERVER);

    private final byte id;
    private final PacketStartDirection packetStartDirection;
    PacketType(int id, PacketStartDirection packetStartDirection) {
        this.id = (byte) id;
        this.packetStartDirection = packetStartDirection;
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
