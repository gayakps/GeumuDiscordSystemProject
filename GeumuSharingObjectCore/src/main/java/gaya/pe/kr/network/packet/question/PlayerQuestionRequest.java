package gaya.pe.kr.network.packet.question;

import gaya.pe.kr.network.packet.global.MinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;

public class PlayerQuestionRequest extends MinecraftPacket {
    protected PlayerQuestionRequest() {
        super(PacketType.PLAYER_QUESTION_REQUEST);
    }
}
