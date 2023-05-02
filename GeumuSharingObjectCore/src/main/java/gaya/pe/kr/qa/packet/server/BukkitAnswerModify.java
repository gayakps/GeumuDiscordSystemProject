package gaya.pe.kr.qa.packet.server;

import gaya.pe.kr.network.packet.global.AbstractMinecraftPacket;
import gaya.pe.kr.network.packet.global.PacketType;
import gaya.pe.kr.qa.answer.data.Answer;
import gaya.pe.kr.qa.packet.type.QAModifyType;
import lombok.Getter;

@Getter
public class BukkitAnswerModify extends AbstractMinecraftPacket {

    QAModifyType qaModifyType;
    Answer[] answers;

    public BukkitAnswerModify(QAModifyType qaModifyType, Answer[] answers) {
        super(PacketType.BUKKIT_ANSWER_MODIFY);
        this.qaModifyType = qaModifyType;
        this.answers = answers;
    }
}
