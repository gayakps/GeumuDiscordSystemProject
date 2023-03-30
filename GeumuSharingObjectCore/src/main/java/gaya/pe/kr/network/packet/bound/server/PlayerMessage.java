package gaya.pe.kr.network.packet.bound.server;

import gaya.pe.kr.network.connection.initializer.MinecraftServerInitializer;
import gaya.pe.kr.network.packet.global.MinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import lombok.Getter;

import java.util.UUID;

@Getter
public class PlayerMessage extends MinecraftPacket {

    UUID targetPlayerUUID;
    String targetPlayerName;
    String message;
    MinecraftServerInitializer serverInitializer;

    public PlayerMessage(UUID targetPlayerUUID, String targetPlayerName, String message) {
        super(PacketType.PLAYER_MESSAGE);
        this.targetPlayerUUID = targetPlayerUUID;
        this.targetPlayerName = targetPlayerName;
        this.message = message;
    }

    @Override
    public ByteBuf getData() {
        ByteBuf buf = Unpooled.buffer();
        ByteBufUtil.writeUtf8(buf, targetPlayerUUID.toString());
        ByteBufUtil.writeUtf8(buf, targetPlayerName);
        ByteBufUtil.writeUtf8(buf, message);
        System.out.println("getData 실행");
        return buf;
    }

    @Override
    public MinecraftPacket fromData(ByteBuf byteBuf) {
        return null;
    }


}
