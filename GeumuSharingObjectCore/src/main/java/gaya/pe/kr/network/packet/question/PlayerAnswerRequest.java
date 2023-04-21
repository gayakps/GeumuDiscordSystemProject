package gaya.pe.kr.network.packet.question;

import gaya.pe.kr.network.packet.global.MinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;

public class PlayerAnswerRequest extends MinecraftPacket {
    protected PlayerAnswerRequest(PacketType type) {
        super(type);
    }
}
