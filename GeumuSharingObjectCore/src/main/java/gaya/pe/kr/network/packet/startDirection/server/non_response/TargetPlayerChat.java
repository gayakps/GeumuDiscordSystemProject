package gaya.pe.kr.network.packet.startDirection.server.non_response;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import lombok.Getter;


@Getter
public class TargetPlayerChat extends AbstractMinecraftPacket {

    String targetPlayerName;
    String[] messages;

    public TargetPlayerChat(String targetPlayerName, String... messages) {
        super(PacketType.TARGET_PLAYER_CHAT);
        this.targetPlayerName = targetPlayerName;
        this.messages = messages;
    }
}
