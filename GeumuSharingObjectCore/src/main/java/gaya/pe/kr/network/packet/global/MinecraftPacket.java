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

    public static MinecraftPacket fromData(int byteId, ByteBuf byteBuf) {
        System.out.println("fromData 실행 " + byteId + " ||| " + byteBuf.readableBytes());
        return null;
    }

    protected final <T> ByteBuf bufAddObjectData(ByteBuf buf, T data) {
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

