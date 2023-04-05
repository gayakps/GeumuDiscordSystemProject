package gaya.pe.kr.network.packet.startDirection.server;

import gaya.pe.kr.network.packet.global.MinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
public class PlayerMessage extends MinecraftPacket {

    UUID targetPlayerUUID;
    String targetPlayerName;
    String message;

    public PlayerMessage(UUID targetPlayerUUID, String targetPlayerName, String message) {
        super(PacketType.PLAYER_MESSAGE);
        this.targetPlayerUUID = targetPlayerUUID;
        this.targetPlayerName = targetPlayerName;
        this.message = message;
    }



}
