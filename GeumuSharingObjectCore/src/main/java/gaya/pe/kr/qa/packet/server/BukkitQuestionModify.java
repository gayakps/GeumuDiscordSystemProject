package gaya.pe.kr.qa.packet.server;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import gaya.pe.kr.qa.packet.type.QAModifyType;
import gaya.pe.kr.qa.question.data.Question;
import lombok.Getter;


@Getter
public class BukkitQuestionModify extends AbstractMinecraftPacket {

    QAModifyType qaModifyType;
    Question[] questions;

    public BukkitQuestionModify(QAModifyType qaModifyType, Question[] questions) {
        super(PacketType.BUKKIT_QUESTION_MODIFY);
        this.qaModifyType = qaModifyType;
        this.questions = questions;
    }


}
