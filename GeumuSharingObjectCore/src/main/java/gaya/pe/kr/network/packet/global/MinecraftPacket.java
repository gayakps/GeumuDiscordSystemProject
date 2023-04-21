package gaya.pe.kr.network.packet.global;


import gaya.pe.kr.util.ObjectConverter;
import io.netty.buffer.ByteBuf;
import lombok.Getter;

import java.io.Serializable;
import java.util.Random;

@Getter
public abstract class MinecraftPacket implements Serializable {

    static Random random = new Random();
    private final byte packetType;
    private final Long packetID;

    protected MinecraftPacket() {
        this.packetType = PacketType.fromClass(this.getClass()).getId();
        this.packetID = random.nextLong();
    }

    protected MinecraftPacket(PacketType type) {
        this.packetType = type.getId();
        this.packetID = random.nextLong();
    }

    public PacketType getType() {
        return PacketType.fromId(getPacketType());
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

