package gaya.pe.kr.qa.answer.packet.client;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import gaya.pe.kr.qa.answer.data.Answer;
import gaya.pe.kr.qa.packet.type.QAModifyType;
import lombok.Getter;

@Getter
public class AnswerModifyRequest extends AbstractMinecraftPacket {

    QAModifyType qaModifyType;
    Answer[] answers;

    public AnswerModifyRequest(QAModifyType qaModifyType, Answer[] answers) {
        super(PacketType.ANSWER_MODIFY_REQUEST);
        this.qaModifyType = qaModifyType;
        this.answers = answers;
    }
}
