package gaya.pe.kr.qa.question.packet.client;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import gaya.pe.kr.qa.packet.type.QAModifyType;
import gaya.pe.kr.qa.question.data.Question;
import lombok.Getter;

/**
 * 서버 측으로 부터 전달받은 Question 객체를 전체 서버에 뿌려주는 역할을 해야함
 */
@Getter
public class QuestionModifyRequest extends AbstractMinecraftPacket {


    QAModifyType qaModifyType;
    Question[] questions;

    public QuestionModifyRequest(QAModifyType qaModifyType, Question[] questions) {
        super(PacketType.QUESTION_MODIFY_REQUEST);
        this.qaModifyType = qaModifyType;
        this.questions = questions;
    }
}
