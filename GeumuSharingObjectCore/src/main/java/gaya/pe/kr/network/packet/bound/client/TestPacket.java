package gaya.pe.kr.network.packet.bound.client;

import gaya.pe.kr.network.connection.handler.MinecraftClientPacketHandler;
import gaya.pe.kr.network.connection.handler.MinecraftServerPacketHandler;
import gaya.pe.kr.network.packet.bound.server.PlayerMessage;
import gaya.pe.kr.network.packet.global.MinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import lombok.Getter;
import lombok.ToString;


@ToString
@Getter
public class TestPacket extends MinecraftPacket {


    MinecraftClientPacketHandler minecraftClientPacketHandler;
    MinecraftServerPacketHandler minecraftServerPacketHandler;


    protected TestPacket(PacketType type) {
        super(type);
    }
}
