package gaya.pe.kr.qa.answer.packet.client;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import gaya.pe.kr.qa.data.QAUser;
import lombok.Getter;

import javax.annotation.Nullable;


@Getter
public class PlayerTransientProceedingAnswerRequest extends AbstractMinecraftPacket {


    long questionId;
    String answerContent;

    QAUser qaUser;

    public PlayerTransientProceedingAnswerRequest(long questionId, String answerContent, QAUser qaUser) {
        super(PacketType.PLAYER_TRANSIENT_PROCEEDING_ANSWER_REQUEST);
        this.questionId = questionId;
        this.answerContent = answerContent;
        this.qaUser = qaUser;
    }
}
