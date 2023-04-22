package gaya.pe.kr.qa.answer.packet.client;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import lombok.Getter;


@Getter
public class PlayerProceedingAnswerRequest extends AbstractMinecraftPacket {


    int questionId;
    String answer;
    String answerPlayerName;

    public PlayerProceedingAnswerRequest(int questionId, String answer, String answerPlayerName) {
        super(PacketType.PLAYER_PROCEEDING_ANSWER_REQUEST);
        this.questionId = questionId;
        this.answer = answer;
        this.answerPlayerName = answerPlayerName;
    }


}
