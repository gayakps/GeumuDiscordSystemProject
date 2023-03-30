package gaya.pe.kr.network.packet.bound.server;

import gaya.pe.kr.network.packet.MinecraftPacket;
import gaya.pe.kr.network.packet.PacketType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
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

    @Override
    public ByteBuf getData() {
        ByteBuf buf = Unpooled.buffer();
        ByteBufUtil.writeUtf8(buf, targetPlayerUUID.toString());
        ByteBufUtil.writeUtf8(buf, targetPlayerName);
        ByteBufUtil.writeUtf8(buf, message);
        return buf;
    }

}
