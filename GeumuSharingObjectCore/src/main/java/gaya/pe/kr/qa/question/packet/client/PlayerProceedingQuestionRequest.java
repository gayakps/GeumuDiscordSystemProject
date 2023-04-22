package gaya.pe.kr.qa.question.packet.client;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import lombok.Getter;


@Getter
public class PlayerProceedingQuestionRequest extends AbstractMinecraftPacket {

    String playerName;
    String contents;

    public PlayerProceedingQuestionRequest(String playerName, String contents) {
        super(PacketType.PLAYER_PROCEEDING_QUESTION_REQUEST);
        this.playerName = playerName;
        this.contents = contents;
    }

}
