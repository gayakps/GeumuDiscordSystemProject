package gaya.pe.kr.network.packet.bound.server;

import gaya.pe.kr.network.packet.global.MinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
public class PlayerRequestResponse extends MinecraftPacket {

    UUID requestPlayerUUID;
    long packetId;

    public PlayerRequestResponse(UUID requestPlayerUUID, long packetId) {
        super(PacketType.PLAYER_REQUEST_RESPONSE);
        this.requestPlayerUUID = requestPlayerUUID;
        this.packetId = packetId;
    }


}
