package gaya.pe.kr.network.packet;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum PacketType {

    PLAYER_MESSAGE(0x01, PacketBound.SERVER_BOUND),
    PLAYER_TITLE(0x02, PacketBound.SERVER_BOUND);

    private final byte id;
    private final PacketBound packetBound;
    PacketType(int id, PacketBound packetBound) {
        this.id = (byte) id;
        this.packetBound = packetBound;
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
