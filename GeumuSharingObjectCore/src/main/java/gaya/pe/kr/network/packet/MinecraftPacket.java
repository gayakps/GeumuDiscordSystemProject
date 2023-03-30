package gaya.pe.kr.network.packet;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public abstract class MinecraftPacket {
    private final PacketType type;

    protected MinecraftPacket(PacketType type) {
        this.type = type;
    }

    public PacketType getType() {
        return type;
    }

    public abstract ByteBuf getData();

    public static MinecraftPacket fromData(byte typeId, ByteBuf data) {
        PacketType type = PacketType.fromId(typeId);
        switch (type) {

            case PLAYER_MESSAGE:

            case PLAYER_TITLE:

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

