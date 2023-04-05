package gaya.pe.kr.network.packet.startDirection.client;

import gaya.pe.kr.network.packet.global.MinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;

import java.util.UUID;

public class MinecraftOptionReloadRequest extends MinecraftPacket {

    UUID requestPlayerUUID;


    protected MinecraftOptionReloadRequest() {
        super(PacketType.MINECRAFT_OPTION_RELOAD_REQUEST);
    }
}
