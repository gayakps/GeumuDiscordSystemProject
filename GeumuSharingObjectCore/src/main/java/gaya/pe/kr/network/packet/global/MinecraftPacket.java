package gaya.pe.kr.network.packet.global;


import gaya.pe.kr.network.packet.bound.server.PlayerMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.CharsetUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.UUID;

public abstract class MinecraftPacket {
    private final PacketType type;

    protected MinecraftPacket(PacketType type) {
        this.type = type;
    }

    public PacketType getType() {
        return type;
    }

    public abstract ByteBuf getData();

    public static MinecraftPacket fromData(byte typeId, ByteBuf byteBuf) {
        PacketType type = PacketType.fromId(typeId);
        switch (type) {
            case PLAYER_MESSAGE:

                String message = ByteBufUtil.readUtf8(data);
                return new PlayerMessage(UUID.fromString(targetPlayerUUID), targetPlayerName, message);
            // 다른 패킷 타입들에 대한 처리
            default:
                throw new IllegalArgumentException("Unknown packet type: " + type);
        }
    }

    public static <T extends MinecraftPacket> ByteBuf getParseData(T data, ByteBuf buf) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(data);
            byte[] serializedUser = byteArrayOutputStream.toByteArray();
            buf.writeInt(serializedUser.length);
            buf.writeBytes(serializedUser);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buf;
    }
}

