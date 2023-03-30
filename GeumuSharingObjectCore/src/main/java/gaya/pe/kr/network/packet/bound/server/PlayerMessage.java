package gaya.pe.kr.network.packet.bound.server;

import gaya.pe.kr.network.connection.initializer.MinecraftServerInitializer;
import gaya.pe.kr.network.packet.global.MinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import gaya.pe.kr.util.ObjectConverter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
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
