package gaya.pe.kr.network.packet.global;


import gaya.pe.kr.network.packet.bound.server.PlayerMessage;
import gaya.pe.kr.util.ObjectConverter;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

@Getter
public abstract class MinecraftPacket implements Serializable {

    static Random random = new Random();
    private final PacketType type;
    private final Long packetID;

    protected MinecraftPacket(PacketType type) {
        this.type = type;
        this.packetID = random.nextLong();
    }

    public PacketType getType() {
        return type;
    }

    public ByteBuf getData() {
        return ObjectConverter.getByteBufFromObject(this);
    }

    @SuppressWarnings("unchecked")
    public static <T extends MinecraftPacket> T fromData(byte byteId, ByteBuf data) {
        PacketType packetType = PacketType.fromId(byteId);
        return (T) ObjectConverter.getMinecraftPacket(data, packetType.getClazz());
    }

}

